package com.emoney.pointweb.service.biz.impl;

import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import com.emoney.pointweb.repository.dao.mapper.PointFeedBackMapper;
import com.emoney.pointweb.service.biz.PointFeedBackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lipengcheng
 * @date 2021-04-15
 */
@Service
public class PointFeedBackServiceImpl implements PointFeedBackService {

    @Autowired
    private PointFeedBackMapper pointFeedBackMapper;

    @Override
    public Integer insert(PointFeedBackDO pointFeedBackDO) {
        return pointFeedBackMapper.insert(pointFeedBackDO);
    }

    @Override
    public Integer update(PointFeedBackDO pointFeedBackDO) {
        return pointFeedBackMapper.update(pointFeedBackDO);
    }

    @Override
    public List<PointFeedBackDO> getAll() {
        return pointFeedBackMapper.getAll();
    }
}
