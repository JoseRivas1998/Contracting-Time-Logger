package com.tcg.contracttimelogger;

import com.tcg.contracttimelogger.gui.containers.NewContract;
import com.tcg.contracttimelogger.utils.App;
import javafx.application.Application;
import javafx.stage.Stage;

public class ContractTimeLogger extends Application {

    @Override
    public void start(Stage stage) {
        App app = App.instance();
        app.mainStage = stage;
        app.switchContainer(new NewContract());
    }

    public static void main(String[] args) {
        launch();
    }

}
