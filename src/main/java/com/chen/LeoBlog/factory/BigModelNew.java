package com.chen.LeoBlog.factory;

import com.google.gson.Gson;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class BigModelNew extends WebSocketListener {
    // 地址与鉴权信息
    public static final String hostUrl = "https://spark-api.xf-yun.com/v2.1/chat";
    //    @Value("${ai.sparkDesk.appid}")
    public static String appid = "299d3700";
    //    @Value("${ai.sparkDesk.api-secret}")
    public static String apiSecret = "NDk1MDQzNmJlYzkyM2QyMmU5MjNkZjY3";
    //    @Value("${ai.sparkDesk.api-key}")
    public static String apiKey = "e71fe93016e105c479e4b92a8a370553";

    // 环境治理的重要性  环保  人口老龄化  我爱我的祖国
    public static final String NewQuestion = "介绍下你自己的能力";

    public static final Gson gson = new Gson();

    // 个性化参数
    private String userId;
    private Boolean wsCloseFlag;

    // 构造函数
    public BigModelNew(String userId, Boolean wsCloseFlag) {
        this.userId = userId;
        this.wsCloseFlag = wsCloseFlag;
    }

    // 主函数
    public static void main(String[] args) throws Exception {
        // 构建鉴权url
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.toString().replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();
        // 个性化参数入口，如果是并发使用，可以在这里模拟
        for (int i = 0; i < 1; i++) {
            WebSocket webSocket = client.newWebSocket(request, new BigModelNew(i + "",
                    false));
        }
    }

    // 线程来发送音频与参数
    class MyThread extends Thread {
        private WebSocket webSocket;

        public MyThread(WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        public void run() {
            String requestJson;//请求参数json串
            try {
                requestJson = "{\n" +
                        "  \"header\": {\n" +
                        "    \"app_id\": \"" + appid + "\",\n" +
                        "    \"uid\": \"" + UUID.randomUUID().toString().substring(0, 10) + "\"\n" +
                        "  },\n" +
                        "  \"parameter\": {\n" +
                        "    \"chat\": {\n" +
                        "      \"domain\": \"general\",\n" +
                        "      \"temperature\": 0.5,\n" +
                        "      \"max_tokens\": 1024\n" +
                        "    }\n" +
                        "  },\n" +
                        "  \"payload\": {\n" +
                        "    \"message\": {\n" +
                        "      \"text\": [\n" +
                        "        {\n" +
                        "          \"role\": \"user\",\n" +
                        "          \"content\": \"中国第一个皇帝是谁？\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"role\": \"assistant\",\n" +
                        "          \"content\": \"秦始皇\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"role\": \"user\",\n" +
                        "          \"content\": \"秦始皇修的长城吗\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"role\": \"assistant\",\n" +
                        "          \"content\": \"是的\"\n" +
                        "        },\n" +
                        "        {\n" +
                        "          \"role\": \"user\",\n" +
                        "          \"content\": \"" + NewQuestion + "\"\n" +
                        "        }\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  }\n" +
                        "}";
                webSocket.send(requestJson);
                // System.err.println(requestJson);
                // 等待服务端返回完毕后关闭
                while (true) {
                    // System.err.println(wsCloseFlag + "---");
                    Thread.sleep(200);
                    if (wsCloseFlag) {
                        break;
                    }
                }
                webSocket.close(1000, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        MyThread myThread = new MyThread(webSocket);
        myThread.start();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // System.out.println(userId + "用来区分那个用户的结果" + text);
        JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
        if (myJsonParse.header.code != 0) {
            System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
            System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
            webSocket.close(1000, "");
        }
        List<Text> textList = myJsonParse.payload.choices.text;
        for (Text temp : textList) {
            System.out.print(temp.content);
        }
        if (myJsonParse.header.status == 2) {
            // 可以关闭连接，释放资源
            wsCloseFlag = true;
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                System.out.println("onFailure code:" + code);
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("connection failed");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }

    //返回的json结果拆解
    class JsonParse {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        int status;
        String sid;
    }

    class Payload {
        Choices choices;
    }

    class Choices {
        List<Text> text;
    }

    class Text {
        String role;
        String content;
    }
}