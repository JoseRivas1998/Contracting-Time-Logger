package com.tcg.contracttimelogger.gui.components.ui;

import javafx.scene.control.TextField;

import java.util.regex.Pattern;

public class CurrencyInputField extends TextField {

    private double doubleValue;

    public CurrencyInputField() {
        this(0.0);
    }

    public CurrencyInputField(double doubleValue) {
        super(String.format("%.2f", doubleValue));
        this.doubleValue = doubleValue;
        this.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!Pattern.matches("(([0-9]*)|([0-9]*\\.[0-9]?[0-9]?))", newValue)) {
                setText(oldValue);
            }
        });

    }


}
