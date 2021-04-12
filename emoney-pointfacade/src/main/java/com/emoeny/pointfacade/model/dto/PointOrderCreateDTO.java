package com.emoeny.pointfacade.model.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/23 10:38
 */
@Data
@ToString
public class PointOrderCreateDTO {

    @NotNull(message = "用户id不能为空")
    private Long uid;

    @NotNull(message = "商品id不能为空")
    private Integer productId;

    @NotNull(message = "商品数量不能为空")
    private Integer productQty;

    @NotNull(message = "emNo不能为空")
    private String emNo;
}
