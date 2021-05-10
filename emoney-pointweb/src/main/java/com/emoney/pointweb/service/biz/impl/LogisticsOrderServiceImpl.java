package com.emoney.pointweb.service.biz.impl;

import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.result.OrderResultInfo;
import com.emoeny.pointcommon.result.ResultInfo;
import com.emoeny.pointcommon.result.userperiod.UserPeriodResult;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoney.pointweb.repository.dao.entity.dto.QueryCancelLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.dto.QueryStockUpLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryLogisticsOrderVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.service.biz.LogisticsOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;

@Service
@Slf4j
public class LogisticsOrderServiceImpl implements LogisticsOrderService {

    @Value("${insideGatewayUrl}")
    private String insideGatewayUrl;


    @Override
    public List<QueryLogisticsOrderVO> getStockUpLogisticsOrder(QueryStockUpLogisticsOrderDTO queryStockUpLogisticsOrderDTO) {
        String url = MessageFormat.format("{0}/api/logistics/1.0/order.queryorderprodlistbyparams?gate_appid={1}&jsonStr={2}", insideGatewayUrl, "10199", JSON.toJSONString(queryStockUpLogisticsOrderDTO));
        String ret = OkHttpUtil.get(url, null);
        if (!StringUtils.isEmpty(ret)) {
            ResultInfo<String> resultInfo = JSON.parseObject(ret, ResultInfo.class);
            if (resultInfo != null && resultInfo.getRetCode().equals(0)) {
                OrderResultInfo<List<QueryLogisticsOrderVO>> orderResultInfo=JsonUtil.toBean(resultInfo.getMessage(),OrderResultInfo.class);
                return JsonUtil.toBeanList(JSON.toJSONString(orderResultInfo),QueryLogisticsOrderVO.class);
            }
        }
        return null;
    }

    @Override
    public List<QueryLogisticsOrderVO> getCancelLogisticsOrder(QueryCancelLogisticsOrderDTO queryCancelLogisticsOrderDTO) {
        String url = MessageFormat.format("{0}/api/logistics/1.0/order.queryorderprodlistbyparams?gate_appid={1}&jsonStr={2}", insideGatewayUrl, "10199", JSON.toJSONString(queryCancelLogisticsOrderDTO));
        String ret = OkHttpUtil.get(url, null);
        if (!StringUtils.isEmpty(ret)) {
            ResultInfo<String> resultInfo = JSON.parseObject(ret, ResultInfo.class);
            if (resultInfo != null && resultInfo.getRetCode().equals(0)) {
                OrderResultInfo<List<QueryLogisticsOrderVO>> orderResultInfo=JsonUtil.toBean(resultInfo.getMessage(),OrderResultInfo.class);
                return JsonUtil.toBeanList(JSON.toJSONString(orderResultInfo),QueryLogisticsOrderVO.class);
            }
        }
        return null;
    }
}
