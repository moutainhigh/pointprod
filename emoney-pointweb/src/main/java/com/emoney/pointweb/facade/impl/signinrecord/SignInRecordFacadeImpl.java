package com.emoney.pointweb.facade.impl.signinrecord;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.enums.BaseResultCodeEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.CollectionBeanUtils;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.facade.signinrecord.SignInRecordFacade;
import com.emoeny.pointfacade.model.dto.SignInRecordCreateDTO;
import com.emoeny.pointfacade.model.vo.PointQuestionVO;
import com.emoeny.pointfacade.model.vo.PointQuotationVO;
import com.emoeny.pointfacade.model.vo.SignInRecordVO;
import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;
import com.emoney.pointweb.repository.dao.entity.PointQuotationDO;
import com.emoney.pointweb.repository.dao.entity.SignInRecordDO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupDTO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupData;
import com.emoney.pointweb.repository.dao.entity.vo.CheckUserGroupVO;
import com.emoney.pointweb.service.biz.PointQuotationService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import com.emoney.pointweb.service.biz.SignInRecordService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.redis.RedissonDistributionLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DateUtil.date;

@RestController
@Validated
@Slf4j
public class SignInRecordFacadeImpl implements SignInRecordFacade {

    @Autowired
    private SignInRecordService signInRecordService;

    @Autowired
    private PointQuotationService pointQuotationService;

    @Autowired
    private PointTaskConfigInfoService pointTaskConfigInfoService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 分布式锁
     */
    @Resource
    private RedissonDistributionLock redissonDistributionLock;

    @Override
    public Result<Object> createSignInRecord(@RequestBody @Valid SignInRecordCreateDTO signInRecordCreateDTO) {
        String lockKey = MessageFormat.format(RedisConstants.REDISKEY_SignInRecord_CREATE_LOCKKEY, signInRecordCreateDTO.getUid());
        try {
            if (redissonDistributionLock.tryLock(lockKey, TimeUnit.SECONDS, 10, 10)) {
                return signInRecordService.createSignInRecord(signInRecordCreateDTO);
            }
            return Result.buildErrorResult(BaseResultCodeEnum.REPETITIVE_OPERATION.code(), BaseResultCodeEnum.REPETITIVE_OPERATION.getMsg());
        } catch (Exception e) {
            log.error("createSignInRecord error:", e);
            return Result.buildErrorResult(e.getMessage());
        } finally {
            redissonDistributionLock.unlock(lockKey);
        }
    }

    @Override
    public Result<List<SignInRecordVO>> querySignInRecord(@NotNull(message = "用户id不能为空") Long uid) {
        try {
            List<SignInRecordDO> signInRecordDOS = signInRecordService.getByUid(uid, DateUtil.parseDate("2021-01-01"));
            if (signInRecordDOS != null && signInRecordDOS.size() > 0) {
                signInRecordDOS.sort(Comparator.comparing(SignInRecordDO::getSignInTime).reversed());
                return Result.buildSuccessResult(CollectionBeanUtils.copyListProperties(signInRecordDOS, SignInRecordVO::new));
            }
            return Result.buildSuccessResult();

        } catch (Exception e) {
            log.error("querySignInRecord error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<String> querySignInRecordTips(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "产品版本不能为空") String productVersion, @NotNull(message = "发布平台不能为空") String publishPlatFormType) {
        try {
            //设置开始时间
            Date startDate = DateUtil.parseDate("2021-05-05");
            Date nowDate = DateUtil.parseDate(DateUtil.today());

            List<PointQuotationVO> pointQuotationVOS = JsonUtil.copyList(pointQuotationService.getAll(), PointQuotationVO.class);
            List<PointQuotationVO> retPointQuotationList = new ArrayList<>();

            if (pointQuotationVOS != null && pointQuotationVOS.size() > 0) {
                if (pointQuotationVOS != null) {
                    pointQuotationVOS = pointQuotationVOS.stream().filter(h -> h.getProductVersion().contains(productVersion) && h.getPublishPlatFormType().contains(publishPlatFormType)).collect(Collectors.toList());
                }

                //接入用户画像
                if (pointQuotationVOS != null) {
                    CheckUserGroupDTO checkUserGroupDTO = new CheckUserGroupDTO();
                    List<CheckUserGroupData> checkUserGroupDataList = new ArrayList<>();
                    CheckUserGroupData checkUserGroupData = null;
                    for (PointQuotationVO pointQuotationVO : pointQuotationVOS
                    ) {
                        if (!StringUtils.isEmpty(pointQuotationVO.getUserGroup())) {
                            for (String groupId : pointQuotationVO.getUserGroup().split(",")
                            ) {
                                checkUserGroupData = new CheckUserGroupData();
                                checkUserGroupData.setGroupId(Integer.valueOf(groupId));
                                checkUserGroupData.setCheckResult(false);
                                checkUserGroupDataList.add(checkUserGroupData);
                            }
                        } else {
                            retPointQuotationList.add(pointQuotationVO);
                        }
                    }
                    checkUserGroupDTO.setUid(String.valueOf(uid));
                    checkUserGroupDTO.setUserGroupList(checkUserGroupDataList);
                    CheckUserGroupVO checkUserGroupVO = userInfoService.getUserGroupCheckUser(checkUserGroupDTO);
                    if (checkUserGroupVO != null && checkUserGroupVO.getUserGroupList() != null && checkUserGroupVO.getUserGroupList().size() > 0) {
                        for (PointQuotationVO pointQuotationVO : pointQuotationVOS
                        ) {
                            if (!StringUtils.isEmpty(pointQuotationVO.getUserGroup())) {
                                for (String groupId : pointQuotationVO.getUserGroup().split(",")) {
                                    if (checkUserGroupVO.getUserGroupList().stream().filter(h -> h.getGroupId().equals(Integer.valueOf(groupId)) && h.getCheckResult()).count() > 0) {
                                        retPointQuotationList.add(pointQuotationVO);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            PointQuotationVO result = pointQuotationVOS.stream().sorted(Comparator.comparing(PointQuotationVO::getUpdateTime).reversed()).collect(Collectors.toList()).get(0);

            //获取有时间的集合
            List<PointQuotationVO> hasDateList = retPointQuotationList.stream().filter(x -> x.getShowTime() != null).collect(Collectors.toList());
            //获取没有时间的集合
            List<PointQuotationVO> noHasDateList = retPointQuotationList.stream().filter(x -> x.getShowTime() == null).collect(Collectors.toList());

            if (hasDateList.stream().anyMatch(x -> x.getShowTime().equals(nowDate))) {
                result = hasDateList.stream().filter(x -> x.getShowTime().equals(nowDate)).sorted(Comparator.comparing(PointQuotationVO::getUpdateTime).reversed()).findFirst().get();
            } else {
                if (noHasDateList.size() > 0) {
                    result = noHasDateList.get((int) DateUtil.between(startDate, nowDate, DateUnit.DAY) % noHasDateList.size());
                }
            }

            return Result.buildSuccessResult(result.getContent());
        } catch (Exception e) {
            log.error("querySignInRecordTips error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
