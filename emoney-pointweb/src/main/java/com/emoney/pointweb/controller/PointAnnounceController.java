package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;
import com.emoney.pointweb.repository.dao.entity.PointOrderDO;
import com.emoney.pointweb.service.biz.PointAnnounceService;
import com.emoney.pointweb.service.biz.UserLoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
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

/**
 * @author lipengcheng
 * @date 2021-04-13
 */
@Controller
@Slf4j
@RequestMapping("/pointannounce")
public class PointAnnounceController {

    @Resource
    private PointAnnounceService pointAnnounceService;

    @Resource
    private UserLoginService userLoginService;

    @RequestMapping
    public String index(){
        return "/pointannounce/pointannounce.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String,Object> pageList(Integer msgType){
        List<PointAnnounceDO> list=pointAnnounceService.getAll();
        if(msgType!=null&&!msgType.equals(0)){
            list=list.stream().filter(x->x.getMsgType().equals(msgType)).collect(Collectors.toList());
        }
        Map<String,Object> result=new HashMap<>();
        result.put("data",list);
        return result;
    }

    @RequestMapping("/edit")
    @ResponseBody
    public String edit(@RequestParam(required = false, defaultValue = "0")Integer id, Integer msgType, String msgContent,
                       String msgSrc, String productVersion, String publishTime, String remark,
                       HttpServletRequest request, HttpServletResponse response){
        try {
            TicketInfo user = userLoginService.getLoginAdminUser(request,response);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            PointAnnounceDO pointAnnounceDO=new PointAnnounceDO();
            pointAnnounceDO.setMsgType(msgType);
            pointAnnounceDO.setMsgContent(msgContent);
            pointAnnounceDO.setMsgSrc(msgSrc);
            pointAnnounceDO.setProductVersion(productVersion);
            pointAnnounceDO.setPublishTime(sdf.parse(publishTime));
            pointAnnounceDO.setRemark(remark);
            pointAnnounceDO.setUpdateTime(new Date());
            pointAnnounceDO.setUpdateBy(user.UserName);
            Integer result=0;
            if(id>0){
                pointAnnounceDO.setId(id);
                result=pointAnnounceService.update(pointAnnounceDO);
            }else {
                pointAnnounceDO.setCreateBy(user.UserName);
                pointAnnounceDO.setCreateTime(new Date());
                result=pointAnnounceService.insert(pointAnnounceDO);
            }
            return result>0?"success":"保存失败";
        }catch (Exception e){
            log.error("保存消息通知失败："+e);
        }
        return null;
    }
}
