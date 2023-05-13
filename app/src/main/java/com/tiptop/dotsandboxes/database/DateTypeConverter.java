package com.tiptop.dotsandboxes.database;


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import androidx.room.TypeConverter;

public class DateTypeConverter {
    @TypeConverter
    public static Calendar calendarFromTimestamp(Date value) {
        if (value == null) {
            return null;
        }
        Calendar cal = new GregorianCalendar();

        cal.setTimeInMillis(value.getTime() * 1000);
        return cal;
    }

    @TypeConverter
    public static Date dateToTimestamp(Calendar cal) {
        if (cal == null) {
            return null;
        }
        return new Date(cal.getTimeInMillis() / 1000);
    }
}