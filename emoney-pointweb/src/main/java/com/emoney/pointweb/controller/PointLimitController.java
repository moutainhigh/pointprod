package com.emoney.pointweb.controller;

import com.emoney.pointweb.service.biz.PointLimitService;
import groovy.util.logging.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/pointlimit")
public class PointLimitController {

    @Resource
    private PointLimitService pointLimitService;

    @RequestMapping
    public String index(){
        return "pointlimit/pointlimit.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String,Object> pageList(){
        return pointLimitService.pageList();
    }
}
