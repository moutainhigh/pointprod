package com.emoney.pointweb.repository.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class PointRecordSummaryDO {
    private Long uid;
    private Integer pointStatus;
    private Float pointTotal;
}