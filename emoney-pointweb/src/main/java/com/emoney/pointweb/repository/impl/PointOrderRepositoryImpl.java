package com.emoney.pointweb.repository.impl;

import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoney.pointweb.repository.PointOrderRepository;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderSummaryDO;
import com.emoney.pointweb.repository.dao.mapper.PointOrderMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/18 16:06
 */
@Repository
public class PointOrderRepositoryImpl implements PointOrderRepository {

    @Autowired
    private PointOrderMapper pointOrderMapper;

    @Autowired
    private RedisService redisCache1;


    @Override
    public List<PointOrderDO> getByUid(Long uid,Integer orderStatus,int pageIndex,int pageSize) {
        PageHelper.startPage(pageIndex-1,pageSize);
        List<PointOrderDO> list=pointOrderMapper.getByUid(uid,orderStatus);
        return list;
    }

    @Override
    public List<PointOrderDO> getByUidAndProductId(Long uid, Integer productId) {
        return pointOrderMapper.getByUidAndProductId(uid, productId);
    }

    @Override
    public List<PointOrderSummaryDO> getSummaryByProductId(Integer productId) {
        return pointOrderMapper.getSummaryByProductId(productId);
    }

    @Override
    public PointOrderDO getById(Integer id) {
        return pointOrderMapper.getById(id);
    }

    @Override
    public PointOrderDO getByOrderNo(Long orderNo) {
        return pointOrderMapper.getByOrderNo(orderNo);
    }

    @Override
    public Integer insert(PointOrderDO pointOrderDO) {
        int ret = pointOrderMapper.insert(pointOrderDO);
        if (ret > 0) {
            //15分钟没支付，自动取消订单
            redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointOrder_SETORDERKEY, pointOrderDO.getId()), pointOrderDO.getOrderNo(), 60 * 15L);
            //10分钟没支付，发消息提醒
            redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointOrderMIND_SETORDERKEY, pointOrderDO.getId()), pointOrderDO.getOrderNo(), 60 * 10L);
        }
        return ret;
    }

    @Override
    public Integer update(PointOrderDO pointOrderDO) {
        return pointOrderMapper.update(pointOrderDO);
    }

    @Override
    public List<PointOrderDO> getAllByOrderStatus(Integer orderStatus) {
        return pointOrderMapper.getAllByOrderStatus(orderStatus);
    }
}
