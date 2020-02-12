package com.tcg.contracttimelogger.gui.components.ui;

import javafx.scene.control.TextField;

public class RealNumberInput extends TextField {

    private double doubleValue;

    public RealNumberInput() {
        this(0.0);
    }

    public RealNumberInput(double doubleValue) {
        super(String.format("%.2f", doubleValue));
        this.doubleValue = doubleValue;
        this.textProperty().
    }


}
