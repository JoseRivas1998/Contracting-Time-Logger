package com.tcg.contracttimelogger.data;

import org.json.JSONException;
import org.json.JSONObject;

public interface JSONAble {

    JSONObject toJSON();

    static void validate(JSONObject json, String... fields) {
        for (String field : fields) {
            if(!json.has(field)) {
                throw new JSONException("Field: \"" + field + "\" is missing.");
            }
        }
    }

}
