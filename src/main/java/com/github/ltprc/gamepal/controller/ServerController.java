package com.github.ltprc.gamepal.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.github.ltprc.gamepal.model.UserData;
import com.github.ltprc.gamepal.entity.UserInfo;
import com.github.ltprc.gamepal.repository.UserInfoRepository;
import com.github.ltprc.gamepal.util.ServerUtil;

@RestController
@RequestMapping("/api/v1")
public class ServerController {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<String> register(HttpServletRequest request) {
        JSONObject rst = new JSONObject();
        UserInfo userInfo = new UserInfo();
        String uuid = UUID.randomUUID().toString();
        while (!userInfoRepository.queryUserInfoByUserid(uuid).isEmpty()) {
            uuid = UUID.randomUUID().toString();
        }
        String username, password;
        try {
            JSONObject jsonObject = ServerUtil.strRequest2JSONObject(request);
            if (null == jsonObject || !jsonObject.containsKey("body")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
            }
            JSONObject body = (JSONObject) JSONObject.parse((String) jsonObject.get("body"));
            username = body.get("username").toString();
            userInfo.setUsername(username);
            password = body.get("password").toString();
            userInfo.setPassword(password);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
        if (!userInfoRepository.queryUserInfoByUsername(username).isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
        userInfo.setUserId(uuid.toString());
        userInfo.setStatus(0);
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");// a为am/pm的标记
        userInfo.setTimeCreated(sdf.format(new Date()));
        userInfo.setTimeUpdated(userInfo.getTimeCreated());
        userInfoRepository.save(userInfo);
        return ResponseEntity.status(HttpStatus.OK).body(rst.toString());
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(HttpServletRequest request) {
        String username, password;
        try {
            JSONObject jsonObject = ServerUtil.strRequest2JSONObject(request);
            if (null == jsonObject || !jsonObject.containsKey("body")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
            }
            JSONObject body = (JSONObject) JSONObject.parse((String) jsonObject.get("body"));
            username = body.get("username").toString();
            password = body.get("password").toString();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
        List<UserInfo> userInfoList = userInfoRepository.queryUserInfoByUsernameAndPassword(username, password);
        if (!userInfoList.isEmpty()) {
            String uuid = (String) userInfoList.get(0).getUserId();
            String token = UUID.randomUUID().toString();
            ServerUtil.tokenMap.put(uuid, token);
            ServerUtil.onlineMap.remove(uuid);
            ServerUtil.onlineMap.put(uuid, Instant.now().getEpochSecond());
            if (!ServerUtil.userDataMap.containsKey(uuid)) {
                UserData userData = new UserData();
                userData.setUserCode(uuid);
                ServerUtil.userDataMap.put(uuid, userData);
            }
            JSONObject rst = new JSONObject();
            rst.put("userCode", uuid);
            rst.put("token", ServerUtil.tokenMap.get(uuid));
            return ResponseEntity.status(HttpStatus.OK).body(JSONObject.toJSONString(rst));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }

    @RequestMapping(value = "/init-user-data", method = RequestMethod.POST)
    public ResponseEntity<String> initUserData(HttpServletRequest request) {
//        ServerUtil.init();
        String userCode;
        try {
            JSONObject jsonObject = ServerUtil.strRequest2JSONObject(request);
            if (null == jsonObject || !jsonObject.containsKey("body")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Operation failed");
            }
            JSONObject body = (JSONObject) JSONObject.parse((String) jsonObject.get("body"));
            userCode = body.getString("userCode");
            if (StringUtils.isBlank(userCode)) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Operation failed");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Operation failed");
        }
        JSONObject rst = new JSONObject();
        rst.put("userCode", userCode);
        rst.put("token", ServerUtil.tokenMap.get(userCode));
        return ResponseEntity.status(HttpStatus.OK).body(JSONObject.toJSONString(rst));
    }
    
    @RequestMapping(value = "/logoff", method = RequestMethod.POST)
    public ResponseEntity<String> logoff(HttpServletRequest request) {
        JSONObject rst = new JSONObject();
        String userCode, token;
        try {
            JSONObject jsonObject = ServerUtil.strRequest2JSONObject(request);
            if (null == jsonObject || !jsonObject.containsKey("body")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Operation failed");
            }
            JSONObject body = (JSONObject) JSONObject.parse((String) jsonObject.get("body"));
            userCode = body.getString("userCode");
            token = body.getString("token");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Operation failed");
        }
        afterLogoff(userCode, token);
        return ResponseEntity.status(HttpStatus.OK).body(rst.toString());
    }

    private void afterLogoff(String userCode, String token) {
        if (token.equals(ServerUtil.tokenMap.get(userCode))) {
            ServerUtil.tokenMap.remove(userCode);
            ServerUtil.onlineMap.remove(userCode);
            ServerUtil.userDataMap.remove(userCode);
        }
    }
}
