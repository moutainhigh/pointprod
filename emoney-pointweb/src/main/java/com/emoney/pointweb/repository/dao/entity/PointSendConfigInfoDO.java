package com.emoney.pointweb.repository.dao.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author lipengcheng
 * @date 2021-05-13
 */
@Data
public class PointSendConfigInfoDO implements Serializable {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 订单版本（888010000,888020000,888080000,888040000,888090000）
     */
    private String productVersion;

    /**
     * 购买类型（1新增 ；2再购）
     */
    private Integer buyType;

    /**
     * 比例
     */
    private BigDecimal ratio;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否有效（<>1无效）
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
     * 更新时间
     */
    private Date updateTime;
}
