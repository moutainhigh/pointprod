package com.emoney.pointweb.service.biz.jobhandler;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class AutoSendCouponOrPrivilegeJob {

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




            XxlJobLogger.log("UnlockPointRecordJob, end:" + DateUtil.formatDateTime(new Date()));
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("AutoSendCouponOrPrivilegeJob error:", e);
        }
        return ReturnT.FAIL;
    }
}
