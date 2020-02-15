package com.tcg.contracttimelogger;

import com.tcg.contracttimelogger.gui.containers.NewContract;
import com.tcg.contracttimelogger.gui.containers.TimeSheetView;
import com.tcg.contracttimelogger.utils.App;
import com.tcg.contracttimelogger.utils.UserData;
import javafx.application.Application;
import javafx.stage.Stage;

public class ContractTimeLogger extends Application {

    @Override
    public void start(Stage stage) {
        App app = App.instance();
        app.mainStage = stage;
        UserData userData = UserData.getInstance();
        userData.loadData();
        app.switchContainer(userData.numberTimeSheets() == 0 ? new NewContract() : new TimeSheetView());
        app.mainStage.show();
        app.setMainStageOnClose();
    }

    public static void main(String[] args) {
        launch();
    }

}
