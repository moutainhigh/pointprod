package com.emoney.pointweb.service.biz.impl;

import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.result.ResultInfo;
import com.emoeny.pointcommon.result.ReturnInfo;
import com.emoeny.pointcommon.result.userperiod.UserPeriodResult;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoney.pointweb.repository.dao.entity.dto.CheckUserGroupDTO;
import com.emoney.pointweb.repository.dao.entity.vo.CheckUserGroupVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserGroupVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.service.biz.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    @Value("${insideGatewayUrl}")
    private String insideGatewayUrl;

    @Value("${dsapiurl}")
    private String dsapiurl;

    @Value("${webApiUrl}")
    private String webApiUrl;

    @Override
    public List<UserInfoVO> getUserInfoByUid(Long uid) {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("gate_appid", "10150");
        stringMap.put("uid", String.valueOf(uid));
        String res = OkHttpUtil.get(insideGatewayUrl + "/api/roboadvisor/1.0/user.boundgroupqrylogin", stringMap);
        if (!StringUtils.isEmpty(res)) {
            String apiResult = JsonUtil.getValue(res, "Message");
            return JsonUtil.toBeanList(JsonUtil.getValue(apiResult, "Message"), UserInfoVO.class);
        }
        return null;
    }

    @Override
    public String getUidByEmNo(String emNo) {
        Map<String, String> stringMap = new HashMap<>();
        stringMap.put("gate_appid", "10015");
        stringMap.put("userName", emNo);
        stringMap.put("createLogin", "0");
        //#????????????em or ?????????????????????uid
        String res = OkHttpUtil.get(insideGatewayUrl + "/api/roboadvisor/1.0/user.getloginidbyname", stringMap);
        String apiResult = JsonUtil.getValue(res, "Message");
        String message = JsonUtil.getValue(apiResult, "Message");
        return JsonUtil.getValue(message, "PID");
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
    public String getPidByUserId(Long uid) {
        String pid = null;
        String userName = "";
        List<UserInfoVO> userInfoVOS = getUserInfoByUid(uid);
        if (userInfoVOS != null) {
            UserInfoVO userInfoVO = userInfoVOS.stream().filter(h -> h.getAccountType() == 0).findFirst().orElse(null);
            if (userInfoVO != null) {
                userName = userInfoVO.getAccountName();
            }
        }
        if (!StringUtils.isEmpty(userName)) {
            Map<String, String> stringMap = new HashMap<>();
            stringMap.put("appid", "10199");
            stringMap.put("username", userName);
            String res = OkHttpUtil.get(webApiUrl + "/User/api/User.GetAccountPID", stringMap);
            if (!StringUtils.isEmpty(res)) {
                return JsonUtil.getValue(res, "Message");
            }
        }
        return null;
    }

    @Override
    public List<UserGroupVO> getUserGroupList() {
        try {
            List<UserGroupVO> userGroupVOList = new ArrayList<>();
            String url = "http://api.userradar.emoney.cn/api/GetUserGroupList";
            String res = OkHttpUtil.get(url, null);
            ReturnInfo<List<UserGroupVO>> resultInfo = JsonUtil.toBean(res, ReturnInfo.class);
            if (resultInfo.getRetCode().equals("0")) {
                userGroupVOList = JsonUtil.toBeanList(resultInfo.getData() != null ? resultInfo.getData().toString() : "", UserGroupVO.class);
            }
            return userGroupVOList;
        } catch (Exception e) {
            log.error("getUserGroupList error", e);
        }
        return null;
    }

    @Override
    public CheckUserGroupVO getUserGroupCheckUser(CheckUserGroupDTO checkUserGroupDTO) {
        try {
            String url = "http://api.userradar.emoney.cn/api/CheckUserGroup";
            String ret = OkHttpUtil.postJsonParams(url, JSON.toJSONString(checkUserGroupDTO));
            if (!StringUtils.isEmpty(ret)) {
                ReturnInfo<CheckUserGroupVO> resultInfo = JsonUtil.toBean(ret, ReturnInfo.class);
                if (resultInfo.getRetCode().equals("0")) {
                    return JsonUtil.toBean(JSON.toJSONString(resultInfo.getData()), CheckUserGroupVO.class);
                }
            }
        } catch (Exception e) {
            log.error("getUserGroupCheckUser error", e);
        }
        return null;
    }
}
