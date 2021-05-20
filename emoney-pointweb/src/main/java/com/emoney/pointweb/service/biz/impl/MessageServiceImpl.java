package com.emoney.pointweb.service.biz.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoney.pointweb.repository.dao.entity.dto.SendMessageDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendMessageData;
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.service.biz.MessageService;
import com.emoney.pointweb.service.biz.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private UserInfoService userInfoService;

    @Value("${swscUrl}")
    private String swscUrl;

    @Override
    public String sendMessage(Long uid, String title, String url) {
        try {
            String pid = userInfoService.getPidByUserId(uid);
            if (!StringUtils.isEmpty(pid)) {
                SendMessageDTO sendMessageDTO = new SendMessageDTO();
                sendMessageDTO.setType("notify");
                sendMessageDTO.setTime(DateUtil.formatDateTime(DateUtil.date()));
                sendMessageDTO.setAppid("10199");
                SendMessageData sendMessageData = new SendMessageData();
                sendMessageData.setTitle(title);
                sendMessageData.setUrl(url);
                sendMessageDTO.setMessage(sendMessageData);
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("uid", String.valueOf(uid));
                stringMap.put("group", "softonline_" + pid);
                stringMap.put("message", URLUtil.encode(JSON.toJSONString(sendMessageDTO)));
                String res = OkHttpUtil.get(swscUrl + "/pushmessage", stringMap);
                return res;
            }
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
        return null;
    }
}
