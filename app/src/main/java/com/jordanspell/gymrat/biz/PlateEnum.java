package com.jordanspell.gymrat.biz;

/**
 * Created by elfrank on 2/16/15.
 */
public enum PlateEnum {

    FortyFiveLB("45"),
    ThirtyFiveLB("35"),
    TwentyFiveLB("25"),
    TenLB("10"),
    FiveLB("5"),
    TwoAndHalfLB("2.5"),
    TwentyKG("20.0"),
    TenKG("10.0"),
    FiveKG("5.0"),
    TwoAndHalfKG("2.5"),
    OneAndQuarterKG("1.25");

    private String displayName;

    PlateEnum(String displayName) {
        this.displayName = displayName;
    }

    public static PlateEnum getEnumByName(PlateEnum enumName) {
        for(PlateEnum e : values()) {
            if(e == enumName) return e;
        }
        return null;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
