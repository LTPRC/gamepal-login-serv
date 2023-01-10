package com.github.ltprc.gamepal.task;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ltprc.gamepal.util.ServerUtil;

@Component
@Deprecated
public class CheckOnlineTask {

    private static final long TIMEOUT_SECOND = 300;

    //@Scheduled(cron = "0 */5 * * * ?")
    public void execute() {
        if (!ServerUtil.onlineMap.isEmpty() && Instant.now().getEpochSecond() - ServerUtil.onlineMap.entrySet().iterator().next().getValue() > TIMEOUT_SECOND) {
            ServerUtil.tokenMap.remove(ServerUtil.onlineMap.entrySet().iterator().next().getKey()); // log off
        }
    }
}
