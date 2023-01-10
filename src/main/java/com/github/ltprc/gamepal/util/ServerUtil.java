package com.github.ltprc.gamepal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;
import javax.websocket.server.PathParam;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ltprc.gamepal.model.Message;
import com.github.ltprc.gamepal.model.UserData;

@Component
public class ServerUtil {

    public final static Map<String, String> tokenMap = new ConcurrentHashMap<>(); // uuid, token
    public final static LinkedHashMap<String, Long> onlineMap = new LinkedHashMap<>(); // uuid, timestamp
    public final static Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    public final static Map<String, UserData> userDataMap = new ConcurrentHashMap<>(); // uuid, userData
    public final static Map<String, Queue<Message>> chatMap = new ConcurrentHashMap<>(); // uuid, message queue

    /**
     * 集体发送消息
     * @param userCode
     * @param message
     */
    public synchronized static void sendMessageToAll(String message) {
        for (Entry<String, Session> entry : sessionMap.entrySet()) {
            try {
                entry.getValue().getBasicRemote().sendText(message);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String generateReplyContent(String userCode) {
        UserData userData = ServerUtil.userDataMap.get(userCode);
        JSONObject rst = new JSONObject();
        rst.put("userCode", userCode);
        rst.put("token", ServerUtil.tokenMap.get(userCode));
        if (ServerUtil.chatMap.containsKey(userCode) && !ServerUtil.chatMap.get(userCode).isEmpty()) {
            JSONArray chatMessages = new JSONArray();
            chatMessages.addAll(ServerUtil.chatMap.get(userCode));
            ServerUtil.chatMap.get(userCode).clear();;
            rst.put("chatMessages", chatMessages);
//            System.out.println("ChatMessage sent:" + userCode);
        }
        return JSONObject.toJSONString(rst);
    }

    /**
     * 发送消息
     * @param userCode
     * @param message
     */
    public synchronized static void sendMessage(@PathParam("userCode") String userCode, String message) {
        // 向指定用户发送消息
        try {
            if (sessionMap.containsKey(userCode)) {
                sessionMap.get(userCode).getBasicRemote().sendText(message);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
