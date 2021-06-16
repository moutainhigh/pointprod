package com.emoney.pointweb.service.biz.kafka;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoney.pointweb.repository.PointUserMessageMappingRepository;
import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;
import com.emoney.pointweb.repository.dao.entity.UserMessageMappingDO;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;

@Component
@Slf4j
public class KafkaConsumerPointMessageUserNameService {

    @Autowired
    private PointUserMessageMappingRepository pointUserMessageMappingRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisService redisCache1;

    // 消费监听
    @KafkaListener(topics = "pointprod-messageusernameadd", groupId = "pointprodgroupprod")
    public void onMessage(@Payload ConsumerRecord<?, ?> record, Acknowledgment acknowledgment) {
        try {
            // 消费的哪个topic、partition的消息,打印出消息内容
            log.info("topic->{},value->{},offset->{},partition->{}", record.topic(), record.value(), record.offset(), record.partition());
            UserMessageMappingDO userMessageMappingDO = JsonUtil.toBean(record.value().toString(), UserMessageMappingDO.class);
            log.info("step1");
            if (userMessageMappingDO != null && !StringUtils.isEmpty(userMessageMappingDO.getAccount())) {
                //根据用户账号获取uid
                String uid = userInfoService.getUidByEmNo(userMessageMappingDO.getAccount());
                log.info("用户账号解密结果->uid=" + uid);
                if (!StringUtils.isEmpty(uid)) {
                    userMessageMappingDO.setUid(uid);
                    //写入数据库
                    Integer result = pointUserMessageMappingRepository.insert(userMessageMappingDO);
                    if (result > 0) {
                        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointAnnounce_GETBYUID, uid));
                    }


                    log.info("写入数据库结果->result=" + result);
                }
            }
            //手工提交offset
            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("消息用户名单消费异常", e);
        }
    }
}
