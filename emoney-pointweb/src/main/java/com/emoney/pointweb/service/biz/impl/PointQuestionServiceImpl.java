package com.emoney.pointweb.service.biz.impl;

import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;
import com.emoney.pointweb.repository.dao.mapper.PointQuestionMapper;
import com.emoney.pointweb.service.biz.PointQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lipengcheng
 * @date 2021-05-07
 */
@Service
public class PointQuestionServiceImpl implements PointQuestionService {

    @Autowired
    private PointQuestionMapper pointQuestionMapper;

    @Override
    public Integer insert(PointQuestionDO pointQuestionDO) {
        return pointQuestionMapper.insert(pointQuestionDO);
    }

    @Override
    public Integer update(PointQuestionDO pointQuestionDO) {
        return pointQuestionMapper.update(pointQuestionDO);
    }

    @Override
    public List<PointQuestionDO> getAll() {
        return pointQuestionMapper.queryAll();
    }
}
