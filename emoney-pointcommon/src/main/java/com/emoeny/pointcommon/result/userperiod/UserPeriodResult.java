package com.emoeny.pointcommon.result.userperiod;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserPeriodResult implements Serializable {
    private String ver;
    private Integer code;
    private String message;
    private UserPeriodData data;
}
