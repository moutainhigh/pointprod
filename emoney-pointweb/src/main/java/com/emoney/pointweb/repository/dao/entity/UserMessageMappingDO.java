package com.emoney.pointweb.repository.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lipengcheng
 * @date 2021-06-11
 */
@Data
public class UserMessageMappingDO implements Serializable {
    private Integer id;
    private Integer messageId;
    private String account;
    private String uid;
    private Date createTime;
}
