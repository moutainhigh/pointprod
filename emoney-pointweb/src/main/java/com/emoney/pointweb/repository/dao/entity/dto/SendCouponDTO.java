package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SendCouponDTO {
    private Integer PRESENT_ACCOUNT_TYPE ;
    private String PRESENT_ACCOUNT ;
    private String PRESENT_FROM_ORDERID ;
    private String COUPON_ACTIVITY_ID ;
    private BigDecimal COUPON_RULE_PRICE ;
    private String PRESENT_PERSON ;
}
