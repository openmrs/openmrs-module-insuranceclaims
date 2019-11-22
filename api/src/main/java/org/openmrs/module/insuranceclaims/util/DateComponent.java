package org.openmrs.module.insuranceclaims.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public final class DateComponent {

    private static final TimeZone DEFAULT_TIME_ZONE = TimeZone.getTimeZone("UTC");

    public static Date now() {
        return getDateWithDefaultTimeZone(new Date());
    }

    public static Date plusDays(Date date, int duration) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, duration);
        return calendar.getTime();
    }

    public static Date getDateWithDefaultTimeZone(Date timestamp) {
        return getDateWithTimeZone(timestamp, DEFAULT_TIME_ZONE);
    }

    public static Date getDateWithLocalTimeZone(Date timestamp) {
        return getDateWithTimeZone(timestamp, getLocalTimeZone());
    }

    public static Date getDateWithTimeZone(Date timestamp, TimeZone timeZone) {
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.setTime(timestamp);
        return calendar.getTime();
    }

    public static TimeZone getLocalTimeZone() {
        return TimeZone.getDefault();
    }
}
