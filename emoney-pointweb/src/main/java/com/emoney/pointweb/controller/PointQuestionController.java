package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;
import com.emoney.pointweb.repository.dao.entity.vo.PointQuestionVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserGroupVO;
import com.emoney.pointweb.service.biz.PointQuestionService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import com.emoney.pointweb.service.biz.UserLoginService;
import org.apache.commons.lang3.Conversion;
import org.apache.commons.lang3.StringUtils;
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

    @RequestMapping
    public String index(Model model) {
        List<UserGroupVO> userGroupVOList = pointTaskConfigInfoService.getUserGroupList();
        model.addAttribute("userGroupVOList", userGroupVOList);
        return "pointquestion/pointquestion.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList() {
        List<PointQuestionDO> list = pointQuestionService.getAll();
        List<PointQuestionVO> data = JsonUtil.copyList(list, PointQuestionVO.class);
        if (data != null && data.size() > 0) {
            List<UserGroupVO> userGroupVOList = pointTaskConfigInfoService.getUserGroupList();
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
                       String ver, String plat, String groupList,
                       HttpServletRequest request, HttpServletResponse response) {
        try {
            TicketInfo user = userLoginService.getLoginAdminUser(request, response);
            if (user == null) {
                return "用户登录已过期，请重新登录";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            PointQuestionDO pointQuestionDO = new PointQuestionDO();
            pointQuestionDO.setId(id);
            pointQuestionDO.setUserGroup(groupList);
            pointQuestionDO.setPublishPlatFormType(plat);
            pointQuestionDO.setProductVersion(ver);
            if (!StringUtils.isEmpty(showTime)) {
                pointQuestionDO.setShowTime(sdf.parse(showTime));
            } else {
                pointQuestionDO.setShowTime(null);
            }
            if (!StringUtils.isEmpty(questionRightoptions)) {
                String[] rightStr = questionRightoptions.split("\\|");
                if (questionType.equals(1) && rightStr.length > 1) {
                    return "单选题不能有多个正确答案";
                } else if (questionType.equals(2) && rightStr.length < 2) {
                    return "多选题正确答案不能少于2个";
                }
            } else {
                return "请填写正确答案";
            }
            if (!StringUtils.isEmpty(questionOptions)) {
                String[] opStr = questionOptions.split("\\|");
                int[] rightOp = Arrays.asList(questionRightoptions.split("\\|")).stream().mapToInt(Integer::parseInt).toArray();
                if (opStr.length < Arrays.stream(rightOp).max().getAsInt()) {
                    return "正确答案超过选项范围";
                }
            } else {
                return "请输入题目选项";
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
            return result > 0 ? "success" : "删除失败";
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
            return result > 0 ? "success" : "删除失败";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
