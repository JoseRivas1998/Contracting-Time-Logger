package com.tcg.contracttimelogger;

import com.tcg.contracttimelogger.gui.containers.NewContract;
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
        if(userData.numberTimeSheets() == 0) {
            app.switchContainer(new NewContract());
        }
        app.mainStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
