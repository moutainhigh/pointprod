package com.emoeny.pointcommon.result;

import lombok.Data;

@Data
public class ResultInfo<T> {
    private Integer RetCode;
    private String RetMsg;
    private T Message;
}
