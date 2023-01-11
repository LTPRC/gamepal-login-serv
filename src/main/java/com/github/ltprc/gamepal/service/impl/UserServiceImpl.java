package com.github.ltprc.gamepal.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ltprc.gamepal.entity.UserInfo;
import com.github.ltprc.gamepal.repository.UserInfoRepository;
import com.github.ltprc.gamepal.service.UserService;
import com.github.ltprc.gamepal.util.ContentUtil;
import com.github.ltprc.gamepal.util.ErrorUtil;

@Transactional
@Component
public class UserServiceImpl implements UserService {

    private static final Log logger = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    private UserInfoRepository userInfoRepository;

    private Map<String, Session> sessionMap = new ConcurrentHashMap<>(); // userId, session
    private Map<String, String> tokenMap = new ConcurrentHashMap<>(); // userId, token
    private LinkedHashMap<String, Long> onlineMap = new LinkedHashMap<>(); // userId, timestamp

    @Override
    public ResponseEntity<String> registerAccount(HttpServletRequest request) {
        JSONObject rst = ContentUtil.generateRst();
        JSONObject req = null;
        try {
            req = ContentUtil.request2JSONObject(request);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1002));
        }

        UserInfo userInfo = new UserInfo();
        String userCode = UUID.randomUUID().toString();
        if (!userInfoRepository.queryUserInfoByUserCode(userCode).isEmpty()) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1001));
        }
        String username = req.getString("username");
        userInfo.setUsername(username);
        String password = req.getString("password");
        userInfo.setPassword(password);

        if (!userInfoRepository.queryUserInfoByUsername(username).isEmpty()) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1004));
        }
        userInfo.setUserCode(userCode);
        userInfo.setStatus(0);
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        userInfo.setTimeCreated(sdf.format(new Date()));
        userInfo.setTimeUpdated(userInfo.getTimeCreated());
        userInfoRepository.save(userInfo);

        rst.put("userCode", userCode);
        return ResponseEntity.ok().body(rst.toString());
    }

    @Override
    public ResponseEntity<String> cancelAccount(HttpServletRequest request) {
        JSONObject rst = ContentUtil.generateRst();
        JSONObject req = null;
        try {
            req = ContentUtil.request2JSONObject(request);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1002));
        }
        String userCode = req.getString("userCode");
        userInfoRepository.deleteUserInfoByUserCode(userCode);

        rst.put("userCode", userCode);
        return ResponseEntity.ok().body(rst.toString());
    }

    @Override
    public ResponseEntity<String> login(HttpServletRequest request) {
        JSONObject rst = ContentUtil.generateRst();
        JSONObject req = null;
        try {
            req = ContentUtil.request2JSONObject(request);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1002));
        }
        String username = req.getString("username");
        String password = req.getString("password");
        List<UserInfo> userInfoList = userInfoRepository.queryUserInfoByUsernameAndPassword(username, password);
        if (userInfoList.isEmpty()) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1005));
        }
        String userCode = userInfoList.get(0).getUserCode();
        updateTokenByUserCode(userCode);
        onlineMap.remove(userCode);
        onlineMap.put(userCode, Instant.now().getEpochSecond());
        rst.put("userCode", userCode);
        rst.put("token", tokenMap.get(userCode));
        return ResponseEntity.ok().body(rst.toString());
    }

    @Override
    public ResponseEntity<String> logoff(HttpServletRequest request) {
        JSONObject rst = ContentUtil.generateRst();
        JSONObject req = null;
        try {
            req = ContentUtil.request2JSONObject(request);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1002));
        }
        String userCode = req.getString("userCode");
        String token = req.getString("token");
        if (token.equals(tokenMap.get(userCode))) {
            tokenMap.remove(userCode);
            onlineMap.remove(userCode);
        } else {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1006));
        }
        return ResponseEntity.ok().body(rst.toString());
    }

    @Override
    public void onOpen(Session session, String userCode) {
        sessionMap.put(userCode, session);
        logger.info("建立连接成功");
    }

    @Override
    public void onClose(String userCode) {
        sessionMap.remove(userCode);
        logger.info("断开连接成功");
    }

    @Override
    public String getTokenByUserCode(String userCode) {
        return tokenMap.getOrDefault(userCode, null);
    }

    @Override
    public Long getOnlineTimestampByUserCode(String userCode) {
        return onlineMap.getOrDefault(userCode, null);
    }

    @Override
    public Session getSessionByUserCode(String userCode) {
        return sessionMap.getOrDefault(userCode, null);
    }

    @Override
    public Set<Entry<String, Session>> getSessionEntrySet() {
        return sessionMap.entrySet();
    }

    @Override
    public String updateTokenByUserCode(String userCode) {
        String token = UUID.randomUUID().toString();
        tokenMap.put(userCode, token);
        return token;
    }

}
