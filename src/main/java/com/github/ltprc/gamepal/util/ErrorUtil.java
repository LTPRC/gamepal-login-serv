package com.github.ltprc.gamepal.util;

import com.github.ltprc.gamepal.model.GamepalError;

public class ErrorUtil {
    public static final GamepalError ERROR_1001 = new GamepalError("1001", "Generating user_id failed.");
    public static final GamepalError ERROR_1002 = new GamepalError("1002", "Converting request failed.");
    public static final GamepalError ERROR_1003 = new GamepalError("1003", "Validating request failed.");
    public static final GamepalError ERROR_1004 = new GamepalError("1004", "Username is already taken.");
    public static final GamepalError ERROR_1005 = new GamepalError("1005", "Invalid username or password.");
    public static final GamepalError ERROR_1006 = new GamepalError("1006", "Expired token.");
}
