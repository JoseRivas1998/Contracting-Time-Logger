package com.tcg.contracttimelogger.utils;

import com.tcg.contracttimelogger.gui.UIContainer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App {

    private static App app;

    public Stage mainStage;
    public Scene mainScene;
    private UIContainer mainUIContainer;

    private App() {
    }

    public static App instance() {
        if(app == null) {
            synchronized (App.class) {
                if(app == null) {
                    app = new App();
                }
            }
        }
        return app;
    }

    public  <T extends Parent & UIContainer> void switchContainer(T uiContainer) {
        if(mainUIContainer != null) mainUIContainer.onViewDestroyed();
        mainScene = new Scene(uiContainer);
        mainStage.setScene(mainScene);
        mainUIContainer = uiContainer;
        mainUIContainer.onAfterViewCreated();
    }

}
