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
    public String index(Model model) {
        List<PointTaskConfigInfoDO> list = pointTaskConfigInfoService.getPointTaskConfigInfoByIsDirectional();
        model.addAttribute("tasklist", list.stream().filter(h -> h.getTaskStartTime().after(new Date()) && h.getTaskEndTime().before(new Date())).sorted(Comparator.comparing(PointTaskConfigInfoDO::getUpdateTime).reversed()).collect(Collectors.toList()));
        return "pointsendrecord/pointsendrecord.index";
    }

    //获取发放记录
    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int pointtype) {
        return pointSendRecordService.DataStatistics(pointtype);
    }

    @RequestMapping("/pointSendRecordDetail")
    public String pointSendRecordDetail(Model model, String batchId) {
        model.addAttribute("batchId", batchId);
        return "pointsendrecord/pointsendrecord.detail";
    }

    @RequestMapping("/getPointSendRecordByBatchId")
    @ResponseBody
    public Map<String, Object> getPointSendRecordByBatchId(String batchId, String status) {
        return pointSendRecordService.getPointSendRecordByBatchId(batchId, status);
    }

    @RequestMapping("/exportData")
    public String exportData(HttpServletResponse response, String classType, String batchId) {
        List<PointSendRecordVO> list = pointSendRecordService.queryPointSendRecordBybatchIdAndstatus(batchId, classType);
        List<LinkedHashMap<String, Object>> maps = new ArrayList<>();
        if (list != null && list.size() > 0) {
            for (PointSendRecordVO item : list) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("类型", item.getTaskPoint() > 0 ? "赠送" : "扣除");
                map.put("任务名称", item.getTaskName());
                map.put("调整说明", item.getRemark());
                map.put("用户账号", item.getEmNo());
                map.put("分值", item.getTaskPoint());
                map.put("状态", item.getSendStatus() > 0 ? "成功" : "失败");
                map.put("状态原因", item.getSendResult());

                maps.add(map);
            }
        } else {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("类型", "");
            map.put("任务名称", "");
            map.put("调整说明", "");
            map.put("用户账号", "");
            map.put("分值", "");
            map.put("状态", "");
            map.put("状态原因", "");

            maps.add(map);
        }

        ExcelUtils.exportToExcel(response, "发送明细", maps);
        return null;
    }

    @RequestMapping("/exportUserData")
    public String exportUserData(HttpServletResponse response, String batchId, String status) {
        List<PointSendRecordVO> list = (List<PointSendRecordVO>) pointSendRecordService.getPointSendRecordByBatchId(batchId, status).get("data");

        return null;
    }

    @RequestMapping("/importUserData")
    @ResponseBody
    public Map<String, Object> importUserData(MultipartFile file, @RequestParam(required = false, defaultValue = "0") Long taskId, String ver, String EMtext, String remark) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> userdata = new ArrayList<>();
            if (ver.equals("2")) {
                Map<String, Object> map = new HashMap<>();
                map.put("EM", EMtext);
                userdata.add(map);
            } else {
                if (file == null) {
                    return null;
                }
                userdata = ExcelUtils.excelToList(file, "Sheet1");
            }

            result = pointSendRecordService.PointUserSend(userdata, taskId, remark);

        } catch (Exception e) {
            log.error("导入名单出错：" + e);
        }
        return result;
    }
}
