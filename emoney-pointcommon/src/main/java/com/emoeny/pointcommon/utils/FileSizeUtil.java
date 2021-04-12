package com.emoeny.pointcommon.utils;


import java.text.DecimalFormat;

public class FileSizeUtil {

    /**
     * 定义GB的计算常量
     */
    private static final long GB = 1024 * 1024 * 1024;

    /**
     * 定义MB的计算常量
     */
    private static final long MB = 1024 * 1024;

    /**
     * 定义KB的计算常量
     */
    private static final long KB = 1024;

    /**
     * 格式化小数
     */
    private static DecimalFormat df = new DecimalFormat("###.0");

    public static String getSizeString(long size) {
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            return df.format(size / (float) GB) + "GB";
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            return df.format(size / (float) MB) + "MB";
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            return df.format(size / (float) KB) + "KB";
        } else {
            return size + "B";
        }
    }
}