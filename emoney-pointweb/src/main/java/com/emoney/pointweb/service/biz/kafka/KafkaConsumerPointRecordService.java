package com.emoney.pointweb.service.biz.kafka;

import com.alibaba.fastjson.JSON;
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
import org.springframework.kafka.annotation.KafkaListeners;
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

    @Value("${logisticsOrderTaskId}")
    private String logisticsOrderTaskId;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;

    // ????????????
    @KafkaListeners({@KafkaListener(topics = "pointrecordadd", groupId = "pointrecordgroup"),
            @KafkaListener(topics = "pointprod-pointadd", groupId = "pointprodgroupprod")})
    public void onMessage(@Payload ConsumerRecord<?, ?> record, Acknowledgment acknowledgment) {
        try {
            // ???????????????topic???partition?????????,?????????????????????
            log.info("topic->{},value->{},offset->{},partition->{}", record.topic(), record.value(), record.offset(), record.partition());

            PointRecordDO pointRecordDO = JsonUtil.toBean(record.value().toString(), PointRecordDO.class);
            if (pointRecordDO != null && pointRecordDO.getUid() != null) {
                //???????????????
                int ret = pointRecordRepository.insert(pointRecordDO);
                if (ret > 0) {
                    //??????ES
                    pointRecordESRepository.save(pointRecordDO);
                    //??????????????????????????????????????????
                    List<PointRecordDO> buyPointRecordDOs = pointRecordRepository.getByUid(pointRecordDO.getUid());
                    if (buyPointRecordDOs != null && buyPointRecordDOs.size() > 0) {
                        //??????????????????
                        if(buyPointRecordDOs.stream().filter(h -> h.getTaskId().equals(Long.parseLong(logisticsOrderTaskId)) && h.getRemark().equals(pointRecordDO.getRemark()) && h.getTaskPoint() < 0).count()>0) {
                            PointRecordDO buyPointRecordDO = buyPointRecordDOs.stream().filter(h -> h.getTaskId().equals(Long.parseLong(logisticsOrderTaskId)) && h.getRemark().equals(pointRecordDO.getRemark()) && h.getTaskPoint() > 0 && h.getLockDays() > 0).findFirst().orElse(null);
                            if (buyPointRecordDO != null) {
                                buyPointRecordDO.setLockDays(0);
                                buyPointRecordDO.setUpdateTime(new Date());
                                pointRecordRepository.update(buyPointRecordDO);
                                log.info("????????????????????????????????????" + JSON.toJSONString(buyPointRecordDO));
                            }
                        }
                    }
                    //???????????????id??????redis????????????0????????????????????????
                    if (pointRecordDO.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()))) {
                        redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_SETPOINTRECORDID, pointRecordDO.getUid(), pointRecordDO.getId()), pointRecordDO, ToolUtils.GetExpireTime(60));
                    }
                    //??????????????????
                    redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUID, pointRecordDO.getUid()));
                    redisCache1.removePattern(MessageFormat.format("pointprod:pointrecord_getsummarybyuidandcreatetime_{0}_*", pointRecordDO.getUid()));
                    //?????????????????????????????????
                    redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETUNCLAIMRECORDSBYUID, pointRecordDO.getUid()));

                    //????????????
                    CompletableFuture.runAsync(() -> {
                        try {
                            //??????????????????????????????
                            List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoRepository.getByTaskIdAndSubId(pointRecordDO.getTaskId(), pointRecordDO.getSubId(), new Date());
                            if (pointTaskConfigInfoDOS != null && pointTaskConfigInfoDOS.size() > 0) {
                                PointTaskConfigInfoDO pointTaskConfigInfoDO = pointTaskConfigInfoDOS.stream().findFirst().orElse(null);
                                if (pointTaskConfigInfoDO != null && pointTaskConfigInfoDO.getTaskType().equals(2)) {
                                    Thread.sleep(1000 * 3);
                                    messageService.sendMessage(pointRecordDO.getUid(), "????????????", pointFrontUrl + "/message/index");
                                }
                            }
                        } catch (Exception e) {
                            log.error("????????????????????????", e);
                        }
                    }, executor);
                }
            }
            //????????????offset
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("????????????????????????", e);
        }
    }
}
