package com.emoeny.pointcommon.result;

import lombok.Data;

@Data
public class LogisticsResultInfo<T> {
    private Integer Code;
    private String Msg;
    private T Data;
}
