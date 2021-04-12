package com.emoney.pointweb.repository.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@ToString
public class PointLimitDO {
    private Integer id;
    private Integer pointLimittype;
    private Integer pointListto;
    private Float pointLimitvalue;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}