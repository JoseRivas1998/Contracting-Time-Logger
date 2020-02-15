package com.tcg.contracttimelogger.data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimeSheet implements JSONAble {

    private Contract contract;
    private final List<TimeRecord> timeRecords;

    private TimeSheet(Contract contract) {
        this.contract = contract;
        this.timeRecords = new ArrayList<>();
    }

    public static TimeSheet newSheet(Contract contract) {
        return new TimeSheet(contract);
    }

    public static TimeSheet ofJSON(String json) {
        return TimeSheet.ofJSON(new JSONObject(json));
    }

    public static TimeSheet ofJSON(JSONObject json) {
        JSONAble.validate(json, "contract", "timeRecords");
        Contract contract = Contract.ofJSON(json.getJSONObject("contract"));
        JSONArray records = json.getJSONArray("timeRecords");
        TimeSheet timeSheet = TimeSheet.newSheet(contract);
        for (int i = 0; i < records.length(); i++) {
            JSONObject recordJSON = records.getJSONObject(i);
            timeSheet.timeRecords.add(TimeRecord.ofJSON(recordJSON));
        }
        return timeSheet;
    }

    public Contract getContract() {
        return Contract.copy(this.contract);
    }

    public boolean isClockedIn() {
        return getClockedInRecord().isPresent();
    }

    public void clockIn(LocalDateTime clockIn) {
        if (isClockedIn()) {
            throw new IllegalStateException("Cannot clock in twice");
        }
        timeRecords.add(TimeRecord.clockIn(clockIn));
    }

    public void clockOut(LocalDateTime clockOut) {
        Optional<TimeRecord> clockedInRecordOptional = getClockedInRecord();
        if (!clockedInRecordOptional.isPresent()) {
            throw new IllegalStateException("Not clocked in");
        }
        TimeRecord record = clockedInRecordOptional.get();
        record.clockOut(clockOut);
    }

    public List<TimeRecord> recordsBetween(LocalDate start, LocalDate end) {
        return timeRecords
                .stream()
                .filter(timeRecord -> timeRecord.isClockedOut() && timeRecord.isBetween(start, end))
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<TimeRecord> getClockedInRecord() {
        return timeRecords
                .stream()
                .filter(timeRecord -> !timeRecord.isClockedOut())
                .findAny();
    }

    public void updateContractName(String name) {
        contract = Contract.of(name, contract.description, contract.centsPerHour, contract.address);
    }

    public void updateContractDescription(String description) {
        contract = Contract.of(contract.name, description, contract.centsPerHour, contract.address);
    }

    public void updateContractCentsPerHour(long centsPerHour) {
        contract = Contract.of(contract.name, contract.description, centsPerHour, contract.address);
    }

    public void updateContractAddress(Address address) {
        contract = Contract.of(contract.name, contract.description, contract.centsPerHour, address);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("contract", contract.toJSON());
        JSONArray records = new JSONArray();
        timeRecords
                .stream()
                .sorted()
                .forEach(timeRecord -> records.put(timeRecord.toJSON()));
        json.put("timeRecords", records);
        return json;
    }
}
