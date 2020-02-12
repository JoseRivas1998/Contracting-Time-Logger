package com.tcg.contracttimelogger.data;

import org.json.JSONObject;

import java.util.Objects;

public final class Contract implements JSONAble {
    public final String name;
    public final String description;
    public final long centsPerHour;
    public final Address address;

    private Contract(String name, String description, long centsPerHour, Address address) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.centsPerHour = centsPerHour;
    }

    public static Contract of(String name, String description, long centsPerHour, Address address) {
        return new Contract(name, description, centsPerHour, address);
    }

    public static Contract copy(Contract contract) {
        return new Contract(contract.name, contract.description, contract.centsPerHour, Address.copy(contract.address));
    }

    public static Contract ofJSON(String json) {
        return Contract.ofJSON(new JSONObject(json));
    }

    public static Contract ofJSON(JSONObject json) {
        JSONAble.validate(json, "name", "description", "address", "centsPerHour");
        final String name = json.getString("name");
        final String description = json.getString("description");
        final Address address = Address.ofJSON(json.getJSONObject("address"));
        final long centsPerHour = json.getLong("centsPerHour");
        return new Contract(name, description, centsPerHour, address);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("description", this.description);
        json.put("centsPerHour", this.centsPerHour);
        json.put("address", this.address.toJSON());
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return name.equals(contract.name) &&
                description.equals(contract.description) &&
                address.equals(contract.address) &&
                centsPerHour == contract.centsPerHour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, address, centsPerHour);
    }
}
