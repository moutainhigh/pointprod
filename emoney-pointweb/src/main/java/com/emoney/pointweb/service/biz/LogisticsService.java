package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.dto.QueryCancelLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.dto.QueryStockUpLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendCouponDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendPrivilegeDTO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryCouponActivityVO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryLogisticsOrderVO;

import java.util.List;

public interface LogisticsService {
    /**
     * 获取支付订单
     * @param queryStockUpLogisticsOrderDTO
     * @return
     */
    List<QueryLogisticsOrderVO> getStockUpLogisticsOrder(QueryStockUpLogisticsOrderDTO queryStockUpLogisticsOrderDTO);

    /**
     * 获取退款订单
     * @param queryCancelLogisticsOrderDTO
     * @return
     */
    List<QueryLogisticsOrderVO> getCancelLogisticsOrder(QueryCancelLogisticsOrderDTO queryCancelLogisticsOrderDTO);

    /**
     * 根据优惠券id获取优惠券详情
     * @param couponActivityID
     * @return
     */
    List<QueryCouponActivityVO> getCouponRulesByAcCode(String couponActivityID);

    /**
     * 发送优惠券
     * @param sendCouponDTO
     * @return
     */
    Boolean SendCoupon(SendCouponDTO sendCouponDTO);

    /**
     * 发放特权
     * @param sendCouponDTO
     * @return
     */
    Boolean SenddPrivilege(SendPrivilegeDTO sendPrivilegeDTO);
}
