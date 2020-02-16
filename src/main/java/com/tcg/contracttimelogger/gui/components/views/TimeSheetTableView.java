package com.tcg.contracttimelogger.gui.components.views;

import com.tcg.contracttimelogger.data.Contract;
import com.tcg.contracttimelogger.data.TimeRecord;
import com.tcg.contracttimelogger.data.TimeSheet;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TimeSheetTableView extends TableView<TimeSheetTableView.TimeRecordDataModel> {

    public TimeSheetTableView() {
        super();
        TableColumn<TimeRecordDataModel, String> clockIn = new TableColumn<>("Clocked In");
        clockIn.setCellValueFactory(param -> param.getValue().clockInProperty());

        TableColumn<TimeRecordDataModel, String> clockOut = new TableColumn<>("Clocked Out");
        clockOut.setCellValueFactory(param -> param.getValue().clockOutProperty());

        TableColumn<TimeRecordDataModel, String> hoursWorked = new TableColumn<>("Hours Worked");
        hoursWorked.setCellValueFactory(param -> param.getValue().hoursWorkedProperty());

        TableColumn<TimeRecordDataModel, String> amountEarned = new TableColumn<>("Amount Earned");
        amountEarned.setCellValueFactory(param -> param.getValue().amountEarnedProperty());

        getColumns().add(clockIn);
        getColumns().add(clockOut);
        getColumns().add(hoursWorked);
        getColumns().add(amountEarned);

        final double percentage = 1.0 / getColumns().size();
        clockIn.prefWidthProperty().bind(widthProperty().multiply(percentage));
        clockOut.prefWidthProperty().bind(widthProperty().multiply(percentage));
        hoursWorked.prefWidthProperty().bind(widthProperty().multiply(percentage));
        amountEarned.prefWidthProperty().bind(widthProperty().multiply(percentage));


        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        setEditable(false);
    }

    public void setTimeSheet(TimeSheet timeSheet) {
        List<TimeRecordDataModel> timeRecordDataModels = timeSheet.allTimeRecords()
                .stream()
                .sorted(Comparator.reverseOrder())
                .map(timeRecord -> new TimeRecordDataModel(timeSheet.getContract(), timeRecord))
                .collect(Collectors.toList());
        this.getItems().setAll(timeRecordDataModels);
    }

    static class TimeRecordDataModel {

        private final SimpleStringProperty clockIn;
        private final SimpleStringProperty clockOut;
        private final SimpleStringProperty hoursWorked;
        private final SimpleStringProperty amountEarned;

        TimeRecordDataModel(Contract contract, TimeRecord timeRecord) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
            clockIn = new SimpleStringProperty(timeRecord.clockIn.format(dateTimeFormatter));
            if (timeRecord.isClockedOut()) {
                this.clockOut = new SimpleStringProperty(timeRecord.getClockOut().format(dateTimeFormatter));
                this.hoursWorked = new SimpleStringProperty(String.format("%.2f", timeRecord.hoursWorked()));
                double dollarsEarned = (contract.centsPerHour * timeRecord.hoursWorked()) / 100.0;
                this.amountEarned = new SimpleStringProperty(String.format("$%.2f", dollarsEarned));
            } else {
                this.clockOut = new SimpleStringProperty("-");
                this.hoursWorked = new SimpleStringProperty("-");
                this.amountEarned = new SimpleStringProperty("-");
            }
        }

        public SimpleStringProperty clockInProperty() {
            return clockIn;
        }

        public SimpleStringProperty clockOutProperty() {
            return clockOut;
        }

        public SimpleStringProperty hoursWorkedProperty() {
            return hoursWorked;
        }

        public SimpleStringProperty amountEarnedProperty() {
            return amountEarned;
        }
    }

}
