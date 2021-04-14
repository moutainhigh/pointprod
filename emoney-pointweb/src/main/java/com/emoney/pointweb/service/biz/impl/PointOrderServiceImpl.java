package com.emoney.pointweb.service.biz.impl;

import cn.hutool.core.util.IdUtil;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.enums.PointOrderStatusEnum;
import com.emoeny.pointcommon.enums.PointRecordStatusEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointOrderExchangeDTO;
import com.emoeny.pointfacade.model.dto.PointOrderCreateDTO;
import com.emoney.pointweb.repository.PointOrderRepository;
import com.emoney.pointweb.repository.PointProductRepository;
import com.emoney.pointweb.repository.PointRecordESRepository;
import com.emoney.pointweb.repository.PointRecordRepository;
import com.emoney.pointweb.repository.dao.entity.*;
import com.emoney.pointweb.service.biz.PointOrderService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;

import static com.emoeny.pointcommon.result.Result.buildErrorResult;
import static com.emoeny.pointcommon.result.Result.buildSuccessResult;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/18 16:09
 */
@Service
public class PointOrderServiceImpl implements PointOrderService {

    @Autowired
    private PointOrderRepository pointOrderRepository;

    @Autowired
    private PointProductRepository pointProductRepository;

    @Autowired
    private PointRecordRepository pointRecordRepository;

    @Autowired
    private PointRecordESRepository pointRecordESRepository;

    @Autowired
    private RedisService redisCache1;


    @Override
    public List<PointOrderDO> getByUid(Long uid,Integer orderStatus,int pageIndex,int pageSize) {
        return pointOrderRepository.getByUid(uid,orderStatus,pageIndex,pageSize);
    }

