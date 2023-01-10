package com.github.ltprc.gamepal.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 276839028119022779L;

    public BusinessException(String msg) {
        super(msg);
    }
}
