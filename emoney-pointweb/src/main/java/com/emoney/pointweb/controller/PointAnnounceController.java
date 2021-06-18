package com.emoney.pointweb.controller;

import com.alibaba.fastjson.JSONObject;
import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoeny.pointcommon.utils.ExcelUtils;
import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.repository.dao.entity.UserMessageMappingDO;
import com.emoney.pointweb.repository.dao.entity.vo.UserGroupVO;
import com.emoney.pointweb.service.biz.PointAnnounceService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.UserLoginService;
import com.emoney.pointweb.service.biz.kafka.KafkaProducerService;
import com.emoney.pointweb.service.biz.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author lipengcheng
 * @date 2021-04-13
 */
@Controller
@Slf4j
@RequestMapping("/pointannounce")
public class PointAnnounceController {

    @Resource
    private PointAnnounceService pointAnnounceService;

    @Resource
    private UserLoginService userLoginService;

    @Autowired
    private UserInfoService userInfoService;

    @Resource
    private PointTaskConfigInfoService pointTaskConfigInfoService;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Value("${pointmessageusername.topic}")
    private String pointmessageusernameTopic;

    @RequestMapping
    public String index(Model model) {
        List<UserGroupVO> userGroupVOList = userInfoService.getUserGroupList();
        model.addAttribute("userGroupVOList", userGroupVOList);
        return "/pointannounce/pointannounce.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(Integer msgType, String ver, String plat) {
        List<PointAnnounceDO> list = pointAnnounceService.getAll();
        if (msgType != null && !msgType.equals(0)) {
            list = list.stream().filter(x -> x.getMsgType().equals(msgType)).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(ver)) {
            list = list.stream().filter(x -> x.getProductVersion().contains(ver)).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(plat)) {
            list = list.stream().filter(x -> x.getPublishPlatFormType().contains(plat)).collect(Collectors.toList());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return result;
    }

    @RequestMapping("/edit")
    @ResponseBody
    public String edit(@RequestParam(required = false, defaultValue = "0") Integer id, Integer msgType, String msgContent,
                       String msgSrc, String productVersion, String publishTime, String remark, String plat, String groupList,
                       MultipartFile file, String account, String classType,
                       HttpServletRequest request, HttpServletResponse response) {
        try {
            TicketInfo user = userLoginService.getLoginAdminUser(request, response);
            if (user == null) {
                return "用户登录已过期，请重新登录";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            PointAnnounceDO pointAnnounceDO = new PointAnnounceDO();
            pointAnnounceDO.setMsgType(msgType);
            pointAnnounceDO.setMsgContent(msgContent);
            pointAnnounceDO.setMsgSrc(msgSrc);
            pointAnnounceDO.setProductVersion(productVersion);
            pointAnnounceDO.setPublishPlatFormType(plat);
            pointAnnounceDO.setUserGroup(groupList);
            if (!StringUtils.isEmpty(publishTime)) {
                pointAnnounceDO.setPublishTime(sdf.parse(publishTime));
            } else {
                pointAnnounceDO.setPublishTime(null);
            }
            pointAnnounceDO.setRemark(remark);
            pointAnnounceDO.setUpdateTime(new Date());
            pointAnnounceDO.setUpdateBy(user.UserName);
            Integer result = 0;
            if (id > 0) {
                pointAnnounceDO.setId(id);
                pointAnnounceService.update(pointAnnounceDO);
                result = id;
            } else {
                pointAnnounceDO.setCreateBy(user.UserName);
                pointAnnounceDO.setCreateTime(new Date());
                result = pointAnnounceService.insert(pointAnnounceDO);
                result=pointAnnounceDO.getId();
            }

            List<Map<String, Object>> userdata = new ArrayList<>();
            if (classType.equals("2")) {
                Map<String, Object> map = new HashMap<>();
                map.put("EM", account);
                userdata.add(map);
            } else {
                if (file != null) {
                    try {
                        userdata = ExcelUtils.excelToList(file, "Sheet1");
                    } catch (Exception e) {
                        return "上传文件有误";
                    }
                }
            }
            UserMessageMappingDO userMessageMappingDO = new UserMessageMappingDO();
            if (userdata != null && userdata.size() > 0) {
                for (Map<String, Object> item : userdata) {
                    userMessageMappingDO.setMessageId(result);
                    userMessageMappingDO.setAccount(item.get("EM").toString());
                    userMessageMappingDO.setCreateTime(new Date());

                    //发消息到kafka
                    kafkaProducerService.sendMessageSync(pointmessageusernameTopic, JSONObject.toJSONString(userMessageMappingDO));
                }
            }
            return result > 0 ? "success" : "保存失败";
        } catch (Exception e) {
            log.error("保存消息通知失败：" + e);
        }
        return "保存失败";
    }
}
