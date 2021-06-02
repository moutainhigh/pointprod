package com.emoney.pointweb.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.Buffer;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public class NetworkIntercepter implements Interceptor{
    @Override
    public Response intercept(Interceptor.Chain chain) {
        long start = System.currentTimeMillis();
        Response response=null;
        String responseBody = null;
        String responseCode = null;
        String url = null;
        String requestBody = null;
        try {
            Request request = chain.request();
            url = request.url().toString();
            requestBody = getRequestBody(request);
            response = chain.proceed(request);
            responseBody = response.body().string();
            responseCode = String.valueOf(response.code());
            MediaType mediaType = response.body().contentType();
            response = response.newBuilder().body(ResponseBody.create(mediaType,responseBody)).build();
        }
        catch (Exception e){
            log.error(e.getMessage());
        }
        finally {
            long end = System.currentTimeMillis();
            String duration = String.valueOf(end - start);
            log.info("responseTime= {}, requestUrl= {}, params={}, responseCode= {}, result= {}",
                    duration, url,requestBody,responseCode,responseBody);
        }

        return response;
    }

    private String getRequestBody(Request request) {
        String requestContent = "";
        if (request == null) {
            return requestContent;
        }
        RequestBody requestBody = request.body();
        if (requestBody == null) {
            return requestContent;
        }
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = Charset.forName("utf-8");
            requestContent = buffer.readString(charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestContent;
    }
}