package com.tcg.contracttimelogger.gui.containers;

import com.tcg.contracttimelogger.gui.UIContainer;
import com.tcg.contracttimelogger.gui.components.views.TimeSheetListView;
import com.tcg.contracttimelogger.utils.AppConstants;
import com.tcg.contracttimelogger.utils.UserData;
import javafx.scene.layout.GridPane;

public class TimeSheetView extends GridPane implements UIContainer {

    private final TimeSheetListView timeSheetListView;

    public TimeSheetView() {
        super();
        this.setHgap(AppConstants.HUD_SPACING);
        this.setVgap(AppConstants.HUD_SPACING);
        this.setPadding(AppConstants.padding());

        UserData userData = UserData.getInstance();

        this.timeSheetListView = new TimeSheetListView();
        this.timeSheetListView.getItems().addAll(userData.timeSheets());

        this.timeSheetListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue.getContract());
        });

        this.timeSheetListView.getSelectionModel().selectFirst();
        this.add(timeSheetListView, 0, 0);

        this.setGridLinesVisible(true);
    }

    @Override
    public String onAfterViewCreated() {
        return this.timeSheetListView.getSelectionModel().getSelectedItem().getContract().toString();
    }

    @Override
    public void onViewDestroyed() {

    }
}
