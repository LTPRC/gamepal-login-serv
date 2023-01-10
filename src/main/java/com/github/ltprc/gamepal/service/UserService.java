package com.github.ltprc.gamepal.service;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<String> registerAccount(HttpServletRequest request);

    ResponseEntity<String> cancelAccount(HttpServletRequest request);

    ResponseEntity<String> login(HttpServletRequest request);

    ResponseEntity<String> logoff(HttpServletRequest request);
}
