package com.emoney.pointweb.service.biz;

import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;

import java.util.List;

public interface PointFeedBackService {
    Integer insert(PointFeedBackDO pointFeedBackDO);
    Integer update(PointFeedBackDO pointFeedBackDO);
    List<PointFeedBackDO> getAll();
}
