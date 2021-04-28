package com.emoeny.pointcommon.result;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

/**
 * @author lipengcheng
 * @date 2021-04-28
 */
@Data
public class ReturnInfo<T> {
    private String retCode;
    private String retMsg;
    private T data;
}
