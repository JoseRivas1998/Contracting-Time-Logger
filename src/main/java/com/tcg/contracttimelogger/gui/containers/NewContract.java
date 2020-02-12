package com.tcg.contracttimelogger.gui.containers;

import com.tcg.contracttimelogger.gui.UIContainer;
import com.tcg.contracttimelogger.gui.components.ui.USStateComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class NewContract extends VBox implements UIContainer {

    public NewContract() {

        final TextField nameField = new TextField();
        getChildren().addAll(new USStateComboBox());

    }

    @Override
    public void onAfterViewCreated() {

    }

    @Override
    public void onViewDestroyed() {

    }
}
