package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoney.pointweb.repository.dao.entity.PointSendConfigInfoDO;
import com.emoney.pointweb.service.biz.PointSendConfigInfoService;
import com.emoney.pointweb.service.biz.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lipengcheng
 * @date 2021-05-13
 */
@Controller
@Slf4j
@RequestMapping("/pointsendconfiginfo")
public class PointSendConfigInfoController {

    @Resource
    private PointSendConfigInfoService pointSendConfigInfoService;

    @Resource
    private UserLoginService userLoginService;

    @RequestMapping
    public String index() {
        return "pointsendconfiginfo/pointsendconfiginfo.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(Integer opType) {
        List<PointSendConfigInfoDO> list = pointSendConfigInfoService.queryAll();
        if (opType > 0) {
            list = list.stream().filter(x -> x.getBuyType().equals(opType)).collect(Collectors.toList());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return result;
    }

    @RequestMapping("/edit")
    @ResponseBody
    public String edit(HttpServletRequest request, HttpServletResponse response, @RequestParam(required = false,
            defaultValue = "0") Integer id, String ver, Integer buyType, @RequestParam(required = false, defaultValue = "0") Float pointNum,
                       @RequestParam(required = false, defaultValue = "0") BigDecimal ratio, String remark) {
        try {
            TicketInfo user = userLoginService.getLoginAdminUser(request, response);
            if (user == null) {
                return "用户登录已过期，请重新登录";
            }

            PointSendConfigInfoDO pointSendConfigInfoDO = new PointSendConfigInfoDO();
            pointSendConfigInfoDO.setId(id);
            pointSendConfigInfoDO.setProductVersion(ver);
            pointSendConfigInfoDO.setBuyType(buyType);
            if (ratio.equals(BigDecimal.ZERO)) {
                pointSendConfigInfoDO.setRatio(null);
            } else {
                pointSendConfigInfoDO.setRatio(ratio);
            }
            if (pointNum != 0) {
                pointSendConfigInfoDO.setPointNum(pointNum);
            } else {
                pointSendConfigInfoDO.setPointNum(null);
            }
            pointSendConfigInfoDO.setRemark(remark);
            pointSendConfigInfoDO.setIsValid(true);
            pointSendConfigInfoDO.setUpdateBy(user.UserName);
            pointSendConfigInfoDO.setUpdateTime(new Date());
            int result = 0;
            if (id > 0) {
                List<PointSendConfigInfoDO> list = pointSendConfigInfoService.queryAll();
                if (list.stream().anyMatch(x -> x.getProductVersion().equals(ver) && x.getBuyType().equals(buyType))) {
                    if (!list.stream().filter(x -> x.getProductVersion().equals(ver) && x.getBuyType().equals(buyType)).collect(Collectors.toList())
                            .get(0).getId().equals(pointSendConfigInfoDO.getId())) {
                        return "已存在该类型配置规则，不允许保存";
                    }
                }

                result = pointSendConfigInfoService.update(pointSendConfigInfoDO);
            } else {
                List<PointSendConfigInfoDO> list = pointSendConfigInfoService.queryAll();
                if (list.stream().anyMatch(x -> x.getProductVersion().equals(ver) && x.getBuyType().equals(buyType))) {
                    return "已存在该类型配置规则，不允许保存";
                }
                pointSendConfigInfoDO.setCreateBy(user.UserName);
                pointSendConfigInfoDO.setCreateTime(new Date());
                result = pointSendConfigInfoService.insert(pointSendConfigInfoDO);
            }

            return result > 0 ? "success" : "保存失败";
        } catch (Exception e) {
            log.error("编辑赠送配置失败 error:" + e.getMessage());
            return e.getMessage();
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam(required = false, defaultValue = "0") Integer id,
                         HttpServletRequest request, HttpServletResponse response) {
        TicketInfo user = userLoginService.getLoginAdminUser(request, response);

        PointSendConfigInfoDO pointSendConfigInfoDO = new PointSendConfigInfoDO();
        pointSendConfigInfoDO.setId(id);
        pointSendConfigInfoDO.setIsValid(false);
        pointSendConfigInfoDO.setUpdateBy(user.getUserName());
        pointSendConfigInfoDO.setUpdateTime(new Date());
        int result = pointSendConfigInfoService.update(pointSendConfigInfoDO);

        return result > 0 ? "success" : "删除失败";
    }
}
