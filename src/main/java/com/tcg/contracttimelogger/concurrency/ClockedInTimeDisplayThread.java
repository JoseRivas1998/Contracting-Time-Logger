package com.tcg.contracttimelogger.concurrency;

import com.tcg.contracttimelogger.data.TimeRecord;
import com.tcg.contracttimelogger.data.TimeSheet;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ClockedInTimeDisplayThread {

    private static ClockedInTimeDisplayThread instance;

    public static ClockedInTimeDisplayThread getInstance() {
        if (instance == null) {
            synchronized (ClockedInTimeDisplayThread.class) {
                if (instance == null) {
                    instance = new ClockedInTimeDisplayThread();
                }
            }
        }
        return instance;
    }

    private WorkingThread workingThread;

    public void stop() {
        if(workingThread != null) {
            workingThread.running = false;
            workingThread = null;
        }
    }

    public void start(final Label label, final TimeSheet timeSheet) {
        if (workingThread != null) {
            this.stop();
        }
        workingThread = new WorkingThread(label, timeSheet);
        workingThread.start();
    }

    private static class WorkingThread extends Thread {
        boolean running;
        final Label label;
        final TimeSheet timeSheet;

        WorkingThread(final Label label, final TimeSheet timeSheet) {
            this.label = label;
            this.running = false;
            this.timeSheet = timeSheet;
        }

        @Override
        public void run() {
            this.running = true;
            while (this.running) {
                Optional<TimeRecord> clockedInOptional = this.timeSheet.getClockedInRecord();
                if (clockedInOptional.isPresent()) {
                    final TimeRecord clockedInRecord = clockedInOptional.get();
                    long millis = Duration.between(clockedInRecord.clockIn, LocalDateTime.now()).toMillis();
                    long hours = TimeUnit.MILLISECONDS.toHours(millis);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60;
                    String clockInTime = clockedInRecord.clockIn.format(DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a"));
                    Platform.runLater(() -> {
                        label.setText(String.format("Last clocked in at %s, clocked in for %dh %02dm %02ds", clockInTime, hours, minutes, seconds));
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
