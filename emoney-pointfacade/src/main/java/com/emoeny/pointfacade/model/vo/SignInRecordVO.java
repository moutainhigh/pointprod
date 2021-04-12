package com.emoeny.pointfacade.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class SignInRecordVO {
    private Long id;
    private Long uid;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date signInTime;
    private Integer platform;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}
