package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.PointRecordDO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.Date;
import java.util.List;

public interface PointRecordESRepository extends ElasticsearchRepository<PointRecordDO, Long> {
    List<PointRecordDO> findByUid(Long uid);

    List<PointRecordDO> findByUidAndCreateTimeBetween(Long uid, Date from, Date to);

    Page<PointRecordDO> findByTaskIdAndSubId(Long taskId, String subId, Pageable pageable);
}
