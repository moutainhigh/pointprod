package com.emoney.pointweb.service.biz.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.*;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointOrderExchangeDTO;
import com.emoeny.pointfacade.model.dto.PointOrderCreateDTO;
import com.emoney.pointweb.repository.*;
import com.emoney.pointweb.repository.dao.entity.*;
import com.emoney.pointweb.repository.dao.entity.dto.CreateActivityGrantApplyAccountDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendCouponDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendPrivilegeDTO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryCouponActivityVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.repository.dao.mapper.PointOrderMapper;
import com.emoney.pointweb.service.biz.LogisticsService;
import com.emoney.pointweb.service.biz.MailerService;
import com.emoney.pointweb.service.biz.PointOrderService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.kafka.KafkaProducerService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.emoeny.pointcommon.result.Result.buildErrorResult;
import static com.emoeny.pointcommon.result.Result.buildSuccessResult;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/3/18 16:09
 */
@Service
@Slf4j
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
    private PointLimitRepository pointLimitRepository;

    @Autowired
    private PointOrderMapper pointOrderMapper;

    @Autowired
    private RedisService redisCache1;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private MailerService mailerService;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor executor;


    @Autowired
    private LogisticsService logisticsService;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${mail.toMail.addr}")
    private String toMailAddress;

    @Override
    public List<PointOrderDO> getByUid(Long uid, Integer orderStatus, int pageIndex, int pageSize) {
        return pointOrderRepository.getByUid(uid, orderStatus, pageIndex, pageSize);
    }

    public List<PointOrderDO> queryAllByProductType(Integer porductType){
        return pointOrderMapper.queryAllByProductType(porductType);
    }

    @Override
    public Result<Object> createPointOrder(PointOrderCreateDTO pointOrderCreateDTO) {
        PointProductDO pointProductDO = pointProductRepository.getById(pointOrderCreateDTO.getProductId());
        log.info("订单测试3:"+DateUtil.formatDateTime(DateUtil.date()));
        if (pointProductDO != null) {
            String errMsg = checkPointOrder(pointOrderCreateDTO.getUid(), pointOrderCreateDTO.getProductQty(), pointProductDO);
            if (StringUtils.isEmpty(errMsg)) {
                //保存订单
                PointOrderDO pointOrderDO = new PointOrderDO();
                pointOrderDO.setUid(pointOrderCreateDTO.getUid());
                pointOrderDO.setEmNo(pointOrderCreateDTO.getEmNo());
                pointOrderDO.setOrderNo("EJF" + IdUtil.getSnowflake(1, 1).nextId());
                pointOrderDO.setProductId(pointOrderCreateDTO.getProductId());
                pointOrderDO.setProductTitle(pointProductDO.getProductName());
                pointOrderDO.setProductQty(pointOrderCreateDTO.getProductQty());
                pointOrderDO.setPoint(pointProductDO.getExchangePoint());
                pointOrderDO.setCash(pointProductDO.getExchangeCash());
                pointOrderDO.setOrderStatus(Integer.valueOf(PointOrderStatusEnum.UNFINISHED.getCode()));
                pointOrderDO.setMobile(pointOrderCreateDTO.getMobile());
                pointOrderDO.setMobileMask(pointOrderCreateDTO.getMobileMask());
                pointOrderDO.setProductFile(pointProductDO.getProductFile());
                pointOrderDO.setProductType(pointProductDO.getProductType());
                pointOrderDO.setCreateTime(new Date());
                if (pointOrderRepository.insert(pointOrderDO) > 0) {
                    log.info("订单测试9:"+DateUtil.formatDateTime(DateUtil.date()));
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
            if (!pointOrderDO.getUid().equals(pointExchangeDTO.getUid())) {
                return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "订单异常，无法兑换");
            }
            PointProductDO pointProductDO = pointProductRepository.getById(pointOrderDO.getProductId());
            if (pointProductDO != null) {
                //增加积分扣除流水
                PointRecordDO pointRecordDO = new PointRecordDO();
                pointRecordDO.setId(IdUtil.getSnowflake(1, 1).nextId());
                pointRecordDO.setUid(pointOrderDO.getUid());
                pointRecordDO.setTaskId(-1L);
                pointRecordDO.setTaskName(pointProductDO.getProductName());
                pointRecordDO.setTaskPoint(-pointProductDO.getExchangePoint());
                pointRecordDO.setPointStatus(Integer.parseInt(PointRecordStatusEnum.CONVERTED.getCode()));
                pointRecordDO.setCreateTime(new Date());
                pointRecordDO.setCreateBy("system");
                pointRecordDO.setIsValid(true);
                int ret = pointRecordRepository.insert(pointRecordDO);
                if (ret > 0) {
                    //记录到ES
                    pointRecordESRepository.save(pointRecordDO);
                    //去掉积分记录
                    redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETBYUID, pointExchangeDTO.getUid()));
                    //去掉积分统计
                    redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUID, pointExchangeDTO.getUid()));
                    //修改订单状态
                    pointOrderDO.setPayType(pointExchangeDTO.getPayType());
                    pointOrderDO.setTradeNo(pointExchangeDTO.getTradeNo());
                    pointOrderDO.setOrderStatus(Integer.valueOf(PointOrderStatusEnum.FINISHED.getCode()));
                    pointOrderDO.setUpdateTime(new Date());
                    int retOrder = pointOrderRepository.update(pointOrderDO);
                    if (retOrder > 0) {
                        //异步处理
                        CompletableFuture.runAsync(() -> {
                            try {
                                //修改分摊记录
                                float exchangePoint = Math.abs(pointRecordDO.getTaskPoint());
                                float tmpExchangePoint = 0f;
                                //当前获得的未使用积分
                                List<PointRecordDO> pointRecordDOS = pointRecordRepository.getByUidAndCreateTime(pointRecordDO.getUid(), pointRecordDO.getCreateTime());
                                List<PointRecordDO> tmpPointRecordDOS = new ArrayList<>();
                                if (pointRecordDOS != null && pointRecordDOS.size() > 0) {
                                    //非定向积分
                                    for (PointRecordDO p : pointRecordDOS.stream().filter(h -> !h.getIsDirectional()).sorted(Comparator.comparing(PointRecordDO::getCreateTime)).collect(Collectors.toList())
                                    ) {
                                        tmpExchangePoint += p.getLeftPoint();
                                        if (tmpExchangePoint <= exchangePoint) {
                                            p.setLeftPoint(0f);
                                            p.setUpdateTime(new Date());
                                            tmpPointRecordDOS.add(p);
                                        } else {
                                            p.setLeftPoint(tmpExchangePoint - exchangePoint);
                                            p.setUpdateTime(new Date());
                                            tmpPointRecordDOS.add(p);
                                            break;
                                        }
                                    }
                                    if (tmpExchangePoint < exchangePoint) {
                                        //定向积分
                                        for (PointRecordDO p : pointRecordDOS.stream().filter(h -> h.getIsDirectional()).sorted(Comparator.comparing(PointRecordDO::getCreateTime)).collect(Collectors.toList())
                                        ) {
                                            tmpExchangePoint += p.getLeftPoint();
                                            if (tmpExchangePoint <= exchangePoint) {
                                                p.setLeftPoint(0f);
                                                p.setUpdateTime(new Date());
                                                tmpPointRecordDOS.add(p);
                                            } else {
                                                p.setLeftPoint(tmpExchangePoint - exchangePoint);
                                                p.setUpdateTime(new Date());
                                                tmpPointRecordDOS.add(p);
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (tmpPointRecordDOS != null && tmpPointRecordDOS.size() > 0) {
                                    log.info("积分兑换记录分摊：兑换记录:" + JSON.toJSONString(pointRecordDO) + "分摊记录:" + JSON.toJSONString(tmpPointRecordDOS));
                                    for (PointRecordDO p : tmpPointRecordDOS
                                    ) {
                                        pointRecordRepository.update(p);
                                        pointRecordESRepository.save(p);
                                    }
                                }

                                if (pointProductDO.getProductType().equals(2)) {
                                    List<QueryCouponActivityVO> queryCouponActivityVOS = logisticsService.getCouponRulesByAcCode(pointProductDO.getActivityCode());
                                    if (queryCouponActivityVOS != null && queryCouponActivityVOS.size() > 0) {
                                        SendCouponDTO sendCouponDTO = new SendCouponDTO();
                                        sendCouponDTO.setPRESENT_ACCOUNT_TYPE(2);
                                        sendCouponDTO.setPRESENT_ACCOUNT(pointOrderDO.getMobile());
                                        sendCouponDTO.setCOUPON_ACTIVITY_ID(pointProductDO.getActivityCode());
                                        sendCouponDTO.setCOUPON_RULE_PRICE(queryCouponActivityVOS.get(0).getCOUPON_RULE_PRICE());
                                        sendCouponDTO.setPRESENT_PERSON("积分商城");
                                        log.info("开始发放优惠券,参数:" + JSON.toJSONString(sendCouponDTO));
                                        Boolean resSendCoupon = logisticsService.SendCoupon(sendCouponDTO);
                                        if (resSendCoupon) {
                                            log.info("发放优惠券成功,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                                        } else {
                                            log.info("发放优惠券失败,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                                        }
                                    } else {
                                        log.warn("特权码无效,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                                    }
                                }
                                //发放新功能体验，特权
                                if (pointProductDO.getProductType().equals(3)) {
                                    SendPrivilegeDTO sendPrivilegeDTO = new SendPrivilegeDTO();
                                    sendPrivilegeDTO.setAppId("A009");
                                    sendPrivilegeDTO.setActivityID(pointProductDO.getActivityCode());
                                    sendPrivilegeDTO.setApplyUserID("scb_public");
                                    List<CreateActivityGrantApplyAccountDTO> createActivityGrantApplyAccountDTOS = new ArrayList<>();
                                    CreateActivityGrantApplyAccountDTO createActivityGrantApplyAccountDTO = new CreateActivityGrantApplyAccountDTO();
                                    createActivityGrantApplyAccountDTO.setAccountType(2);
                                    createActivityGrantApplyAccountDTO.setMID(pointOrderDO.getMobile());
                                    createActivityGrantApplyAccountDTOS.add(createActivityGrantApplyAccountDTO);
                                    sendPrivilegeDTO.setAccounts(createActivityGrantApplyAccountDTOS);
                                    log.info("开始发放特权,参数:" + JSON.toJSONString(sendPrivilegeDTO));
                                    Boolean resultSenddPrivilege = logisticsService.SenddPrivilege(sendPrivilegeDTO);
                                    if (resultSenddPrivilege) {
                                        log.info("发放特权成功,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                                    } else {
                                        log.info("发放特权失败,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO));
                                    }
                                }
                            } catch (Exception e) {
                                log.error("发放优惠券异常,商品:" + JSON.toJSONString(pointProductDO) + "订单:" + JSON.toJSONString(pointOrderDO), e);
                            }

                        }, executor);


                    }
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
    public List<PointOrderDO> getAllByOrderStatus(Integer orderStatus) {
        return pointOrderRepository.getAllByOrderStatus(orderStatus);
    }

    @Override
    public PointOrderDO getByOrderNo(String orderNo) {
        return pointOrderRepository.getByOrderNo(orderNo);
    }

    @Override
    public List<PointOrderDO> getByUidAndProductId(Long uid, Integer productId) {
        return pointOrderRepository.getByUidAndProductId(uid, productId);
    }

    @Override
    public List<PointOrderDO> getOrdersByStatusAndIsSend() {
        return pointOrderRepository.getOrdersByStatusAndIsSend();
    }

    @Override
    public Integer update(PointOrderDO pointOrderDO) {
        return pointOrderRepository.update(pointOrderDO);
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
        log.info("订单测试4:"+DateUtil.formatDateTime(DateUtil.date()));
        List<PointRecordSummaryDO> pointRecordSummaryDOS = pointRecordRepository.getPointRecordSummaryByUid(uid);
        if (pointRecordSummaryDOS != null && pointRecordSummaryDOS.size() > 0) {
            PointRecordSummaryDO pointRecordSummaryDO = pointRecordSummaryDOS.stream().filter(h -> h.getPointStatus() != null && h.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode()))).findAny().orElse(null);
            totalPoint = pointRecordSummaryDO != null ? pointRecordSummaryDO.getPointTotal() : 0;
        }
        log.info("订单测试5:"+DateUtil.formatDateTime(DateUtil.date()));
        List<PointOrderDO> myPointOrders = pointOrderRepository.getByUidAndProductId(uid, null);
        if (myPointOrders != null && myPointOrders.size() > 0) {
            curQty = myPointOrders.stream().filter(h -> h.getProductId().equals(pointProductDO.getId())).mapToInt(PointOrderDO::getProductQty).sum();
            for (PointOrderDO pointOrderDO : myPointOrders
            ) {
                if(DateUtil.formatDate(pointOrderDO.getCreateTime()).equals(DateUtil.formatDate(DateUtil.date()))) {
                    curPoint += pointOrderDO.getPoint() * pointOrderDO.getProductQty();
                }
            }
        }
        log.info("订单测试6:"+DateUtil.formatDateTime(DateUtil.date()));
        if ((curQty + productQty) > pointProductDO.getPerLimit()) {
            return "个人购买商品超限";
        }
        if ((curPoint + (productQty * pointProductDO.getExchangePoint())) > totalPoint) {
            return "积分不足,无法兑换";
        }
        PointLimitDO pointLimitDO = pointLimitRepository.getByType(Integer.valueOf(PointLimitTypeEnum.EXCHANGE.code()), Integer.valueOf(PointLimitToEnum.PERSONAL.code()));
        if (pointLimitDO != null) {
            if ((curPoint + (productQty * pointProductDO.getExchangePoint())) > pointLimitDO.getPointLimitvalue()) {
                log.info("订单测试7:"+DateUtil.formatDateTime(DateUtil.date()));
                //异步处理
                CompletableFuture.runAsync(() -> {
                    try {
                        //发送邮件
                        String subject = "积分兑换异常通知";
                        String userName = "";
                        List<UserInfoVO> userInfoVOS = userInfoService.getUserInfoByUid(uid);
                        if (userInfoVOS != null) {
                            UserInfoVO userInfoVO = userInfoVOS.stream().filter(h -> h.getAccountType() == 0).findFirst().orElse(null);
                            if (userInfoVO != null) {
                                userName = userInfoVO.getAccountName();
                            }
                        }
                        String content = MessageFormat.format("积分兑换超限，用户ID：{0},用户名称：{1},商品ID:{2},商品名称:{3},发生时间:{4}", uid, userName, pointProductDO.getId(), pointProductDO.getProductName(), new Date());
                        mailerService.sendSimpleTextMailActual(subject, content, toMailAddress.split(","), null, null, null);
                    } catch (Exception e) {
                        log.error("积分兑换异常通知,sendSimpleTextMailActual error", e);
                    }

                }, executor);
                log.info("订单测试8:"+DateUtil.formatDateTime(DateUtil.date()));
                return "今天积分兑换额度已满，请明天早点来吧！";
            }
        }
        return "";
    }
}
