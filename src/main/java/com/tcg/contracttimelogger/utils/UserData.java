package com.tcg.contracttimelogger.utils;

import com.tcg.contracttimelogger.data.Contract;
import com.tcg.contracttimelogger.data.JSONAble;
import com.tcg.contracttimelogger.data.Profile;
import com.tcg.contracttimelogger.data.TimeSheet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class UserData implements JSONAble {

    private static final String APP_FILE_PATH = Files.getAppFilePath(AppConstants.CONTRACTS_FILE);

    private static UserData instance;

    private List<TimeSheet> timeSheets;
    private Profile profile;
    private String lastSaveLocation = "";

    private UserData() {
        this.timeSheets = new ArrayList<>();
        this.profile = null;
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
            if(json.has("profile")) {
                this.profile = Profile.ofJSON(json.getJSONObject("profile"));
            }
            if(json.has("lastSaveLocation")) {
                this.lastSaveLocation = json.getString("lastSaveLocation");
            }
        } catch (IOException ioe) {
            System.out.printf("Unable to read contracts file, writing a new one to %s\n", APP_FILE_PATH);
            saveData();
        }
    }

    public void saveData() {
        File contractsFile = new File(APP_FILE_PATH);
        try {
            Files.writeUTF8File(this.toJSON().toString(), contractsFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int numberTimeSheets() {
        return timeSheets.size();
    }

    public void createTimeSheet(Contract contract) {
        this.timeSheets.add(TimeSheet.newSheet(contract));
        saveData();
    }

    public List<TimeSheet> timeSheets() {
        return new ArrayList<>(this.timeSheets);
    }

    public boolean hasProfile() {
        return this.profile != null;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getLastSaveLocation() {
        return lastSaveLocation;
    }

    public void setLastSaveLocation(String lastSaveLocation) {
        this.lastSaveLocation = lastSaveLocation;
    }

    public boolean hasLastSaveLocation() {
        return this.lastSaveLocation.length() > 0;
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        JSONArray timeSheetArr = new JSONArray();
        for (TimeSheet timeSheet : timeSheets) {
            timeSheetArr.put(timeSheet.toJSON());
        }
        json.put("timeSheets", timeSheetArr);
        if(this.hasProfile()) {
            json.put("profile", this.profile.toJSON());
        }
        if(this.lastSaveLocation.length() > 0) {
            json.put("lastSaveLocation", this.lastSaveLocation);
        }
        return json;
    }

    public Profile getProfile() {
        return this.profile;
    }
}
