package com.emoney.pointweb.repository.dao.entity.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author lipengcheng
 * @date 2021-04-28
 */
@Data
public class UserGroupVO {
    private Integer id;
    private String userGroupName;
    private String description;
    private String author;
    private String opType;
    private String tag;
    private String kjTag;
    private Integer isValid;
    private Date createTime;
    private Date updateTime;
}
