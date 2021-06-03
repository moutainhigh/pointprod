package com.emoney.pointweb.service.biz.kafka;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.model.dto.SignInRecordCreateDTO;
import com.emoney.pointweb.repository.*;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;
import com.emoney.pointweb.service.biz.SignInRecordService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component
@Slf4j
public class KafkaConsumerSignInRecordService {

    @Autowired
    private SignInRecordRepository signInRecordRepository;

    @Autowired
    private SingInRecordESRepository singInRecordESRepository;

    // 消费监听
    @KafkaListener(topics = {"signinrecordadd"}, groupId = "pointprodgroup")
    public void onMessage(@Payload ConsumerRecord<?, ?> record, Acknowledgment acknowledgment) {
        try {
            // 消费的哪个topic、partition的消息,打印出消息内容
            log.info("topic->{},value->{},offset->{}", record.topic(), record.value(), record.offset());
            SignInRecordDO signInRecordDO = JsonUtil.toBean(record.value().toString(), SignInRecordDO.class);
            if (signInRecordDO != null && signInRecordDO.getUid() != null) {
                //写入数据库
                int ret = signInRecordRepository.insert(signInRecordDO);
                if (ret > 0) {
                    //写入ES
                    singInRecordESRepository.save(signInRecordDO);
                }
            }
            //手工提交offset
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("signinrecordadd消费异常", e);
        }
    }
}
