package com.emoney.pointweb.repository.impl;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoney.pointweb.repository.PointProductRepository;
import com.emoney.pointweb.repository.dao.entity.PointProductDO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.mapper.PointProductMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/19 16:27
 */
@Repository
public class PointProductRepositoryImpl implements PointProductRepository {

    @Autowired
    private PointProductMapper pointProductMapper;

    @Autowired
    private RedisService redisCache1;


    @Override
    public List<PointProductDO> getAllEffectiveProducts(Date curDate) {
        List<PointProductDO> pointProductDOS = redisCache1.getList(RedisConstants.REDISKEY_PointProduct_GETALLEFFECTIVEPRODUCTS, PointProductDO.class);
        if (pointProductDOS == null) {
            pointProductDOS = pointProductMapper.getAllEffectiveProducts(curDate);
            if (pointProductDOS != null && pointProductDOS.size() > 0) {
                redisCache1.set(RedisConstants.REDISKEY_PointProduct_GETALLEFFECTIVEPRODUCTS, pointProductDOS, ToolUtils.GetExpireTime(60));
            }
        }
        return pointProductDOS;
    }

    @Override
    public PointProductDO getById(int id) {
        PointProductDO pointProductDO = redisCache1.get(MessageFormat.format(RedisConstants.REDISKEY_PointProduct_GETBYID, id), PointProductDO.class);
        if (pointProductDO == null) {
            pointProductDO = pointProductMapper.getById(id);
            if (pointProductDO != null) {
                redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointProduct_GETBYID, id), pointProductDO);
            }
        }
        return pointProductDO;
    }
}
