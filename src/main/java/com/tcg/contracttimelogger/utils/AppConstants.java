package com.tcg.contracttimelogger.utils;

import javafx.geometry.Insets;

public final class AppConstants {

    public static final int HUD_SPACING = 10;
    public static final String CONTRACTS_FILE = "contracts.json";

    public static Insets padding() {
        return new Insets(HUD_SPACING);
    }

}
