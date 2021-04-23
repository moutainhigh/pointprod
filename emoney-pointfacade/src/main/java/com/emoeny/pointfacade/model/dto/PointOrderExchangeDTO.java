package com.emoeny.pointfacade.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/29 15:42
 */
@Data
@ToString
public class PointOrderExchangeDTO {
    @NotNull(message = "用户id不能为空")
    private Long uid;

    @NotNull(message = "订单号不能为空")
    private String orderNo;

    private String tradeNo;

    private String payType;
}
