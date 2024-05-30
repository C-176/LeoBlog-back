package com.chen.LeoBlog.factory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SparkDeskAiFactory {

    // 地址与鉴权信息
    public static final String hostUrl = "https://spark-api.xf-yun.com/v2.1/chat";
    //    @Value("${ai.sparkDesk.appid}")
    public static String appid = "299d3700";
    //    @Value("${ai.sparkDesk.api-secret}")
    public static String apiSecret = "NDk1MDQzNmJlYzkyM2QyMmU5MjNkZjY3";
    //    @Value("${ai.sparkDesk.api-key}")
    public static String apiKey = "e71fe93016e105c479e4b92a8a370553";

    private static String url;


    @PostConstruct
    public void init() {
        try {
            String authUrl = SparkDeskAi.getAuthUrl(hostUrl, apiKey, apiSecret);
            url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static SparkDeskAi getInstance(Long userId) {

        OkHttpClient client = new OkHttpClient.Builder().build();
        System.out.println(url);
        Request request = new Request.Builder().url(url).build();
        SparkDeskAi sparkDeskAi = new SparkDeskAi(userId);
        client.newWebSocket(request, sparkDeskAi);
        return sparkDeskAi;
    }


}

