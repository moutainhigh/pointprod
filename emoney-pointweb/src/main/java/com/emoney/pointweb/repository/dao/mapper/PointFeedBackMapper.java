package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointFeedBackMapper {
    Integer insert(PointFeedBackDO pointFeedBackDO);
    Integer update(PointFeedBackDO pointFeedBackDO);
    List<PointFeedBackDO> getAll();
}
