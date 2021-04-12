package com.emoeny.pointfacade.facade.pointrecord;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.dto.PointRecordCreateDTO;
import com.emoeny.pointfacade.model.dto.PointRecordQueryByTaskIdsDTO;
import com.emoeny.pointfacade.model.dto.PointRecordRecevieDTO;
import com.emoeny.pointfacade.model.vo.PointRecordSummaryVO;
import com.emoeny.pointfacade.model.vo.PointRecordVO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@RequestMapping("/api/pointrecord")
@Validated
public interface PointRecordFacade {

    /**
     * 对外提供服务，增加积分
     */
    @PostMapping("/add")
    Result<Object> createPointRecord(@RequestBody @Valid PointRecordCreateDTO pointRecordCreateDTO);

    /**
     * 根据用户id查询用户积分记录，每日任务只返回当日积分
     */
    @GetMapping("/query")
    Result<List<PointRecordVO>> queryPointRecords(@NotNull(message = "用户id不能为空") Long uid);

    /**
     * PointRecordFacade
     * 领取任务
     */
    @PostMapping("/recevie")
    Result<Object> receviePointRecord(@RequestBody @Valid PointRecordRecevieDTO pointRecordRecevieDTO);

    /**
     * 根据用户id查询用户积分总计
     */
    @GetMapping("/querysummary")
    Result<List<PointRecordSummaryVO>> queryPointRecordSummary(@NotNull(message = "用户id不能为空") Long uid);

    /**
     * 根据用户id查询用户指定时间段（当月）积分总计
     *
     * @param uid
     * @param dtStart
     * @param dtEnd
     * @return
     */
    @GetMapping("/querysummarybycreatetime")
    Result<List<PointRecordSummaryVO>> queryPointRecordSummaryByCreateTime(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "开始时间不能为空") Date dtStart, @NotNull(message = "结束时间不能为空") Date dtEnd);


    /**
     * 根据用户id查询时间内所有用户的积分记录
     */
    @GetMapping("/querybytimeperiod")
    Result<List<PointRecordVO>> queryPointRecords(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "开始时间不能为空") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dtStart, @NotNull(message = "结束时间不能为空") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date dtEnd);

    /**
     * 根据用户id查询当前待领取积分
     *
     * @param uid
     * @return
     */
    @GetMapping("/queryunclaimedrecordpoints")
    Result<Float> queryUnclaimedRecordPoints(@NotNull(message = "用户id不能为空") Long uid);

    /**
     * 根据任务id查询用户积分记录
     *
     * @param pointRecordQueryByTaskIdsDTO
     * @return
     */
    @PostMapping("/querybytaskids")
    Result<List<PointRecordVO>> queryPointRecordsByTaskids(@RequestBody @Valid PointRecordQueryByTaskIdsDTO pointRecordQueryByTaskIdsDTO);

}
