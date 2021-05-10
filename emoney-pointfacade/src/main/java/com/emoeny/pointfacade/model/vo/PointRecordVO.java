package com.emoeny.pointfacade.model.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class PointRecordVO {
    private Long id;
    private Long uid;
    private Long taskId;
    private Integer platform;
    private Float taskPoint;
    private String taskName;
    private Integer pointStatus;
    private String pid;
    private String emNo;
    private Boolean isDailytask;
    private String subId;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date expirationTime;
    private Float leftPoint;
    private Boolean isValid;
    private Boolean isDirectional;
    private Integer lockDays;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}
