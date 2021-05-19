package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

@Data
public class SendMessageDTO {
    private String type;
    private String time;
    private String appid;
    private SendMessageData message;
}
