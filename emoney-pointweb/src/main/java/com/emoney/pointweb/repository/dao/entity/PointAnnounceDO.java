package com.emoney.pointweb.repository.dao.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * t_point_announce
 * @author 
 */
@Data
public class PointAnnounceDO implements Serializable {
    private Integer id;

    private Integer msgType;

    private String msgContent;

    private String msgSrc;

    private String productVersion;

    private Date publishTime;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

    private String remark;

    private static final long serialVersionUID = 1L;
}