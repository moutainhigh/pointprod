package com.emoney.pointweb.service.biz.jobhandler;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.service.biz.PointOrderService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class AutoSendCouponOrPrivilegeJob {

    @Autowired
    private PointOrderService pointOrderService;

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
            XxlJobLogger.log("UnlockPointRecordJob, Started:" + DateUtil.formatDateTime(new Date()));
            List<PointOrderDO> pointOrderDOS = pointOrderService.getOrdersByStatusAndIsSend();
            if (pointOrderDOS != null && pointOrderDOS.size() > 0) {
                for (PointOrderDO pointOrderDO : pointOrderDOS
                ) {
                    if (pointOrderDO.getProductType().equals(2)) {
                        //优惠券
                    } else if (pointOrderDO.getProductType().equals(3)) {
                        //新功能体验
                    }
                }


            }


            XxlJobLogger.log("UnlockPointRecordJob, end:" + DateUtil.formatDateTime(new Date()));
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("AutoSendCouponOrPrivilegeJob error:", e);
        }
        return ReturnT.FAIL;
    }
}
