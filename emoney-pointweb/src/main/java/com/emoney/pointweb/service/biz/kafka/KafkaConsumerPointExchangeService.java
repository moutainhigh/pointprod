package com.emoney.pointweb.service.biz.kafka;

import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.service.biz.PointRecordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/21 20:14
 */
@Component
@Slf4j

public class KafkaConsumerPointExchangeService {

    @Autowired
    private PointRecordService pointRecordService;

    // 消费监听
    @KafkaListener(topics = {"pointrecordexchange"}, groupId = "pointrecordgroup")
    public void onMessage(@Payload ConsumerRecord<?, ?> record, Acknowledgment acknowledgment) {
        try {
            // 消费的哪个topic、partition的消息,打印出消息内容
            log.info("topic->{},value->{},offset->{}", record.topic(), record.value(), record.offset());
            PointRecordDO pointRecordDO = JsonUtil.toBean(record.value().toString(), PointRecordDO.class);
            if (pointRecordDO != null && pointRecordDO.getUid() != null) {
                //本次兑换的积分
                float exchangePoint = Math.abs(pointRecordDO.getTaskPoint());
                //当前获得的未使用积分
                List<PointRecordDO> pointRecordDOS = pointRecordService.getByUidAndCreateTime(pointRecordDO.getUid(), pointRecordDO.getCreateTime());

                if (pointRecordDOS != null && pointRecordDOS.size() > 0) {

                }


            }
        } catch (Exception e) {
            log.error("pointrecordexchange 兑换异常", e);
        }
    }
}
