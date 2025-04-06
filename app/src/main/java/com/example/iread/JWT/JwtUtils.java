package com.example.iread.JWT;

import android.util.Base64;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class JwtUtils {
    public static String getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length == 3) {
                String payload = parts[1];
                byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
                String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
                JSONObject jsonObject = new JSONObject(decodedPayload);

                // Tìm khóa nào chứa "primarysid" (userId) trong JWT
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (key.toLowerCase().contains("primarysid")) {
                        return jsonObject.getString(key);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static String getUsernameFromToken(String token) {
//        try {
//            String[] parts = token.split("\\.");
//            if (parts.length == 3) {
//                String payload = parts[1];
//                byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
//                String decodedPayload = new String(decodedBytes, StandardCharsets.UTF_8);
//
//                JSONObject jsonObject = new JSONObject(decodedPayload);
//
//                // Tìm khóa nào chứa "nameidentifier" (username)
//                Iterator<String> keys = jsonObject.keys();
//                while (keys.hasNext()) {
//                    String key = keys.next();
//                    if (key.toLowerCase().contains("nameidentifier")) {
//                        return jsonObject.getString(key);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
