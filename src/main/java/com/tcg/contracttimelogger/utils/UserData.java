package com.tcg.contracttimelogger.utils;

import com.tcg.contracttimelogger.data.JSONAble;
import com.tcg.contracttimelogger.data.TimeSheet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
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
        timeSheets.clear();
        try {
            String file = Files.readContractsFile();
            JSONObject json = new JSONObject(file);
            JSONAble.validate(json, "timeSheets");
            JSONArray timeSheetsArr = json.getJSONArray("timeSheets");
            for (int i = 0; i < timeSheetsArr.length(); i++) {
                JSONObject timeSheet = timeSheetsArr.getJSONObject(i);
                this.timeSheets.add(TimeSheet.ofJSON(timeSheet));
            }
        } catch (IOException ioe) {
            final String path = Files.getAppFilePath(AppConstants.CONTRACTS_FILE);
            System.out.printf("Unable to read contracts file, writing a new one to %s\n", path);
            File contractsFile = new File(path);
            try {
                Files.writeUTF8File(this.toJSON().toString(), contractsFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int numberTimeSheets() {
        return timeSheets.size();
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
