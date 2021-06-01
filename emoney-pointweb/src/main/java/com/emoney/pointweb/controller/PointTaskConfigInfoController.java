package com.emoney.pointweb.controller;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.entity.vo.UserGroupVO;
import com.emoney.pointweb.service.biz.PointRecordService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/pointtaskconfiginfo")
public class PointTaskConfigInfoController {

    @Resource
    private PointTaskConfigInfoService pointTaskConfigInfoService;

    @Resource
    private PointRecordService pointRecordService;

    @Resource
    private UserLoginService userLoginService;

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping
    public String index(Model model) {
        List<UserGroupVO> userGroupVOList = userInfoService.getUserGroupList();
        model.addAttribute("userGroupVOList", userGroupVOList);
        return "pointtaskconfiginfo/pointtaskconfiginfo.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") Integer start,
                                        @RequestParam(required = false, defaultValue = "10") Integer length,
                                        @RequestParam(required = false, defaultValue = "0") Integer task_type,
                                        @RequestParam(required = false, defaultValue = "0") Integer task_status) {
        return pointTaskConfigInfoService.pageList(start, length, task_type, task_status);
    }

    @RequestMapping("/edit")
    @ResponseBody
    public String edit(@RequestParam(required = false, defaultValue = "0") Integer id, @RequestParam(required = false, defaultValue = "0") Long taskId,
                       String subid, Integer tasktype, String taskname, Float taskpoints, String starttime, String endtime,
                       @RequestParam(required = false, defaultValue = "0") Integer is_directional, Integer daily, String taskremark, String groupList, Integer sendType,
                       Integer jointimes, String ver, @RequestParam(required = false, defaultValue = "0") Integer ishomepage,
                       String platfrom, String pcurl, String appurl, Integer taskorder,
                       String wechaturl, String buttontext, String pcimageurl, String appimageurl, String wechatimageurl,
                       @RequestParam(required = false, defaultValue = "0") Integer is_bigimg, HttpServletRequest request, HttpServletResponse response) {
        try {
            TicketInfo user = userLoginService.getLoginAdminUser(request, response);
            if (user == null) {
                return "用户登录已过期，请重新登录";
            }
            //获取同一类型同一排序
            List<PointTaskConfigInfoDO> data = pointTaskConfigInfoService.getPointTaskConfigInfoByOrderAndType(tasktype, taskorder);
            if ((data.size() >= 1 && id == 0) || (data.size() > 1 && id > 0)) {
                return "排序相同，不能保存";
            }

            List<PointTaskConfigInfoDO> checkData = pointTaskConfigInfoService.getByTaskIdAndSubId(taskId, subid);

            PointTaskConfigInfoDO ptci = new PointTaskConfigInfoDO();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ptci.setId(id);
            ptci.setTaskId(taskId);
            ptci.setSubId(subid.isEmpty() ? null : subid);
            ptci.setTaskOrder(taskorder);
            ptci.setTaskType(tasktype);
            ptci.setTaskName(taskname);
            ptci.setTaskPoints(taskpoints);
            ptci.setTaskStartTime(sdf.parse(starttime));
            ptci.setTaskEndTime(sdf.parse(endtime));
            ptci.setTaskRemark(taskremark);
            ptci.setUserGroup(groupList);
            ptci.setIsDirectional(is_directional == 1);
            ptci.setSendType(sendType == 1);
            ptci.setIsBigImg(is_bigimg == 1);
            ptci.setIsDailyTask(daily == 1);
            ptci.setDailyJoinTimes(jointimes);
            ptci.setProductVersion(ver);
            ptci.setIsShowInHomePage(ishomepage == 1);
            ptci.setPublishPlatFormType(platfrom);
            ptci.setPcRedirectUrl(pcurl);
            ptci.setAppRedirectUrl(appurl);
            ptci.setWechatRedirectUrl(wechaturl);
            ptci.setTaskButtonText(buttontext);
            ptci.setPcTaskImgUrl(pcimageurl);
            ptci.setAppTaskImgUrl(appimageurl);
            ptci.setWechatTaskImgUrl(wechatimageurl);
            ptci.setUpdateBy(user.UserName);
            ptci.setUpdateTime(new Date());
            ptci.setRemark("");
            int result = 0;
            if (id > 0) {
                if (checkData.size() >= 2) {
                    return "已存在相同任务，不允许重复创建";
                }

                result = pointTaskConfigInfoService.update(ptci);
            } else {
                if (checkData.size() > 0) {
                    return "已存在相同任务，不允许重复创建";
                }

                ptci.setCreateTime(new Date());
                ptci.setCreateBy(user.UserName);
                result = pointTaskConfigInfoService.insert(ptci);
            }
            return result > 0 ? "success" : "保存出错";
        } catch (ParseException e) {
            log.error("保存任务配置出错：" + e);
        }
        return "保存出错";
    }

    @RequestMapping("/checkTaskStatus")
    @ResponseBody
    public Boolean checkTaskStatus(Long taskId, String subId) {
        Long queryNum = pointRecordService.calPointRecordByTaskId(taskId, subId.isEmpty() ? null : subId, 0, 1);
        return queryNum > 0 ? true : false;
    }

    @RequestMapping("/getTaskId")
    @ResponseBody
    public String getTaskId() {
        Long taskid = IdUtil.getSnowflake(1, 1).nextId();
        return taskid.toString();
    }
}
