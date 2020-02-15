package com.tcg.contracttimelogger.gui.components.views;

import com.tcg.contracttimelogger.data.TimeSheet;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

public class TimeSheetListView extends ListView<TimeSheet> {

    public TimeSheetListView() {
        super();
        this.setCellFactory(param -> new ListCell<TimeSheet>() {
            @Override
            protected void updateItem(TimeSheet item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null) {
                    this.setText(item.getContract().name);
                }
            }
        });
        this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }
}
