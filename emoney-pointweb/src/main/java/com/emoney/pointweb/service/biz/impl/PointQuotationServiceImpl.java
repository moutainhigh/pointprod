package com.emoney.pointweb.service.biz.impl;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoney.pointweb.repository.dao.entity.PointQuotationDO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.mapper.PointQuotationMapper;
import com.emoney.pointweb.service.biz.PointQuotationService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
@Slf4j
public class PointQuotationServiceImpl implements PointQuotationService {
    @Autowired
    private PointQuotationMapper pointQuotationMapper;

    @Autowired
    private RedisService redisCache1;

    public List<PointQuotationDO> getAll(){
        List<PointQuotationDO> pointQuotationDOS = redisCache1.getList(RedisConstants.REDISKEY_PointQuotation_GETALL, PointQuotationDO.class);
        if (pointQuotationDOS == null) {
            pointQuotationDOS = pointQuotationMapper.getAll();
            if (pointQuotationDOS != null) {
                redisCache1.set(RedisConstants.REDISKEY_PointQuotation_GETALL, pointQuotationDOS, (long) 60 * 60 * 8);
            }
        }
        return pointQuotationDOS;
    }

    public int insert(PointQuotationDO pointQuotationDO){
        redisCache1.remove(RedisConstants.REDISKEY_PointQuotation_GETALL);
        return pointQuotationMapper.insert(pointQuotationDO);
    }

    public int update(PointQuotationDO pointQuotationDO){
        redisCache1.remove(RedisConstants.REDISKEY_PointQuotation_GETALL);
        return pointQuotationMapper.update(pointQuotationDO);
    }
}
