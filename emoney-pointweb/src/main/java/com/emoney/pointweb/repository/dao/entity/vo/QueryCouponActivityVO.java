package com.emoney.pointweb.repository.dao.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class QueryCouponActivityVO {
    private String ID ;
    private String COUPON_ACTIVITY_ID ;
    private String COUPON_ACTIVITY_NAME ;
    private Integer COUPON_COUNT ;
    private Integer COUPON_DAYS ;
    private Integer COUPON_PRESENT_COUNT ;
    private Integer COUPON_USED_COUNT ;
    private Date COUPON_ACTIVITY_START_TIME ;
    private Date COUPON_ACTIVITY_END_TIME ;
    private String COUPON_ACTIVITY_DESCRIPTION ;
    private Integer COUPON_SOLD_OUT_STATE ;
    private Date COUPON_SOLD_OUT_TIME ;
    private Integer COUPON_USE_TYPE ;
    private Integer COUPON_TEMPLATE_ID ;
    private Integer IsCanGetMore ;
    private Integer IsCanUseMore ;
    private Integer UseDateType ;
    private Date UseStartDate ;
    private Date UseEndDate ;
    private Integer UseMoreType ;
    private Integer GetCouponType ;
    private String UserGroupType ;
    private String LAST_UPDATE_USER_NAME ;
    private String CREATE_USER_NAME ;
    private Date CREATE_TIME ;
    private String CouponPlatForm ;
    private Date LAST_UPDATE_TIME ;
    private String COUPON_RULE_NAME ;
    private BigDecimal COUPON_RULE_PRICE ;
    private Integer COUPON_RULE_COUNT ;
}
