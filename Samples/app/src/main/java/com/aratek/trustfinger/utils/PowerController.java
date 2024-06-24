package com.aratek.trustfinger.utils;

public class PowerController {
    private static final String cmdpowerON = "/sys/devices/platform/m536as_gpio_pin/usbhub4_power 1";
    private static final String cdmpowerOFF = "/sys/devices/platform/m536as_gpio_pin/usbhub4_power 0";


    public static void powerOn() {
        ShellUtils.execCmd(cmdpowerON, true);

    }

    public static void powerOff() {
        ShellUtils.execCmd(cdmpowerOFF, true);
    }
    
}



