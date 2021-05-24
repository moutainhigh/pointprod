package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SendCouponDTO {
    //赠送帐号类型 1：em号；2：手机号；
    private Integer PRESENT_ACCOUNT_TYPE ;
    //赠送帐号密文0x
    private String PRESENT_ACCOUNT ;

    private String PRESENT_FROM_ORDERID ;
    //优惠券活动编号
    private String COUPON_ACTIVITY_ID ;
    //优惠券金额
    private BigDecimal COUPON_RULE_PRICE ;
    //赠送人帐号
    private String PRESENT_PERSON ;
}
