package com.emoney.pointweb.service.biz.kafka;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * producer 同步方式发送数据
     *
     * @param topic   topic名称
     * @param message producer发送的数据
     */
    public void sendMessageSync(String topic, String message) {
        try {
            kafkaTemplate.send(topic, message);
        } catch (Exception e) {
            log.error("sendMessageSync消息发送异常", e);
        }
    }

    /**
     * producer 异步方式发送数据
     *
     * @param topic   topic名称
     * @param message producer发送的数据
     */
    public void sendMessageAsync(String topic, String message) {
        try {
            kafkaTemplate.send(topic, message).addCallback(new ListenableFutureCallback() {
                @Override
                public void onFailure(Throwable throwable) {
                    System.out.println("failure");
                }

                @Override
                public void onSuccess(Object o) {
                    System.out.println("success");

                }
            });
        } catch (Exception e) {
            log.error("sendMessageAsync消息发送异常", e);
        }

    }
}
