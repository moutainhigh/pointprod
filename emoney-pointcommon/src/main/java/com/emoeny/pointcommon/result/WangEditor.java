package com.emoeny.pointcommon.result;

import lombok.Data;

/**
 * @author lipengcheng
 * @date 2021-04-14
 */
@Data
public class WangEditor {
    /*
     * 错误代码，0 表示没有错误。
     */
    private Integer errno;
    /*
     * 已上传的图片路径
     */
    private String[] data;


    public WangEditor(String[] data) {
        super();
        this.errno = 0;
        this.data = data;
    }
}
