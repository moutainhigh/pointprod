package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoney.pointweb.repository.dao.entity.PointProductDO;
import com.emoney.pointweb.repository.dao.entity.vo.UserGroupVO;
import com.emoney.pointweb.service.biz.PointProductService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.emoney.pointweb.service.biz.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/pointproduct")
public class PointProductController {

    @Resource
    private PointProductService pointProductService;

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
        return "pointproduct/pointproduct.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int productType,
                                        @RequestParam(required = false, defaultValue = "0") int productStatus,
                                        String ver, String plat) {
        List<PointProductDO> list = pointProductService.getPointProductListByProductType(productType);
        switch (productStatus) {
            case 1:
                list = list.stream().filter(x -> x.getExchangeStarttime().before(new Date()) && x.getExchangeEndtime().after(new Date())).collect(Collectors.toList());
                break;
            case 2:
                list = list.stream().filter(x -> x.getExchangeStarttime().after(new Date())).collect(Collectors.toList());
                break;
            case 3:
                list = list.stream().filter(x -> x.getExchangeEndtime().before(new Date())).collect(Collectors.toList());
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

    @RequestMapping("/checkActivityCode")
    @ResponseBody
    public Map<String, Object> checkActivityCode(@RequestParam(required = false, defaultValue = "") String acCode) {
        Map<String, Object> map = new HashMap<>();
        if (acCode.isEmpty()) {
            map.put("code", -1);
        } else {
            map = pointProductService.checkActivityCode(acCode);
        }
        return map;
    }

    @RequestMapping("/edit")
    @ResponseBody
    public String edit(@RequestParam(required = false, defaultValue = "0") Integer id, Integer productType, String ver, String plat, String pcimg, String appimg, String wechatimg,
                       Integer exchangeType, String acCode, @RequestParam(required = false, defaultValue = "0") Integer productDays, String actStartTime, String actEndTime, String productName,
                       @RequestParam(required = false, defaultValue = "0") BigDecimal productPrice, @RequestParam(required = false, defaultValue = "0") BigDecimal activityPrice, String exChangeStartTime, String exChangeEndTime, String groupList, String fileurl,
                       @RequestParam(required = false, defaultValue = "0") BigDecimal productCash, @RequestParam(required = false, defaultValue = "0") float productPoint,
                       Integer totalLimit, Integer perLimit, String exChangeContent, String pcdetailimg, String appdetailimg, String wechatdetailimg, String remark, String Pid, String statisticalClassification,
                       HttpServletRequest request, HttpServletResponse response) {
        try {
            TicketInfo user = userLoginService.getLoginAdminUser(request, response);
            if (user == null) {
                return "???????????????????????????????????????";
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            PointProductDO pointProductDO = new PointProductDO();
            pointProductDO.setId(id);
            pointProductDO.setProductType(productType);
            pointProductDO.setProductDays(productDays);
            pointProductDO.setProductVersion(ver);
            pointProductDO.setPublishPlatFormType(plat);
            pointProductDO.setActivityCode(acCode);
            pointProductDO.setProductName(productName);
            pointProductDO.setPerLimit(perLimit);
            pointProductDO.setTotalLimit(totalLimit);
            pointProductDO.setUserGroup(groupList);
            pointProductDO.setProductFile(fileurl);
            pointProductDO.setActivityPid(Pid);
            pointProductDO.setStatisticalClassification(statisticalClassification);
            if (!exChangeStartTime.isEmpty()) {
                pointProductDO.setExchangeStarttime(sdf.parse(exChangeStartTime));
            }
            if (!exChangeEndTime.isEmpty()) {
                pointProductDO.setExchangeEndtime(sdf.parse(exChangeEndTime));
            }
            pointProductDO.setExchangeType(exchangeType);
            pointProductDO.setProductVersion(ver);
            if (!actStartTime.isEmpty()) {
                pointProductDO.setActivityStartTime(sdf.parse(actStartTime.replace("T", " ")));
            }
            if (!actEndTime.isEmpty()) {
                pointProductDO.setActivityEndTime(sdf.parse(actEndTime.replace("T", " ")));
            }
            pointProductDO.setExchangePoint(productPoint);
            pointProductDO.setExchangeCash(productCash);
            pointProductDO.setProductPrice(productPrice);
            pointProductDO.setActivityPrice(activityPrice);
            pointProductDO.setExchangeRemark(exChangeContent.replace("'", "???"));
            pointProductDO.setPcExangeimgurl(pcimg);
            pointProductDO.setAppExangeimgurl(appimg);
            pointProductDO.setWebchatExangeimgurl(wechatimg);
            pointProductDO.setPcExangeDetailimgurl(pcdetailimg);
            pointProductDO.setAppExangeDetailimgurl(appdetailimg);
            pointProductDO.setWebchatExangeDetailimgurl(wechatdetailimg);
            pointProductDO.setIsValid(true);
            pointProductDO.setUpdateTime(new Date());
            pointProductDO.setUpdateBy(user.UserName);
            pointProductDO.setRemark(remark.replace("'", "???"));
            if (pointProductDO.getProductType().equals(1)) {
                if (pointProductDO.getExchangeCash().doubleValue() > pointProductDO.getProductPrice().doubleValue()) {
                    return "????????????????????????????????????";
                }
                if (pointProductDO.getExchangeCash().doubleValue() > pointProductDO.getActivityPrice().doubleValue()) {
                    return "???????????????????????????????????????";
                }
            }
            int result = 0;
            if (id > 0) {
                result = pointProductService.updatePointProduct(pointProductDO);
            } else {
                pointProductDO.setCreateBy(user.UserName);
                pointProductDO.setCreateTime(new Date());
                result = pointProductService.insertPointProduct(pointProductDO);
            }
            return result > 0 ? "success" : "????????????";
        } catch (Exception e) {
            log.error("???????????????????????????" + e);
            return e.getMessage();
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam(required = false, defaultValue = "0") Integer id) {
        PointProductDO pointProductDO = new PointProductDO();
        pointProductDO.setId(id);
        pointProductDO.setIsValid(false);

        int result = pointProductService.updatePointProduct(pointProductDO);
        return result > 0 ? "success" : "error";
    }
}
