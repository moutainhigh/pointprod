package com.emoeny.pointcommon.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
public class JsonUtil {
    private JsonUtil() {
    }

    /***
     *  字符串转对象
     *
     * @param jsonString
     * @param t
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> T toBean(String jsonString, Class<T> t) {
        if (Strings.isNullOrEmpty(jsonString)) {
            return null;
        }
        return JSONObject.parseObject(jsonString, t);
    }

    /***
     *  字符串转Bean List
     *
     * @param jsonArrayString
     * @param t
     * @param <T>
     * @return
     */
    @SneakyThrows
    public static <T> List<T> toBeanList(String jsonArrayString, Class<T> t) {
        if (Strings.isNullOrEmpty(jsonArrayString)) {
            return Collections.emptyList();
        }
        return JSONObject.parseArray(jsonArrayString, t);
    }


    public static <E, T> List<T> copyList(List<E> list, Class<T> clzz) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList();
        }
        return JSONObject.parseArray(JSON.toJSONString(list), clzz);
    }

    public static String getValue(String jsonStr, String propertyName) {
        if (Strings.isNullOrEmpty(jsonStr)) {
            return null;
        }
        JSONObject obj=JsonUtil.toBean(jsonStr,JSONObject.class);
        String value=obj.getString(propertyName);
        return value;
    }
}

