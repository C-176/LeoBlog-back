package com.chen.LeoBlog.utils;

import cn.hutool.core.util.StrUtil;

import java.util.Base64;


public class UserIDBase64 {

    /**
     * userID解密
     *
     * @param encodedUserID 加密后的用户id
     * @return
     */
    public static Integer decoderUserID(String encodedUserID) {
        if (StrUtil.isBlank(encodedUserID)) {
            return null;
        }
        try {
            String reversedString = new StringBuffer(encodedUserID).reverse().toString();
            String base64String = reversedString.replaceAll("#", "=");
            int userIDPos = base64String.indexOf("==") + 6;
            String realBase64UserID = base64String.substring(userIDPos);
            String base64Encoded = new String(Base64.getDecoder().decode(realBase64UserID.getBytes()));
            return Integer.parseInt(base64Encoded);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 用户id加密
     *
     * @param userID 用户id
     * @return
     */
    public static String encoderUserID(Integer userID) {
        String base64UserIDEncoded = Base64.getEncoder().encodeToString((userID + "").getBytes());
        String currentStringBase64Encoded = Base64.getEncoder().encodeToString((System.currentTimeMillis() + "").getBytes());
        String keyString = currentStringBase64Encoded
                + currentStringBase64Encoded.substring(4, 8) + base64UserIDEncoded;
        byte[] codeBytes = keyString.getBytes();
        byte[] ordedBytes = new byte[codeBytes.length];
        for (int i = 0; i < codeBytes.length; i++) {
            ordedBytes[i] = codeBytes[codeBytes.length - i - 1];
        }
        return new String(ordedBytes).replaceAll("=", "#");
    }

    public static void main(String[] args) {
        System.out.println(encoderUserID(20));
        System.out.println(decoderUserID("#AjMzgjM##QN1AjN4gTOzgjM3UTM"));
    }
}