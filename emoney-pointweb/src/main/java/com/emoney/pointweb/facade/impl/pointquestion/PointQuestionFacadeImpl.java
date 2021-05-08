package com.emoney.pointweb.facade.impl.pointquestion;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.facade.pointquestion.PointQuestionFacade;
import com.emoeny.pointfacade.model.vo.PointProductVO;
import com.emoeny.pointfacade.model.vo.PointQuestionVO;
import com.emoney.pointweb.repository.dao.entity.PointQuestionDO;
import com.emoney.pointweb.service.biz.PointQuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lipengcheng
 * @date 2021-05-07
 */
@Service
@Slf4j
public class PointQuestionFacadeImpl implements PointQuestionFacade {

    @Autowired
    private PointQuestionService pointQuestionService;

    @Override
    public Result<PointQuestionVO> queryPointQuestion() {
        try{
            //设置开始时间
            Date startDate=DateUtil.parseDate("2021-05-05");
            Date nowDate = DateUtil.parseDate(DateUtil.today());

            List<PointQuestionDO> pointQuestionDOS = pointQuestionService.getAll();
            List<PointQuestionVO> pointQuestionVOS = JsonUtil.copyList(pointQuestionDOS,PointQuestionVO.class);
            PointQuestionVO result=new PointQuestionVO();

            //获取有时间的集合
            List<PointQuestionVO> hasDateList = pointQuestionVOS.stream().filter(x->x.getShowTime() !=null).collect(Collectors.toList());
            //获取没有时间的集合
            List<PointQuestionVO> noHasDateList = pointQuestionVOS.stream().filter(x->x.getShowTime() == null).collect(Collectors.toList());

            if(hasDateList.stream().anyMatch(x->x.getShowTime().equals(nowDate))){
                result = hasDateList.stream().filter(x->x.getShowTime().equals(nowDate)).findFirst().get();
            }else {
                if(noHasDateList.size()>0){
                    result=noHasDateList.get((int) DateUtil.between(startDate,nowDate, DateUnit.DAY)%noHasDateList.size());
                }
            }

            return Result.buildSuccessResult(result);
        }catch (Exception e){
            log.error("queryPointQuestion error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
