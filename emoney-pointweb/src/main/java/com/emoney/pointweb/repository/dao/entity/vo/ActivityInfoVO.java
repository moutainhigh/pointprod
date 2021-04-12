package com.emoney.pointweb.repository.dao.entity.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ActivityInfoVO {
    private String ActivityID;
    private String ActivityName;
    private Date ActivityStartTime;
    private Date ActivityEndTime;
    private String ActivityPrice;
}
