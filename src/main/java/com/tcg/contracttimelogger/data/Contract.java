package com.tcg.contracttimelogger.data;

import org.json.JSONObject;

import java.util.Objects;

public final class Contract implements JSONAble {
    public final String name;
    public final String description;
    public final Address address;

    private Contract(String name, String description, Address address) {
        this.name = name;
        this.description = description;
        this.address = address;
    }

    public static Contract of(String name, String description, Address address) {
        return new Contract(name, description, address);
    }

    public static Contract copy(Contract contract) {
        return new Contract(contract.name, contract.description, Address.copy(contract.address));
    }

    public static Contract ofJSON(String json) {
        return Contract.ofJSON(new JSONObject(json));
    }

    public static Contract ofJSON(JSONObject json) {
        JSONAble.validate(json, "name", "description", "address");
        final String name = json.getString("name");
        final String description = json.getString("description");
        final Address address = Address.ofJSON(json.getJSONObject("address"));
        return new Contract(name, description, address);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("description", this.description);
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
                address.equals(contract.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, address);
    }
}
