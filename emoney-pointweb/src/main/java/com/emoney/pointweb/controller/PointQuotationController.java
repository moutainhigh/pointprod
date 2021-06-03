package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoney.pointweb.repository.dao.entity.PointQuotationDO;
import com.emoney.pointweb.repository.dao.entity.vo.PointQuotationVO;
import com.emoney.pointweb.repository.dao.entity.vo.UserGroupVO;
import com.emoney.pointweb.service.biz.PointQuotationService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pointquotation")
@Slf4j
public class PointQuotationController {

    @Resource
    private PointQuotationService pointQuotationService;

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
        return "pointquotation/pointquotation.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(String ver, String plat) {
        List<PointQuotationDO> list = pointQuotationService.getAll();
        List<PointQuotationVO> data = JsonUtil.copyList(list, PointQuotationVO.class);
        if (!StringUtils.isEmpty(ver)) {
            data = data.stream().filter(x -> x.getProductVersion().contains(ver)).collect(Collectors.toList());
        }
        if (!StringUtils.isEmpty(plat)) {
            data = data.stream().filter(x -> x.getPublishPlatFormType().contains(plat)).collect(Collectors.toList());
        }
        if (data != null && data.size() > 0) {
            List<UserGroupVO> userGroupVOList = userInfoService.getUserGroupList();
            for (PointQuotationVO item : data) {
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
    public String edit(@RequestParam(required = false, defaultValue = "0") Integer id,
                       String ver, String plat, String groupList, String showTime,
                       String content, HttpServletRequest request, HttpServletResponse response) {
        try {
            TicketInfo user = userLoginService.getLoginAdminUser(request, response);
            if (user == null) {
                return "用户登录已过期，请重新登录";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            PointQuotationDO pointQuotationDO = new PointQuotationDO();
            pointQuotationDO.setId(id);
            pointQuotationDO.setContent(content);
            pointQuotationDO.setUserGroup(groupList);
            pointQuotationDO.setPublishPlatFormType(plat);
            pointQuotationDO.setProductVersion(ver);
            if (!StringUtils.isEmpty(showTime)) {
                pointQuotationDO.setShowTime(sdf.parse(showTime));
            } else {
                pointQuotationDO.setShowTime(null);
            }
            pointQuotationDO.setUpdateBy(user.UserName);
            pointQuotationDO.setUpdateTime(new Date());
            int result = 0;
            if (id > 0) {
                result = pointQuotationService.update(pointQuotationDO);
            } else {
                pointQuotationDO.setIsValid(true);
                pointQuotationDO.setCreateTime(new Date());
                pointQuotationDO.setCreateBy(user.UserName);
                result = pointQuotationService.insert(pointQuotationDO);
            }
            return result > 0 ? "success" : "error";
        } catch (Exception e) {
            log.error("语录保存出错：" + e.toString());
        }
        return null;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam(required = false, defaultValue = "0") Integer id) {
        PointQuotationDO pointQuotationDO = new PointQuotationDO();
        pointQuotationDO.setId(id);
        pointQuotationDO.setIsValid(false);

        int result = pointQuotationService.update(pointQuotationDO);
        return result > 0 ? "success" : "error";
    }
}
