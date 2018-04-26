package com.abc.newcalendar.view;

import android.util.SparseArray;

import com.abc.newcalendar.view.calendar.CalendarDay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Anton P. on 26.04.2018.
 */
public class DataUtils {
    public static final SimpleDateFormat HEADER_DATE_FORMAT = new SimpleDateFormat("MMM dd EE");

    private DataUtils() {
    }

    public static SparseArray<List<CalendarDay>> getStubDataForDates(int daysAhead, Calendar calendar) {
        SparseArray<List<CalendarDay>> array = new SparseArray<>();
        Date startDate = calendar.getTime();
        int daysPerHeader = 5;
        for (int i = 0; i < daysAhead; i++) {
            calendar.setTime(startDate);
            calendar.add(Calendar.DAY_OF_YEAR, i * daysPerHeader);
            ArrayList<CalendarDay> calendarDays = new ArrayList<>();
            for (int numOfDays = 0; numOfDays < daysPerHeader; numOfDays++) {
                if (numOfDays != 0) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                }
                Date time = calendar.getTime();
                String format = HEADER_DATE_FORMAT.format(time);
                String[] split = format.split(" ");
                CalendarDay calendarDay = new CalendarDay(isWeekend(calendar), time, split);
                calendarDays.add(calendarDay);
            }
            array.append(i, calendarDays);
        }
        return array;
    }

    public static boolean isWeekend(Calendar calendar) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }
}
