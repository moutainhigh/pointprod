package com.emoney.pointweb.service.biz.impl;

import cn.hutool.core.util.IdUtil;
import com.emoeny.pointcommon.result.ApiResult;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.result.userinfo.TicketInfo;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointcommon.utils.OkHttpUtil;
import com.emoeny.pointfacade.model.dto.PointRecordCreateDTO;
import com.emoney.pointweb.repository.dao.entity.PointSendRecordDO;
import com.emoney.pointweb.repository.dao.entity.vo.PointSendRecordVO;
import com.emoney.pointweb.repository.dao.mapper.PointSendRecordMapper;
import com.emoney.pointweb.service.biz.PointRecordService;
import com.emoney.pointweb.service.biz.PointSendRecordService;
import com.emoney.pointweb.service.biz.UserLoginService;
import groovy.util.logging.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PointSendRecordServiceImp implements PointSendRecordService {
    @Autowired
    private PointSendRecordMapper pointSendRecordMapper;

    @Autowired
    private PointRecordService pointRecordService;

    @Autowired
    private UserLoginService userLoginService;

    @Value("${getuseruidurl}")
    private String getuseruidurl;

    public Map<String, Object> DataStatistics(int pointtype) {
        List<PointSendRecordVO> list= pointSendRecordMapper.getDataStatistics(pointtype);

        Map<String,Object> maps=new HashMap<String, Object>();
        maps.put("data", list);
        return maps;
    }

    public  Map<String,Object> getPointSendRecordByBatchId(String batch_id,String status){
        List<PointSendRecordVO> list= pointSendRecordMapper.getPointSendRecordByBatchId(batch_id,status);

        Map<String,Object> maps=new HashMap<String, Object>();
        maps.put("data", list);
        return maps;
    }

    @Override
    public Map<String,Object> PointUserSend(List<Map<String,Object>> userList,long taskId,String remark){
        Map<String,Object> resultMap=new HashMap<>();
        int successCount=0;
        int errorCount=0;
        if(userList!=null&&userList.size()>0){
            PointRecordCreateDTO pointRecordCreateDTO=new PointRecordCreateDTO();
            pointRecordCreateDTO.setTaskId(taskId);
            pointRecordCreateDTO.setRemark(remark);

            PointSendRecordDO pointSendRecordDO=new PointSendRecordDO();
            pointSendRecordDO.setBatchId(IdUtil.objectId());
            pointSendRecordDO.setTaskId(taskId);
            pointSendRecordDO.setCreateBy("admin");
            pointSendRecordDO.setUpdateBy("admin");
            pointSendRecordDO.setCreateTime(new Date());
            pointSendRecordDO.setUpdateTime(new Date());
            pointSendRecordDO.setRemark(remark);
            for (var item:userList) {
                Map<String,String> stringMap=new HashMap<>();
                stringMap.put("gate_appid","10015");
                stringMap.put("userName",item.get("EM").toString());
                stringMap.put("createLogin","0");
                String res= OkHttpUtil.get(getuseruidurl,stringMap);
                String apiResult= JsonUtil.getValue(res, "Message");
                String message=JsonUtil.getValue(apiResult,"Message");
                String Uid= JsonUtil.getValue(message, "PID");
                pointRecordCreateDTO.setEmNo(item.get("EM").toString());
                pointRecordCreateDTO.setUid(Long.valueOf(Uid));
                pointSendRecordDO.setEmNo(item.get("EM").toString());
                pointSendRecordDO.setUid(Long.valueOf(Uid));

                Result<Object> result = pointRecordService.createPointRecord(pointRecordCreateDTO);

                pointSendRecordDO.setSendResult(result.getMsg());
                if(result.isSuccess()){
                    pointSendRecordDO.setSendStatus(1);
                    successCount += 1;
                }else {
                    pointSendRecordDO.setSendStatus(0);
                    errorCount += 1;
                }
                pointSendRecordMapper.insert(pointSendRecordDO);
            }
        }
        resultMap.put("success",successCount);
        resultMap.put("error",errorCount);
        return resultMap;
    }
}
