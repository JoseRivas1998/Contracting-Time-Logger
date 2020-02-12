package com.tcg.contracttimelogger.gui.components.ui;

import javafx.scene.control.ComboBox;

public class USStateComboBox extends ComboBox<USStateComboBox.USState> {

    public enum USState {
        Alabama("AL"),
        Alaska("AK"),
        Arizona("AZ"),
        Arkansas("AR"),
        California("CA"),
        Colorado("CO"),
        Connecticut("CT"),
        Delaware("DE"),
        Florida("FL"),
        Georgia("GA"),
        Hawaii("HI"),
        Idaho("ID"),
        Illinois("IL"),
        Indiana("IN"),
        Iowa("IA"),
        Kansas("KS"),
        Kentucky("KY"),
        Louisiana("LA"),
        Maine("ME"),
        Maryland("MD"),
        Massachusetts("MA"),
        Michigan("MI"),
        Minnesota("MN"),
        Mississippi("MS"),
        Missouri("MO"),
        Montana("MT"),
        Nebraska("NE"),
        Nevada("NV"),
        NewHampshire("NH"),
        NewJersey("NJ"),
        NewMexico("NM"),
        NewYork("NY"),
        NorthCarolina("NC"),
        NorthDakota("ND"),
        Ohio("OH"),
        Oklahoma("OK"),
        Oregon("OR"),
        Pennsylvania("PA"),
        RhodeIsland("RI"),
        SouthCarolina("SD"),
        Tennessee("TN"),
        Texas("TX"),
        Utah("UT"),
        Vermont("VT"),
        Virginia("VA"),
        Washington("WA"),
        WestVirginia("WV"),
        Wisconsin("WI"),
        Wyoming("WY");
        public final String stateCode;

        USState(String stateCode) {
            this.stateCode = stateCode;
        }


        @Override
        public String toString() {
            return super.toString().replaceAll(
                    String.format("%s|%s|%s",
                            "(?<=[A-Z])(?=[A-Z][a-z])",
                            "(?<=[^A-Z])(?=[A-Z])",
                            "(?<=[A-Za-z])(?=[^A-Za-z])"
                    ),
                    " "
            );
        }
    }

    public USStateComboBox() {
        super();
        getItems().addAll(USState.values());
    }

}
