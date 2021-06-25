package com.emoney.pointweb.service.biz.jobhandler;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import com.alibaba.fastjson.JSON;
import com.emoney.pointweb.repository.PointRecordESRepository;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.service.biz.PointRecordService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import com.xxl.job.core.log.XxlJobLogger;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class UnlockPointRecordJob {

    @Autowired
    private PointRecordService pointRecordService;

    @Autowired
    private PointRecordESRepository pointRecordESRepository;

    /**
     * 自动解锁被锁定的积分记录，每5分钟执行一次
     *
     * @param param
     * @return
     * @throws Exception
     */
    @XxlJob("UnlockPointRecordJobHandler")
    public ReturnT<String> UnlockPointRecordJobHandler(String param) throws Exception {
        try {
            XxlJobLogger.log("UnlockPointRecordJob, Started:" + DateUtil.formatDateTime(new Date()));
            //锁定的记录
            List<PointRecordDO> pointRecordDOS = pointRecordESRepository.findByLockDaysIsGreaterThanAndIsValidAndCreateTimeIsBefore(0, true, DateUtil.offsetDay(DateUtil.date(), -30));
            if (pointRecordDOS != null && pointRecordDOS.size() > 0) {
                log.info("UnlockPointRecordJob Records" + JSON.toJSONString(pointRecordDOS));
                for (PointRecordDO pointRecordDO : pointRecordDOS
                ) {
                    if (DateUtil.offsetDay(DateUtil.date(), -pointRecordDO.getLockDays()).after(pointRecordDO.getCreateTime())) {
                        pointRecordDO.setLockDays(0);
                        pointRecordDO.setUpdateTime(new Date());
                        int ret = pointRecordService.update(pointRecordDO);
                        if (ret > 0) {
                            pointRecordESRepository.save(pointRecordDO);
                            log.info("解锁成功，参数:" + JSON.toJSONString(pointRecordDO));
                        }
                    }
                }
            }
            XxlJobLogger.log("UnlockPointRecordJob, end:" + DateUtil.formatDateTime(new Date()));
            return ReturnT.SUCCESS;
        } catch (Exception e) {
            log.error("UnlockPointRecordJob error:", e);
        }
        return ReturnT.FAIL;
    }
}

