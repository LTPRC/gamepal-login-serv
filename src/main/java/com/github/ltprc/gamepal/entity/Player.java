package com.github.ltprc.gamepal.entity;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

@Component
public class Player {

    private String name;
    private HttpSession httpSession;

    public Player() {
    }

    public Player(String name, HttpSession httpSession) {
        this.name = name;
        this.httpSession = httpSession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }
}
