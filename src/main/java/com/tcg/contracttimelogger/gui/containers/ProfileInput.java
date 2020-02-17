package com.tcg.contracttimelogger.gui.containers;

import com.tcg.contracttimelogger.data.Address;
import com.tcg.contracttimelogger.data.Profile;
import com.tcg.contracttimelogger.gui.UIContainer;
import com.tcg.contracttimelogger.gui.components.dialogs.ErrorDialog;
import com.tcg.contracttimelogger.gui.components.ui.USStateComboBox;
import com.tcg.contracttimelogger.utils.App;
import com.tcg.contracttimelogger.utils.AppConstants;
import com.tcg.contracttimelogger.utils.UserData;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class ProfileInput extends GridPane implements UIContainer {

    public ProfileInput() {
        super();
        final TextField nameField = new TextField();
        final TextField streetField = new TextField();
        final TextField cityField = new TextField();
        final USStateComboBox stateBox = new USStateComboBox();
        final TextField zipCodeField = new TextField();

        final Button save = new Button("Save");
        final Button cancel = new Button("Cancel");

        this.add(new Label("Name"), 0, 0, 4, 1);
        this.add(nameField, 0, 1, 3, 1);
        GridPane.setHgrow(nameField, Priority.ALWAYS);

        this.add(new Label("Street Address:"), 0, 2);
        this.add(streetField, 0, 3, 3, 1);
        GridPane.setHgrow(streetField, Priority.ALWAYS);

        this.add(new HBox(AppConstants.HUD_SPACING, new Label("City:"), cityField), 0, 4);

        this.add(new HBox(AppConstants.HUD_SPACING, new Label("State:"), stateBox), 1, 4);

        final HBox zipBox = new HBox(AppConstants.HUD_SPACING, new Label("Zip Code:"), zipCodeField);
        GridPane.setHgrow(zipBox, Priority.ALWAYS);
        this.add(zipBox, 2, 4);

        this.add(new HBox(AppConstants.HUD_SPACING, save, cancel), 0, 5);

        save.setOnAction(event -> {
            final String name = nameField.getText().trim();
            final String street = streetField.getText().trim();
            final String city = cityField.getText().trim();
            final USStateComboBox.USState state = stateBox.getValue();
            final String zipCode = zipCodeField.getText().trim();
            ErrorDialog errorDialog = null;
            if (name.isEmpty()) {
                errorDialog = new ErrorDialog("No Name", null, "You must enter a name.");
            } else if (street.isEmpty()) {
                errorDialog = new ErrorDialog("No Street Address", null, "You must enter a street address.");
            } else if (city.isEmpty()) {
                errorDialog = new ErrorDialog("No City", null, "You must enter a city.");
            } else if (state == null) {
                errorDialog = new ErrorDialog("No State", null, "You must select a state.");
            } else if (zipCode.isEmpty()) {
                errorDialog = new ErrorDialog("No Zip Code", null, "You must enter a zip code.");
            }
            App app = App.instance();
            if (errorDialog != null) {
                errorDialog.initOwner(app.mainStage);
                errorDialog.showAndWait();
                return;
            }
            UserData userData = UserData.getInstance();
            final Address address = Address.of(street, city, state.stateCode, zipCode);
            userData.setProfile(Profile.of(name, address));
            userData.saveData();
            app.switchContainer(userData.numberTimeSheets() == 0 ? new NewContract() : new TimeSheetView());
        });

        cancel.setOnAction(event -> {
            UserData userData = UserData.getInstance();
            App app = App.instance();
            if (userData.hasProfile()) {
                app.switchContainer(userData.numberTimeSheets() == 0 ? new NewContract() : new TimeSheetView());
            } else {
                ErrorDialog errorDialog = new ErrorDialog("No profile", null, "You must enter a profile.");
                errorDialog.initOwner(app.mainStage);
                errorDialog.showAndWait();
            }
        });

        this.setPadding(AppConstants.padding());
        this.setHgap(AppConstants.HUD_SPACING);
        this.setVgap(AppConstants.HUD_SPACING);

        UserData userData = UserData.getInstance();
        if (userData.hasProfile()) {
            Profile profile = userData.getProfile();
            nameField.setText(profile.name);
            streetField.setText(profile.address.street);
            cityField.setText(profile.address.city);
            zipCodeField.setText(profile.address.zipCode);
            USStateComboBox.USState state = null;
            for (int i = 0; i < USStateComboBox.USState.values().length && state == null; ++i) {
                if(USStateComboBox.USState.values()[i].stateCode.equals(profile.address.state)) {
                    state = USStateComboBox.USState.values()[i];
                }
            }
            if(state != null) stateBox.setValue(state);
        }

//        this.setGridLinesVisible(true);

    }

    @Override
    public String onAfterViewCreated() {
        return "Edit Profile";
    }

    @Override
    public void onViewDestroyed() {

    }
}
