package com.emoney.pointweb.repository.dao.entity.vo;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ActivityInfoVO implements Serializable {
    private String ActivityID;
    private String ActivityName;
    private Date ActivityStartTime;
    private Date ActivityEndTime;
    private String ActivityPrice;
    private List<ActivityInfoChildDetail> children;
}
