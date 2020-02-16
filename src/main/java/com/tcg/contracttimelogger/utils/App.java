package com.tcg.contracttimelogger.utils;

import com.tcg.contracttimelogger.gui.UIContainer;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App {

    private static App app;

    public Stage mainStage;
    public Scene mainScene;
    private BorderPane mainLayout;
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

    public void initLayout() {
        mainLayout = new BorderPane();
        mainScene = new Scene(mainLayout, 1280, 720);
        mainStage.setScene(mainScene);
    }

    public  <T extends Parent & UIContainer> void switchContainer(T uiContainer) {
        if(mainUIContainer != null) mainUIContainer.onViewDestroyed();
        mainUIContainer = uiContainer;
        mainLayout.setCenter(uiContainer);
        String title = mainUIContainer.onAfterViewCreated();
        setTitle(title + " | Contracting Time Logger");
    }

    public void setTitle(String title) {
        mainStage.setTitle(title);
    }

    public void setMainStageOnClose() {
        mainStage.setOnCloseRequest(event -> {
            if(mainUIContainer != null) {
                mainUIContainer.onViewDestroyed();
                mainUIContainer = null;
            }
            UserData userData = UserData.getInstance();
            userData.saveData();
        });
    }

}
