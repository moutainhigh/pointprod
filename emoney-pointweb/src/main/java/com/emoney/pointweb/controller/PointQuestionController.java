package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;
import com.emoney.pointweb.repository.dao.entity.vo.PointQuestionVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserGroupVO;
import com.emoney.pointweb.service.biz.PointQuestionService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.UserLoginService;
import org.apache.commons.lang3.Conversion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lipengcheng
 * @date 2021-05-07
 */
@Controller
@RequestMapping("/pointquestion")
public class PointQuestionController {

    @Resource
    private PointQuestionService pointQuestionService;

    @Resource
    private UserLoginService userLoginService;

    @Resource
    private PointTaskConfigInfoService pointTaskConfigInfoService;

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping
    public String index(Model model) {
        List<UserGroupVO> userGroupVOList = userInfoService.getUserGroupList();
        model.addAttribute("userGroupVOList", userGroupVOList);
        return "pointquestion/pointquestion.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(String ver, String plat) {
        List<PointQuestionDO> list = pointQuestionService.getAll();
        List<PointQuestionVO> data = JsonUtil.copyList(list, PointQuestionVO.class);
        if (!StringUtils.isEmpty(ver)) {
            data = data.stream().filter(x -> x.getProductVersion().contains(ver)).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(plat)) {
            data = data.stream().filter(x -> x.getPublishPlatFormType().contains(plat)).collect(Collectors.toList());
        }
        if (data != null && data.size() > 0) {
            List<UserGroupVO> userGroupVOList = userInfoService.getUserGroupList();
            for (PointQuestionVO item : data) {
                if (!StringUtils.isEmpty(item.getUserGroup())) {
                    String[] groupArr = item.getUserGroup().split(",");
                    if (groupArr.length > 0) {
                        String name = "";
                        for (String groupId : groupArr) {
                            if (userGroupVOList.stream().anyMatch(h -> h.getId().equals(Integer.valueOf(groupId)))) {
                                name += userGroupVOList.stream().filter(h -> h.getId().equals(Integer.valueOf(groupId))).findFirst().get().getUserGroupName() + ",";
                            }
                        }
                        item.setUserGroupName(name);
                    }
                }
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        return result;
    }

    @RequestMapping("/edit")
    @ResponseBody
    public String edit(@RequestParam(required = false, defaultValue = "0") Integer id, Integer questionType,
                       String showTime, String questionContent, String questionOptions, String questionRightoptions,
                       String ver, String plat, String groupList, String remark,
                       HttpServletRequest request, HttpServletResponse response) {
        try {
            TicketInfo user = userLoginService.getLoginAdminUser(request, response);
            if (user == null) {
                return "???????????????????????????????????????";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            PointQuestionDO pointQuestionDO = new PointQuestionDO();
            pointQuestionDO.setId(id);
            pointQuestionDO.setUserGroup(groupList);
            pointQuestionDO.setPublishPlatFormType(plat);
            pointQuestionDO.setProductVersion(ver);
            pointQuestionDO.setRemark(remark);
            if (!StringUtils.isEmpty(showTime)) {
                pointQuestionDO.setShowTime(sdf.parse(showTime));
            } else {
                pointQuestionDO.setShowTime(null);
            }
            if (!StringUtils.isEmpty(questionRightoptions)) {
                String[] rightStr = questionRightoptions.split("\\|");
                if (questionType.equals(1) && rightStr.length > 1) {
                    return "????????????????????????????????????";
                } else if (questionType.equals(2) && rightStr.length < 2) {
                    return "?????????????????????????????????2???";
                }
            } else {
                return "?????????????????????";
            }
            if (!StringUtils.isEmpty(questionOptions)) {
                String[] opStr = questionOptions.split("\\|");
                int[] rightOp = Arrays.asList(questionRightoptions.split("\\|")).stream().mapToInt(Integer::parseInt).toArray();
                if (opStr.length < Arrays.stream(rightOp).max().getAsInt()) {
                    return "??????????????????????????????";
                }
            } else {
                return "?????????????????????";
            }
            pointQuestionDO.setQuestionContent(questionContent);
            pointQuestionDO.setQuestionType(questionType);
            pointQuestionDO.setQuestionOptions(questionOptions);
            pointQuestionDO.setQuestionRightoptions(questionRightoptions);
            pointQuestionDO.setUpdateBy(user.UserName);
            pointQuestionDO.setUpdateTime(new Date());

            int result = 0;
            if (id > 0) {
                result = pointQuestionService.update(pointQuestionDO);
            } else {
                pointQuestionDO.setIsValid(true);
                pointQuestionDO.setCreateBy(user.UserName);
                pointQuestionDO.setCreateTime(new Date());
                result = pointQuestionService.insert(pointQuestionDO);
            }
            return result > 0 ? "success" : "????????????";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String delete(Integer id, HttpServletRequest request, HttpServletResponse response) {
        try {
            TicketInfo user = userLoginService.getLoginAdminUser(request, response);

            PointQuestionDO pointQuestionDO = new PointQuestionDO();
            pointQuestionDO.setId(id);
            pointQuestionDO.setIsValid(false);
            pointQuestionDO.setUpdateBy(user.UserName);
            pointQuestionDO.setUpdateTime(new Date());

            int result = pointQuestionService.update(pointQuestionDO);
            return result > 0 ? "success" : "????????????";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
