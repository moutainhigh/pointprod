package com.emoney.pointweb.repository.dao.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author lipengcheng
 * @date 2021-05-31
 */
@Data
public class PointQuotationVO {
    private Integer id;

    private String content;

    private Date showTime;

    private String publishPlatFormType;

    private String productVersion;

    private String userGroup;

    private String userGroupName;

    private Boolean isValid;

    private String createBy;

    private String updateBy;

    private Date createTime;

    private Date updateTime;
}
