package com.emoney.pointweb.facade.impl.pointmessage;

import cn.hutool.core.date.DateUtil;
import com.emoeny.pointcommon.enums.MessageTypeEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.facade.pointmessage.PointMessageFacade;
import com.emoeny.pointfacade.model.vo.PointMessageVO;
import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;
import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupDTO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupData;
import com.emoney.pointweb.repository.dao.entity.vo.CheckUserGroupVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.service.biz.PointAnnounceService;
import com.emoney.pointweb.service.biz.PointFeedBackService;
import com.emoney.pointweb.service.biz.PointMessageService;
import com.emoney.pointweb.service.biz.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/12 15:37
 */
@RestController
@Validated
@Slf4j
public class PointMessageFacadeImpl implements PointMessageFacade {

    @Autowired
    private PointMessageService pointMessageService;

    @Autowired
    private PointAnnounceService pointAnnounceService;

    @Autowired
    private PointFeedBackService pointFeedBackService;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public Result<List<PointMessageVO>> queryPointMessages(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "产品版本不能为空") String productVersion, @NotNull(message = "查询类型不能为空") Integer queryType) {
        try {
            List<PointMessageVO> pointMessageVOS = new ArrayList<>();
            List<PointAnnounceDO> retPointAnnounceDOList = new ArrayList<>();
            PointMessageVO pointMessageVO = null;
            List<Integer> mstTypes = new ArrayList<>();
            //即将到期,待支付
            if (queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE0.getCode())) || queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE1.getCode())) || queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE3.getCode()))) {
                pointMessageVOS = JsonUtil.copyList(pointMessageService.getByUid(uid, DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -90))), PointMessageVO.class);
                if (pointMessageVOS != null) {
                    if (!queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE0.getCode()))) {
                        pointMessageVOS = pointMessageVOS.stream().filter(h -> h.getMsgType().equals(queryType)).collect(Collectors.toList());
                    }
                }
            }
            //商品上新，活动上新,通知
            if (queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE0.getCode())) || queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE2.getCode())) || queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE4.getCode()))) {
                if (queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE0.getCode()))) {
                    mstTypes.add(Integer.valueOf(MessageTypeEnum.TYPE2.getCode()));
                    mstTypes.add(Integer.valueOf(MessageTypeEnum.TYPE4.getCode()));
                    mstTypes.add(5);
                } else if (queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE2.getCode()))) {
                    mstTypes.add(Integer.valueOf(MessageTypeEnum.TYPE2.getCode()));
                } else if (queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE4.getCode()))) {
                    mstTypes.add(Integer.valueOf(MessageTypeEnum.TYPE4.getCode()));
                }
                List<PointAnnounceDO> pointAnnounceDOS = pointAnnounceService.getPointAnnouncesByType(mstTypes, DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -90)));
                if (pointAnnounceDOS != null) {
                    pointAnnounceDOS = pointAnnounceDOS.stream().filter(h -> h.getProductVersion()!=null&&h.getProductVersion().contains(productVersion)).collect(Collectors.toList());
                    //接入用户画像
                    if (pointAnnounceDOS != null) {
                        CheckUserGroupDTO checkUserGroupDTO = new CheckUserGroupDTO();
                        List<CheckUserGroupData> checkUserGroupDataList = new ArrayList<>();
                        CheckUserGroupData checkUserGroupData = null;
                        for (PointAnnounceDO pointAnnounceDO : pointAnnounceDOS
                        ) {
                            if (!org.springframework.util.StringUtils.isEmpty(pointAnnounceDO.getUserGroup())) {
                                for (String groupId : pointAnnounceDO.getUserGroup().split(",")
                                ) {
                                    checkUserGroupData = new CheckUserGroupData();
                                    checkUserGroupData.setGroupId(Integer.valueOf(groupId));
                                    checkUserGroupData.setCheckResult(false);
                                    checkUserGroupDataList.add(checkUserGroupData);
                                }
                            } else {
                                retPointAnnounceDOList.add(pointAnnounceDO);
                            }
                        }
                        checkUserGroupDTO.setUid(String.valueOf(uid));
                        checkUserGroupDTO.setUserGroupList(checkUserGroupDataList);
                        CheckUserGroupVO checkUserGroupVO = userInfoService.getUserGroupCheckUser(checkUserGroupDTO);
                        if (checkUserGroupVO != null && checkUserGroupVO.getUserGroupList() != null && checkUserGroupVO.getUserGroupList().size() > 0) {
                            for (PointAnnounceDO pointAnnounceDO : pointAnnounceDOS
                            ) {
                                if (!org.springframework.util.StringUtils.isEmpty(pointAnnounceDO.getUserGroup())) {
                                    for (String groupId : pointAnnounceDO.getUserGroup().split(",")) {
                                        if (checkUserGroupVO.getUserGroupList().stream().filter(h -> h.getGroupId().equals(Integer.valueOf(groupId)) && h.getCheckResult()).count() > 0) {
                                            retPointAnnounceDOList.add(pointAnnounceDO);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //查找导入的用户名单
                    List<PointAnnounceDO> mapperPointAnnounceDOS = pointAnnounceService.getPointAnnouncesByMapping(DateUtil.beginOfDay(DateUtil.offsetDay(DateUtil.date(), -90)), String.valueOf(uid));
                    if (mapperPointAnnounceDOS != null) {
                        for (PointAnnounceDO pointAnnounceDo : mapperPointAnnounceDOS
                        ) {
                            if (retPointAnnounceDOList.stream().filter(h -> h.getId().equals(pointAnnounceDo.getId())).count() == 0) {
                                retPointAnnounceDOList.add(pointAnnounceDo);
                            }
                        }
                    }


                    for (PointAnnounceDO p : retPointAnnounceDOList
                    ) {
                        pointMessageVO = new PointMessageVO();
                        pointMessageVO.setUid(uid);
                        pointMessageVO.setMsgType(p.getMsgType());
                        pointMessageVO.setMsgContent(p.getMsgContent());
                        pointMessageVO.setMsgSrc(p.getMsgSrc());
                        pointMessageVO.setCreateTime(p.getPublishTime());
                        pointMessageVOS.add(pointMessageVO);
                    }
                }
            }

            if (pointMessageVOS != null) {
                pointMessageVOS = pointMessageVOS.stream().sorted(Comparator.comparing(PointMessageVO::getCreateTime).reversed()).collect(Collectors.toList());
            }

            return Result.buildSuccessResult(pointMessageVOS);
        } catch (Exception e) {
            log.error("queryPointMessages error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
