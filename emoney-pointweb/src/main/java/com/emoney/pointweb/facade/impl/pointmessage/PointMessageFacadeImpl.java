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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

            List<PointMessageVO> pointMessageVOS = new ArrayList<>();
            PointMessageVO pointMessageVO = null;
            List<Integer> mstTypes = new ArrayList<>();
            //即将到期,待支付
            if (queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE0.getCode())) || queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE1.getCode())) || queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE3.getCode()))) {
                pointMessageVOS = JsonUtil.copyList(pointMessageService.getByUid(uid), PointMessageVO.class);
                if (pointMessageVOS != null) {
                    if (!queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE0.getCode()))) {
                        pointMessageVOS = pointMessageVOS.stream().filter(h -> h.getMsgType().equals(queryType)).collect(Collectors.toList());
                    }
                }
            }
            //商品上新，活动上新
            if (queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE0.getCode())) || queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE2.getCode())) || queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE4.getCode()))) {
                if(queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE0.getCode()))){
                    mstTypes.add(Integer.valueOf(MessageTypeEnum.TYPE2.getCode()));
                    mstTypes.add(Integer.valueOf(MessageTypeEnum.TYPE4.getCode()));
                }
                else if(queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE2.getCode()))){
                    mstTypes.add(Integer.valueOf(MessageTypeEnum.TYPE2.getCode()));
                }
                else if(queryType.equals(Integer.valueOf(MessageTypeEnum.TYPE4.getCode()))){
                    mstTypes.add(Integer.valueOf(MessageTypeEnum.TYPE4.getCode()));
                }
                List<PointAnnounceDO> pointAnnounceDOS = pointAnnounceService.getPointAnnouncesByType(mstTypes);
                if (pointAnnounceDOS != null) {
                    for (PointAnnounceDO p : pointAnnounceDOS
                    ) {
                        pointMessageVO = new PointMessageVO();
                        pointMessageVO.setUid(uid);
                        pointMessageVO.setMsgType(queryType);
                        pointMessageVO.setMsgContent(p.getMsgContent());
                        pointMessageVO.setMsgSrc(p.getMsgSrc());
                        pointMessageVO.setCreateTime(p.getPublishTime());
                        pointMessageVOS.add(pointMessageVO);
                    }
                }
            }
            return Result.buildSuccessResult(pointMessageVOS);
        } catch (Exception e) {
            log.error("queryPointMessages error:", e);
            return Result.buildErrorResult(e.getMessage());
        }
    }
}
