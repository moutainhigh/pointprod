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
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

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
    private RedisService redisCache1;


    // 消费监听
    @KafkaListener(topics = {"pointrecordadd"}, groupId = "pointrecordgroup")
    public void onMessage(@Payload ConsumerRecord<?, ?> record, Acknowledgment acknowledgment) {
        try {
            // 消费的哪个topic、partition的消息,打印出消息内容
            log.info("topic->{},value->{},offset->{}", record.topic(), record.value(), record.offset());
            PointRecordDO pointRecordDO = JsonUtil.toBean(record.value().toString(), PointRecordDO.class);
            if (pointRecordDO.getTaskId() != null) {
                //写入数据库
                int ret = pointRecordRepository.insert(pointRecordDO);
                if (ret > 0) {
                    //将成长任务id写入redis，如果在0点未领取，则清掉
                    if (pointRecordDO.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.UNCLAIMED.getCode()))){
                        redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_SETPOINTRECORDID, pointRecordDO.getUid(),pointRecordDO.getId()),pointRecordDO, ToolUtils.GetExpireTime(1));
                    }
                    //写入ES
                    pointRecordESRepository.save(pointRecordDO);
                }
            }
            //手工提交offset
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("消费异常", e);
        }
    }
}
