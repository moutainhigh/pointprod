package com.emoney.pointweb.repository.dao.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;


@Data
public class PointProductDO implements Serializable {
    /**
     *
     */
    private Integer id;

    /**
     * 1:产品使用期(必须现金+积分兑换) 2:优惠券（纯积分） 3:新功能体验（特权：纯积分、积分+现金） 4:门票兑换 5:实物兑换（暂无）
     */
    private Integer productType;

    /**
     * 0:全部 1:小智盈 2:深度资金版 3:掘金版
     */
    private String productVersion;

    /**
     *
     */
    private String productName;

    private Float productPrice;

    private Integer productDays;

    /**
     *
     */
    private Date activityStartTime;

    /**
     *
     */
    private Date activityEndTime;

    /**
     *
     */
    private String activityCode;

    /**
     *
     */
    private Integer totalLimit;

    /**
     *
     */
    private Integer perLimit;

    /**
     *
     */
    private Date exchangeStarttime;

    /**
     *
     */
    private Date exchangeEndtime;

    /**
     * 0:积分兑换 1:积分+现金兑换
     */
    private Integer exchangeType;

    /**
     *
     */
    private Float exchangePoint;

    /**
     *
     */
    private BigDecimal exchangeCash;

    /**
     *
     */
    private String pcExangeimgurl;

    /**
     *
     */
    private String appExangeimgurl;

    /**
     *
     */
    private String webchatExangeimgurl;

    /**
     *
     */
    private String pcExangeDetailimgurl;

    /**
     *
     */
    private String appExangeDetailimgurl;

    /**
     *
     */
    private String webchatExangeDetailimgurl;

    /**
     *
     */
    private String exchangeRemark;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     *
     */
    private String createBy;

    /**
     *
     */
    private String updateBy;

    private Boolean isValid;

    /**
     *
     */
    private String remark;

    private String userGroup;

    private static final long serialVersionUID = 1L;
}