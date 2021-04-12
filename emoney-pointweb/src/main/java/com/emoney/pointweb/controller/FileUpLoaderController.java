package com.emoney.pointweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/fileuploader")
@Slf4j
public class FileUpLoaderController {

    @Value("${fileurl}")
    private String fileurl;

    @RequestMapping(value = "/upload")
    @ResponseBody
    public String uploadFile(MultipartFile file) throws Exception {
        try {

            // 换行符
            final String newLine = "\r\n";
            final String boundaryPrefix = "--";
            // 定义数据分隔线
            String BOUNDARY = "------------7d4a6d158c9";
            // 服务器的域名
            URL url = new URL(fileurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置为POST情
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求头参数
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Charsert", "gb2312");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            // 上传文件
            String Point="Point";

            StringBuilder sb = new StringBuilder();
            sb.append(boundaryPrefix);
            sb.append(BOUNDARY);
            sb.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb.append("Content-Disposition: form-data;name=\"module\"");
            sb.append(newLine);
            sb.append(newLine);

            out.write(sb.toString().getBytes(),0,sb.length());
            out.write(Point.getBytes(),0,Point.length());
            out.write(newLine.getBytes(),0,newLine.length());

            StringBuilder sb1 = new StringBuilder();
            String fileName = file.getOriginalFilename();
            sb1.append(boundaryPrefix);
            sb1.append(BOUNDARY);
            sb1.append(newLine);
            // 文件参数,photo参数名可以随意修改
            sb1.append("Content-Disposition: form-data;name=\"file1\";filename=\"" + fileName  + "\"" + newLine);
            sb1.append("Content-Type:application/octet-stream");
            // 参数头设置完以后需要两个换行，然后才是参数内容
            sb1.append(newLine);
            sb1.append(newLine);

            // 将参数头的数据写入到输出流中
            out.write(sb1.toString().getBytes());

            // 数据输入流,用于读取文件数据
            DataInputStream in = new DataInputStream(file.getInputStream());
            byte[] bufferOut = new byte[1024];
            int bytes = 0;
            // 每次读1KB数据,并且将文件数据写入到输出流中
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            // 最后添加换行
            out.write(newLine.getBytes());
            in.close();

            // 定义最后数据分隔线，即--加上BOUNDARY再加上--。
            byte[] end_data = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes();

            // 写上结尾标识
            out.write(end_data);
            out.flush();
            out.close();

            // 定义BufferedReader输入流来读取URL的响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));

            String line = null;
            StringBuffer strs = new StringBuffer("");
            while ((line = reader.readLine()) != null) {
                strs.append(line);
            }

            System.out.println(strs);
            String txt = strs.toString();
            return txt;

        } catch (Exception e) {
            log.error("上传文件发生错误："+e);
        }

        return null;
    }
}
