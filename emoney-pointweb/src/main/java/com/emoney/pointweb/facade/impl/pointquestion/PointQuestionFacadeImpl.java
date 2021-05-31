package com.emoney.pointweb.facade.impl.pointquestion;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.facade.pointquestion.PointQuestionFacade;
import com.emoeny.pointfacade.model.vo.PointProductVO;
import com.emoeny.pointfacade.model.vo.PointQuestionVO;
import com.emoney.pointweb.repository.PointQuestionRepository;
import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupDTO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupData;
import com.emoney.pointweb.repository.dao.entity.vo.CheckUserGroupVO;
import com.emoney.pointweb.service.biz.PointQuestionService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lipengcheng
 * @date 2021-05-07
 */
@RestController
@Slf4j
public class PointQuestionFacadeImpl implements PointQuestionFacade {

    @Autowired
    private PointQuestionRepository pointQuestionRepository;

    @Autowired
    private PointTaskConfigInfoService pointTaskConfigInfoService;

    @Override
    public Result<PointQuestionVO> queryPointQuestion(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "产品版本不能为空") String productVersion, @NotNull(message = "发布平台不能为空") String publishPlatFormType) {
        try {
            //设置开始时间
            Date startDate = DateUtil.parseDate("2021-05-05");
            Date nowDate = DateUtil.parseDate(DateUtil.today());

            List<PointQuestionDO> pointQuestionDOS = pointQuestionRepository.queryAll();
            List<PointQuestionDO> retPointQuestionList = new ArrayList<>();

            if (pointQuestionDOS != null) {
                pointQuestionDOS = pointQuestionDOS.stream().filter(h -> h.getProductVersion().contains(productVersion) && h.getPublishPlatFormType().contains(publishPlatFormType)).collect(Collectors.toList());
            }
            //接入用户画像
            if (pointQuestionDOS != null) {
                CheckUserGroupDTO checkUserGroupDTO = new CheckUserGroupDTO();
                List<CheckUserGroupData> checkUserGroupDataList = new ArrayList<>();
                CheckUserGroupData checkUserGroupData = null;
                for (PointQuestionDO pointQuestionDO : pointQuestionDOS
                ) {
                    if (!StringUtils.isEmpty(pointQuestionDO.getUserGroup())) {
                        for (String groupId : pointQuestionDO.getUserGroup().split(",")
                        ) {
                            checkUserGroupData = new CheckUserGroupData();
                            checkUserGroupData.setGroupId(Integer.valueOf(groupId));
                            checkUserGroupData.setCheckResult(false);
                            checkUserGroupDataList.add(checkUserGroupData);
                        }
                    } else {
                        retPointQuestionList.add(pointQuestionDO);
                    }
                }
                checkUserGroupDTO.setUid(String.valueOf(uid));
                checkUserGroupDTO.setUserGroupList(checkUserGroupDataList);
                CheckUserGroupVO checkUserGroupVO = pointTaskConfigInfoService.getUserGroupCheckUser(checkUserGroupDTO);
                if (checkUserGroupVO != null && checkUserGroupVO.getUserGroupList() != null && checkUserGroupVO.getUserGroupList().size() > 0) {
                    for (PointQuestionDO pointQuestionDO : pointQuestionDOS
                    ) {
                        if (!StringUtils.isEmpty(pointQuestionDO.getUserGroup())) {
                            for (String groupId : pointQuestionDO.getUserGroup().split(",")) {
                                if (checkUserGroupVO.getUserGroupList().stream().filter(h -> h.getGroupId().equals(Integer.valueOf(groupId)) && h.getCheckResult()).count() > 0) {
                                    retPointQuestionList.add(pointQuestionDO);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            List<PointQuestionVO> pointQuestionVOS = JsonUtil.copyList(retPointQuestionList, PointQuestionVO.class);
            PointQuestionVO result = pointQuestionVOS.stream().sorted(Comparator.comparing(PointQuestionVO::getId).reversed()).collect(Collectors.toList()).get(0);

            //获取有时间的集合
            List<PointQuestionVO> hasDateList = pointQuestionVOS.stream().filter(x -> x.getShowTime() != null).collect(Collectors.toList());
            //获取没有时间的集合
            List<PointQuestionVO> noHasDateList = pointQuestionVOS.stream().filter(x -> x.getShowTime() == null).collect(Collectors.toList());

            if (hasDateList.stream().anyMatch(x -> x.getShowTime().equals(nowDate))) {
                result = hasDateList.stream().filter(x -> x.getShowTime().equals(nowDate)).sorted(Comparator.comparing(PointQuestionVO::getUpdateTime)).findFirst().get();
            } else {
                if (noHasDateList.size() > 0) {
                    result = noHasDateList.get((int) DateUtil.between(startDate, nowDate, DateUnit.DAY) % noHasDateList.size());
                }
            }

            return Result.buildSuccessResult(result);
        } catch (Exception e) {
            log.error("queryPointQuestion error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }

    @Override
    public Result<PointQuestionVO> queryPointQuestionById(Integer id) {
        try {
            PointQuestionDO pointQuestionDOS = pointQuestionRepository.queryAllById(id);
            PointQuestionVO pointQuestionVOS = JsonUtil.toBean(JSON.toJSONString(pointQuestionDOS), PointQuestionVO.class);

            return Result.buildSuccessResult(pointQuestionVOS);
        } catch (Exception e) {
            log.error("queryPointQuestionById error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
