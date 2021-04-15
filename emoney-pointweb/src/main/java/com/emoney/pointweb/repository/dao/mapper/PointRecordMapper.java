package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import com.emoney.pointweb.repository.dao.entity.PointRecordSummaryDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface PointRecordMapper {
    PointRecordDO getById(Long uid, Long id);

    Integer insert(PointRecordDO pointRecordDO);

    List<PointRecordDO> getByPager(Long uid,Integer pointStatus,Date startDate, Date endDate);

    List<PointRecordDO> getByUid1(Long uid);

    List<PointRecordDO> getByUid2(Long uid, Date startDate, Date endDate);

    List<PointRecordDO> getUnClaimRecordsByUid(Long uid);

    Integer update(PointRecordDO pointRecordDO);

    List<PointRecordSummaryDO> getPointRecordSummaryByUid(Long uid);

    List<PointRecordSummaryDO> getPointRecordSummaryByUidAndCreateTime(Long uid, Date dtStart, Date dtEnd);

    List<PointRecordDO> getPointRecordByTaskIds(Long uid, @Param("list") List<Long> taskIds);
}
