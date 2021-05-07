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
    private RedisService redisCache1;

    @Value("${dsapiurl}")
    private String dsapiurl;

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
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoRepository.getAllEffectiveTasks(new Date());
        if (pointTaskConfigInfoDOS != null) {
            pointTaskConfigInfoDOS = pointTaskConfigInfoDOS.stream().filter(h -> h.getProductVersion().contains(productVersion) && h.getProductVersion().contains(publishPlatFormType)).collect(Collectors.toList());
        }
        //接入用户画像
        return pointTaskConfigInfoDOS;
    }

    @Override
    public Map<String, Object> pageList(int start, int length, int task_type) {
        List<PointTaskConfigInfoDO> list = pointTaskConfigInfoMapper.pageList(start, length, task_type);

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
    public UserPeriodResult getUserPeriod(long uid) {
        String url = MessageFormat.format("{0}/saas/userperiod?uid={1}", dsapiurl, String.valueOf(uid));
        String ret = OkHttpUtil.get(url, null);
        if (!StringUtils.isEmpty(ret)) {
            UserPeriodResult userPeriodResult = JSON.parseObject(ret, UserPeriodResult.class);
            return userPeriodResult;
        }
        return null;
    }

    @Override
    public List<PointTaskConfigInfoDO> getByTaskIdAndSubId(Long taskId, String subId) {
        return pointTaskConfigInfoRepository.getByTaskIdAndSubId(taskId, subId);
    }

    @Override
    public List<PointTaskConfigInfoDO> getTasksByTaskType(int taskType, Long uid, String productVersion, String publishPlatFormType) {
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = pointTaskConfigInfoRepository.getTasksByTaskType(taskType);
        if (pointTaskConfigInfoDOS != null) {
            pointTaskConfigInfoDOS = pointTaskConfigInfoDOS.stream().filter(h -> h.getProductVersion().contains(productVersion) && h.getPublishPlatFormType().contains(publishPlatFormType)).collect(Collectors.toList());
        }
        //接入用户生命周期
        List<PointTaskConfigInfoDO> retPointTaskConfigInfoDOs = new ArrayList<>();
        try {
            UserPeriodResult userPeriodResult = getUserPeriod(uid);
            if (userPeriodResult != null
                    && userPeriodResult.getData() != null
                    && userPeriodResult.getData().getSoftware() != null
            ) {
                Software software = JSON.parseObject(userPeriodResult.getData().getSoftware(), Software.class);
                if (software != null && !StringUtils.isEmpty(software.getStartDate())
                        && !StringUtils.isEmpty(software.getEndDate())
                ) {
                    Date userPeroidStartDate = DateUtil.parse(software.getStartDate().replace("T", " "), "yyyy-MM-dd HH:mm:ss");
                    Date userPeroidEndDate = DateUtil.parse(software.getEndDate().replace("T", " "), "yyyy-MM-dd HH:mm:ss");
                    for (PointTaskConfigInfoDO p : pointTaskConfigInfoDOS
                    ) {
                        if (p.getActivationStartTime() != null && p.getActivationEndTime() != null && p.getExpireStartTime() != null && p.getExpireEndTime() != null) {
                            if (userPeroidStartDate.after(p.getActivationStartTime())
                                    && userPeroidStartDate.before(p.getActivationEndTime())
                                    && userPeroidEndDate.after(p.getExpireStartTime())
                                    && userPeroidEndDate.before(p.getExpireEndTime())
                            ) {
                                retPointTaskConfigInfoDOs.add(p);
                            }
                        } else if (p.getActivationStartTime() != null && p.getActivationEndTime() != null) {
                            if (userPeroidStartDate.after(p.getActivationStartTime())
                                    && userPeroidStartDate.before(p.getActivationEndTime())
                            ) {
                                retPointTaskConfigInfoDOs.add(p);
                            }
                        } else if (p.getExpireStartTime() != null && p.getExpireEndTime() != null) {
                            if (userPeroidEndDate.after(p.getExpireStartTime())
                                    && userPeroidEndDate.before(p.getExpireEndTime())
                            ) {
                                retPointTaskConfigInfoDOs.add(p);
                            }
                        } else {
                            retPointTaskConfigInfoDOs.add(p);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("queryPointTaskConfigs getUserPeriod error:", e);
        }
        return retPointTaskConfigInfoDOs;
    }

    @Override
    public List<PointTaskConfigInfoDO> getByTaskIds(List<Long> listTaskIds) {
        List<PointTaskConfigInfoDO> pointTaskConfigInfoDOS = new ArrayList<>();
        for (Long taskId : listTaskIds
        ) {
            List<PointTaskConfigInfoDO> tmp = pointTaskConfigInfoRepository.getByTaskIdAndSubId(taskId, null);
            if (tmp != null) {
                pointTaskConfigInfoDOS.addAll(tmp);
            }
        }
        return pointTaskConfigInfoDOS;
    }

    @Override
    public List<UserGroupVO> getUserGroupList() {
        List<UserGroupVO> userGroupVOList = new ArrayList<>();
        String url = "http://api.userradar.emoney.cn/api/GetUserGroupList";
        String res = OkHttpUtil.get(url, null);
        ReturnInfo<List<UserGroupVO>> resultInfo = JsonUtil.toBean(res, ReturnInfo.class);
        if (resultInfo.getRetCode().equals("0")) {
            userGroupVOList = JsonUtil.toBeanList(resultInfo.getData() != null ? resultInfo.getData().toString() : "", UserGroupVO.class);
            ;
        }
        return userGroupVOList;
    }

    @Override
    public CheckUserGroupVO getUserGroupCheckUser(CheckUserGroupDTO checkUserGroupDTO) {
        String url = "http://api.userradar.emoney.cn/api/CheckUserGroup";
        String ret = OkHttpUtil.postJsonParams(url, JSON.toJSONString(checkUserGroupDTO));
        if (!StringUtils.isEmpty(ret)) {
            ReturnInfo<CheckUserGroupVO> resultInfo = JsonUtil.toBean(ret, ReturnInfo.class);
            if (resultInfo.getRetCode().equals("0")) {
                return JsonUtil.toBean(JSON.toJSONString(resultInfo.getData()), CheckUserGroupVO.class);
            }
        }
        return null;
    }
}
