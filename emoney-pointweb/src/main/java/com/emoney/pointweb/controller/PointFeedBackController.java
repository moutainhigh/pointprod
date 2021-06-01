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

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(Integer classType, Integer isReply) {
        List<PointFeedBackDO> list = pointFeedBackService.getAll();
        if (!classType.equals(0)) {
            list = list.stream().filter(x -> x.getFeedType().equals(classType)).collect(Collectors.toList());
        }
        if (!isReply.equals(0)) {
            if (isReply.equals(1)) {
                list = list.stream().filter(x -> !StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
            } else {
                list = list.stream().filter(x -> StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
            }
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", list);
        return result;
    }

//    @RequestMapping("/pageList")
//    @ResponseBody
//    public Map<String, Object> queryPointFeedback(@RequestParam(required = false, defaultValue = "0") Integer start,
//                                                  @RequestParam(required = false, defaultValue = "10") Integer length,
//                                                  Integer classType, Integer isReply) {
//        PageHelper.startPage(start,length);
//        List<PointFeedBackDO> list=pointFeedBackService.queryAllByRemarkAndStatus(classType,isReply);
//        PageInfo<PointFeedBackDO> pageInfo = new PageInfo<>(list);
//
//        Map<String, Object> result=new HashMap<>();
//        result.put("data",list);
//        result.put("recordsTotal",pageInfo.getTotal());
//        result.put("recordsFiltered",pageInfo.getTotal());
//        return result;
//    }

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
                    pointMessageDO.setMsgContent("【意见反馈】" + pointFeedBackDO.getRemark());
                    pointMessageDO.setMsgExt(String.valueOf(pointFeedBackDO.getId()) + pointMessageDO.getMsgType());
                    pointMessageDO.setCreateTime(new Date());
                    pointMessageService.insert(pointMessageDO);
                }
            }
        }

        return result > 0 ? "success" : "回复失败";
    }

    @RequestMapping("/adopt")
    @ResponseBody
    public String adopt(Integer id, String remark) {
        PointFeedBackDO pointFeedBackDO = new PointFeedBackDO();
        pointFeedBackDO.setId(id);
        pointFeedBackDO.setStatus(1);
        pointFeedBackDO.setAdoptRemark(remark);
        int result = pointFeedBackService.update(pointFeedBackDO);
        if (result > 0) {
            //赠送积分
            pointFeedBackDO = pointFeedBackService.getById(id);
            pointSendRecordService.sendPointRecord(Long.parseLong("1384354667126984704"), pointFeedBackDO.getAccount());
            pointFeedBackDO = pointFeedBackService.getById(id);
            if (pointFeedBackDO != null) {
                String uid = userInfoService.getUidByEmNo(pointFeedBackDO.getAccount());
                if (!StringUtils.isEmpty(uid)) {
                    PointMessageDO pointMessageDO = new PointMessageDO();
                    pointMessageDO.setUid(Long.parseLong(uid));
                    pointMessageDO.setMsgType(Integer.parseInt(MessageTypeEnum.TYPE7.getCode()));
                    pointMessageDO.setMsgContent("【意见采纳】" + pointFeedBackDO.getAdoptRemark());
                    pointMessageDO.setMsgExt(String.valueOf(pointFeedBackDO.getId()) + pointMessageDO.getMsgType());
                    pointMessageDO.setCreateTime(new Date());
                    pointMessageService.insert(pointMessageDO);
                }
            }
        }
        return result > 0 ? "success" : "采纳失败";
    }

    @RequestMapping("/exportData")
    public String exportData(HttpServletResponse response, Integer classType, Integer isReply) {
        List<PointFeedBackDO> list = pointFeedBackService.getAll();
        if (!classType.equals(0)) {
            list = list.stream().filter(x -> x.getFeedType().equals(classType)).collect(Collectors.toList());
        }
        if (!isReply.equals(0)) {
            if (isReply.equals(1)) {
                list = list.stream().filter(x -> !StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
            } else {
                list = list.stream().filter(x -> StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
            }
        }
        List<LinkedHashMap<String, Object>> maps = new ArrayList<>();

        if (list != null && list.size() > 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for (PointFeedBackDO item : list) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("反馈类型", getClassTypeName(item.getFeedType()));
                map.put("提交时间", formatter.format(item.getCreateTime()));
                map.put("产品版本", item.getPid());
                map.put("账号", item.getAccount());
                map.put("加密手机号", item.getMobileX());
                map.put("邮箱", item.getEmail());
                map.put("用户建议内容", item.getSuggest());
                map.put("客服处理意见", item.getRemark());
                map.put("最新进展", StringUtils.isEmpty(item.getRemark()) ? "待处理" : "已处理");
                map.put("是否采纳", item.getStatus().equals(1) ? "是" : "否");

                maps.add(map);
            }
        } else {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("反馈类型", "");
            map.put("提交时间", "");
            map.put("产品版本", "");
            map.put("账号", "");
            map.put("加密手机号", "");
            map.put("邮箱", "");
            map.put("用户建议内容", "");
            map.put("客服处理意见", "");
            map.put("最新进展", "");
            map.put("是否采纳", "");

            maps.add(map);
        }
        ExcelUtils.exportToExcel(response, "意见反馈", maps);
        return null;
    }

    public String getClassTypeName(Integer feedType) {
        String result = "";
        switch (feedType) {
            case 1:
                result = "产品建议";
                break;
            case 2:
                result = "使用心得";
                break;
            case 3:
                result = "提问咨询";
                break;
            case 4:
                result = "其他建议";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }
}
