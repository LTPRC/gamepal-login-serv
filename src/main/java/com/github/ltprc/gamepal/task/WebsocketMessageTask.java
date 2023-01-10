package com.github.ltprc.gamepal.task;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.github.ltprc.gamepal.model.UserData;
import com.github.ltprc.gamepal.util.ServerUtil;

@Component
@Deprecated
public class WebsocketMessageTask {

//    @Scheduled(cron = "* * * * * ?")
//    @Scheduled(fixedRate = 40)
    public void execute() {
        UserData ud = new UserData();
        ud.setUserCode("John");
        ServerUtil.userDataMap.put("Wick", ud);
        UserData ud2 = new UserData();
        ServerUtil.userDataMap.put("Connor", ud2);
        System.out.println("WebsocketMessageTask:size="+ServerUtil.userDataMap.size());
        ServerUtil.sendMessageToAll(JSONArray.toJSONString(ServerUtil.userDataMap.entrySet()));
    }
}
