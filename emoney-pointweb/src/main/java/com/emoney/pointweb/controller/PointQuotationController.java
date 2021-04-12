package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoney.pointweb.repository.dao.entity.PointQuotationDO;
import com.emoney.pointweb.service.biz.PointQuotationService;
import com.emoney.pointweb.service.biz.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/pointquotation")
@Slf4j
public class PointQuotationController {

    @Resource
    private PointQuotationService pointQuotationService;

    @Resource
    private UserLoginService userLoginService;

    @RequestMapping
    public String index(){
        return "pointquotation/pointquotation.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String,Object> pageList(){
        List<PointQuotationDO> list=pointQuotationService.getAll();
        Map<String,Object> result=new HashMap<>();
        result.put("data",list);
        return result;
    }

    @RequestMapping("/edit")
    @ResponseBody
    public String edit(@RequestParam(required = false, defaultValue = "0") Integer id,
                       String content, HttpServletRequest request, HttpServletResponse response){
        try{
            TicketInfo user = userLoginService.GetLoginAdminUser(request,response);

            PointQuotationDO pointQuotationDO=new PointQuotationDO();
            pointQuotationDO.setId(id);
            pointQuotationDO.setContent(content);
            pointQuotationDO.setUpdateBy(user.UserName);
            pointQuotationDO.setUpdateTime(new Date());
            int result = 0;
            if(id>0){
                result=pointQuotationService.update(pointQuotationDO);
            }else {
                pointQuotationDO.setIsValid(true);
                pointQuotationDO.setCreateTime(new Date());
                pointQuotationDO.setCreateBy(user.UserName);
                result=pointQuotationService.insert(pointQuotationDO);
            }
            return result > 0 ? "success":"error";
        }catch (Exception e){
            log.error("语录保存出错："+e.toString());
        }
        return null;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam(required = false, defaultValue = "0") Integer id){
        PointQuotationDO pointQuotationDO=new PointQuotationDO();
        pointQuotationDO.setId(id);
        pointQuotationDO.setIsValid(false);

        int result= pointQuotationService.update(pointQuotationDO);
        return result > 0 ? "success":"error";
    }
}
