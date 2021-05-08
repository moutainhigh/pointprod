package com.emoney.pointweb.repository.dao.entity.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private String AccountName;
    private String CustomerID;
    private Integer AccountType;
    private String EncryptMobile;
}
