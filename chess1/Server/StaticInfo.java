package org.example.chess1.Server;

public class StaticInfo {
    public static int portFrom = 56024;
    public static int portTo = 56025;

    public static String address = "localhost";

    public static int portUsing;

    public static void setAddress(String address) {
        StaticInfo.address = address;
    }

    public static void setPortRange(int portFrom, int portTo) {
        StaticInfo.portFrom = portFrom;
        StaticInfo.portTo= portTo;
    }
}
