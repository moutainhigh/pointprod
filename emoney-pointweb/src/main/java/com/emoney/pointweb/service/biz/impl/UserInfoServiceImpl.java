package com.emoney.pointweb.service.biz.impl;

import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoney.pointweb.repository.dao.entity.vo.UserInfoVO;
import com.emoney.pointweb.service.biz.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserInfoServiceImpl implements UserInfoService {

    @Value("${insideGatewayUrl}")
    private String insideGatewayUrl;

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
}
