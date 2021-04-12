package com.emoeny.pointfacade.facade.pointrecord;

import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointfacade.model.vo.PointTaskConfigVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/api/pointtask")
@Validated
public interface PointTaskConfigFacade {
    /**
     * 根据用户id查询所有有效的任务
     */
    @GetMapping("/query")
    Result<List<PointTaskConfigVO>> queryPointTaskConfigs(@NotNull(message = "用户id不能为空") Long uid,@NotNull(message = "产品版本不能为空") String productVersion,@NotNull(message = "发布平台不能为空") String publishPlatFormType);

    /**
     * 根据用户id查询所有有效的任务
     */
    @GetMapping("/querytaskinfo")
    Result<List<PointTaskConfigVO>> queryPointTaskConfig(@NotNull(message = "任务id不能为空") Long taskId,String subId);

    /**
     * 根据用户id,任务类型查询所有的任务
     */
    @GetMapping("/querybytasktype")
    Result<List<PointTaskConfigVO>> queryPointTaskConfigsByTaskType(@NotNull(message = "用户id不能为空") Long uid,@NotNull(message = "产品版本不能为空") String productVersion,@NotNull(message = "发布平台不能为空") String publishPlatFormType,@NotNull(message = "任务类型不能为空") Integer taskType);


    /**
     * 根据用户id,任务类型查询所有的任务
     */
    @GetMapping("/querybytaskids")
    Result<List<PointTaskConfigVO>> queryPointTaskConfigsByTaskIds(@NotNull(message = "taskids不能为空") String taskIds);

}
