package com.tcg.contracttimelogger.gui.containers;

import com.tcg.contracttimelogger.gui.UIContainer;
import com.tcg.contracttimelogger.gui.components.ui.CurrencyInputField;
import com.tcg.contracttimelogger.gui.components.ui.USStateComboBox;
import com.tcg.contracttimelogger.utils.AppConstants;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class NewContract extends VBox implements UIContainer {

    public NewContract() {
        super(AppConstants.HUD_SPACING);

        final TextField nameField = new TextField();
        getChildren().addAll(new Label("Name:"), nameField);

        final TextField streetField = new TextField();
        getChildren().addAll(new Label("Street Address:"), streetField);

        final HBox addrLine2 = new HBox(AppConstants.HUD_SPACING);

        final TextField cityField = new TextField();
        addrLine2.getChildren().addAll(new Label("City:"), cityField);

        final USStateComboBox usStateComboBox = new USStateComboBox();
        addrLine2.getChildren().addAll(new Label("State:"), usStateComboBox);

        final TextField zipCodeField = new TextField();
        addrLine2.getChildren().addAll(new Label("Zip:"), zipCodeField);

        getChildren().add(addrLine2);

        final CurrencyInputField rateField = new CurrencyInputField();
        getChildren().addAll(new HBox(AppConstants.HUD_SPACING, new Label("Hourly Rate:"), rateField));

        final TextArea descriptionArea = new TextArea();
        getChildren().addAll(new Label("Description:"), descriptionArea);

        final Button addBtn = new Button("Add");

        final Button cancelBtn = new Button("Cancel");

        getChildren().addAll(new HBox(AppConstants.HUD_SPACING, addBtn, cancelBtn));

        setPadding(AppConstants.padding());

    }

    @Override
    public void onAfterViewCreated() {

    }

    @Override
    public void onViewDestroyed() {

    }
}
