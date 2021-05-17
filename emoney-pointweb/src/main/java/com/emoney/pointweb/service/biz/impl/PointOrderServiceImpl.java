package com.emoney.pointweb.service.biz.impl;

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
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.service.biz.MailerService;
import com.emoney.pointweb.service.biz.PointOrderService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.kafka.KafkaProducerService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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
    private RedisService redisCache1;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private MailerService mailerService;

    @Autowired
    private UserInfoService userInfoService;

    @Value("${mail.toMail.addr}")
    private String toMailAddress;

    @Override
    public List<PointOrderDO> getByUid(Long uid, Integer orderStatus, int pageIndex, int pageSize) {
        return pointOrderRepository.getByUid(uid, orderStatus, pageIndex, pageSize);
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
            PointRecordSummaryDO pointRecordSummaryDO = pointRecordSummaryDOS.stream().filter(h -> h.getPointStatus() != null && h.getPointStatus().equals(Integer.valueOf(PointRecordStatusEnum.FINISHED.getCode()))).findAny().orElse(null);
            totalPoint = pointRecordSummaryDO != null ? pointRecordSummaryDO.getPointTotal() : 0;
        }
        List<PointOrderDO> myPointOrders = pointOrderRepository.getByUidAndProductId(uid, null);
        if (myPointOrders != null && myPointOrders.size() > 0) {
            curQty = myPointOrders.stream().filter(h -> h.getProductId().equals(pointProductDO.getId())).mapToInt(PointOrderDO::getProductQty).sum();
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

        PointLimitDO pointLimitDO = pointLimitRepository.getByType(Integer.valueOf(PointLimitTypeEnum.EXCHANGE.code()), Integer.valueOf(PointLimitToEnum.PERSONAL.code()));
        if (pointLimitDO != null) {
            if ((curPoint + (productQty * pointProductDO.getExchangePoint())) > pointLimitDO.getPointLimitvalue()) {
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

                return "今天积分兑换额度已满，请明天早点来吧！";
            }
        }
        return "";
    }
}
