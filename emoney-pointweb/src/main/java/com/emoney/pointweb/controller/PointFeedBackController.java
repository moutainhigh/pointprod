package com.emoney.pointweb.controller;

import com.emoney.pointweb.repository.dao.entity.PointFeedBackDO;
import com.emoney.pointweb.service.biz.PointFeedBackService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<String, Object> result=new HashMap<>();
        result.put("data",list);
        return result;
    };

    @RequestMapping("/editReply")
    @ResponseBody
    public String editReply(Integer id,String reply){
        PointFeedBackDO pointFeedBackDO=new PointFeedBackDO();
        return  null;
    }
}
