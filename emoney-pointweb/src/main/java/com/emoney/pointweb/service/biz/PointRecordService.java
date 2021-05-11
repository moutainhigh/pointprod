package com.emoney.pointweb.service.biz;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.result.userperiod.UserPeriodResult;
import com.emoeny.pointfacade.model.dto.PointRecordCreateDTO;
import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordSummaryDO;

import java.util.Date;
import java.util.List;


public interface PointRecordService {
    Result<Object> createPointRecord(PointRecordCreateDTO pointRecordCreateDTO);

//    List<PointRecordDO> getPointRecordDOs(long uid,  List<Integer> pointStatus,int pageSize,int pageIndex);
//
//    List<PointRecordDO> getPointRecordDOs(long uid,  List<Integer> pointStatus,Date from, Date to,int pageSize,int pageIndex);

    List<PointRecordDO> getByPager(Long uid,Integer pointStatus,Date startDate, Date endDate,int pageIndex, int pageSize);

    Long calPointRecordByTaskId(long taskId, String subId, int page, int size);

    List<PointRecordDO> getUnClaimRecordsByUid(Long uid);

    List<PointRecordSummaryDO> getPointRecordSummaryByUid(Long uid);

    List<PointRecordSummaryDO> getPointRecordSummaryByUidAndCreateTime(Long uid, Date dtStart, Date dtEnd);

    List<PointRecordDO> getPointRecordByTaskIds(Long uid,List<Long> taskIds);

    Integer update(PointRecordDO pointRecordDO);
}
