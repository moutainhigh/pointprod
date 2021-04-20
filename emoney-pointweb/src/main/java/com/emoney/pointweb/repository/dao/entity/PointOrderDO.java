package com.emoney.pointweb.repository.dao.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/18 14:34
 */

@Data
@ToString
public class PointOrderDO {
    private Integer id;
    private Long uid;
    private String emNo;
    private Long orderNo;
    private String tradeNo;
    private Integer orderStatus;
    private Integer productId;
    private String productTitle;
    private Integer productQty;
    private Float point;
    private BigDecimal cash;
    private String payType;
    private String mobile;
    private String mobileMask;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private String remark;
}
