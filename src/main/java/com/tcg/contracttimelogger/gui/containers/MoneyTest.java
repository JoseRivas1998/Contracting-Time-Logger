package com.tcg.contracttimelogger.gui.containers;

import com.tcg.contracttimelogger.gui.components.ui.CurrencyInputField;
import javafx.scene.layout.HBox;

public class MoneyTest extends HBox {

    public MoneyTest() {
        super(5);
        CurrencyInputField field = new CurrencyInputField();
        getChildren().addAll(field);
    }

}
