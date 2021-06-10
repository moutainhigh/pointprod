package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface PointAnnounceMapper {

    Integer insert(PointAnnounceDO pointAnnounceDO);

    Integer update(PointAnnounceDO pointAnnounceDO);

    List<PointAnnounceDO> getAll();

    List<PointAnnounceDO> getPointAnnouncesByType(Date curDate, Date endDate,@Param("list") List<Integer> msgTypes);
}