package com.emoeny.pointcommon.result;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ApiResult<T> implements Serializable {
    private Integer RetCode;
    private String RetMsg;
    private String LastLoadApisTime;
    private Integer IntervalTime;
    public T Message;
}
