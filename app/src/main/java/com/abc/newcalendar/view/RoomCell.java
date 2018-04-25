package com.abc.newcalendar.view;

import android.graphics.RectF;

/**
 * Created by Anton P. on 25.04.2018.
 */
public class RoomCell {
    private boolean isSelected;
    private CalendarDay calendarDay;
    private RectF rooomRect;
    private int roomNumber;

    public RoomCell(CalendarDay calendarDay, RectF rooomRect, int roomNumber) {
        this.calendarDay = calendarDay;
        this.rooomRect = rooomRect;
        this.roomNumber = roomNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public CalendarDay getCalendarDay() {
        return calendarDay;
    }

    public RectF getRooomRect() {
        return rooomRect;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    @Override
    public String toString() {
        return "RoomCell{" +
                "isSelected=" + isSelected +
                ", calendarDay=" + calendarDay +
                ", rooomRect=" + rooomRect +
                ", roomNumber=" + roomNumber +
                '}';
    }
}
