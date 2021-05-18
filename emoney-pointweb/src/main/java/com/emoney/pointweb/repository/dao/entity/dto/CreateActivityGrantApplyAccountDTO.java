package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

@Data
public class CreateActivityGrantApplyAccountDTO {
    //关联账号类型（1：EM号，2：手机号）
    private Integer AccountType;
    private String Account;
    private String MID;
    private String OrderID;
    private String OrderDetailID;
}
