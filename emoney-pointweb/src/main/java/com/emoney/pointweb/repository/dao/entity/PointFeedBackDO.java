package com.emoney.pointweb.repository.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

/**
 * @author lipengcheng
 * @date 2021-04-12
 */
@Data
public class PointFeedBackDO {
    private Integer id;
    private String pid;
    private String account;
    private String contactInfo;
    private String email;
    private String suggest;
    private String imgUrl;
    private Integer status;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}
