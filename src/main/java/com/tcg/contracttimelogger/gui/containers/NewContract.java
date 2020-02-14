package com.tcg.contracttimelogger.gui.containers;

import com.tcg.contracttimelogger.data.Address;
import com.tcg.contracttimelogger.data.Contract;
import com.tcg.contracttimelogger.gui.UIContainer;
import com.tcg.contracttimelogger.gui.components.dialogs.ErrorDialog;
import com.tcg.contracttimelogger.gui.components.ui.CurrencyInputField;
import com.tcg.contracttimelogger.gui.components.ui.USStateComboBox;
import com.tcg.contracttimelogger.utils.App;
import com.tcg.contracttimelogger.utils.AppConstants;
import com.tcg.contracttimelogger.utils.UserData;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

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

        addBtn.setOnAction(event -> {
            final String name = nameField.getText().trim();
            final String street = streetField.getText().trim();
            final String city = cityField.getText().trim();
            final USStateComboBox.USState state = usStateComboBox.getValue();
            final String zipCode = zipCodeField.getText().trim();
            final double rate = rateField.getDoubleValue();
            final String description = descriptionArea.getText().trim();
            ErrorDialog error;
            if (name.isEmpty()) {
                error = new ErrorDialog("No Name", null, "You must enter the contract name.");
            } else if (street.isEmpty()) {
                error = new ErrorDialog("No Street", null, "You must enter the street address.");
            } else if (city.isEmpty()) {
                error = new ErrorDialog("No City", null, "You must enter the city name.");
            } else if (state == null) {
                error = new ErrorDialog("No State", null, "You must select a state.");
            } else if (zipCode.isEmpty()) {
                error = new ErrorDialog("No Zip Code", null, "You must enter a zip code.");
            } else if (Double.compare(rate, 0.0) <= 0) {
                error = new ErrorDialog("Invalid Rate", null, "Rate must be more than zero.");
            } else if (description.isEmpty()) {
                error = new ErrorDialog("No Description", null, "You must enter a description.");
            } else {
                error = null;
            }
            App app = App.instance();
            if (error != null) {
                error.initOwner(app.mainStage);
                error.showAndWait();
                return;
            }
            final long centsPerHour = (long) (rate * 100.0);
            UserData userData = UserData.getInstance();
            Address address = Address.of(street, city, state.stateCode, zipCode);
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Does this look ok?");
            confirmation.setContentText(String.format("%s\n%s\n$%.2f/h\n%s",
                    name, address, centsPerHour / 100.0, description));
            Optional<ButtonType> result = confirmation.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK) {
                userData.createTimeSheet(Contract.of(name, description, centsPerHour, address));
                userData.saveData();
            }
        });

        final Button cancelBtn = new Button("Cancel");

        cancelBtn.setOnAction(event -> {
            UserData userData = UserData.getInstance();
            if (userData.numberTimeSheets() == 0) {
                ErrorDialog errorDialog = new ErrorDialog("No Timesheets", null, "There are no existing timesheets, please add one to continue.");
                errorDialog.initOwner(App.instance().mainStage);
                errorDialog.showAndWait();
            }
        });

        getChildren().addAll(new HBox(AppConstants.HUD_SPACING, addBtn, cancelBtn));

        setPadding(AppConstants.padding());

    }

    @Override
    public String onAfterViewCreated() {
        return "New Contract";
    }

    @Override
    public void onViewDestroyed() {

    }
}
