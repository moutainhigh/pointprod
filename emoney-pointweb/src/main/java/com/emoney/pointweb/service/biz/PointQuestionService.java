package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;

import java.util.List;

public interface PointQuestionService {
    List<PointQuestionDO> getAll();
    Integer insert(PointQuestionDO pointQuestionDO);
    Integer update(PointQuestionDO pointQuestionDO);
}
