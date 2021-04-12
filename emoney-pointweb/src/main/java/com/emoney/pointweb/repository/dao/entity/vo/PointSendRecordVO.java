package com.emoney.pointweb.repository.dao.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class PointSendRecordVO {
    private Integer id;
    private long taskId;
    private String taskName;
    private float taskPoint;
    private Integer successCount;
    private Integer errorCount;
    private Integer sendStatus;
    private String sendResult;
    private String batchId;
    private long uid;
    private String emNo;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}
