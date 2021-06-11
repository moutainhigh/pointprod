package com.emoney.pointweb.repository;

import com.emoney.pointweb.repository.dao.entity.UserMessageMappingDO;

import java.util.List;

public interface PointUserMessageMappingRepository {
    Integer insert(UserMessageMappingDO userMessageMappingDO);

    List<UserMessageMappingDO> getByuid(String uid);
}
