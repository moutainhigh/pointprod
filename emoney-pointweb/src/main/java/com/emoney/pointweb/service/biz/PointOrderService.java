package com.emoney.pointweb.service.biz;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointOrderExchangeDTO;
import com.emoeny.pointfacade.model.dto.PointOrderCreateDTO;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderSummaryDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordSummaryDO;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/18 16:09
 */
public interface PointOrderService {

    List<PointOrderDO> getByUid(Long uid,Integer orderStatus,int pageIndex,int pageSize);

    /**
     * 创建订单
     * @param pointOrderCreateDTO
     * @return
     */
    Result<Object> createPointOrder(PointOrderCreateDTO pointOrderCreateDTO);

    /**
     * 积分兑换，积分+现金兑换回调
     * @param pointExchangeDTO
     * @return
     */
    Result<Object> exanchangeByPoint(PointOrderExchangeDTO pointExchangeDTO);

    /**
     * 根据产品id获取已经购买数量
     * @param productId
     * @return
     */
    List<PointOrderSummaryDO> getSummaryByProductId(Integer productId);

    List<PointOrderDO> getAllByOrderStatus(Integer orderStatus);

    PointOrderDO getByOrderNo(String orderNo);

    List<PointOrderDO> getByUidAndProductId(Long uid,Integer productId);

    List<PointOrderDO> getOrdersByStatusAndIsSend();

    Integer update(PointOrderDO pointOrderDO);

    List<PointOrderDO> queryAllByProductType(Integer productType);
}
