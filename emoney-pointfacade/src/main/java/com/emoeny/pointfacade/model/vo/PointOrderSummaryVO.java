package com.emoeny.pointfacade.model.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/26 15:42
 */
@Data
@ToString
public class PointOrderSummaryVO {
    private Integer productId;
    private Integer totalQty;
}
