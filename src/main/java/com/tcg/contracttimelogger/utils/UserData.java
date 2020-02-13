package com.tcg.contracttimelogger.utils;

import com.tcg.contracttimelogger.data.JSONAble;
import com.tcg.contracttimelogger.data.TimeSheet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class UserData implements JSONAble {

    private static UserData instance;

    private List<TimeSheet> timeSheets;

    private UserData() {
        this.timeSheets = new ArrayList<>();
    }

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

    public void loadData() {

    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONArray timeSheetArr = new JSONArray();
        for (TimeSheet timeSheet : timeSheets) {
            timeSheetArr.put(timeSheet.toJSON());
        }
        json.put("timeSheets", timeSheetArr);
        return json;
    }
}
