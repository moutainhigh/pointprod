package com.emoney.pointweb.service.biz.jobhandler;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.repository.dao.entity.PointProductDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.dto.CreateActivityGrantApplyAccountDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendCouponDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendPrivilegeDTO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryCouponActivityVO;
import com.emoney.pointweb.service.biz.LogisticsService;
import com.emoney.pointweb.service.biz.PointOrderService;
import com.emoney.pointweb.service.biz.PointProductService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class AutoSendCouponOrPrivilegeJob {

    @Autowired
    private PointOrderService pointOrderService;

    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private PointProductService pointProductService;

    /**
     * 自动赠送特权或者优惠券，10分钟执行一次
     *
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("AutoSendCouponOrPrivilegeJobHandler")
    public ReturnT<String> AutoSendCouponOrPrivilegeJobHandler(String param) throws Exception {
        try {
            XxlJobLogger.log("AutoSendCouponOrPrivilegeJob, Started:" + DateUtil.formatDateTime(new Date()));
            List<PointOrderDO> pointOrderDOS = pointOrderService.getOrdersByStatusAndIsSend();
            if (pointOrderDOS != null && pointOrderDOS.size() > 0) {
                for (PointOrderDO pointOrderDO : pointOrderDOS
                ) {
                    PointProductDO pointProductDO = pointProductService.getById(pointOrderDO.getProductId());
                    if (pointProductDO != null && !StringUtils.isEmpty(pointProductDO.getActivityCode())) {
                        if (pointOrderDO.getProductType().equals(2)) {
                            //优惠券
                            List<QueryCouponActivityVO> queryCouponActivityVOS = logisticsService.getCouponRulesByAcCode(pointProductDO.getActivityCode());
                            if (queryCouponActivityVOS != null && queryCouponActivityVOS.size() > 0) {
                                SendCouponDTO sendCouponDTO = new SendCouponDTO();
                                sendCouponDTO.setPRESENT_ACCOUNT_TYPE(2);
                                sendCouponDTO.setPRESENT_ACCOUNT(pointOrderDO.getMobile());
                                sendCouponDTO.setCOUPON_ACTIVITY_ID(pointProductDO.getActivityCode());
                                sendCouponDTO.setCOUPON_RULE_PRICE(queryCouponActivityVOS.get(0).getCOUPON_RULE_PRICE());
                                sendCouponDTO.setPRESENT_PERSON("积分商城");
                                log.info("开始发放优惠券,参数:" + JSON.toJSONString(sendCouponDTO));
                                Boolean resSendCoupon = logisticsService.SendCoupon(sendCouponDTO);
                                if (resSendCoupon) {
                                    pointOrderDO.setIsSend(true);
                                    pointOrderDO.setUpdateTime(new Date());
                                    pointOrderService.update(pointOrderDO);
                                    log.info("发放优惠券成功,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                                } else {
                                    log.info("发放优惠券失败,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                                }
                            } else {
                                log.warn("获取优惠券异常,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                            }
                        } else if (pointOrderDO.getProductType().equals(3)) {
                            //新功能体验
                            SendPrivilegeDTO sendPrivilegeDTO = new SendPrivilegeDTO();
                            sendPrivilegeDTO.setAppId("A009");
                            sendPrivilegeDTO.setActivityID(pointProductDO.getActivityCode());
                            sendPrivilegeDTO.setApplyUserID("scb_public");
                            List<CreateActivityGrantApplyAccountDTO> createActivityGrantApplyAccountDTOS = new ArrayList<>();
                            CreateActivityGrantApplyAccountDTO createActivityGrantApplyAccountDTO = new CreateActivityGrantApplyAccountDTO();
                            createActivityGrantApplyAccountDTO.setAccountType(2);
                            createActivityGrantApplyAccountDTO.setMID(pointOrderDO.getMobile());
                            createActivityGrantApplyAccountDTOS.add(createActivityGrantApplyAccountDTO);
                            sendPrivilegeDTO.setAccounts(createActivityGrantApplyAccountDTOS);
                            log.info("开始发放特权,参数:" + JSON.toJSONString(sendPrivilegeDTO));
                            Boolean resultSenddPrivilege = logisticsService.SenddPrivilege(sendPrivilegeDTO);
                            if (resultSenddPrivilege) {
                                pointOrderDO.setIsSend(true);
                                pointOrderDO.setUpdateTime(new Date());
                                pointOrderService.update(pointOrderDO);
                                log.info("发放特权成功,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                            } else {
                                log.info("发放特权失败,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                            }
                        }
                    }
                }
            }
            XxlJobLogger.log("AutoSendCouponOrPrivilegeJob, end:" + DateUtil.formatDateTime(new Date()));
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("AutoSendCouponOrPrivilegeJob error:", e);
        }
        return ReturnT.FAIL;
    }
}
