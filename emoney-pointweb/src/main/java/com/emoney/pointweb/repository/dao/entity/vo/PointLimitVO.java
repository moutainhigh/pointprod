package com.emoney.pointweb.repository.dao.entity.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class PointLimitVO {
    private Integer id;
    private String pointLimittype;
    private String pointListto;
    private Float pointLimitvalue;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}
