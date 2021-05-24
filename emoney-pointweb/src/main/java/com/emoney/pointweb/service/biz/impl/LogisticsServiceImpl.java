package com.emoney.pointweb.service.biz.impl;

import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.result.LogisticsResultInfo;
import com.emoeny.pointcommon.result.ResultInfo;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoney.pointweb.repository.dao.entity.dto.QueryCancelLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.dto.QueryStockUpLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendCouponDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendPrivilegeDTO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryCouponActivityVO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryLogisticsOrderVO;
import com.emoney.pointweb.service.biz.LogisticsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.List;

@Service
@Slf4j
public class LogisticsServiceImpl implements LogisticsService {

    @Value("${insideGatewayUrl}")
    private String insideGatewayUrl;


    @Override
    public List<QueryLogisticsOrderVO> getStockUpLogisticsOrder(QueryStockUpLogisticsOrderDTO queryStockUpLogisticsOrderDTO) {
        String url = MessageFormat.format("{0}/api/logistics/1.0/order.queryorderprodlistbyparams?gate_appid={1}&jsonStr={2}", insideGatewayUrl, "10199", JSON.toJSONString(queryStockUpLogisticsOrderDTO));
        String ret = OkHttpUtil.get(url, null);
        if (!StringUtils.isEmpty(ret)) {
            ResultInfo<String> resultInfo = JSON.parseObject(ret, ResultInfo.class);
            if (resultInfo != null && resultInfo.getRetCode().equals(0)) {
                LogisticsResultInfo<List<QueryLogisticsOrderVO>> logisticsResultInfo = JsonUtil.toBean(resultInfo.getMessage(), LogisticsResultInfo.class);
                return JsonUtil.toBeanList(JSON.toJSONString(logisticsResultInfo.getData()), QueryLogisticsOrderVO.class);
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
                LogisticsResultInfo<List<QueryLogisticsOrderVO>> logisticsResultInfo = JsonUtil.toBean(resultInfo.getMessage(), LogisticsResultInfo.class);
                return JsonUtil.toBeanList(JSON.toJSONString(logisticsResultInfo.getData()), QueryLogisticsOrderVO.class);
            }
        }
        return null;
    }

    @Override
    public List<QueryCouponActivityVO> getCouponRulesByAcCode(String couponActivityID) {
        String url = MessageFormat.format("{0}/api/logistics/1.0/coupon.getcouponrulesbycouponaccode?gate_appid={1}&CouponActivityID={2}", insideGatewayUrl, "10199", couponActivityID);
        String ret = OkHttpUtil.get(url, null);
        if (!StringUtils.isEmpty(ret)) {
            ResultInfo<String> resultInfo = JSON.parseObject(ret, ResultInfo.class);
            if (resultInfo != null && resultInfo.getRetCode().equals(0)) {
                LogisticsResultInfo<List<QueryCouponActivityVO>> logisticsResultInfo = JsonUtil.toBean(resultInfo.getMessage(), LogisticsResultInfo.class);
                return JsonUtil.toBeanList(JSON.toJSONString(logisticsResultInfo.getData()), QueryCouponActivityVO.class);
            }
        }
        return null;
    }

    @Override
    public Boolean SendCoupon(SendCouponDTO sendCouponDTO) {
        String url = MessageFormat.format("{0}/api/logistics/1.0/coupon.sendcoupon?gate_appid={1}", insideGatewayUrl, "10199");
        String ret = OkHttpUtil.postJsonParams(url, JSON.toJSONString(sendCouponDTO));
        if (!StringUtils.isEmpty(ret)) {
            ResultInfo<String> resultInfo = JSON.parseObject(ret, ResultInfo.class);
            if (resultInfo != null && resultInfo.getRetCode().equals(0)) {
                LogisticsResultInfo<String> logisticsResultInfo = JsonUtil.toBean(resultInfo.getMessage(), LogisticsResultInfo.class);
                return (logisticsResultInfo != null && logisticsResultInfo.getCode().equals(0)) ? true : false;
            }
        }
        return false;
    }

    @Override
    public Boolean SenddPrivilege(SendPrivilegeDTO sendPrivilegeDTO) {
        String url = MessageFormat.format("{0}/api/logistics/1.0/privilege.sendprivilege?gate_appid={1}", insideGatewayUrl, "10199");
        String ret = OkHttpUtil.postJsonParams(url, JSON.toJSONString(sendPrivilegeDTO));
        if (!StringUtils.isEmpty(ret)) {
            ResultInfo<String> resultInfo = JSON.parseObject(ret, ResultInfo.class);
            if (resultInfo != null && resultInfo.getRetCode().equals(0)) {
                LogisticsResultInfo<String> logisticsResultInfo = JsonUtil.toBean(resultInfo.getMessage(), LogisticsResultInfo.class);
                return (logisticsResultInfo != null && logisticsResultInfo.getCode().equals(0)) ? true : false;
            }
        }
        return false;
    }
}
