package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PointAnnounceMapper {

    Integer insert(PointAnnounceDO pointAnnounceDO);

    Integer update(PointAnnounceDO pointAnnounceDO);
}