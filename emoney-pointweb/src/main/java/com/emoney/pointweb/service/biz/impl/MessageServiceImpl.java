package com.emoney.pointweb.service.biz.impl;

import com.emoney.pointweb.service.biz.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Value("${swscUrl}")
    private String swscUrl;

    @Override
    public void sendMessage(Long uid, String title, String url) {

    }
}
