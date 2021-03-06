package com.emoney.pointweb.controller;

import com.alibaba.fastjson.JSON;
import com.emoeny.pointcommon.enums.MessageTypeEnum;
import com.emoeny.pointcommon.utils.ExcelUtils;
import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import com.emoney.pointweb.repository.dao.entity.PointMessageDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.service.biz.PointFeedBackService;
import com.emoney.pointweb.service.biz.PointMessageService;
import com.emoney.pointweb.service.biz.PointSendRecordService;
import com.emoney.pointweb.service.biz.UserInfoService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lipengcheng
 * @date 2021-04-15
 */
@Controller
@Slf4j
@RequestMapping("/pointfeedback")
public class PointFeedBackController {

    @Resource
    private PointFeedBackService pointFeedBackService;

    @Resource
    private PointSendRecordService pointSendRecordService;

    @Autowired
    private PointMessageService pointMessageService;

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping
    public String index() {
        return "/pointfeedback/pointfeedback.index";
    }

    ;

//    @RequestMapping("/pageList")
//    @ResponseBody
//    public Map<String, Object> pageList(Integer classType, Integer isReply, Integer isAdopt) {
//        List<PointFeedBackDO> list = pointFeedBackService.getAll();
//        if (!classType.equals(0)) {
//            list = list.stream().filter(x -> x.getFeedType().equals(classType)).collect(Collectors.toList());
//        }
//        if (!isReply.equals(0)) {
//            if (isReply.equals(1)) {
//                list = list.stream().filter(x -> !StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
//            } else {
//                list = list.stream().filter(x -> StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
//            }
//        }
//        if (!isAdopt.equals(-1)) {
//            list = list.stream().filter(x -> x.getStatus().equals(isAdopt)).collect(Collectors.toList());
//        }
//        Map<String, Object> result = new HashMap<>();
//        result.put("data", list);
//        return result;
//    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> queryPointFeedback(@RequestParam(required = false, defaultValue = "0") Integer start,
                                                  @RequestParam(required = false, defaultValue = "10") Integer length,
                                                  Integer classType, Integer isReply, Integer isAdopt, String content) {
        PageHelper.startPage(start, length);
        List<PointFeedBackDO> list = pointFeedBackService.queryAllByRemarkAndStatus(classType, isReply, isAdopt, content);
        PageInfo<PointFeedBackDO> pageInfo = new PageInfo<>(list);

        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        result.put("recordsTotal", pageInfo.getTotal());
        result.put("recordsFiltered", pageInfo.getTotal());
        return result;
    }

    @RequestMapping("/editReply")
    @ResponseBody
    public String editReply(Integer id, String remark) {
        PointFeedBackDO pointFeedBackDO = new PointFeedBackDO();
        pointFeedBackDO.setId(id);
        pointFeedBackDO.setRemark(remark);
        int result = pointFeedBackService.update(pointFeedBackDO);
        if (result > 0) {
            pointFeedBackDO = pointFeedBackService.getById(id);
            if (pointFeedBackDO != null) {
                String uid = userInfoService.getUidByEmNo(pointFeedBackDO.getAccount());
                if (!StringUtils.isEmpty(uid)) {
                    PointMessageDO pointMessageDO = new PointMessageDO();
                    pointMessageDO.setUid(Long.parseLong(uid));
                    pointMessageDO.setMsgType(Integer.parseInt(MessageTypeEnum.TYPE5.getCode()));
                    pointMessageDO.setMsgContent("??????????????????" + pointFeedBackDO.getRemark());
                    pointMessageDO.setMsgExt(String.valueOf(pointFeedBackDO.getId()) + pointMessageDO.getMsgType());
                    pointMessageDO.setCreateTime(new Date());
                    pointMessageService.insert(pointMessageDO);
                }
            }
        }

        return result > 0 ? "success" : "????????????";
    }

    @RequestMapping("/adopt")
    @ResponseBody
    public String adopt(Integer id, String remark) {
        PointFeedBackDO pointFeedBackDO = new PointFeedBackDO();
        PointFeedBackDO pointFeedBackDO1 = pointFeedBackService.getById(id);
        pointFeedBackDO.setId(id);
        pointFeedBackDO.setStatus(1);
        pointFeedBackDO.setAdoptRemark(remark);
        int result = pointFeedBackService.update(pointFeedBackDO);
        if (result > 0) {
            //????????????
            pointFeedBackDO = pointFeedBackService.getById(id);

            //??????bug 1????????????
            if (pointFeedBackDO1.getStatus().equals(0)) {
                log.info("????????????????????????,account:" + pointFeedBackDO.getAccount());
                pointSendRecordService.sendPointRecord(Long.parseLong("1384354667126984704"), pointFeedBackDO.getAccount());
            }
            if (pointFeedBackDO != null) {
                String uid = userInfoService.getUidByEmNo(pointFeedBackDO.getAccount());
                if (!StringUtils.isEmpty(uid)) {
                    PointMessageDO pointMessageDO = new PointMessageDO();
                    pointMessageDO.setUid(Long.parseLong(uid));
                    pointMessageDO.setMsgType(Integer.parseInt(MessageTypeEnum.TYPE7.getCode()));
                    pointMessageDO.setMsgContent("??????????????????" + pointFeedBackDO.getAdoptRemark());
                    pointMessageDO.setMsgExt(String.valueOf(pointFeedBackDO.getId()) + pointMessageDO.getMsgType());
                    pointMessageDO.setCreateTime(new Date());
                    pointMessageService.insert(pointMessageDO);
                }
            }
        }
        return result > 0 ? "success" : "????????????";
    }

    @RequestMapping("/exportData")
    public String exportData(HttpServletResponse response, Integer classType, Integer isReply, Integer isAdopt, String content) {
        List<PointFeedBackDO> list = pointFeedBackService.queryAllByRemarkAndStatus(classType, isReply, isAdopt, content);
        List<LinkedHashMap<String, Object>> maps = new ArrayList<>();

        if (list != null && list.size() > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for (PointFeedBackDO item : list) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("????????????", getClassTypeName(item.getFeedType()));
                map.put("????????????", formatter.format(item.getCreateTime()));
                map.put("????????????", item.getPid());
                map.put("??????", item.getAccount());
                map.put("???????????????", item.getMobileX());
                map.put("??????", item.getEmail());
                map.put("??????????????????", item.getSuggest());
                map.put("??????????????????", item.getRemark());
                map.put("????????????", StringUtils.isEmpty(item.getRemark()) ? "?????????" : "?????????");
                map.put("????????????", item.getStatus().equals(1) ? "???" : "???");

                maps.add(map);
            }
        } else {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("????????????", "");
            map.put("????????????", "");
            map.put("????????????", "");
            map.put("??????", "");
            map.put("???????????????", "");
            map.put("??????", "");
            map.put("??????????????????", "");
            map.put("??????????????????", "");
            map.put("????????????", "");
            map.put("????????????", "");

            maps.add(map);
        }
        ExcelUtils.exportToExcel(response, "????????????", maps);
        return null;
    }

    public String getClassTypeName(Integer feedType) {
        String result = "";
        switch (feedType) {
            case 1:
                result = "????????????";
                break;
            case 2:
                result = "????????????";
                break;
            case 3:
                result = "????????????";
                break;
            case 4:
                result = "????????????";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }
}
