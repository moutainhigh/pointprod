package com.emoney.pointweb.repository.dao.entity.dto;

import lombok.Data;

/**
 * 查询购买订单入参
 */
@Data
public class QueryStockUpOrderDTO {
    private String StockUpDate_Start;
    private String StockUpDate_End;
    private Integer Refund_Sign;
    private String ProductID;
}
