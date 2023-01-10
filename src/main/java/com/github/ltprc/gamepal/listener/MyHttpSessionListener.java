package com.github.ltprc.gamepal.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.github.ltprc.gamepal.entity.Player;
import com.github.ltprc.gamepal.entity.Server;

public class MyHttpSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        System.out.println("创建session");
        Server.addOnline(1);
        Server.getSessionMap().put(se.getSession().getId(), se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        System.out.println("销毁session");
        Server.addOnline(-1);
        HttpSession httpSession = se.getSession();
        /**
         * Log out the player.
         */
        Player player = (Player) httpSession.getAttribute("player");
        if (null != player) {
            player.setHttpSession(null);
            httpSession.setAttribute("player", null);
        }
        Server.getSessionMap().remove(se.getSession().getId());
    }
}
