package com.emoney.pointweb.service.biz.impl;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;
import com.emoney.pointweb.repository.dao.mapper.PointQuestionMapper;
import com.emoney.pointweb.service.biz.PointQuestionService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

/**
 * @author lipengcheng
 * @date 2021-05-07
 */
@Service
public class PointQuestionServiceImpl implements PointQuestionService {

    @Autowired
    private PointQuestionMapper pointQuestionMapper;

    @Autowired
    private RedisService redisCache1;

    @Override
    public Integer insert(PointQuestionDO pointQuestionDO) {
        //每日一答编辑或者新增将缓存删除
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointQuestion_GETBYID, pointQuestionDO.getId()));
        redisCache1.remove(RedisConstants.REDISKEY_PointQuestion_QUERYAll);
        return pointQuestionMapper.insert(pointQuestionDO);
    }

    @Override
    public Integer update(PointQuestionDO pointQuestionDO) {
        //每日一答编辑或者新增将缓存删除
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointQuestion_GETBYID, pointQuestionDO.getId()));
        redisCache1.remove(RedisConstants.REDISKEY_PointQuestion_QUERYAll);
        return pointQuestionMapper.update(pointQuestionDO);
    }

    @Override
    public List<PointQuestionDO> getAll() {
        return pointQuestionMapper.queryAll();
    }

    @Override
    public PointQuestionDO getAllById(Integer id){
        return pointQuestionMapper.queryAllById(id);
    }
}
