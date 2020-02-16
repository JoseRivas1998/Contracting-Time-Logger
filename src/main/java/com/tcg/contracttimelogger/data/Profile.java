package com.tcg.contracttimelogger.data;

import org.json.JSONObject;

public class Profile implements JSONAble{

    public final String name;
    public final Address address;

    private Profile(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public static Profile of(String name, Address address) {
        return new Profile(name, address);
    }

    public static Profile ofJSON(JSONObject json) {
        JSONAble.validate(json, "name", "address");
        final String name = json.getString("name");
        final Address address = Address.ofJSON(json.getJSONObject("address"));
        return Profile.of(name, address);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("address", address.toJSON());
        return json;
    }
}
