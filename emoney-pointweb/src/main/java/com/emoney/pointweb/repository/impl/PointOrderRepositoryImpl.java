package com.emoney.pointweb.repository.impl;

import cn.hutool.core.date.DateUtil;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.utils.ToolUtils;
import com.emoney.pointweb.repository.PointOrderRepository;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderSummaryDO;
import com.emoney.pointweb.repository.dao.mapper.PointOrderMapper;
import com.emoney.pointweb.service.biz.redis.RedisService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class PointOrderRepositoryImpl implements PointOrderRepository {

    @Autowired
    private PointOrderMapper pointOrderMapper;

    @Autowired
    private RedisService redisCache1;


    @Override
    public List<PointOrderDO> getByUid(Long uid, Integer orderStatus, int pageIndex, int pageSize) {
        PageHelper.startPage(pageIndex, pageSize);
        List<PointOrderDO> list = pointOrderMapper.getByUid(uid, orderStatus);
        return list;
    }

    @Override
    public List<PointOrderDO> getByUidAndProductId(Long uid, Integer productId) {
        if (productId == null) {
            List<PointOrderDO> pointOrderDOS = redisCache1.getList(MessageFormat.format(RedisConstants.REDISKEY_PointOrder_GETBYUID, uid), PointOrderDO.class);
            if (pointOrderDOS == null) {
                pointOrderDOS = pointOrderMapper.getByUidAndProductId(uid, productId);
                if (pointOrderDOS != null && pointOrderDOS.size() > 0) {
                    redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointOrder_GETBYUID, uid), pointOrderDOS, ToolUtils.GetExpireTime(60));
                }
            }
            return pointOrderDOS;
        }
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
    public PointOrderDO getByOrderNo(String orderNo) {
        return pointOrderMapper.getByOrderNo(orderNo);
    }

    @Override
    public Integer insert(PointOrderDO pointOrderDO) {
        log.info("订单测试11-1:"+ DateUtil.formatDateTime(DateUtil.date()));
        int ret = pointOrderMapper.insert(pointOrderDO);
        log.info("订单测试11-2:"+ DateUtil.formatDateTime(DateUtil.date()));
        if (ret > 0) {
            //30分钟没支付，自动取消订单
            redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointOrder_SETORDERKEY, pointOrderDO.getId()), pointOrderDO.getOrderNo(), 60 * 30L);
            //10分钟没支付，发消息提醒
            redisCache1.set(MessageFormat.format(RedisConstants.REDISKEY_PointOrderMIND_SETORDERKEY, pointOrderDO.getId()), pointOrderDO.getOrderNo(), 60 * 10L);
            //订单更新将订单列表缓存清除
            redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointOrder_GETBYUID, pointOrderDO.getUid()));
        }
        return ret;
    }

    @Override
    public Integer update(PointOrderDO pointOrderDO) {
        int ret = pointOrderMapper.update(pointOrderDO);
        if (ret > 0) {
            //订单更新将订单列表缓存清除
            redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointOrder_GETBYUID, pointOrderDO.getUid()));
        }
        return ret;
    }

    @Override
    public List<PointOrderDO> getAllByOrderStatus(Integer orderStatus) {
        return pointOrderMapper.getAllByOrderStatus(orderStatus);
    }

    @Override
    public List<PointOrderDO> getOrdersByStatusAndIsSend() {
        return pointOrderMapper.getOrdersByStatusAndIsSend();
    }
}