    @Override
    public Result<Object> createPointOrder(PointOrderCreateDTO pointOrderCreateDTO) {
        PointProductDO pointProductDO = pointProductRepository.getById(pointOrderCreateDTO.getProductId());
        if (pointProductDO != null) {
            String errMsg = checkPointOrder(pointOrderCreateDTO.getUid(), pointOrderCreateDTO.getProductQty(), pointProductDO);
            if (StringUtils.isEmpty(errMsg)) {
                //保存订单
                PointOrderDO pointOrderDO = new PointOrderDO();
                pointOrderDO.setUid(pointOrderCreateDTO.getUid());
                pointOrderDO.setEmNo(pointOrderCreateDTO.getEmNo());
                pointOrderDO.setOrderNo(IdUtil.getSnowflake(1, 1).nextId());
                pointOrderDO.setProductId(pointOrderCreateDTO.getProductId());
                pointOrderDO.setProductTitle(pointProductDO.getProductName());
                pointOrderDO.setProductQty(pointOrderCreateDTO.getProductQty());
                pointOrderDO.setPoint(pointProductDO.getExchangePoint());
                pointOrderDO.setCash(pointProductDO.getExchangeCash());
                pointOrderDO.setOrderStatus(Integer.valueOf(PointOrderStatusEnum.UNFINISHED.getCode()));
                pointOrderDO.setCreateTime(new Date());
                if (pointOrderRepository.insert(pointOrderDO) > 0) {
                    return buildSuccessResult(pointOrderDO);
                }
            } else {
                return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), errMsg);
            }
        } else {
            return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "商品不存在");
        }
        return buildErrorResult("创建订单失败");
    }

    @Override
    public Result<Object> exanchangeByPoint(PointOrderExchangeDTO pointExchangeDTO) {
        PointOrderDO pointOrderDO = pointOrderRepository.getByOrderNo(pointExchangeDTO.getOrderNo());
        if (pointOrderDO == null || !pointOrderDO.getOrderStatus().equals(Integer.valueOf(PointOrderStatusEnum.UNFINISHED.getCode()))) {
            return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "订单不存在或者订单异常，无法兑换");
        } else {
            if(!pointOrderDO.getUid().equals(pointExchangeDTO.getUid())){
                return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "订单异常，无法兑换");
            }
            PointProductDO pointProductDO = pointProductRepository.getById(pointOrderDO.getProductId());
            if (pointProductDO != null) {
                //增加积分扣除流水
                PointRecordDO pointRecordDO = new PointRecordDO();
                pointRecordDO.setId(IdUtil.getSnowflake(1, 1).nextId());
                pointRecordDO.setUid(pointOrderDO.getUid());
                pointRecordDO.setTaskId(-1L);
                pointRecordDO.setTaskName("积分兑换");
                pointRecordDO.setTaskPoint(-pointProductDO.getExchangePoint());
                pointRecordDO.setPointStatus(Integer.parseInt(PointRecordStatusEnum.CONVERTED.getCode()));
                pointRecordDO.setCreateTime(new Date());
                pointRecordDO.setCreateBy("system");
                int ret = pointRecordRepository.insert(pointRecordDO);
                if (ret > 0) {
                    //记录到ES
                    pointRecordESRepository.save(pointRecordDO);
                    //修改订单状态
                    pointOrderDO.setPayType(pointExchangeDTO.getPayType());
                    pointOrderDO.setOrderStatus(Integer.valueOf(PointOrderStatusEnum.FINISHED.getCode()));
                    pointOrderDO.setUpdateTime(new Date());
                    pointOrderRepository.update(pointOrderDO);
                    //去掉积分记录
                    redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETBYUID, pointExchangeDTO.getUid()));
                    //去掉积分统计
                    redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUID, pointExchangeDTO.getUid()));
                    redisCache1.removePattern("pointprod:pointrecord_getsummarybyuidandcreatetime_" + pointExchangeDTO.getUid()+ "_*");
                    return buildSuccessResult(pointOrderDO);
                }
                return buildErrorResult("积分兑换失败");
            } else {
                return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "商品不存在");
            }
        }
    }

    @Override
    public List<PointOrderSummaryDO> getSummaryByProductId(Integer productId) {
        return pointOrderRepository.getSummaryByProductId(productId);
    }

    @Override
    public List<PointOrderDO> getAllByOrderStatus(Integer orderStatus){
        return pointOrderRepository.getAllByOrderStatus(orderStatus);
    }

    @Override
    public PointOrderDO getByOrderNo(Long orderNo) {
        return pointOrderRepository.getByOrderNo(orderNo);
    }

    /**
     * 订单检查
     *
     * @param uid
     * @param productQty
     * @param pointProductDO
     * @return
     */
    private String checkPointOrder(long uid, int productQty, PointProductDO pointProductDO) {
        String ret = "";
        //当前用户下单商品总数
        int curQty = 0;
        //已下单商品总数
        int totalQty = 0;
        //当前用户已兑换积分
        float curPoint = 0;
        //当前用户已获得总积分
        float totalPoint = 0;
        List<PointOrderSummaryDO> pointOrderSummaryDOs = pointOrderRepository.getSummaryByProductId(pointProductDO.getId());
        PointOrderSummaryDO pointOrderSummaryDO = pointOrderSummaryDOs != null ? pointOrderSummaryDOs.stream().findFirst().orElse(null) : null;
        if (pointOrderSummaryDO != null) {
            totalQty = pointOrderSummaryDO.getTotalQty();
            //判断库存
            if ((totalQty + productQty) > pointProductDO.getTotalLimit() * 0.9) {
                return "商品库存不足";
            }
        }
        List<PointRecordSummaryDO> pointRecordSummaryDOS = pointRecordRepository.getPointRecordSummaryByUid(uid);
        if (pointRecordSummaryDOS != null && pointRecordSummaryDOS.size() > 0) {
            PointRecordSummaryDO pointRecordSummaryDO = pointRecordSummaryDOS.stream().filter(h -> h.getPointStatus()!=null&& h.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode()))).findAny().orElse(null);
            totalPoint = pointRecordSummaryDO != null ? pointRecordSummaryDO.getPointTotal() : 0;
        }
        List<PointOrderDO> myPointOrders = pointOrderRepository.getByUidAndProductId(uid, null);
        if (myPointOrders != null && myPointOrders.size() > 0) {
            curQty = myPointOrders.stream().filter(h->h.getProductId().equals(pointProductDO.getId())).mapToInt(PointOrderDO::getProductQty).sum();
            for (PointOrderDO pointOrderDO : myPointOrders
            ) {
                curPoint += pointOrderDO.getPoint() * pointOrderDO.getProductQty();
            }
        }
        if ((curQty + productQty) > pointProductDO.getPerLimit()) {
            return "个人购买商品超限";
        }
        if ((curPoint + (productQty * pointProductDO.getExchangePoint())) > totalPoint) {
            return "积分不足,无法兑换";
        }
        return "";
    }


}
