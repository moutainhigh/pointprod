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
     * 根据用户id分页查询积分记录
     */
    @GetMapping("/querybypager")
    Result<List<PointRecordVO>> queryPointRecords(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "查询类型不能为空") Integer queryType, @NotNull(message = "pageIndex不能为空") Integer pageIndex, @NotNull(message = "pageSize不能为空") Integer pageSize) ;

    /**
     * 根据用户id查询当前待领取积分
     *
     * @param uid
     * @return
     */
    @GetMapping("/queryunclaimedrecordpoints")
    Result<Float> queryUnclaimedRecordPoints(@NotNull(message = "用户id不能为空") Long uid);

    /**
     * 根据用户id查询当前待领任务记录
     * @param uid
     * @return
     */
    @GetMapping("/queryunclaimedrecords")
    Result<List<PointRecordVO>> queryUnclaimedRecords(@NotNull(message = "用户id不能为空") Long uid);
    /**
     * 根据任务id查询用户积分记录
     *
     * @param pointRecordQueryByTaskIdsDTO
     * @return
     */
    @PostMapping("/querybytaskids")
    Result<List<PointRecordVO>> queryPointRecordsByTaskids(@RequestBody @Valid PointRecordQueryByTaskIdsDTO pointRecordQueryByTaskIdsDTO);


    /**
     * 刷新所缓存
     * @return
     */
    @GetMapping("/clearredis")
    Result<Object> clearRedis();

    /**
     * 根据uid刷新缓存
     * @param uid
     * @return
     */
    @GetMapping("/clearredisbyuid")
    Result<Object> clearRedisByUid(@NotNull(message = "用户id不能为空") Long uid);

}
