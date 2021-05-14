package com.emoney.pointweb.service.biz.jobhandler;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointRecordCreateDTO;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.PointSendConfigInfoDO;
import com.emoney.pointweb.repository.dao.entity.dto.QueryCancelLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.dto.QueryStockUpLogisticsOrderDTO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryLogisticsOrderVO;
import com.emoney.pointweb.service.biz.LogisticsOrderService;
import com.emoney.pointweb.service.biz.PointRecordService;
import com.emoney.pointweb.service.biz.PointSendConfigInfoService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class AutoSendRecordToLogisticsOrderJob {

    @Autowired
    private LogisticsOrderService logisticsOrderService;

    @Autowired
    private PointRecordService pointRecordService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private PointSendConfigInfoService pointSendConfigInfoService;

    @Value("${logisticsOrderTaskId}")
    private String logisticsOrderTaskId;

    /**
     * 物流订单自动送积分，每天24；00执行一次
     *
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("AutoSendRecordToLogisticsOrderHandler")
    public ReturnT<String> AutoSendRecordToLogisticsOrderHandler(String param) throws Exception {
        try {
            XxlJobLogger.log("AutoSendRecordToLogisticsOrderJob, Started:" + DateUtil.formatDateTime(new Date()));
            PointRecordCreateDTO pointRecordCreateDTO = null;
            //支付订单
            QueryStockUpLogisticsOrderDTO queryStockUpLogisticsOrderDTO = new QueryStockUpLogisticsOrderDTO();
            queryStockUpLogisticsOrderDTO.setProductID("888010000,888020000,888080000,888040000,888090000");
            queryStockUpLogisticsOrderDTO.setRefund_Sign(1);
            queryStockUpLogisticsOrderDTO.setStockUpDate_Start(DateUtil.format(DateUtil.date(), "yyyy-MM-dd"));
            queryStockUpLogisticsOrderDTO.setStockUpDate_End(DateUtil.format(DateUtil.offsetDay(DateUtil.date(), 1), "yyyy-MM-dd"));
            //queryStockUpLogisticsOrderDTO.setStockUpDate_Start("2021-01-01");
            //queryStockUpLogisticsOrderDTO.setStockUpDate_End("2021-05-01");

            List<QueryLogisticsOrderVO> logisticsStockUpDateOrderVOS = logisticsOrderService.getStockUpLogisticsOrder(queryStockUpLogisticsOrderDTO);
            if (logisticsStockUpDateOrderVOS != null && logisticsStockUpDateOrderVOS.size() > 0) {
                for (QueryLogisticsOrderVO queryStockUp : logisticsStockUpDateOrderVOS
                ) {
                    if (!StringUtils.isEmpty(queryStockUp.getMIDPWD())) {
                        String uid = userInfoService.getUidByEmNo(queryStockUp.getMIDPWD());
                        if (!StringUtils.isEmpty(uid)) {
                            List<PointRecordDO> pointRecordDOS = pointRecordService.getByUid(Long.parseLong(uid));
                            if (pointRecordDOS == null || pointRecordDOS.stream().filter(h -> h.getTaskId().equals(Long.parseLong(logisticsOrderTaskId)) && h.getRemark().equals(queryStockUp.getDetID())).count() == 0) {
                                pointRecordCreateDTO = new PointRecordCreateDTO();
                                pointRecordCreateDTO.setUid(Long.parseLong(uid));
                                pointRecordCreateDTO.setTaskId(Long.parseLong(logisticsOrderTaskId));
                                pointRecordCreateDTO.setPlatform(1);
                                pointRecordCreateDTO.setPid(queryStockUp.getProductID());
                                pointRecordCreateDTO.setLockDays(15);
                                pointRecordCreateDTO.setEmNo(queryStockUp.getEmCard());
                                pointRecordCreateDTO.setRemark(queryStockUp.getDetID());
                                BigDecimal rato = getRate(pointRecordCreateDTO.getPid(), queryStockUp.getProdType());
                                if (rato != null) {
                                    pointRecordCreateDTO.setManualPoint(Float.parseFloat(String.valueOf(Math.round(queryStockUp.getSPRICE().floatValue() * (rato.floatValue() / 100)))));
                                    pointRecordCreateDTO.setTaskName("消费奖励积分+" + pointRecordCreateDTO.getManualPoint() + "积分");
                                    Result<Object> objectResult = pointRecordService.createPointRecord(pointRecordCreateDTO);
                                    log.info("购买赠送积分成功" + JSON.toJSONString(objectResult));
                                } else {
                                    log.warn("购买赠送积分失败,没有配置比例" + JSON.toJSONString(queryStockUp));
                                }
                            }
                        }
                    }
                }
            }
            //退款订单
            QueryCancelLogisticsOrderDTO queryCancelLogisticsOrderDTO = new QueryCancelLogisticsOrderDTO();
            queryCancelLogisticsOrderDTO.setProductID("888010000,888020000,888080000,888040000,888090000");
            queryCancelLogisticsOrderDTO.setRefund_Sign(-1);
            queryCancelLogisticsOrderDTO.setCancel_Time_Start(DateUtil.format(DateUtil.date(), "yyyy-MM-dd"));
            queryCancelLogisticsOrderDTO.setCancel_Time_End(DateUtil.format(DateUtil.offsetDay(DateUtil.date(), 1), "yyyy-MM-dd"));
//            queryCancelLogisticsOrderDTO.setCancel_Time_Start("2021-01-01");
//            queryCancelLogisticsOrderDTO.setCancel_Time_End("2021-05-01");
            List<QueryLogisticsOrderVO> logisticsCancelDateOrderVOS = logisticsOrderService.getCancelLogisticsOrder(queryCancelLogisticsOrderDTO);
            if (logisticsCancelDateOrderVOS != null && logisticsCancelDateOrderVOS.size() > 0) {
                for (QueryLogisticsOrderVO queryCancel : logisticsCancelDateOrderVOS
                ) {
                    if (!StringUtils.isEmpty(queryCancel.getMIDPWD())) {
                        String uid = userInfoService.getUidByEmNo(queryCancel.getMIDPWD());
                        if (!StringUtils.isEmpty(uid)) {
                            List<PointRecordDO> pointRecordDOS = pointRecordService.getByUid(Long.parseLong(uid));
                            if (pointRecordDOS == null || pointRecordDOS.stream().filter(h -> h.getTaskId().equals(Long.parseLong(logisticsOrderTaskId)) && h.getRemark().equals(queryCancel.getDetID())).count() == 0) {
                                pointRecordCreateDTO = new PointRecordCreateDTO();
                                pointRecordCreateDTO.setUid(Long.parseLong(uid));
                                pointRecordCreateDTO.setTaskId(Long.parseLong(logisticsOrderTaskId));
                                pointRecordCreateDTO.setPlatform(1);

                                pointRecordCreateDTO.setPid(queryCancel.getProductID());
                                pointRecordCreateDTO.setLockDays(15);
                                pointRecordCreateDTO.setEmNo(queryCancel.getEmCard());
                                pointRecordCreateDTO.setRemark(queryCancel.getORDER_ID());
                                BigDecimal rato = getRate(pointRecordCreateDTO.getPid(), queryCancel.getProdType());
                                if (rato != null) {
                                    pointRecordCreateDTO.setManualPoint(-Float.parseFloat(String.valueOf(Math.round(queryCancel.getSPRICE().floatValue() * (rato.floatValue() / 100)))));
                                    pointRecordCreateDTO.setTaskName("消费退款扣减积分-" + pointRecordCreateDTO.getManualPoint() + "积分");
                                    Result<Object> objectResult = pointRecordService.createPointRecord(pointRecordCreateDTO);
                                    log.info("退款订单扣积分成功" + JSON.toJSONString(objectResult));
                                } else {
                                    log.warn("退款订单扣积分失败,没有配置比例" + JSON.toJSONString(queryCancel));
                                }
                            }
                        }
                    }
                }
            }
            XxlJobLogger.log("AutoSendRecordToLogisticsOrderJob, end:" + DateUtil.formatDateTime(new Date()));
            return ReturnT.SUCCESS;

        } catch (Exception e) {
            log.error("AutoSendRecordToLogisticsOrderJob error:", e);
        }
        return ReturnT.FAIL;
    }

    private BigDecimal getRate(String pid, String prodType) {
        List<PointSendConfigInfoDO> pointSendConfigInfoDOS = pointSendConfigInfoService.queryAll();
        PointSendConfigInfoDO pointSendConfigInfoDO = pointSendConfigInfoDOS.stream().filter(h -> h.getBuyType().equals((prodType.equals("A23001") || prodType.equals("A23004")) ? 1 : 2) && h.getProductVersion().equals(pid)).findFirst().orElse(null);
        return pointSendConfigInfoDO != null ? pointSendConfigInfoDO.getRatio() : null;
    }
}
