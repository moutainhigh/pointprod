package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.utils.ExcelUtils;
import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.service.biz.PointFeedBackService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lipengcheng
 * @date 2021-04-15
 */
@Controller
@RequestMapping("/pointfeedback")
public class PointFeedBackController {

    @Resource
    private PointFeedBackService pointFeedBackService;

    @RequestMapping
    public String index(){return "/pointfeedback/pointfeedback.index";};

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(Integer classType,Integer isReply){
        List<PointFeedBackDO> list=pointFeedBackService.getAll();
        if(!classType.equals(0)){
            list=list.stream().filter(x->x.getFeedType().equals(classType)).collect(Collectors.toList());
        }
        if(!isReply.equals(0)){
            if(isReply.equals(1)){
                list=list.stream().filter(x-> !StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
            }else {
                list=list.stream().filter(x-> StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
            }
        }
        Map<String, Object> result=new HashMap<>();
        result.put("data",list);
        return result;
    };

    @RequestMapping("/editReply")
    @ResponseBody
    public String editReply(Integer id,String remark){
        PointFeedBackDO pointFeedBackDO=new PointFeedBackDO();
        pointFeedBackDO.setId(id);
        pointFeedBackDO.setRemark(remark);
        int result = pointFeedBackService.update(pointFeedBackDO);
        return result>0?"success":"回复失败";
    }

    @RequestMapping("/adopt")
    @ResponseBody
    public String adopt(Integer id){
        PointFeedBackDO pointFeedBackDO=new PointFeedBackDO();
        pointFeedBackDO.setId(id);
        pointFeedBackDO.setStatus(1);
        int result = pointFeedBackService.update(pointFeedBackDO);
        return result>0?"success":"采纳失败";
    }

    @RequestMapping("/exportData")
    public String exportData(HttpServletResponse response, Integer classType,Integer isReply){
        List<PointFeedBackDO> list=pointFeedBackService.getAll();
        if(!classType.equals(0)){
            list=list.stream().filter(x->x.getFeedType().equals(classType)).collect(Collectors.toList());
        }
        if(!isReply.equals(0)){
            if(isReply.equals(1)){
                list=list.stream().filter(x-> !StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
            }else {
                list=list.stream().filter(x-> StringUtils.isEmpty(x.getRemark())).collect(Collectors.toList());
            }
        }
        List<LinkedHashMap<String,Object>> maps=new ArrayList<>();

        if(list!=null&&list.size()>0){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for (PointFeedBackDO item:list) {
                LinkedHashMap<String,Object> map=new LinkedHashMap<>();
                map.put("反馈类型",getClassTypeName(item.getFeedType()));
                map.put("提交时间",formatter.format(item.getCreateTime()));
                map.put("产品版本",item.getPid());
                map.put("账号",item.getAccount());
                map.put("邮箱",item.getEmail());
                map.put("用户建议内容",item.getSuggest());
                map.put("客服处理意见",item.getRemark());
                map.put("最新进展",StringUtils.isEmpty(item.getRemark())?"待处理":"已处理");
                map.put("是否采纳",item.getStatus().equals(1)?"是":"否");

                maps.add(map);
            }
        }else {
            LinkedHashMap<String,Object> map=new LinkedHashMap<>();
            map.put("反馈类型","");
            map.put("提交时间","");
            map.put("产品版本","");
            map.put("账号","");
            map.put("邮箱","");
            map.put("用户建议内容","");
            map.put("客服处理意见","");
            map.put("最新进展","");
            map.put("是否采纳","");

            maps.add(map);
        }
        ExcelUtils.exportToExcel(response,"意见反馈", maps);
        return null;
    }

    public String getClassTypeName(Integer feedType){
        String result="";
        switch (feedType)
        {
            case 1:
                result= "产品建议";
                break;
            case 2:
                result= "使用心得";
                break;
            case 3:
                result= "提问咨询";
                break;
            case 4:
                result= "其他建议";
                break;
            default:
                result= "";
                break;
        }
        return result;
    }
}
