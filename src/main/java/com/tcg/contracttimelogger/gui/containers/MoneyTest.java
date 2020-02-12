package com.tcg.contracttimelogger.gui.containers;

import com.tcg.contracttimelogger.gui.components.ui.RealNumberInput;
import javafx.scene.layout.HBox;

public class MoneyTest extends HBox {

    public MoneyTest() {
        super(5);
        RealNumberInput field = new RealNumberInput();
        getChildren().addAll(field);
    }

}
