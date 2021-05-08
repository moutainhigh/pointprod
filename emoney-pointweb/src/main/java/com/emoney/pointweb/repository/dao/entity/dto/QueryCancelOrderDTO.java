package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

/**
 * 查询退款订单入参
 */
@Data
public class QueryCancelOrderDTO {
    private String Cancel_Time_Start;
    private String Cancel_Time_End;
    private Integer Refund_Sign;
    private String ProductID;
}
