package com.tcg.contracttimelogger.gui.containers;

import com.tcg.contracttimelogger.concurrency.ClockedInTimeDisplayThread;
import com.tcg.contracttimelogger.data.Address;
import com.tcg.contracttimelogger.data.Contract;
import com.tcg.contracttimelogger.data.TimeSheet;
import com.tcg.contracttimelogger.gui.UIContainer;
import com.tcg.contracttimelogger.gui.components.views.TimeSheetListView;
import com.tcg.contracttimelogger.utils.AppConstants;
import com.tcg.contracttimelogger.utils.UserData;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.time.LocalDateTime;

public class TimeSheetView extends GridPane implements UIContainer {

    private final TimeSheetListView timeSheetListView;
    private final Label lastClockedInLabel;
    private final Button clockInOutBtn;
    private final Label selectedContractLabel;

    public TimeSheetView() {
        super();
        this.setHgap(AppConstants.HUD_SPACING);
        this.setVgap(AppConstants.HUD_SPACING);
        this.setPadding(AppConstants.padding());

        this.timeSheetListView = new TimeSheetListView();
        lastClockedInLabel = new Label();
        clockInOutBtn = new Button();
        selectedContractLabel = new Label();

        initTimeSheetListView();

        TableView tableView = new TableView<>();
        GridPane.setVgrow(tableView, Priority.ALWAYS);
        GridPane.setHgrow(tableView, Priority.ALWAYS);
        this.add(tableView, 1, 1, 2, 1);
        initClockInClockOutRow();

        this.add(selectedContractLabel, 1, 0);
        updateSelectedContractLabel();

//        this.setGridLinesVisible(true);
    }

    private void updateSelectedContractLabel() {
        TimeSheet timeSheet = selectedTimeSheet();
        Contract contract = timeSheet.getContract();
        Address address = contract.address;
        double hourlyRate = contract.centsPerHour / 100.0;
        String description = contract.description;
        this.selectedContractLabel.setText(String.format("%s\n%s\n$%.2f/h\n%s", contract, address, hourlyRate, description));
    }

    private void initClockInClockOutRow() {
        lastClockedInLabel.setAlignment(Pos.BASELINE_RIGHT);
        GridPane.setHgrow(lastClockedInLabel, Priority.ALWAYS);
        GridPane.setHalignment(lastClockedInLabel, HPos.RIGHT);
        this.add(lastClockedInLabel, 1, 2);
        this.add(clockInOutBtn, 2, 2);

        updateClockInLabel();

    }

    private void updateClockInLabel() {
        final TimeSheet timeSheet = selectedTimeSheet();
        if (timeSheet.isClockedIn()) {
            ClockedInTimeDisplayThread.getInstance().start(this.lastClockedInLabel, selectedTimeSheet());
            clockInOutBtn.setText("Clock Out");
            clockInOutBtn.setOnAction(event -> {
                timeSheet.clockOut(LocalDateTime.now());
                UserData userData = UserData.getInstance();
                userData.saveData();
                updateClockInLabel();
            });
        } else {
            lastClockedInLabel.setText("Not currently clocked in, click the button to clock in.");
            clockInOutBtn.setText("Clock In");
            clockInOutBtn.setOnAction(event -> {
                timeSheet.clockIn(LocalDateTime.now());
                UserData userData = UserData.getInstance();
                userData.saveData();
                updateClockInLabel();
            });
        }
    }

    private void initTimeSheetListView() {
        UserData userData = UserData.getInstance();
        this.timeSheetListView.getItems().addAll(userData.timeSheets());

        this.timeSheetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateClockInLabel();
            updateSelectedContractLabel();
        });

        GridPane.setVgrow(this.timeSheetListView, Priority.ALWAYS);

        this.timeSheetListView.getSelectionModel().selectFirst();
        this.add(timeSheetListView, 0, 0, 1, 3);
    }

    private TimeSheet selectedTimeSheet() {
        return this.timeSheetListView.getSelectionModel().getSelectedItem();
    }

    @Override
    public String onAfterViewCreated() {
        return this.selectedTimeSheet().getContract().toString();
    }

    @Override
    public void onViewDestroyed() {
        ClockedInTimeDisplayThread.getInstance().stop();
    }
}
