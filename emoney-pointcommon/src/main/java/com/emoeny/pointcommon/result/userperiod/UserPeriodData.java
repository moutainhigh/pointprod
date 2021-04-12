package com.emoeny.pointcommon.result.userperiod;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserPeriodData implements Serializable {
    private  String privilege;
    private  String software;
}
