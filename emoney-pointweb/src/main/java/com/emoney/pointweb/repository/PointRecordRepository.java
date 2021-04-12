package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordSummaryDO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PointRecordRepository {
    PointRecordDO getById(Long uid, Long id);

    Integer insert(PointRecordDO pointRecordDO);

    List<PointRecordDO> getByUid(Long uid);

    List<PointRecordDO> getUnClaimRecordsByUid(Long uid);

    Integer update(PointRecordDO pointRecordDO);

    List<PointRecordSummaryDO> getPointRecordSummaryByUid(Long uid);

    List<PointRecordSummaryDO> getPointRecordSummaryByUidAndCreateTime(Long uid, Date dtStart, Date dtEnd);

    List<PointRecordDO> getPointRecordByTaskIds(Long uid, List<Long> taskIds);
}
