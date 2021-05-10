package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.dto.QueryCancelLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.dto.QueryStockUpLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryLogisticsOrderVO;

import java.util.List;

public interface LogisticsOrderService {
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
}
