package com.tcg.contracttimelogger.data;

import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class TimeRecord implements JSONAble, Comparable<TimeRecord> {

    public final LocalDateTime clockIn;
    private LocalDateTime clockOut;

    private TimeRecord(LocalDateTime clockIn) {
        this.clockIn = clockIn;
        this.clockOut = null;
    }

    public static TimeRecord clockIn(LocalDateTime clockIn) {
        return new TimeRecord(clockIn);
    }

    public static TimeRecord ofJSON(String json) {
        return TimeRecord.ofJSON(new JSONObject(json));
    }

    public static TimeRecord ofJSON(JSONObject json) {
        JSONAble.validate(json, "clockIn");
        final LocalDateTime clockIn = LocalDateTime.parse(json.getString("clockIn"));
        TimeRecord timeRecord = TimeRecord.clockIn(clockIn);
        if(json.has("clockOut")) {
            final LocalDateTime clockOut = LocalDateTime.parse(json.getString("clockOut"));
            timeRecord.clockOut(clockOut);
        }
        return timeRecord;
    }

    public boolean isClockedOut() {
        return this.clockOut != null;
    }

    public LocalDateTime getClockOut() {
        if(this.clockOut == null) {
            throw new IllegalStateException("This time record has not been clocked out");
        }
        return this.clockOut;
    }

    public void clockOut(LocalDateTime clockOut) {
        if(this.clockOut != null) {
            throw new IllegalStateException("This time record has already been clocked out");
        }
        if(clockOut.isBefore(clockIn)) {
            throw new IllegalArgumentException("Cannot clock out after clock in");
        }
        this.clockOut = clockOut;
    }

    public double hoursWorked() {
        if(this.clockOut == null) {
            throw new IllegalStateException("This time record is still clocked in");
        }
        long millis = Duration.between(this.clockIn, this.clockOut).toMillis();
        double seconds = millis / 1000.0;
        double minutes = seconds / 60.0;
        return minutes / 60.0;
    }

    public boolean isOnOrAfter(LocalDate date) {
        LocalDateTime midnight = date.atStartOfDay();
        return clockIn.isAfter(midnight);
    }

    public boolean isOnOrBefore(LocalDate date) {
        LocalDateTime endOfDay = date.atTime(23, 59);
        LocalDateTime outTime;
        if(isClockedOut()) {
            outTime = clockOut;
        } else {
            outTime = clockIn;
        }
        return outTime.isBefore(endOfDay);
    }

    public boolean isBetween(LocalDate start, LocalDate end) {
        return isOnOrAfter(start) && isOnOrBefore(end);
    }

    @Override
    public int compareTo(TimeRecord o) {
        return this.clockIn.compareTo(o.clockIn);
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("clockIn", this.clockIn.toString());
        if(this.isClockedOut()) {
            json.put("clockOut", this.clockOut.toString());
        }
        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeRecord that = (TimeRecord) o;
        return clockIn.equals(that.clockIn) &&
                Objects.equals(clockOut, that.clockOut);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clockIn, clockOut);
    }
}
