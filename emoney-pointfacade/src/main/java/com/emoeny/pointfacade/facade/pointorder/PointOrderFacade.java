package com.emoeny.pointfacade.facade.pointorder;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointOrderCancelDTO;
import com.emoeny.pointfacade.model.dto.PointOrderExchangeDTO;
import com.emoeny.pointfacade.model.dto.PointOrderCreateDTO;
import com.emoeny.pointfacade.model.vo.PointOrderSummaryVO;
import com.emoeny.pointfacade.model.vo.PointOrderVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/18 16:33
 */
@RequestMapping("/api/pointorder")
@Validated
public interface PointOrderFacade {

    /**
     * 根据用户id查询用户订单列表
     */
    @GetMapping("/query")
    Result<List<PointOrderVO>> queryPointOrders(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "查询类型不能为空") Integer queryType, @NotNull(message = "pageIndex不能为空") Integer pageIndex, @NotNull(message = "pageSize不能为空") Integer pageSize);

    /**
     * 创建兑换订单
     * @param pointOrderCreateDTO
     * @return
     */
    @PostMapping("/add")
    Result<Object> createPointOrder(@RequestBody @Valid PointOrderCreateDTO pointOrderCreateDTO);

    /**
     * 查询产品购买统计
     * @param productId
     * @return
     */
    @GetMapping("/queryproductqty")
    Result<List<PointOrderSummaryVO>> getSummaryByProductId(@NotNull(message = "商品id不能为空") Integer productId);

    /**
     * 积分兑换
     * @param pointExchangeDTO
     * @return
     */
    @PostMapping("/exanchangepypoint")
    Result<Object> exanchangeByPoint(@RequestBody @Valid PointOrderExchangeDTO pointExchangeDTO);

    /**
     * 根据订单号查询订单信息
     * @param orderNo
     * @return
     */
    @GetMapping("/querybyorder")
    Result<PointOrderVO> getOrderByOrderNo(@NotNull(message = "订单号不能为空") String orderNo);

    @GetMapping("/querybyuidandproductid")
    Result<List<PointOrderVO>> getByUidAndProductId(@NotNull(message = "用户id不能为空") Long uid, Integer productId);

    @PostMapping("/cancel")
    Result<Object> cancelPointOrder(@RequestBody @Valid PointOrderCancelDTO pointOrderCancelDTO);

    @GetMapping("/paycallback")
    Result<Object> payCallBack(@NotNull(message = "订单号不能为空") String orderID, @NotNull(message = "支付单号不能为空") String tradeOrderID, String paytype);
}
