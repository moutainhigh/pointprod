package com.emoney.pointweb.repository.dao.entity;

import lombok.Data;
import lombok.ToString;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/24 11:20
 */
@Data
@ToString
public class PointOrderSummaryDO {
    private Integer productId;
    private Integer totalQty;
}
