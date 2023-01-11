package com.github.ltprc.gamepal.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;
import javax.websocket.server.PathParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.ltprc.gamepal.model.Message;
import com.github.ltprc.gamepal.service.MessageService;
import com.github.ltprc.gamepal.service.UserService;
import com.github.ltprc.gamepal.util.ContentUtil;
import com.github.ltprc.gamepal.util.ErrorUtil;

@Component
public class MessageServiceImpl implements MessageService {

    private static final Map<String, Queue<Message>> chatMap = new ConcurrentHashMap<>(); // uuid, message queue

    private static final Log logger = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public void onMessage(String message) {
//      System.out.println("Received String (size:" + message.length() + ")");
//      System.out.println("Received String:" + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        if (null == jsonObject || !jsonObject.containsKey("userCode")) {
            logger.error(ErrorUtil.ERROR_1008);
            return;
        }
        String userCode = jsonObject.getString("userCode").toString();
        // Reply automatically
        sendMessage(userCode, generateReplyContent(userCode));
    }

    /**
     * 发送消息
     * 
     * @param userCode
     * @param message
     */
    private ResponseEntity sendMessage(@PathParam("userCode") String userCode, String message) {
        JSONObject rst = ContentUtil.generateRst();
        Session session = userService.getSessionByUserCode(userCode);
        if (null == session) {
            logger.warn(ErrorUtil.ERROR_1009 + "userCode: " + userCode);
            return ResponseEntity.badRequest().body(ErrorUtil.ERROR_1009);
        }
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            logger.warn(ErrorUtil.ERROR_1010 + "userCode: " + userCode);
            return ResponseEntity.badRequest().body(ErrorUtil.ERROR_1010);
        }
        return ResponseEntity.ok().body(rst.toString());
    }

    /**
     * 集体发送消息
     * @param userCode
     * @param message
     */
    private ResponseEntity sendMessageToAll(String message) {
        JSONObject rst = ContentUtil.generateRst();
        for (Entry<String, Session> entry : userService.getSessionEntrySet()) {
            try {
                entry.getValue().getBasicRemote().sendText(message);
            } catch (IOException e) {
                logger.warn(ErrorUtil.ERROR_1010 + "userCode: " + entry.getKey());
                return ResponseEntity.badRequest().body(ErrorUtil.ERROR_1010);
            }
        }
        return ResponseEntity.ok().body(rst.toString());
    }

    private String generateReplyContent(String userCode) {
        JSONObject rst = ContentUtil.generateRst();
        rst.put("userCode", userCode);
        // Update token automatically
        String token = userService.updateTokenByUserCode(userCode);
        rst.put("token", token);
        // Flush messages automatically
        if (chatMap.containsKey(userCode) && !chatMap.get(userCode).isEmpty()) {
            JSONArray messages = new JSONArray();
            messages.addAll(chatMap.get(userCode));
            chatMap.get(userCode).clear();
            rst.put("messages", messages);
//            System.out.println("Messages sent:" + userCode);
        }
        return JSONObject.toJSONString(rst);
    }

    @Override
    public ResponseEntity sendMessage(HttpServletRequest request) {
        JSONObject rst = ContentUtil.generateRst();
        JSONObject req = null;
        try {
            req = ContentUtil.request2JSONObject(request);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1002));
        }
        String userCode = req.getString("userCode");
        String content = req.getString("content");
        return sendMessage(userCode, content);
    }
}
