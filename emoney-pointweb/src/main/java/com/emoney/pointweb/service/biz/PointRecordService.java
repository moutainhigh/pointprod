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

    List<PointRecordDO> getPointRecordDOs(long uid, Date dtStart,Date dtTo);

    Long calPointRecordByTaskId(long taskId, String subId, int page, int size);

    List<PointRecordDO> getUnClaimRecordsByUid(Long uid);

    List<PointRecordSummaryDO> getPointRecordSummaryByUid(Long uid);

    List<PointRecordSummaryDO> getPointRecordSummaryByUidAndCreateTime(Long uid, Date dtStart, Date dtEnd);

    List<PointRecordDO> getPointRecordByTaskIds(Long uid,List<Long> taskIds);
}
