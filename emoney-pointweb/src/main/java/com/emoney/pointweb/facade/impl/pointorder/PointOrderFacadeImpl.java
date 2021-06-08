package com.emoney.pointweb.facade.impl.pointorder;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.enums.PointOrderStatusEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.facade.pointorder.PointOrderFacade;
import com.emoeny.pointfacade.model.dto.PointOrderCancelDTO;
import com.emoeny.pointfacade.model.dto.PointOrderExchangeDTO;
import com.emoeny.pointfacade.model.dto.PointOrderCreateDTO;
import com.emoeny.pointfacade.model.vo.PointOrderSummaryVO;
import com.emoeny.pointfacade.model.vo.PointOrderVO;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.service.biz.PointOrderService;
import com.emoney.pointweb.service.biz.redis.RedissonDistributionLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/18 16:35
 */
@RestController
@Validated
@Slf4j
public class PointOrderFacadeImpl implements PointOrderFacade {

    @Autowired
    private PointOrderService pointOrderService;

    /**
     * 分布式锁
     */
    @Resource
    private RedissonDistributionLock redissonDistributionLock;

    @Override
    public Result<List<PointOrderVO>> queryPointOrders(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "查询类型不能为空") Integer queryType, @NotNull(message = "pageIndex不能为空") Integer pageIndex, @NotNull(message = "pageSize不能为空") Integer pageSize) {
        try {
            return Result.buildSuccessResult(JsonUtil.copyList(pointOrderService.getByUid(uid, queryType, pageIndex, pageSize), PointOrderVO.class));
        } catch (Exception e) {
            log.error("queryPointOrders error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<Object> createPointOrder(@RequestBody @Valid PointOrderCreateDTO pointOrderCreateDTO) {
        String lockKey = MessageFormat.format(RedisConstants.REDISKEY_PointOrder_CREATE_LOCKKEY, pointOrderCreateDTO.getUid(), pointOrderCreateDTO.getProductId());
        try {
            if (redissonDistributionLock.tryLock(lockKey, TimeUnit.SECONDS, 10, 10)) {
                return pointOrderService.createPointOrder(pointOrderCreateDTO);
            }
            return Result.buildErrorResult(BaseResultCodeEnum.REPETITIVE_OPERATION.code(), BaseResultCodeEnum.REPETITIVE_OPERATION.getMsg());
        } catch (Exception e) {
            log.error("createPointOrder error:", e);
            return Result.buildErrorResult(e.getMessage());
        } finally {
            redissonDistributionLock.unlock(lockKey);
        }

    }

    @Override
    public Result<List<PointOrderSummaryVO>> getSummaryByProductId(Integer productId) {
        try {
            return Result.buildSuccessResult(JsonUtil.copyList(pointOrderService.getSummaryByProductId(productId), PointOrderSummaryVO.class));
        } catch (Exception e) {
            log.error("getSummaryByProductId error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<Object> exanchangeByPoint(@RequestBody @Valid PointOrderExchangeDTO pointExchangeDTO) {
        try {
            return pointOrderService.exanchangeByPoint(pointExchangeDTO);
        } catch (Exception e) {
            log.error("exanchangeByPoint error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<PointOrderVO> getOrderByOrderNo(@NotNull(message = "订单号不能为空") String orderNo) {
        try {
            return Result.buildSuccessResult(JsonUtil.toBean(JSON.toJSONString(pointOrderService.getByOrderNo(orderNo)), PointOrderVO.class));
        } catch (Exception e) {
            log.error("getOrderByOrderNo error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<List<PointOrderVO>> getByUidAndProductId(@NotNull(message = "用户id不能为空") Long uid, Integer productId) {
        try {
            return Result.buildSuccessResult(JsonUtil.toBeanList(JSON.toJSONString(pointOrderService.getByUidAndProductId(uid, productId)), PointOrderVO.class));
        } catch (Exception e) {
            log.error("getByUidAndProductId error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<Object> cancelPointOrder(@RequestBody @Valid PointOrderCancelDTO pointOrderCancelDTO) {
        try {
            PointOrderDO pointOrderDO = pointOrderService.getByOrderNo(pointOrderCancelDTO.getOrderNo());
            if (pointOrderDO != null && pointOrderDO.getUid().equals(pointOrderCancelDTO.getUid())) {
                pointOrderDO.setOrderStatus(Integer.valueOf(PointOrderStatusEnum.CANCELLED.getCode()));
                pointOrderDO.setUpdateTime(new Date());
                int result = pointOrderService.update(pointOrderDO);
                if (result > 0) {
                    return Result.buildSuccessResult(result);
                }
            }
            return Result.buildErrorResult("取消失败");
        } catch (Exception e) {
            log.error("cancelPointOrder error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
