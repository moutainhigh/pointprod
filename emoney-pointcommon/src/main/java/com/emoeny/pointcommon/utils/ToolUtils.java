package com.emoeny.pointcommon.utils;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static cn.hutool.core.date.DateUtil.date;

@Slf4j
public class ToolUtils {

    @SneakyThrows
    public static long GetExpireTime(int randomStep){
        long expireTime = DateUtil.between(date(), DateUtil.endOfDay(date()), DateUnit.SECOND) - RandomUtil.randomInt(0, randomStep);
        if(expireTime<0){
            expireTime=randomStep;
        }
        return expireTime;
    }
}
