package com.aratek.trustfinger.utils;

/**
 * @author zhangx by 2019-01-09
 * @description
 */
public enum DeviceType {
    AUTO("AUTO", 0), A400("A400", 1), A600("A600", 2), EM4010("EM4010", 3), EM3011("EM3011", 4),
    EM2010("EM2010", 5), EM1920("EM1920", 6), FRT610A("FRT610A", 7), EM03_4010("EM034010", 8),
    EM03_3011("EM033011", 9), A700("A700", 10), FPK402("FPK402", 11), A620("A620", 12);
    private String type;
    private int position;

    DeviceType(String type, int position) {
        this.type = type;
        this.position = position;
    }

    public static int getDeviceTypePosition(String type) {
        for (DeviceType deviceType : DeviceType.values()) {
            if (deviceType.toString().equals(type)) {
                return deviceType.getPosition();
            }
        }
        return 0;
    }

    public String toString() {
        return this.type;
    }


    public int getPosition() {
        return position;
    }

}
