package com.emoney.pointweb.repository.impl;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoeny.pointfacade.model.vo.PointQuestionVO;
import com.emoney.pointweb.repository.PointQuestionRepository;
import com.emoney.pointweb.repository.dao.entity.PointProductDO;
import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;
import com.emoney.pointweb.repository.dao.mapper.PointProductMapper;
import com.emoney.pointweb.repository.dao.mapper.PointQuestionMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

/**
 * @author lipengcheng
 * @date 2021-05-08
 */
@Repository
public class PointQuestionRepositoryImpl implements PointQuestionRepository {

    @Autowired
    private PointQuestionMapper pointQuestionMapper;

    @Autowired
    private RedisService redisCache1;

    @Override
    public List<PointQuestionDO> queryAll() {
        List<PointQuestionDO> pointQuestionDOS = redisCache1.getList(RedisConstants.REDISKEY_PointQuestion_QUERYAll, PointQuestionDO.class);
        if (pointQuestionDOS == null) {
            pointQuestionDOS = pointQuestionMapper.queryAll();
            if (pointQuestionDOS != null&&pointQuestionDOS.size()>0) {
                redisCache1.set(RedisConstants.REDISKEY_PointQuestion_QUERYAll, pointQuestionDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointQuestionDOS;
    }

    @Override
    public PointQuestionDO queryAllById(Integer id) {
        PointQuestionDO pointQuestionDO = redisCache1.get(MessageFormat.format(RedisConstants.REDISKEY_PointQuestion_GETBYID, id), PointQuestionDO.class);
        if (pointQuestionDO == null) {
            pointQuestionDO = pointQuestionMapper.queryAllById(id);
            if (pointQuestionDO != null) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointQuestion_GETBYID, id), pointQuestionDO);
            }
        }
        return pointQuestionDO;
    }
}
