package org.openmrs.module.insuranceclaims.util;

import java.time.Clock;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {

    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private Clock clock = Clock.systemUTC();

    public Date now() {
        return Date.from(Instant.now(clock));
    }

    public Date plusDays(Date date, int duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, duration);
        return calendar.getTime();
    }

    public void setClock(Clock clock) {
        this.clock = clock;
    }

    public Date getDateWithDefaultTimeZone(Date timestamp) {
        return getDateWithTimeZone(timestamp, DEFAULT_TIME_ZONE);
    }

    public Date getDateWithLocalTimeZone(Date timestamp) {
        return getDateWithTimeZone(timestamp, getLocalTimeZone());
    }

    public Date getDateWithTimeZone(Date timestamp, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(timestamp);
        return calendar.getTime();
    }

    public TimeZone getLocalTimeZone() {
        return TimeZone.getDefault();
    }
}
