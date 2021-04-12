package com.emoeny.pointcommon.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel工具类
 */
@Slf4j
public class ExcelUtils {

    /**
     * 将Excel内容转换list
     * @param file
     * @param name
     * @return
     * @throws Exception
     */
    public static List<Map<String, Object>> excelToList(MultipartFile file, String name) throws Exception {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheet(name);
        //行数
        int num = sheet.getLastRowNum();
        //列数
        int col = sheet.getRow(0).getLastCellNum();
        List<Map<String, Object>> list = new ArrayList<>();
        String[] colName = new String[col];
        //获取列名
        Row row = sheet.getRow(0);
        for (int i = 0; i < col; i++) {
            String[] s = row.getCell(i).getStringCellValue().split("-");
            colName[i] = s[0];
        }

        //将一行中每列数据放入一个map中,然后把map放入list
        for (int i = 1; i <= num; i++) {
            Map<String, Object> map = new HashMap<>();
            Row row1 = sheet.getRow(i);
            if (row1 != null) {
                for (int j = 0; j < col; j++) {
                    Cell cell = row1.getCell(j);
                    if (cell != null) {
                        cell.setCellType(CellType.STRING);
                        map.put(colName[j], cell.getStringCellValue());
                    }
                }
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 导出集合到excel
     * @param response
     * @param name
     * @param list
     */
    public static void exportToExcel(HttpServletResponse response, String name, List<LinkedHashMap<String, Object>> list) {
        try {
            //文件名称
            String fileName = name + ".xls";
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = hssfWorkbook.createSheet(name);
            int rowNum = 0;
            //新建行
            HSSFRow hssfRow = hssfSheet.createRow(rowNum++);
            //列
            int j = 0;
            if (list.size() > 0) {
                for (String i : list.get(0).keySet()) {
                    //新建第一行
                    hssfRow.createCell(j++).setCellValue(i);
                }
                //将数据放入表中
                for (int i = 0; i < list.size(); i++) {
                    //新建一行
                    HSSFRow row = hssfSheet.createRow(rowNum++);
                    Map map = list.get(i);
                    System.out.println(map);
                    j = 0;
                    for (Object obj : map.values()) {
                        if (obj != null) {
                            row.createCell(j++).setCellValue(obj.toString());
                        } else {
                            row.createCell(j++);
                        }
                    }
                }
            }
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            hssfWorkbook.write(response.getOutputStream());

        } catch (Exception e) {
            log.error("数据导出失败" + e.toString());
        }
    }

    /***
     * 解析Excel日期格式
     * @param strDate
     * @return
     */
    public static String ExcelDoubleToDate(String strDate) {
        if (strDate.length() == 5) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date tDate = DoubleToDate(Double.parseDouble(strDate));
                return sdf.format(tDate);
            } catch (Exception e) {
                e.printStackTrace();
                return strDate;
            }
        }
        return strDate;
    }

    /**
     * 解析Excel日期格式
     * @param dVal
     * @return
     */
    public static Date DoubleToDate(Double dVal) {
        Date tDate = new Date();
        //系统时区偏移 1900/1/1 到 1970/1/1 的 25569 天
        long localOffset = tDate.getTimezoneOffset() * 60000;
        tDate.setTime((long) ((dVal - 25569) * 24 * 3600 * 1000 + localOffset));
        return tDate;
    }
}
