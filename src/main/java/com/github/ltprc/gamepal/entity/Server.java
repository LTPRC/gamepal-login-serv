package com.github.ltprc.gamepal.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.lang.NonNull;

public class Server {

    private static final int MAX_PLAYER_NUMBER = 100;

    private static AtomicInteger online = new AtomicInteger(0);
    @NonNull
    private static Map<String, HttpSession> sessionMap = new ConcurrentHashMap<>();
    @NonNull
    private static Map<String, Player> playerMap = new ConcurrentHashMap<>();

    public static int getOnline() {
        return online.get();
    }

    public static String[] getOnlinePlayers() {
        String[] rst = new String[playerMap.entrySet().size()];
        Object[] players = playerMap.values().toArray();
        for (int i = 0; i < rst.length; i++) {
            rst[i] = (String) players[i];
        }
        return rst;
    }

    public static void addOnline(int num) {
        System.out.println("addOnline by " + num);
        online.addAndGet(num);
    }

    public static Map<String, HttpSession> getSessionMap() {
        return sessionMap;
    }

    public static Map<String, Player> getPlayerMap() {
        return playerMap;
    }

    public static boolean isLoggedIn(@NonNull HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return null != session && sessionMap.containsKey(session.getId());
    }

    public static boolean registerPlayer(@NonNull Player player) {

        if (playerMap.containsKey(player.getName())) {
            //Already registered
            return true;
        }
        if (playerMap.size() >= MAX_PLAYER_NUMBER) {
            //Lobby is full
            return false;
        }
        playerMap.put(player.getName(), player);
        return true;
    }

    public static boolean deletePlayer(@NonNull String playerName) {
        if (!playerMap.containsKey(playerName)) {
            return false;
        }
        playerMap.remove(playerName);
        Player player = playerMap.get(playerName);
        if (null != player) {
            HttpSession httpSession = player.getHttpSession();
            if (null != httpSession) {
                httpSession.setAttribute("player", null);
            }
        }
        return true;
    }
}
