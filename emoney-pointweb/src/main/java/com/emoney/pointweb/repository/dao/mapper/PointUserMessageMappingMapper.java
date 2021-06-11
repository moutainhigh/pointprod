package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.UserMessageMappingDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointUserMessageMappingMapper {
    Integer insert(UserMessageMappingDO userMessageMappingDO);

    List<UserMessageMappingDO> getByuid(String uid);
}
