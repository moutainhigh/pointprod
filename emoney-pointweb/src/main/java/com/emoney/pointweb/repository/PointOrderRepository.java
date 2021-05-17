package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderSummaryDO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/18 16:05
 */
public interface PointOrderRepository {

    List<PointOrderDO> getByUid(Long uid,Integer orderStatus,int pageIndex,int pageSize);

    /**
     * 根据uid和productId获取所有订单
     * @param uid
     * @return
     */
    List<PointOrderDO> getByUidAndProductId(Long uid,Integer productId);

    /**
     * 根据产品id获取已经购买数量
     * @param productId
     * @return
     */
    List<PointOrderSummaryDO> getSummaryByProductId(Integer productId);
    /**
     * 根据Id获取订单详情
     * @param id
     * @return
     */
    PointOrderDO getById(Integer id);
    /**
     * 根据订单号获取订单详情
     * @param orderNo
     * @return
     */
    PointOrderDO getByOrderNo(String orderNo);

    /**
     * 创建订单
     * @param pointOrderDO
     * @return
     */
    Integer insert(PointOrderDO pointOrderDO);

    /**
     * 修改订单
     * @param pointOrderDO
     * @return
     */
    Integer update(PointOrderDO pointOrderDO);

    List<PointOrderDO> getAllByOrderStatus(Integer orderStatus);

    /**
     * 查询优惠券和新功能体验的已支付订单
     * @return
     */
    List<PointOrderDO> getOrdersByStatusAndIsSend();
}
