package com.ellen.security.lab.utils;


import java.util.Base64;

public class EncoderDecoder {

    public static void main(String[] args) {
        String originalString = "user:password";

        String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());

        System.out.println("原始字符串: " + originalString);
        System.out.println("Base64编码: " + encodedString);
    }
}
