package com.emoney.pointweb.repository.dao.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QueryLogisticsOrderVO {
    //订单号码
    private String ORDER_ID;
    //客户名称
    private String CUSTOMER_NAME;
    //密文电话号码
    private String MIDPWD;
    //加*电话号码
    private String MID;
    //EM账号
    private String EmCard;
    //录单时间
    private String ORDADD_TIME;
    //备货日期
    private String StockUpDate;
    //产品线ID
    private String ProdLineid;
    //产品线名称
    private String ProdLIneName;
    //可销售单元ID
    private String PRODID;
    //产品名称
    private String PRODNAME;
    //产品ID
    private String ProductID;
    //可销售单元类型（A23001：软件；A23002：续费充值；A23004：产品换购；）
    private String ProdType;
    //活动代码
    private String ACTIVITY_CODE;
    //活动名称
    private String ACTIVITY_NAME;
    //产品购买金额
    private BigDecimal SPRICE;
    //卡号
    private String OLDPROD;
    //退货状态
    private Integer Refund_Sign;
    //退款时间
    private String Cancel_Time;
    //产品实际退款金额
    private BigDecimal RealBackPrice;
    //订单明细ID
    private String DetID;
}
