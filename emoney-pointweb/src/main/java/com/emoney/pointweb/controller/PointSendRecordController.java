package com.emoney.pointweb.controller;

import com.emoeny.pointcommon.utils.ExcelUtils;
import com.emoney.pointweb.repository.dao.entity.PointTaskConfigInfoDO;
import com.emoney.pointweb.repository.dao.entity.vo.PointSendRecordVO;
import com.emoney.pointweb.service.biz.PointSendRecordService;
import com.emoney.pointweb.service.biz.PointTaskConfigInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/pointsendrecord")
public class PointSendRecordController {
    @Resource
    private PointSendRecordService pointSendRecordService;

    @Resource
    private PointTaskConfigInfoService pointTaskConfigInfoService;

    @RequestMapping
    public String index(Model model){
        List<PointTaskConfigInfoDO> list = pointTaskConfigInfoService.getPointTaskConfigInfoByIsDirectional();
        model.addAttribute("tasklist", list.stream().sorted(Comparator.comparing(PointTaskConfigInfoDO::getUpdateTime).reversed()).collect(Collectors.toList()));
        return "pointsendrecord/pointsendrecord.index";
    }

    //获取发放记录
    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String,Object> pageList(@RequestParam(required = false, defaultValue = "0") int pointtype){
        return pointSendRecordService.DataStatistics(pointtype);
    }

    @RequestMapping("/pointSendRecordDetail")
    public String pointSendRecordDetail(Model model,String batchId){
        model.addAttribute("message",batchId);
        return "pointsendrecord/pointsendrecord.detail";
    }

    @RequestMapping("/getPointSendRecordByBatchId")
    @ResponseBody
    public Map<String,Object> getPointSendRecordByBatchId(String batchId,String status){
        return pointSendRecordService.getPointSendRecordByBatchId(batchId,status);
    }

    @RequestMapping("/exportUserData")
    public String exportUserData(HttpServletResponse response,String batchId,String status){
        List<PointSendRecordVO> list = (List<PointSendRecordVO>)pointSendRecordService.getPointSendRecordByBatchId(batchId,status).get("data");

        return null;
    }

    @RequestMapping("/importUserData")
    @ResponseBody
    public Map<String,Object> importUserData(MultipartFile file,@RequestParam(required = false, defaultValue = "0")  Long taskId,String ver,String EMtext,String remark){
        Map<String,Object> result=new HashMap<>();
        try {
            List<Map<String,Object>> userdata=new ArrayList<>();
            if(ver.equals("2")){
                Map<String,Object> map=new HashMap<>();
                map.put("EM",EMtext);
                userdata.add(map);
            }
            else {
                if (file==null) return null;
                userdata = ExcelUtils.excelToList(file,"Sheet1");
            }

            result= pointSendRecordService.PointUserSend(userdata,taskId,remark);

        }catch (Exception e) {
            log.error("导入名单出错："+e);
        }
        return result;
    }
}
