package com.tcg.contracttimelogger.gui.containers;

import com.itextpdf.text.DocumentException;
import com.tcg.contracttimelogger.concurrency.ClockedInTimeDisplayThread;
import com.tcg.contracttimelogger.data.Address;
import com.tcg.contracttimelogger.data.Contract;
import com.tcg.contracttimelogger.data.TimeSheet;
import com.tcg.contracttimelogger.gui.UIContainer;
import com.tcg.contracttimelogger.gui.components.dialogs.ErrorDialog;
import com.tcg.contracttimelogger.gui.components.views.TimeSheetListView;
import com.tcg.contracttimelogger.gui.components.views.TimeSheetTableView;
import com.tcg.contracttimelogger.reports.InvoiceGenerator;
import com.tcg.contracttimelogger.utils.App;
import com.tcg.contracttimelogger.utils.AppConstants;
import com.tcg.contracttimelogger.utils.UserData;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TimeSheetView extends GridPane implements UIContainer {

    private final TimeSheetListView timeSheetListView;
    private final Label lastClockedInLabel;
    private final Button clockInOutBtn;
    private final Label selectedContractLabel;
    private final TimeSheetTableView tableView;

    private final DatePicker invoiceStartDatePicker;
    private final DatePicker invoiceEndDatePicker;
    private final Button generateInvoiceBtn;

    public TimeSheetView() {
        super();
        this.setHgap(AppConstants.HUD_SPACING);
        this.setVgap(AppConstants.HUD_SPACING);
        this.setPadding(AppConstants.padding());

        this.timeSheetListView = new TimeSheetListView();
        lastClockedInLabel = new Label();
        clockInOutBtn = new Button();
        selectedContractLabel = new Label();
        invoiceStartDatePicker = new DatePicker();
        invoiceEndDatePicker = new DatePicker();
        generateInvoiceBtn = new Button("Generate Invoice");

        this.tableView = new TimeSheetTableView();

        initTimeSheetListView();

        GridPane.setVgrow(tableView, Priority.ALWAYS);
        GridPane.setHgrow(tableView, Priority.ALWAYS);
        this.add(tableView, 1, 2, 2, 1);
        this.tableView.setTimeSheet(this.selectedTimeSheet());
        initClockInClockOutRow();

        initInvoiceRow();

        this.add(selectedContractLabel, 1, 0);
        updateSelectedContractLabel();

//        this.setGridLinesVisible(true);
    }

    private void initInvoiceRow() {
        final HBox invoiceBox = new HBox(AppConstants.HUD_SPACING, invoiceStartDatePicker, invoiceEndDatePicker, generateInvoiceBtn);
        invoiceStartDatePicker.setValue(LocalDate.now().withDayOfMonth(1));
        invoiceEndDatePicker.setValue(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()));
        invoiceStartDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isAfter(invoiceEndDatePicker.getValue())) {
                invoiceStartDatePicker.setValue(invoiceEndDatePicker.getValue());
            }
        });
        invoiceEndDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBefore(invoiceStartDatePicker.getValue())) {
                invoiceEndDatePicker.setValue(invoiceStartDatePicker.getValue());
            }
        });
        generateInvoiceBtn.setOnAction(event -> {
            LocalDate start = invoiceStartDatePicker.getValue();
            LocalDate end = invoiceEndDatePicker.getValue();
            TimeSheet timeSheet = selectedTimeSheet();
            InvoiceGenerator gen = InvoiceGenerator.newInstance();
            try {
                gen.generate(timeSheet, start, end);
            } catch (FileNotFoundException | DocumentException e) {
                ErrorDialog dialog = new ErrorDialog(e);
                dialog.initOwner(App.instance().mainStage);
                dialog.showAndWait();
            }
        });
        this.add(invoiceBox, 1, 1);
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
        this.add(lastClockedInLabel, 1, 3);
        this.add(clockInOutBtn, 2, 3);

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
                this.tableView.setTimeSheet(this.selectedTimeSheet());
            });
        } else {
            ClockedInTimeDisplayThread.getInstance().stop();
            lastClockedInLabel.setText("Not currently clocked in, click the button to clock in.");
            clockInOutBtn.setText("Clock In");
            clockInOutBtn.setOnAction(event -> {
                timeSheet.clockIn(LocalDateTime.now());
                UserData userData = UserData.getInstance();
                userData.saveData();
                updateClockInLabel();
                this.tableView.setTimeSheet(this.selectedTimeSheet());
            });
        }
    }

    private void initTimeSheetListView() {
        UserData userData = UserData.getInstance();
        this.timeSheetListView.getItems().addAll(userData.timeSheets());

        this.timeSheetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateClockInLabel();
            updateSelectedContractLabel();
            this.tableView.setTimeSheet(this.selectedTimeSheet());
        });

        GridPane.setVgrow(this.timeSheetListView, Priority.ALWAYS);

        this.timeSheetListView.getSelectionModel().selectFirst();
        this.add(timeSheetListView, 0, 0, 1, 4);
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
