package com.tcg.contracttimelogger.utils;

public final class UserData {

    private static UserData instance;

    public static UserData getInstance() {
        if(UserData.instance == null) {
            synchronized (UserData.class) {
                if(UserData.instance == null) {
                    instance = new UserData();
                }
            }
        }
        return UserData.instance;
    }

}
