import com.tcg.contracttimelogger.data.TimeRecord;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class MainTester {

    @Test
    public void testClockIn() {
        LocalDateTime dateTime = LocalDateTime.of(2019, 5, 21, 8, 30);
        TimeRecord timeRecord = TimeRecord.clockIn(dateTime);
        assertEquals(dateTime, timeRecord.clockIn);
    }

    @Test
    public void testClockOut() {
        LocalDateTime clockIn = LocalDateTime.of(2019, 5, 21, 8, 30);
        LocalDateTime clockOut = LocalDateTime.of(2019, 5, 21, 10, 0);
        TimeRecord timeRecord = TimeRecord.clockIn(clockIn);
        timeRecord.clockOut(clockOut);
        assertTrue(timeRecord.isClockedOut());
        assertEquals(clockIn, timeRecord.clockIn);
        assertEquals(clockOut, timeRecord.getClockOut());
    }

    @Test(expected = IllegalStateException.class)
    public void testGettingClockOutWithoutClockingOut() {
        LocalDateTime clockIn = LocalDateTime.of(2019, 5, 21, 8, 30);
        TimeRecord timeRecord = TimeRecord.clockIn(clockIn);
        assertFalse(timeRecord.isClockedOut());
        timeRecord.getClockOut();
    }

    @Test(expected = IllegalStateException.class)
    public void testDoubleClockOut(){
        LocalDateTime clockIn = LocalDateTime.of(2019, 5, 21, 8, 30);
        LocalDateTime clockOut = LocalDateTime.of(2019, 5, 21, 10, 0);
        TimeRecord timeRecord = TimeRecord.clockIn(clockIn);
        timeRecord.clockOut(clockOut);
        timeRecord.clockOut(clockOut);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClockOutBeforeClockIn(){
        LocalDateTime clockIn = LocalDateTime.of(2019, 5, 21, 8, 30);
        LocalDateTime clockOut = LocalDateTime.of(2019, 5, 21, 10, 0);
        TimeRecord timeRecord = TimeRecord.clockIn(clockOut);
        timeRecord.clockOut(clockIn);
    }

    @Test
    public void testHoursWorkedWholeNumber() {
        LocalDateTime clockIn = LocalDateTime.of(2019, 5, 21, 8, 30);
        LocalDateTime clockOut = LocalDateTime.of(2019, 5, 21, 10, 30);
        TimeRecord timeRecord = TimeRecord.clockIn(clockIn);
        timeRecord.clockOut(clockOut);
        final double expected = 2.0;
        final double actual = timeRecord.hoursWorked();
        assertEquals(expected, actual, 1e-5);
    }

    @Test
    public void testHoursWorkedHalfHour() {
        LocalDateTime clockIn = LocalDateTime.of(2019, 5, 21, 8, 30);
        LocalDateTime clockOut = LocalDateTime.of(2019, 5, 21, 10, 0);
        TimeRecord timeRecord = TimeRecord.clockIn(clockIn);
        timeRecord.clockOut(clockOut);
        final double expected = 1.5;
        final double actual = timeRecord.hoursWorked();
        assertEquals(expected, actual, 1e-5);
    }

    @Test(expected = IllegalStateException.class)
    public void testHoursWorkedNotClockedOut() {
        LocalDateTime clockIn = LocalDateTime.of(2019, 5, 21, 8, 30);
        TimeRecord timeRecord = TimeRecord.clockIn(clockIn);
        timeRecord.hoursWorked();
    }

}
