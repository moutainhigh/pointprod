package com.emoney.pointweb.repository.dao.entity;

import lombok.Data;

import java.util.Date;

@Data
public class PointSendRecordDO {
    private Integer id;
    private long taskId;
    private String batchId;
    private long uid;
    private String emNo;
    private Integer sendStatus;
    private String sendResult;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}
