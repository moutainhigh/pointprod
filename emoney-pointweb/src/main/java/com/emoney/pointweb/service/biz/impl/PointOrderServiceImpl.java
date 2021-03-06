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
import com.emoney.pointweb.repository.dao.entity.dto.CheckWebOrderDTO;
import com.emoney.pointweb.repository.dao.entity.dto.CreateActivityGrantApplyAccountDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendCouponDTO;
import com.emoney.pointweb.repository.dao.entity.dto.SendPrivilegeDTO;
import com.emoney.pointweb.repository.dao.entity.vo.QueryCouponActivityVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.repository.dao.mapper.PointOrderMapper;
import com.emoney.pointweb.service.biz.*;
import com.emoney.pointweb.service.biz.kafka.KafkaProducerService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import com.emoney.pointweb.service.biz.redis.RedissonDistributionLock;
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
import java.util.concurrent.TimeUnit;
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
    private PointMessageService pointMessageService;

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

    /**
     * ????????????
     */
    @Resource
    private RedissonDistributionLock redissonDistributionLock;

    @Override
    public List<PointOrderDO> getByUid(Long uid, Integer orderStatus, int pageIndex, int pageSize) {
        return pointOrderRepository.getByUid(uid, orderStatus, pageIndex, pageSize);
    }

    public List<PointOrderDO> queryAllByProductType(Integer porductType) {
        return pointOrderMapper.queryAllByProductType(porductType);
    }

    @Override
    public Result<Object> createPointOrder(PointOrderCreateDTO pointOrderCreateDTO) {
        PointProductDO pointProductDO = pointProductRepository.getById(pointOrderCreateDTO.getProductId());
        if (pointProductDO != null) {
            String errMsg = checkPointOrder(pointOrderCreateDTO.getUid(), pointOrderCreateDTO.getProductQty(), pointOrderCreateDTO.getEmNo(), pointOrderCreateDTO.getMobile(), pointProductDO);
            if (StringUtils.isEmpty(errMsg)) {
                //????????????
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
            return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "???????????????");
        }
        return buildErrorResult("??????????????????");
    }

    @Override
    public Result<Object> exanchangeByPoint(PointOrderExchangeDTO pointExchangeDTO) {
        String lockKey = MessageFormat.format(RedisConstants.REDISKEY_ExchangePoint_BYUID, pointExchangeDTO.getUid());
        try {
            if (!redissonDistributionLock.tryLock(lockKey, TimeUnit.SECONDS, 10, 10)) {
                return Result.buildErrorResult(BaseResultCodeEnum.FREQUENT_INTERFACE_ACCESS.code(), BaseResultCodeEnum.FREQUENT_INTERFACE_ACCESS.getMsg());
            }

            PointOrderDO pointOrderDO = pointOrderRepository.getByOrderNo(pointExchangeDTO.getOrderNo());
            if (pointOrderDO == null || !pointOrderDO.getOrderStatus().equals(Integer.valueOf(PointOrderStatusEnum.UNFINISHED.getCode()))) {
                return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "????????????????????????????????????????????????");
            } else {
                if (!pointOrderDO.getUid().equals(pointExchangeDTO.getUid())) {
                    return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "???????????????????????????");
                }
                PointProductDO pointProductDO = pointProductRepository.getById(pointOrderDO.getProductId());
                if (pointProductDO != null) {
                    //????????????????????????
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
                        //?????????ES
                        pointRecordESRepository.save(pointRecordDO);
                        //??????????????????
                        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETBYUID, pointExchangeDTO.getUid()));
                        //??????????????????
                        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointRecord_GETSUMMARYBYUID, pointExchangeDTO.getUid()));
                        redisCache1.removePattern(MessageFormat.format("pointprod:pointrecord_getsummarybyuidandcreatetime_{0}_*", pointExchangeDTO.getUid()));
                        //??????????????????
                        pointOrderDO.setPayType(pointExchangeDTO.getPayType());
                        pointOrderDO.setTradeNo(pointExchangeDTO.getTradeNo());
                        pointOrderDO.setOrderStatus(Integer.valueOf(PointOrderStatusEnum.FINISHED.getCode()));
                        pointOrderDO.setExpressMobile(pointExchangeDTO.getExpressMobile());
                        pointOrderDO.setExpressMobileMask(pointExchangeDTO.getExpressMobileMask());
                        pointOrderDO.setExpressAddress(pointExchangeDTO.getExpressAddress());
                        pointOrderDO.setUpdateTime(new Date());
                        int retOrder = pointOrderRepository.update(pointOrderDO);
                        if (retOrder > 0) {
                            //????????????
                            CompletableFuture.runAsync(() -> {
                                try {
                                    //??????????????????
                                    float exchangePoint = Math.abs(pointRecordDO.getTaskPoint());
                                    float tmpExchangePoint = 0f;
                                    //??????????????????????????????
                                    List<PointRecordDO> pointRecordDOS = pointRecordRepository.getByUidAndCreateTime(pointRecordDO.getUid(), pointRecordDO.getCreateTime());
                                    List<PointRecordDO> tmpPointRecordDOS = new ArrayList<>();
                                    if (pointRecordDOS != null && pointRecordDOS.size() > 0) {
                                        //???????????????
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
                                            //????????????
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
                                        log.info("???????????????????????????????????????:" + JSON.toJSONString(pointRecordDO) + "????????????:" + JSON.toJSONString(tmpPointRecordDOS));
                                        for (PointRecordDO p : tmpPointRecordDOS
                                        ) {
                                            pointRecordRepository.update(p);
                                            pointRecordESRepository.save(p);
                                        }
                                    }
                                    //????????????????????????
                                    if (pointProductDO.getProductType().equals(5)) {
                                        PointMessageDO pointMessageDO = new PointMessageDO();
                                        pointMessageDO.setUid(pointOrderDO.getUid());
                                        pointMessageDO.setMsgType(Integer.parseInt(MessageTypeEnum.TYPE6.getCode()));
                                        pointMessageDO.setMsgContent(MessageFormat.format("?????????????????????-???{0}??????????????????????????????{1},{2},{3}????????????3-5????????????????????????????????????????????????????????????????????????10108688???", pointOrderDO.getProductTitle(), pointOrderDO.getExpressAddress(), pointOrderDO.getExpressMobileMask(), pointOrderDO.getEmNo()));
                                        pointMessageDO.setMsgExt(String.valueOf(pointOrderDO.getOrderNo()));
                                        pointMessageDO.setCreateTime(new Date());
                                        int retMessage = pointMessageService.insert(pointMessageDO);
                                        if (retMessage > 0) {
                                            log.info("------------------?????????????????????????????????uid=" + pointOrderDO.getUid() + ", orderno = " + pointOrderDO.getOrderNo());
                                        }
                                    }
                                    //???????????????
                                    if (pointProductDO.getProductType().equals(2)) {
                                        List<QueryCouponActivityVO> queryCouponActivityVOS = logisticsService.getCouponRulesByAcCode(pointProductDO.getActivityCode());
                                        if (queryCouponActivityVOS != null && queryCouponActivityVOS.size() > 0) {
                                            SendCouponDTO sendCouponDTO = new SendCouponDTO();
                                            sendCouponDTO.setPRESENT_ACCOUNT_TYPE(2);
                                            sendCouponDTO.setPRESENT_ACCOUNT(pointOrderDO.getMobile());
                                            sendCouponDTO.setCOUPON_ACTIVITY_ID(pointProductDO.getActivityCode());
                                            sendCouponDTO.setCOUPON_RULE_PRICE(queryCouponActivityVOS.get(0).getCOUPON_RULE_PRICE());
                                            sendCouponDTO.setPRESENT_PERSON("????????????");
                                            log.info("?????????????????????,??????:" + JSON.toJSONString(sendCouponDTO));
                                            Boolean resSendCoupon = logisticsService.SendCoupon(sendCouponDTO);
                                            if (resSendCoupon) {
                                                pointOrderDO.setIsSend(true);
                                                pointOrderDO.setUpdateTime(new Date());
                                                pointOrderRepository.update(pointOrderDO);
                                                log.info("?????????????????????,??????:" + JSON.toJSONString(pointProductDO) + "??????:" + JSON.toJSONString(pointOrderDO));
                                            } else {
                                                log.info("?????????????????????,??????:" + JSON.toJSONString(pointProductDO) + "??????:" + JSON.toJSONString(pointOrderDO));
                                            }
                                        } else {
                                            log.warn("?????????????????????,??????:" + JSON.toJSONString(pointProductDO) + "??????:" + JSON.toJSONString(pointOrderDO));
                                        }
                                    }
                                    //??????????????????????????????
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
                                        log.info("??????????????????,??????:" + JSON.toJSONString(sendPrivilegeDTO));
                                        Boolean resultSenddPrivilege = logisticsService.SenddPrivilege(sendPrivilegeDTO);
                                        if (resultSenddPrivilege) {
                                            pointOrderDO.setIsSend(true);
                                            pointOrderDO.setUpdateTime(new Date());
                                            pointOrderRepository.update(pointOrderDO);
                                            log.info("??????????????????,??????:" + JSON.toJSONString(pointProductDO) + "??????:" + JSON.toJSONString(pointOrderDO));
                                        } else {
                                            log.info("??????????????????,??????:" + JSON.toJSONString(pointProductDO) + "??????:" + JSON.toJSONString(pointOrderDO));
                                        }
                                    }
                                } catch (Exception e) {
                                    log.error("??????????????????,??????:" + JSON.toJSONString(pointProductDO) + "??????:" + JSON.toJSONString(pointOrderDO), e);
                                }
                            }, executor);
                        }
                        return buildSuccessResult(pointOrderDO);
                    }
                    return buildErrorResult("??????????????????");
                } else {
                    return buildErrorResult(BaseResultCodeEnum.ILLEGAL_ARGUMENT.getCode(), "???????????????");
                }
            }
        } catch (Exception e) {
            log.error("exanchangeByPoint error:", e);
            return buildErrorResult(e.getMessage());
        } finally {
            redissonDistributionLock.unlock(lockKey);
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
     * ????????????
     *
     * @param uid
     * @param productQty
     * @param pointProductDO
     * @return
     */
    private String checkPointOrder(long uid, int productQty, String emNo, String mobile, PointProductDO pointProductDO) {
        String ret = "";
        //??????????????????????????????
        int curQty = 0;
        //?????????????????????
        int totalQty = 0;
        //???????????????????????????
        float curPoint = 0;
        //??????????????????????????????
        float totalPoint = 0;

        //?????????????????????????????? ??????+????????????
        if (pointProductDO != null && !StringUtils.isEmpty(pointProductDO.getActivityCode()) && pointProductDO.getExchangeType().equals(1)) {
            CheckWebOrderDTO checkWebOrderDTO = new CheckWebOrderDTO();
            checkWebOrderDTO.setAccount(emNo);
            checkWebOrderDTO.setActivityCode(pointProductDO.getActivityCode());
            checkWebOrderDTO.setMID(mobile);
            if (!logisticsService.checkWebOrder(checkWebOrderDTO)) {
                return "?????????????????????????????????????????????????????????";
            }
        }

        List<PointOrderSummaryDO> pointOrderSummaryDOs = pointOrderRepository.getSummaryByProductId(pointProductDO.getId());
        PointOrderSummaryDO pointOrderSummaryDO = pointOrderSummaryDOs != null ? pointOrderSummaryDOs.stream().findFirst().orElse(null) : null;
        if (pointOrderSummaryDO != null) {
            totalQty = pointOrderSummaryDO.getTotalQty();
            //????????????
            if ((totalQty + productQty) > pointProductDO.getTotalLimit() * 0.9) {
                return "??????????????????";
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
                if (DateUtil.formatDate(pointOrderDO.getCreateTime()).equals(DateUtil.formatDate(DateUtil.date()))) {
                    curPoint += pointOrderDO.getPoint() * pointOrderDO.getProductQty();
                }
            }
        }
        if ((curQty + productQty) > pointProductDO.getPerLimit()) {
            return "????????????????????????";
        }
        if ((curPoint + (productQty * pointProductDO.getExchangePoint())) > totalPoint) {
            return "????????????,????????????";
        }
        PointLimitDO pointLimitDO = pointLimitRepository.getByType(Integer.valueOf(PointLimitTypeEnum.EXCHANGE.code()), Integer.valueOf(PointLimitToEnum.PERSONAL.code()));
        if (pointLimitDO != null) {
            if ((curPoint + (productQty * pointProductDO.getExchangePoint())) > pointLimitDO.getPointLimitvalue()) {
                //????????????
                CompletableFuture.runAsync(() -> {
                    try {
                        //????????????
                        String subject = "????????????????????????";
                        String userName = "";
                        List<UserInfoVO> userInfoVOS = userInfoService.getUserInfoByUid(uid);
                        if (userInfoVOS != null) {
                            UserInfoVO userInfoVO = userInfoVOS.stream().filter(h -> h.getAccountType() == 0).findFirst().orElse(null);
                            if (userInfoVO != null) {
                                userName = userInfoVO.getAccountName();
                            }
                        }
                        String content = MessageFormat.format("???????????????????????????ID???{0},???????????????{1},??????ID:{2},????????????:{3},????????????:{4}", uid, userName, pointProductDO.getId(), pointProductDO.getProductName(), new Date());
                        mailerService.sendSimpleTextMailActual(subject, content, toMailAddress.split(","), null, null, null);
                    } catch (Exception e) {
                        log.error("????????????????????????,sendSimpleTextMailActual error", e);
                    }

                }, executor);
                return "?????????????????????????????????????????????????????????";
            }
        }
        return "";
    }
}
