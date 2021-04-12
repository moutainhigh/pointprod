package com.emoney.pointweb.repository;


import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SingInRecordESRepository extends ElasticsearchRepository<SignInRecordDO, Long> {
}
