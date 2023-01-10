package com.github.ltprc.gamepal.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ltprc.gamepal.entity.UserInfo;
import com.github.ltprc.gamepal.model.UserData;
import com.github.ltprc.gamepal.repository.UserInfoRepository;
import com.github.ltprc.gamepal.service.UserService;
import com.github.ltprc.gamepal.util.ErrorUtil;
import com.github.ltprc.gamepal.util.ServerUtil;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Override
    public ResponseEntity<String> registerAccount(HttpServletRequest request) {
        JSONObject rst = new JSONObject();
        JSONObject req = null;
        try {
            BufferedReader br = request.getReader();
            String str = "";
            String listString = "";
            while ((str = br.readLine()) != null) {
                listString += str;
            }
            req = JSON.parseObject(listString);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1002));
        }

        UserInfo userInfo = new UserInfo();
        String userCode = UUID.randomUUID().toString();
        if (userInfoRepository.queryUserInfoByUserCode(userCode).isEmpty()) {
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
        JSONObject rst = new JSONObject();
        JSONObject req = null;
        try {
            BufferedReader br = request.getReader();
            String str = "";
            String listString = "";
            while ((str = br.readLine()) != null) {
                listString += str;
            }
            req = JSON.parseObject(listString);
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
        JSONObject rst = new JSONObject();
        JSONObject req = null;
        try {
            BufferedReader br = request.getReader();
            String str = "";
            String listString = "";
            while ((str = br.readLine()) != null) {
                listString += str;
            }
            req = JSON.parseObject(listString);
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
        String token = UUID.randomUUID().toString();
        ServerUtil.tokenMap.put(userCode, token);
        ServerUtil.onlineMap.remove(userCode);
        ServerUtil.onlineMap.put(userCode, Instant.now().getEpochSecond());
        if (!ServerUtil.userDataMap.containsKey(userCode)) {
            // Initialize new userData
            UserData userData = new UserData();
            userData.setUserCode(userCode);
            ServerUtil.userDataMap.put(userCode, userData);
        }
        rst.put("userCode", userCode);
        rst.put("token", ServerUtil.tokenMap.get(userCode));
        return ResponseEntity.ok().body(rst.toString());
    }

    @Override
    public ResponseEntity<String> logoff(HttpServletRequest request) {
        JSONObject rst = new JSONObject();
        JSONObject req = null;
        try {
            BufferedReader br = request.getReader();
            String str = "";
            String listString = "";
            while ((str = br.readLine()) != null) {
                listString += str;
            }
            req = JSON.parseObject(listString);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1002));
        }
        String userCode = req.getString("userCode");
        String token = req.getString("token");
        if (token.equals(ServerUtil.tokenMap.get(userCode))) {
            ServerUtil.tokenMap.remove(userCode);
            ServerUtil.onlineMap.remove(userCode);
            ServerUtil.userDataMap.remove(userCode);
        } else {
            return ResponseEntity.badRequest().body(JSON.toJSONString(ErrorUtil.ERROR_1006));
        }
        return ResponseEntity.status(HttpStatus.OK).body(rst.toString());
    }

}
