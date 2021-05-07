package com.emoney.pointweb.repository.dao.mapper;

import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PointQuestionMapper {
    Integer insert(PointQuestionDO pointQuestionDO);

    Integer update(PointQuestionDO pointQuestionDO);

    List<PointQuestionDO> queryAll();
}
