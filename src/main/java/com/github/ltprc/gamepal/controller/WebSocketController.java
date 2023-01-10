package com.github.ltprc.gamepal.controller;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.github.ltprc.gamepal.util.ServerUtil;

@Component
@ServerEndpoint("/websocket/v1/{userCode}")
public class WebSocketController {

    private static final Log logger = LogFactory.getLog(WebSocketController.class);

    /**
     * 建立连接调用的方法
     * @param session
     * @param userCode
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userCode") String userCode) {
        ServerUtil.sessionMap.put(userCode, session);
        logger.info("建立连接成功");
    }

    /**
     * 关闭链接调用接口
     * @param userCode
     */
    @OnClose
    public void onClose(@PathParam("userCode") String userCode) {
        ServerUtil.sessionMap.remove(userCode);
        logger.info("断开连接成功");
    }

    /**
     * 接收消息
     * @param userCode
     * @param message
     */
    @OnMessage
    public void onMessage(@NonNull String message) {
//        System.out.println("Received String (size:" + message.length() + ")");
//        System.out.println("Received String:" + message);
        JSONObject jsonObject = JSONObject.parseObject(message);
        if (null == jsonObject || !jsonObject.containsKey("userCode")) {
            return;
        }
        String userCode = jsonObject.getString("userCode").toString();

        String content = ServerUtil.generateReplyContent(userCode);
        ServerUtil.sendMessage(userCode, content);
    }
}