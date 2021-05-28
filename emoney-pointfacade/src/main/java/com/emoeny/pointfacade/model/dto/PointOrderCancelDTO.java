package com.emoeny.pointfacade.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PointOrderCancelDTO {
    @NotNull(message = "用户id不能为空")
    private Long uid;

    @NotNull(message = "订单号不能为空")
    private String orderNo;
}
