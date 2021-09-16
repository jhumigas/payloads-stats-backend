package com.company.payloadsstatsbackend.common;

public class Constants {
    public static final int WINDOW_LENGTH_IN_SECONDS = 5 * 60;
    public static final String ZONE_ID = "Europe/Paris";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}