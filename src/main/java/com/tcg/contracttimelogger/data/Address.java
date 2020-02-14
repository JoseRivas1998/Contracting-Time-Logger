package com.tcg.contracttimelogger.data;

import org.json.JSONObject;

import java.util.Objects;

public final class Address implements JSONAble {

    public final String street;
    public final String city;
    public final String state;
    public final String zipCode;

    private Address(String street, String city, String state, String zipCode) {
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public static Address of(String street, String city, String state, String zipCode) {
        return new Address(street, city, state, zipCode);
    }

    public static Address copy(Address address) {
        return new Address(address.street, address.city, address.state, address.zipCode);
    }

    public static Address ofJSON(String json) {
        return Address.ofJSON(new JSONObject(json));
    }

    public static Address ofJSON(JSONObject json) {
        JSONAble.validate(json, "street", "city", "state", "zipCode");
        final String street = json.getString("street");
        final String city = json.getString("city");
        final String state = json.getString("state");
        final String zipCode = json.getString("zipCode");
        return new Address(street, city, state, zipCode);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("street", this.street);
        json.put("city", this.city);
        json.put("state", this.state);
        json.put("zipCode", this.zipCode);
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return street.equals(address.street) &&
                city.equals(address.city) &&
                state.equals(address.state) &&
                zipCode.equals(address.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, state, zipCode);
    }

    @Override
    public String toString() {
        return String.format("%s\n%s, %s %s", street, city, state, zipCode);
    }
}
