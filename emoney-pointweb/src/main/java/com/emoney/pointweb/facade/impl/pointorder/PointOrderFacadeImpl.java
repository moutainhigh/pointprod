package com.emoney.pointweb.facade.impl.pointorder;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.enums.PointOrderStatusEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.result.ResultInfo;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoeny.pointfacade.facade.pointorder.PointOrderFacade;
import com.emoeny.pointfacade.model.dto.PointOrderCancelDTO;
import com.emoeny.pointfacade.model.dto.PointOrderExchangeDTO;
import com.emoeny.pointfacade.model.dto.PointOrderCreateDTO;
import com.emoeny.pointfacade.model.vo.*;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.repository.dao.entity.PointProductDO;
import com.emoney.pointweb.service.biz.MailerService;
import com.emoney.pointweb.service.biz.PointOrderService;
import com.emoney.pointweb.service.biz.PointProductService;
import com.emoney.pointweb.service.biz.redis.RedissonDistributionLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

    @Autowired
    private PointProductService pointProductService;

    @Autowired
    private MailerService mailerService;

    @Value("${mail.toMail.addr}")
    private String toMailAddress;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;

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

    @Override
    public Result<Object> payCallBack(@NotNull(message = "订单号不能为空") String orderID, @NotNull(message = "支付单号不能为空") String tradeOrderID, String paytype){
        try {
            log.info(MessageFormat.format("支付回调开始，参数->订单号:{0},支付单号:{1},支付类型:{2}",orderID,tradeOrderID,paytype));
            PointOrderDO pointOrderVO = pointOrderService.getByOrderNo(orderID);
            //创建物流订单
            CreateOrderVO createOrderVO = getRequrstInfo(orderID, tradeOrderID, paytype,pointOrderVO);

            if (!createOrderToLogistics(createOrderVO)) {
                //发送邮件
                CompletableFuture.runAsync(() -> {
                    String subject = "创建物流订单异常通知";
                    String content = MessageFormat.format("创建物流订单失败，订单号：{0} 支付流水号：{1} 回传时间：{2}", orderID, tradeOrderID, new Date());
                    mailerService.sendSimpleTextMailActual(subject, content, toMailAddress.split(","), null, null, null);
                }, executor);
                return Result.buildErrorResult("创建物流订单失败");
            }

            if (pointOrderVO != null) {
                PointOrderExchangeDTO pointOrderExchangeDTO = new PointOrderExchangeDTO();
                pointOrderExchangeDTO.setUid(pointOrderVO.getUid());
                pointOrderExchangeDTO.setOrderNo(orderID);
                pointOrderExchangeDTO.setPayType(paytype);
                pointOrderExchangeDTO.setTradeNo(tradeOrderID);

                Result<Object> result = pointOrderService.exanchangeByPoint(pointOrderExchangeDTO);
                log.info(MessageFormat.format("更新订单状态，订单号：{0}  结果：{1}", orderID, JSON.toJSONString(result)));
                if (result != null) {
                    if (result.getCode().equals("200")) {
                        return Result.buildSuccessResult(JSON.parseObject(JSON.toJSONString(result.getData()), PointOrderVO.class));
                    } else {
                        //发送邮件
                        CompletableFuture.runAsync(() -> {
                            if (!result.getCode().equals(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode())){
                                String subject = "更新订单状态异常通知";
                                String content = MessageFormat.format("更新订单状态失败，订单号：{0} 支付流水号：{1} 回传时间：{2}", orderID, tradeOrderID, new Date());
                                mailerService.sendSimpleTextMailActual(subject, content, toMailAddress.split(","), null, null, null);
                            }
                        }, executor);
                        return Result.buildErrorResult(result.getCode(), result.getMsg());
                    }
                }
            }
            return Result.buildErrorResult(BaseResultCodeEnum.ILLEGAL_OPERATION.getCode(), BaseResultCodeEnum.ILLEGAL_OPERATION.getMsg());
        } catch (Exception e) {
            log.error("payCallback error:", e);
            return Result.buildErrorResult(BaseResultCodeEnum.SYSTEM_ERROR.getMsg());
        }
    }

    /*
     * 创建物流请求参数
     * @author lipengcheng
     * @date 2021-4-25 13:56
     * @param orderID
     * @param tradeOrderID
     * @param paytype
     * @return com.emoney.pointfront.model.vo.pay.CreateOrderVO
     */
    public CreateOrderVO getRequrstInfo(String orderID, String tradeOrderID, String paytype,PointOrderDO pointOrderVO) throws Exception {
        //获取订单详情
        //PointOrderDO pointOrderVO = pointOrderService.getByOrderNo(orderID);
        //获取产品详情
        PointProductDO pointProductVO = pointProductService.getById(pointOrderVO.getProductId());
        //生成创建物流订单请求参数
        CreateOrderVO createOrderVO = new CreateOrderVO();
        createOrderVO.setChannelCode("QD10000237");
        createOrderVO.setAgentID("100000000");
        createOrderVO.setOrderID(orderID);
        createOrderVO.setMID(pointOrderVO.getMobile());
        createOrderVO.setCustomerName(pointOrderVO.getMobileMask());
        createOrderVO.setCustomerAccount(pointOrderVO.getEmNo());
        createOrderVO.setBillHead("个人");
        createOrderVO.setCreateDate(DateUtil.format(pointOrderVO.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        createOrderVO.setIsNeedInvoice("2");
        createOrderVO.setActionType("A8022");
        createOrderVO.setInvoiceType("A6001");
        createOrderVO.setProvince("");
        createOrderVO.setCity("");
        createOrderVO.setArea("");
        createOrderVO.setAddress("");

        List<PayInfoVO> payInfoVOList = new ArrayList<>();
        PayInfoVO payInfo = new PayInfoVO();
        payInfo.setOnlineType(paytype);
        payInfo.setPayCode(tradeOrderID);
        payInfo.setPayPrice(pointProductVO.getExchangeCash().toString());
        payInfo.setPayTime(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss"));
        payInfoVOList.add(payInfo);

        List<IntegralVO> integralVOList = new ArrayList<>();
        IntegralVO integralVO = new IntegralVO();
        integralVO.setCode(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        integralVO.setNumber(pointOrderVO.getPoint().toString());
        integralVO.setAmount(String.valueOf((pointProductVO.getActivityPrice().floatValue() - pointOrderVO.getCash().floatValue())));
        integralVOList.add(integralVO);

        List<ProductVO> productVOList = new ArrayList<>();
        ProductVO productVO = new ProductVO();
        productVO.setActCode(pointProductVO.getActivityCode());
        productVO.setSalePrice(pointOrderVO.getCash().toString());
        productVOList.add(productVO);

        createOrderVO.setPayList(payInfoVOList);
        createOrderVO.setIntegralList(integralVOList);
        createOrderVO.setProdList(productVOList);

        return createOrderVO;
    }

    public Boolean createOrderToLogistics(CreateOrderVO createOrderVO) {
        try {
            String url = "http://webapi.emoney.cn/Logistics/api/order.LogisticsOrderCreateNew?AppId=10199";
            String res = OkHttpUtil.get(MessageFormat.format(url + "&orderJson={0}", URLEncoder.encode(JSON.toJSONString(createOrderVO), "UTF-8")), null);
            log.info(MessageFormat.format("创建物流订单，订单号：{0}  结果：{1}", createOrderVO.getOrderID(), res));
            if (!StringUtils.isEmpty(res)) {
                ResultInfo<Object> apiResult = JsonUtil.toBean(res, ResultInfo.class);
                if (apiResult.getRetCode().equals("0")) {
                    log.info(MessageFormat.format("创建物流订单成功，订单号：{0} 支付流水号：{1} 回传时间：{2}", createOrderVO.getOrderID(),
                            createOrderVO.getPayList().stream().findFirst().get().getPayCode(), new Date().toString()));
                    return true;
                }
                String temp = JsonUtil.getValue(res, "RetCode");
                if (JsonUtil.getValue(res, "RetCode").equals("0")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.error(MessageFormat.format("创建物流订单失败，订单号：{0} 支付流水号：{1} 回传时间：{2}", createOrderVO.getOrderID(),
                    createOrderVO.getPayList().stream().findFirst().get().getPayCode(), new Date().toString()));
            return false;
        }
    }
}
