package com.emoney.pointweb.service.biz.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.constants.RedisConstants;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.result.ResultInfo;
import com.emoeny.pointcommon.result.ReturnInfo;
import com.emoeny.pointcommon.result.userperiod.Software;
import com.emoeny.pointcommon.result.userperiod.UserPeriodResult;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoeny.pointfacade.model.vo.PointOrderVO;
import com.emoney.pointweb.repository.PointTaskConfigInfoRepository;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupDTO;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupData;
import com.emoney.pointweb.repository.dao.entity.vo.CheckUserGroupVO;
import com.emoney.pointweb.repository.dao.entity.vo.PointTaskConfigInfoVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserGroupVO;
import com.emoney.pointweb.repository.dao.mapper.PointTaskConfigInfoMapper;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PointTaskConfigInfoServiceImpl implements PointTaskConfigInfoService {

    @Autowired
    private PointTaskConfigInfoMapper pointTaskConfigInfoMapper;

    @Autowired
    private PointTaskConfigInfoRepository pointTaskConfigInfoRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private RedisService redisCache1;

    @Override
    public List<PointTaskConfigInfoDO> getPointTaskConfigInfoByOrderAndType(int task_type, int task_order) {
        List<PointTaskConfigInfoDO> list = pointTaskConfigInfoMapper.getPointTaskConfigInfoByOrderAndType(task_type, task_order);
        return list;
    }

    @Override
    public List<PointTaskConfigInfoDO> getPointTaskConfigInfoByIsDirectional() {
        List<PointTaskConfigInfoDO> list = pointTaskConfigInfoMapper.getPointTaskConfigInfoByIsDirectional(1);
        return list;
    }

    @Override
    public List<PointTaskConfigInfoDO> getAllEffectiveTasks(Date curDate, Long uid, String productVersion, String publishPlatFormType) {
        List<PointTaskConfigInfoDO> retPointTaskConfigInfoList = new ArrayList<>();
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoRepository.getAllEffectiveTasks(new Date());
        if (pointTaskConfigInfoDOS != null) {
            pointTaskConfigInfoDOS = pointTaskConfigInfoDOS.stream().filter(h -> h.getProductVersion().contains(productVersion) && h.getPublishPlatFormType().contains(publishPlatFormType)).collect(Collectors.toList());
        }
        //接入用户画像
        if (pointTaskConfigInfoDOS != null) {
            CheckUserGroupDTO checkUserGroupDTO = new CheckUserGroupDTO();
            List<CheckUserGroupData> checkUserGroupDataList = new ArrayList<>();
            CheckUserGroupData checkUserGroupData = null;
            for (PointTaskConfigInfoDO pointTaskConfigInfoDO : pointTaskConfigInfoDOS
            ) {
                if (!StringUtils.isEmpty(pointTaskConfigInfoDO.getUserGroup())) {
                    for (String groupId : pointTaskConfigInfoDO.getUserGroup().split(",")
                    ) {
                        checkUserGroupData = new CheckUserGroupData();
                        checkUserGroupData.setGroupId(Integer.valueOf(groupId));
                        checkUserGroupData.setCheckResult(false);
                        checkUserGroupDataList.add(checkUserGroupData);
                    }
                } else {
                    retPointTaskConfigInfoList.add(pointTaskConfigInfoDO);
                }
            }
            checkUserGroupDTO.setUid(String.valueOf(uid));
            checkUserGroupDTO.setUserGroupList(checkUserGroupDataList);
            CheckUserGroupVO checkUserGroupVO = userInfoService.getUserGroupCheckUser(checkUserGroupDTO);
            if (checkUserGroupVO != null && checkUserGroupVO.getUserGroupList() != null && checkUserGroupVO.getUserGroupList().size() > 0) {
                for (PointTaskConfigInfoDO pointTaskConfigInfoDO : pointTaskConfigInfoDOS
                ) {
                    if (!StringUtils.isEmpty(pointTaskConfigInfoDO.getUserGroup())) {
                        for (String groupId : pointTaskConfigInfoDO.getUserGroup().split(",")) {
                            if (checkUserGroupVO.getUserGroupList().stream().filter(h -> h.getGroupId().equals(Integer.valueOf(groupId)) && h.getCheckResult()).count() > 0) {
                                retPointTaskConfigInfoList.add(pointTaskConfigInfoDO);
                                break;
                            }
                        }
                    }
                }
            }
        }

        //处理链接
        if (retPointTaskConfigInfoList != null) {
            for (PointTaskConfigInfoDO pointTaskConfigInfoDO : retPointTaskConfigInfoList
            ) {
                if (!StringUtils.isEmpty(pointTaskConfigInfoDO.getPcRedirectUrl()) && pointTaskConfigInfoDO.getPcRedirectUrl().contains("http") && !pointTaskConfigInfoDO.getPcRedirectUrl().contains("source=point")) {
                    if (pointTaskConfigInfoDO.getPcRedirectUrl().contains("?")) {
                        pointTaskConfigInfoDO.setPcRedirectUrl(pointTaskConfigInfoDO.getPcRedirectUrl() + "&source=point");
                    } else {
                        pointTaskConfigInfoDO.setPcRedirectUrl(pointTaskConfigInfoDO.getPcRedirectUrl() + "?source=point");
                    }
                }
            }
        }

        //处理新手任务
        UserPeriodResult userPeriodResult = userInfoService.getUserPeriod(uid);
        if (userPeriodResult != null && userPeriodResult.getData() != null && userPeriodResult.getData().getSoftware() != null
        ) {
            Software software = JSON.parseObject(userPeriodResult.getData().getSoftware(), Software.class);
            if (software != null && !StringUtils.isEmpty(software.getStartDate()) && !StringUtils.isEmpty(software.getEndDate())
            ) {
                for (PointTaskConfigInfoDO pointTaskConfigInfoDO : retPointTaskConfigInfoList
                ) {
                    if (pointTaskConfigInfoDO.getTaskId().equals(1380422772903251968L) || pointTaskConfigInfoDO.getTaskId().equals(1380424279476277248L)) {
                        Date userPeroidStartDate = DateUtil.parse(software.getStartDate().replace("T", " "), "yyyy-MM-dd HH:mm:ss");
                        if (userPeroidStartDate.before(pointTaskConfigInfoDO.getTaskStartTime())) {
                            if (retPointTaskConfigInfoList.stream().filter(h -> h.getTaskId().equals(pointTaskConfigInfoDO.getTaskId())).count() > 0) {
                                retPointTaskConfigInfoList.remove(pointTaskConfigInfoDO);
                            }
                        }
                    }
                }
            }
        }


        return retPointTaskConfigInfoList;
    }

    @Override
    public Map<String, Object> pageList(Integer start, Integer length, Integer task_type, Integer task_status, String ver, String plat) {
        List<PointTaskConfigInfoDO> list = pointTaskConfigInfoMapper.pageList(start, length, task_type);
        switch (task_status) {
            case 1:
                list = list.stream().filter(x -> x.getTaskStartTime().before(new Date()) && x.getTaskEndTime().after(new Date())).collect(Collectors.toList());
                break;
            case 2:
                list = list.stream().filter(x -> x.getTaskStartTime().after(new Date())).collect(Collectors.toList());
                break;
            case 3:
                list = list.stream().filter(x -> x.getTaskEndTime().before(new Date())).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(ver)) {
            list = list.stream().filter(x -> x.getProductVersion().contains(ver)).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(plat)) {
            list = list.stream().filter(x -> x.getPublishPlatFormType().contains(plat)).collect(Collectors.toList());
        }
        List<PointTaskConfigInfoVO> data = JsonUtil.copyList(list, PointTaskConfigInfoVO.class);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("data", data);                    // 分页列表
        return maps;
    }

    @Override
    public int insert(PointTaskConfigInfoDO pointTaskConfigInfoDO) {

        //任务编辑或者新增将缓存删除
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointTaskConfigInfo_GETBYTASKID, pointTaskConfigInfoDO.getTaskId(), pointTaskConfigInfoDO.getSubId()));
        redisCache1.remove(RedisConstants.REDISKEY_PointTaskConfigInfo_GETALLEFFECTIVETASKS);
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointTaskConfigInfo_GETTASKSBYTASKTYPE, pointTaskConfigInfoDO.getTaskType()));
        return pointTaskConfigInfoMapper.insert(pointTaskConfigInfoDO);
    }

    @Override
    public int update(PointTaskConfigInfoDO pointTaskConfigInfoDO) {
        //任务编辑或者新增将缓存删除
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointTaskConfigInfo_GETBYTASKID, pointTaskConfigInfoDO.getTaskId(), pointTaskConfigInfoDO.getSubId()));
        redisCache1.remove(RedisConstants.REDISKEY_PointTaskConfigInfo_GETALLEFFECTIVETASKS);
        redisCache1.remove(MessageFormat.format(RedisConstants.REDISKEY_PointTaskConfigInfo_GETTASKSBYTASKTYPE, pointTaskConfigInfoDO.getTaskType()));
        return pointTaskConfigInfoMapper.update(pointTaskConfigInfoDO);
    }

    @Override
    public List<PointTaskConfigInfoDO> getByTaskIdAndSubId(Long taskId, String subId, Date curDate) {
        return pointTaskConfigInfoRepository.getByTaskIdAndSubId(taskId, subId, curDate);
    }

    @Override
    public List<PointTaskConfigInfoDO> getTasksByTaskType(int taskType, Long uid, String productVersion, String publishPlatFormType) {
        List<PointTaskConfigInfoDO> retPointTaskConfigInfoList = new ArrayList<>();
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoRepository.getTasksByTaskType(taskType);
        if (pointTaskConfigInfoDOS != null) {
            pointTaskConfigInfoDOS = pointTaskConfigInfoDOS.stream().filter(h -> h.getProductVersion().contains(productVersion) && h.getPublishPlatFormType().contains(publishPlatFormType)).collect(Collectors.toList());
        }
        //接入用户画像
        if (pointTaskConfigInfoDOS != null) {
            CheckUserGroupDTO checkUserGroupDTO = new CheckUserGroupDTO();
            List<CheckUserGroupData> checkUserGroupDataList = new ArrayList<>();
            CheckUserGroupData checkUserGroupData = null;
            for (PointTaskConfigInfoDO pointTaskConfigInfoDO : pointTaskConfigInfoDOS
            ) {
                if (!StringUtils.isEmpty(pointTaskConfigInfoDO.getUserGroup())) {
                    for (String groupId : pointTaskConfigInfoDO.getUserGroup().split(",")
                    ) {
                        checkUserGroupData = new CheckUserGroupData();
                        checkUserGroupData.setGroupId(Integer.valueOf(groupId));
                        checkUserGroupData.setCheckResult(false);
                        checkUserGroupDataList.add(checkUserGroupData);
                    }
                } else {
                    retPointTaskConfigInfoList.add(pointTaskConfigInfoDO);
                }
            }
            checkUserGroupDTO.setUid(String.valueOf(uid));
            checkUserGroupDTO.setUserGroupList(checkUserGroupDataList);
            CheckUserGroupVO checkUserGroupVO = userInfoService.getUserGroupCheckUser(checkUserGroupDTO);
            if (checkUserGroupVO != null && checkUserGroupVO.getUserGroupList() != null && checkUserGroupVO.getUserGroupList().size() > 0) {
                for (PointTaskConfigInfoDO pointTaskConfigInfoDO : pointTaskConfigInfoDOS
                ) {
                    if (!StringUtils.isEmpty(pointTaskConfigInfoDO.getUserGroup())) {
                        for (String groupId : pointTaskConfigInfoDO.getUserGroup().split(",")) {
                            if (checkUserGroupVO.getUserGroupList().stream().filter(h -> h.getGroupId().equals(Integer.valueOf(groupId)) && h.getCheckResult()).count() > 0) {
                                retPointTaskConfigInfoList.add(pointTaskConfigInfoDO);
                                break;
                            }
                        }
                    }
                }
            }
        }

        //处理链接
        if (retPointTaskConfigInfoList != null) {
            for (PointTaskConfigInfoDO pointTaskConfigInfoDO : retPointTaskConfigInfoList
            ) {
                if (!StringUtils.isEmpty(pointTaskConfigInfoDO.getPcRedirectUrl()) && pointTaskConfigInfoDO.getPcRedirectUrl().contains("http") && !pointTaskConfigInfoDO.getPcRedirectUrl().contains("source=point")) {
                    if (pointTaskConfigInfoDO.getPcRedirectUrl().contains("?")) {
                        pointTaskConfigInfoDO.setPcRedirectUrl(pointTaskConfigInfoDO.getPcRedirectUrl() + "&source=point");
                    } else {
                        pointTaskConfigInfoDO.setPcRedirectUrl(pointTaskConfigInfoDO.getPcRedirectUrl() + "?source=point");
                    }
                }
            }
        }
        return retPointTaskConfigInfoList;
    }

    @Override
    public List<PointTaskConfigInfoDO> getByTaskIds(List<Long> listTaskIds) {
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = new ArrayList<>();
        for (Long taskId : listTaskIds
        ) {
            List<PointTaskConfigInfoDO> tmp = pointTaskConfigInfoRepository.getByTaskIdAndSubId(taskId, null, new Date());
            if (tmp != null) {
                pointTaskConfigInfoDOS.addAll(tmp);
            }
        }
        return pointTaskConfigInfoDOS;
    }

}
