package com.emoney.pointweb.service.biz.jobhandler;

import cn.hutool.core.date.DateUtil;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class AutoSendRecordToLogisticsOrderJob {

    /**
     * 物流订单自动送积分，每天24；00执行一次
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("AutoSendRecordToLogisticsOrderHandler")
    public ReturnT<String> AutoSendRecordToLogisticsOrderHandler(String param) throws Exception {
        try {
            XxlJobLogger.log("AutoSendRecordToLogisticsOrderJob, Started:" + DateUtil.formatDateTime(new Date()));



            XxlJobLogger.log("AutoSendRecordToLogisticsOrderJob, end:" + DateUtil.formatDateTime(new Date()));
            return ReturnT.SUCCESS;

        }catch (Exception e) {
            log.error("AutoSendRecordToLogisticsOrderJob error:", e);
        }
        return ReturnT.FAIL;
    }
}
