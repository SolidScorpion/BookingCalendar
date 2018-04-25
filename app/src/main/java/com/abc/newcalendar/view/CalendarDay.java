package com.abc.newcalendar.view;

import java.util.Arrays;
import java.util.Date;

/**
 * Created by Anton P. on 25.04.2018.
 */
public class CalendarDay {
    private boolean isWeekend;
    private Date date;
    private String[] formattedData;
    private boolean isSelected;

    CalendarDay(boolean isWeekend, Date date, String[] formattedData) {
        this.isWeekend = isWeekend;
        this.date = date;
        this.formattedData = formattedData;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isWeekend() {
        return isWeekend;
    }

    public Date getDate() {
        return date;
    }

    public String[] getFormattedData() {
        return formattedData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarDay that = (CalendarDay) o;

        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        return date.hashCode();
    }

    @Override
    public String toString() {
        return "CalendarDay{" +
                "isWeekend=" + isWeekend +
                ", date=" + date +
                ", formattedData=" + Arrays.toString(formattedData) +
                '}';
    }
}
