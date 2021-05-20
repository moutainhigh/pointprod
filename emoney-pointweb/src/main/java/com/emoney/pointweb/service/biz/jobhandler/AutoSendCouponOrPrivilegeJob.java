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
                                QueryCouponActivityVO queryCouponActivityVO = queryCouponActivityVOS.stream().findFirst().orElse(null);
                                if (queryCouponActivityVO != null) {
                                    SendCouponDTO sendCouponDTO = new SendCouponDTO();
                                    sendCouponDTO.setPRESENT_ACCOUNT_TYPE(2);
                                    sendCouponDTO.setPRESENT_ACCOUNT(pointOrderDO.getMobile());
                                    sendCouponDTO.setPRESENT_FROM_ORDERID(pointProductDO.getActivityCode());
                                    sendCouponDTO.setCOUPON_RULE_PRICE(queryCouponActivityVO.getCOUPON_RULE_PRICE());
                                    sendCouponDTO.setPRESENT_PERSON("积分商城");
                                    Boolean ret = logisticsService.SendCoupon(sendCouponDTO);
                                    if (ret) {
                                        log.info("积分赠送优惠券成功,参数:" + JSON.toJSONString(sendCouponDTO));
                                        pointOrderDO.setIsSend(true);
                                        pointOrderDO.setUpdateTime(new Date());
                                        pointOrderService.update(pointOrderDO);
                                    } else {
                                        log.info("积分赠送优惠券失败,参数:" + JSON.toJSONString(sendCouponDTO));
                                    }
                                }
                            }
                        } else if (pointOrderDO.getProductType().equals(3)) {
                            //新功能体验
                            SendPrivilegeDTO sendPrivilegeDTO=new SendPrivilegeDTO();
                            List<CreateActivityGrantApplyAccountDTO> activityGrantApplyAccountDTOS=new ArrayList<>();
                            sendPrivilegeDTO.setAppId("a002");
                            sendPrivilegeDTO.setReason("");
                            sendPrivilegeDTO.setCostBearDeptCode("");
                            sendPrivilegeDTO.setApplyUserID("");
                            CreateActivityGrantApplyAccountDTO createActivityGrantApplyAccountDTO=new CreateActivityGrantApplyAccountDTO();
                            createActivityGrantApplyAccountDTO.setAccountType(2);
                            createActivityGrantApplyAccountDTO.setMID(pointOrderDO.getMobile());
                            activityGrantApplyAccountDTOS.add(createActivityGrantApplyAccountDTO);
                            Boolean ret=logisticsService.SenddPrivilege(sendPrivilegeDTO);
                            if (ret) {
                                log.info("积分赠送特权成功,参数:" + JSON.toJSONString(sendPrivilegeDTO));
                                pointOrderDO.setIsSend(true);
                                pointOrderDO.setUpdateTime(new Date());
                                pointOrderService.update(pointOrderDO);
                            } else {
                                log.info("积分赠送特权失败,参数:" + JSON.toJSONString(sendPrivilegeDTO));
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
