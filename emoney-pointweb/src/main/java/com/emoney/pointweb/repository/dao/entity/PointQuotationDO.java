package com.emoney.pointweb.repository.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PointQuotationDO implements Serializable {
    /**
     * 主键Id
     */
    private Integer id;

    /**
     * 语录内容
     */
    private String content;

    /**
     * 是否有效（1有效；0无效）
     */
    private Boolean isValid;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;
}