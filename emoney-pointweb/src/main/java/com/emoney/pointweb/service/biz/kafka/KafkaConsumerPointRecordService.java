package com.emoney.pointweb.service.biz.kafka;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.PointRecordStatusEnum;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoeny.pointfacade.model.vo.PointRecordSummaryVO;
import com.emoeny.pointfacade.model.vo.PointRecordVO;
import com.emoney.pointweb.repository.PointRecordESRepository;
import com.emoney.pointweb.repository.PointRecordRepository;
import com.emoney.pointweb.repository.PointTaskConfigInfoRepository;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordSummaryDO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.service.biz.MessageService;
import com.emoney.pointweb.service.biz.impl.PointRecordServiceImpl;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class KafkaConsumerPointRecordService {

    @Autowired
    private PointTaskConfigInfoRepository pointTaskConfigInfoRepository;

    @Autowired
    private PointRecordRepository pointRecordRepository;

    @Autowired
    private PointRecordESRepository pointRecordESRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private RedisService redisCache1;

    @Value("${pointfront.url}")
    private String pointFrontUrl;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;

    // 消费监听
    @KafkaListener(topics = {"pointrecordadd"})
    public void onMessage(@Payload ConsumerRecord<?, ?> record, Acknowledgment acknowledgment) {
        try {
            // 消费的哪个topic、partition的消息,打印出消息内容
            log.info("topic->{},value->{},offset->{}", record.topic(), record.value(), record.offset());
            log.info("积分弹窗URL2" + pointFrontUrl);
            PointRecordDO pointRecordDO = JsonUtil.toBean(record.value().toString(), PointRecordDO.class);
            if (pointRecordDO != null && pointRecordDO.getUid() != null) {
                //写入数据库
                int ret = pointRecordRepository.insert(pointRecordDO);
                if (ret > 0) {
                    //将成长任务id写入redis，如果在0点未领取，则清掉
                    if (pointRecordDO.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()))) {
                        redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_SETPOINTRECORDID, pointRecordDO.getUid(), pointRecordDO.getId()), pointRecordDO, ToolUtils.GetExpireTime(60));
                    }
                    //去掉积分统计
                    redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUID, pointRecordDO.getUid()));
                    //去掉我的待领取任务统计
                    redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETUNCLAIMRECORDSBYUID, pointRecordDO.getUid()));
                    //写入ES
                    pointRecordESRepository.save(pointRecordDO);
                    //异步处理
                    CompletableFuture.runAsync(() -> {
                        try {
                            //成长任务发送积分通知
                            List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoRepository.getByTaskIdAndSubId(pointRecordDO.getTaskId(), pointRecordDO.getSubId());
                            if (pointTaskConfigInfoDOS != null && pointTaskConfigInfoDOS.size() > 0) {
                                PointTaskConfigInfoDO pointTaskConfigInfoDO = pointTaskConfigInfoDOS.stream().findFirst().orElse(null);
                                if (pointTaskConfigInfoDO != null && pointTaskConfigInfoDO.getTaskType().equals(2)) {
                                    Thread.sleep(1000 * 3);
                                    messageService.sendMessage(pointRecordDO.getUid(), "积分弹窗", pointFrontUrl + "/message/index");
                                }
                            }
                        } catch (Exception e) {
                            log.error("调用积分弹窗异常", e);
                        }
                    }, executor);
                }
            }
            //手工提交offset
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("pointrecordadd 消费异常", e);
        }
    }
}
