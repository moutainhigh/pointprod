package com.emoney.pointweb.service.biz.listener;

import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.MessageTypeEnum;
import com.emoeny.pointcommon.enums.PointOrderStatusEnum;
import com.emoeny.pointcommon.enums.PointRecordStatusEnum;
import com.emoney.pointweb.repository.PointOrderRepository;
import com.emoney.pointweb.repository.PointRecordESRepository;
import com.emoney.pointweb.repository.PointRecordRepository;
import com.emoney.pointweb.repository.dao.entity.PointMessageDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.repository.dao.entity.PointProductDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.service.biz.PointMessageService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import com.emoney.pointweb.service.biz.redis.RedissonDistributionLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    private PointRecordRepository pointRecordRepository;

    @Autowired
    private PointRecordESRepository pointRecordESRepository;

    @Autowired
    private PointOrderRepository pointOrderRepository;

    @Autowired
    private RedisService redisCache1;

    /**
     * 分布式锁
     */
    @Resource
    private RedissonDistributionLock redissonDistributionLock;


    public RedisKeyExpirationListener(@Qualifier("redisMessageListenerContainer") RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * redis key失效监听处理
     *
     * @param message
     * @param pattern
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        if (message != null) {
            String expiredKey = message.toString();
            if (!StringUtils.isEmpty(expiredKey) && !expiredKey.contains("null")) {
                String lockKey = "";
                String[] arrString = expiredKey.split("_");
                try {
                    //订单30分钟没支付自动关闭
                    if (expiredKey.startsWith("pointprod:pointorder_setorderkey")) {
                        if (arrString.length == 3) {
                            if (!StringUtils.isEmpty(arrString[2])) {
                                int orderId = Integer.parseInt(arrString[2].replace(",",""));
                                PointOrderDO pointOrderDO = pointOrderRepository.getById(orderId);
                                if (pointOrderDO != null && pointOrderDO.getOrderStatus().equals(Integer.valueOf(PointOrderStatusEnum.UNFINISHED.getCode()))) {
                                    pointOrderDO.setOrderStatus(Integer.valueOf(PointOrderStatusEnum.CANCELLED.getCode()));
                                    pointOrderDO.setUpdateTime(new Date());
                                    int ret = pointOrderRepository.update(pointOrderDO);
                                    if (ret > 0) {
                                        log.info("------------------订单取消成功; orderno = " + pointOrderDO.getOrderNo());
                                    }
                                }
                            }
                        }
                    }
                    //待领取任务在当天24：00没领取自动取消
                    else if (expiredKey.startsWith("pointprod:pointrecord_setpointrecordid")) {
                        if (arrString.length == 4) {
                            if (!StringUtils.isEmpty(arrString[2]) && !StringUtils.isEmpty(arrString[3])) {
                                long uid = Long.parseLong(arrString[2].replace(",", ""));
                                long id = Long.parseLong(arrString[3].replace(",", ""));
                                PointRecordDO pointRecordDO = pointRecordRepository.getById(uid, id);
                                if (pointRecordDO != null && pointRecordDO.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()))) {
                                    //修改积分记录状态
                                    pointRecordDO.setPointStatus(Integer.parseInt(PointRecordStatusEnum.CANCELED.getCode()));
                                    int ret = pointRecordRepository.update(pointRecordDO);
                                    if (ret > 0) {
                                        pointRecordESRepository.save(pointRecordDO);
                                        //去掉积分记录
                                        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETBYUID, pointRecordDO.getUid()));
                                        //去掉积分统计
                                        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUID, pointRecordDO.getUid()));
                                        redisCache1.removePattern(MessageFormat.format("pointprod:pointrecord_getsummarybyuidandcreatetime_{0}_*", pointRecordDO.getUid()));
                                        //去掉待领取任务积分记录
                                        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETUNCLAIMRECORDSBYUID, pointRecordDO.getUid()));
                                        log.info("------------------待领取任务取消成功; 参数 = " + JSON.toJSONString(pointRecordDO));
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("RedisKeyExpirationListener error:" + expiredKey, e);
                } finally {
                    if (!StringUtils.isEmpty(lockKey)) {
                        redissonDistributionLock.unlock(lockKey);
                    }
                }
            }
        }
    }
}
