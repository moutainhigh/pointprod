package com.emoney.pointweb.service.biz.kafka;

import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumerPointMessageUserNameService {
    // 消费监听
    @KafkaListeners({@KafkaListener(topics="pointmessageusernameadd", groupId = "pointrecordgroup"),
            @KafkaListener(topics="pointprod-messageusernameadd", groupId = "pointprodgroup")})
    public void onMessage(@Payload ConsumerRecord<?, ?> record, Acknowledgment acknowledgment) {
        try {
            // 消费的哪个topic、partition的消息,打印出消息内容
            log.info("topic->{},value->{},offset->{},partition->{}", record.topic(), record.value(), record.offset(),record.partition());

            //手工提交offset
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("消息用户名单消费异常", e);
        }
    }
}
