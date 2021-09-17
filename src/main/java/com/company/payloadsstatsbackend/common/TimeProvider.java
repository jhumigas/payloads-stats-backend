package com.company.payloadsstatsbackend.common;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeProvider {
    private static Clock clock = Clock.systemDefaultZone();
    private static ZoneId zoneId = ZoneId.systemDefault();

    public static LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public static void useFixedClockAt(LocalDateTime date) {
        clock = Clock.fixed(date.atZone(zoneId).toInstant(), zoneId);
    }
}
