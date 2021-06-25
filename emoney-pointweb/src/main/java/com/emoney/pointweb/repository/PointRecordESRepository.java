package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Date;
import java.util.List;

public interface PointRecordESRepository extends ElasticsearchRepository<PointRecordDO, Long> {
    List<PointRecordDO> findByUid(Long uid);

    Page<PointRecordDO> findByUidAndCreateTimeBetweenOrderByCreateTimeDesc(Long uid, List<Integer> pointStatus, Date from, Date to, Pageable pageable);

    Page<PointRecordDO> findByUidAndPointStatusInOrderByCreateTimeDesc(Long uid, List<Integer> pointStatus, Pageable pageable);

    Page<PointRecordDO> findByTaskIdAndSubId(Long taskId, String subId, Pageable pageable);

    List<PointRecordDO> findByLockDaysIsGreaterThanAndIsValidAndCreateTimeIsBefore(Integer lockDays, Boolean isValid, Date createTime);
}
