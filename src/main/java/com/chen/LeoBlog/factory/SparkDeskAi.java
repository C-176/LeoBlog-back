package com.chen.LeoBlog.factory;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.chen.LeoBlog.utils.AssertUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.HttpUrl;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SparkDeskAi extends WebSocketListener {
    // 地址与鉴权信息
    public static final String hostUrl = "https://spark-api.xf-yun.com/v2.1/chat";
    //    @Value("${ai.sparkDesk.appid}")
    public static String appid = "299d3700";
    //    @Value("${ai.sparkDesk.api-secret}")
    public static String apiSecret = "NDk1MDQzNmJlYzkyM2QyMmU5MjNkZjY3";
    //    @Value("${ai.sparkDesk.api-key}")
    public static String apiKey = "e71fe93016e105c479e4b92a8a370553";

    public final int contextNumber = 5;
    private boolean wsCloseFlag = false;
    private List<Record> chatList = new ArrayList<>();
    private Long userId;
    private WebSocket webSocket;

    SparkDeskAi(Long userId) {
        this.userId = userId;
    }

    // 将消息添加到chatList中，若是上下文消息查过限制，去除最早的。LFU
    public void addToList(String s) {
        chatList.add(Record.builder().content(s).role("user").build());
        if (chatList.size() > contextNumber) chatList.remove(0);
    }

    public void send(String text) {
        AssertUtil.isTrue(webSocket != null, "websocket链接断开，发送消息失败");
        String requestJson;//请求参数json串
        try {
            addToList(text);
            AssertUtil.isFalse(chatList.isEmpty(), "无效信息");
            requestJson = JSONUtil.toJsonStr(new SparkReq(chatList));
            webSocket.send(requestJson);
            // 等待服务端返回完毕后关闭
            while (true) {
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


    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        // todo：监听器启动之后，暴露websocket
        this.webSocket = webSocket;
        System.out.println("onOpen");
    }


    @Override
    public void onMessage(WebSocket webSocket, String text) {
        SparkResp myJsonParse = null;
        try {
            myJsonParse = JSONUtil.toBean(text, SparkResp.class);
            System.out.println(myJsonParse);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        SparkRespHeader header = myJsonParse.getHeader();
        int code = header.getCode();
        if (code != 0) {
            System.out.println("发生错误，错误码为：" + header.code);
            System.out.println("本次请求的sid为：" + header.sid);
            webSocket.close(1000, "");
            wsCloseFlag = true;
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
            }
        } catch (IOException e) {
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
                addQueryParameter("host", url.getHost()).
                build();
        return httpUrl.toString();
    }


}


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class SparkReq {
    SparkReqHeader header;
    Parameter parameter;
    SparkReqPayload payload;

    //    @Value("${ai.sparkDesk.appid}")
    public static String appid = "299d3700";


    public SparkReq(String content) {
        this.header = SparkReqHeader.builder().app_id(appid).uid(UUID.randomUUID().toString()).build();
        this.parameter = Parameter.builder().chat(Chat.builder().domain("general").temperature(0.5).max_tokens(1024).build()).build();
        Record record1 = Record.builder().role("user").content(content).build();
        this.payload = new SparkReqPayload(Collections.singletonList(record1));
    }

    public SparkReq(List<Record> list) {
        System.out.println(appid);
        this.header = SparkReqHeader.builder().app_id(appid).uid(UUID.randomUUID().toString()).build();
        this.parameter = Parameter.builder().chat(Chat.builder().domain("general").temperature(0.5).max_tokens(1024).build()).build();
        this.payload = new SparkReqPayload(list);
    }

}

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
class SparkReqHeader {
    String app_id;
    String uid;
}

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
class Parameter {
    Chat chat;

}

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
class Chat {
    String domain;
    double temperature;
    int max_tokens;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class SparkReqPayload {
    Message message;

    SparkReqPayload(List<Record> list) {
        this.message = Message.builder().text(list).build();
    }
}

@Data
@Builder
class Message {
    List<Record> text;
}


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
class Record {
    String role;
    String content;
}


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class SparkResp {
    SparkRespHeader header;
    SparkRespPayload payload;
}

@Data
class SparkRespHeader {
    int code;
    int status;
    String sid;
    String message;
}

@Data
class SparkRespPayload {
    Choices choices;
}

@Data
class Choices {
    List<Text> text;
    int status;
    int seq;

}

@Data
class Text {
    String role;
    String content;
    int index;
}

