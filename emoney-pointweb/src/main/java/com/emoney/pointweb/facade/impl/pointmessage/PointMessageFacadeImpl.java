package com.emoney.pointweb.facade.impl.pointmessage;

import com.emoeny.pointcommon.enums.MessageTypeEnum;
import com.emoeny.pointcommon.result.Result;
import com.emoeny.pointcommon.utils.JsonUtil;
import com.emoeny.pointfacade.facade.pointmessage.PointMessageFacade;
import com.emoeny.pointfacade.model.vo.PointMessageVO;
import com.emoney.pointweb.repository.dao.entity.PointAnnounceDO;
import com.emoney.pointweb.service.biz.PointAnnounceService;
import com.emoney.pointweb.service.biz.PointMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : meixiaohu
 * @create 2021/4/12 15:37
 */
@RestController
@Validated
@Slf4j
public class PointMessageFacadeImpl implements PointMessageFacade {

    @Autowired
    private PointMessageService pointMessageService;

    @Autowired
    private PointAnnounceService pointAnnounceService;

//    @Override
//    public Result<List<PointMessageVO>> queryPointMessages(@NotNull(message = "用户id不能为空") Long uid) {
//        try {
//            return Result.buildSuccessResult(JsonUtil.copyList(pointMessageService.getByUid(uid), PointMessageVO.class));
//        } catch (Exception e) {
//            log.error("queryPointMessages error:", e);
//            return Result.buildErrorResult(e.getMessage());
//        }
//    }

    @Override
    public Result<List<PointMessageVO>> queryPointMessages(@NotNull(message = "用户id不能为空") Long uid, @NotNull(message = "产品版本不能为空") String productVersion, @NotNull(message = "查询类型不能为空") Integer queryType) {
        try {
            //即将到期，最新活动
            if (queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE1.getCode())) || queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE3.getCode()))) {
                return Result.buildSuccessResult(JsonUtil.copyList(pointMessageService.getByUid(uid), PointMessageVO.class));
            }
            //商品上新，活动上新
            else {
                List<PointAnnounceDO> pointAnnounceDOS=pointAnnounceService.getPointAnnouncesByType(queryType);

            }
            return  null;

        } catch (Exception e) {
            log.error("queryPointMessages error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
