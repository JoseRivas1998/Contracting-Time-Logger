package com.tcg.contracttimelogger.gui.components.menu;

import com.tcg.contracttimelogger.gui.containers.NewContract;
import com.tcg.contracttimelogger.gui.containers.ProfileInput;
import com.tcg.contracttimelogger.utils.App;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class TopMenuBar extends MenuBar {

    public TopMenuBar() {
        super();

        final Menu contracts = new Menu("Contracts");
        final MenuItem newContract = new MenuItem("New Contract");
        newContract.setOnAction(event -> {
            App app = App.instance();
            app.switchContainer(new NewContract());
        });
        contracts.getItems().addAll(newContract);

        final Menu edit = new Menu("Edit");
        final MenuItem editProfile = new MenuItem("Edit Profile");
        editProfile.setOnAction(event -> {
            App app = App.instance();
            app.switchContainer(new ProfileInput());
        });
        edit.getItems().addAll(editProfile);

        getMenus().addAll(contracts, edit);

    }
}
