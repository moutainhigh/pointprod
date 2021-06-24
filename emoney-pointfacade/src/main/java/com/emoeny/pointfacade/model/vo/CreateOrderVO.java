package com.emoeny.pointfacade.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderVO {
    private String ChannelCode;
    private String AgentID;
    private String OrderID;
    private String MID;
    private String QQ;
    private String CustomerName;
    private String CustomerAccount;
    private String Province;
    private String City;
    private String Area;
    private String Address;
    private String IsNeedInvoice;
    private String InvoiceType;
    private String InvioceGroup;
    private String BillHead;
    private String IsEInvoice;
    private String ActionType;
    private String CreateDate;
    private String TsrCode;
    private String Lecturer;
    private List<PayInfoVO> PayList;
    private List<IntegralVO> IntegralList;
    private List<ProductVO> ProdList;
}


