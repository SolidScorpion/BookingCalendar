package com.abc.newcalendar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abc.newcalendar.view.calendar.CalendarDay;
import com.abc.newcalendar.view.calendar.CalendarGrid;
import com.abc.newcalendar.view.calendar.CalendarHeader;

import java.util.List;

/**
 * Created by Anton P. on 24.04.2018.
 */
public class SimpleWeekAdapter extends RecyclerView.Adapter<SimpleWeekAdapter.ViewHolder> {
    public static final int INITIAL_SIZE = 10;
    private SparseArray<List<CalendarDay>> calendarDataForViews;

    SimpleWeekAdapter(SparseArray<List<CalendarDay>> calendarDataForViews) {
        this.calendarDataForViews = calendarDataForViews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_week, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<CalendarDay> calendarDays = calendarDataForViews.get(position);
        holder.calendarGrid.forDates(calendarDays);
        holder.calendarHeader.setCalendarDays(calendarDays);
    }

    @Override
    public int getItemCount() {
        return INITIAL_SIZE;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CalendarGrid calendarGrid;
        CalendarHeader calendarHeader;
        ViewHolder(View itemView) {
            super(itemView);
            calendarHeader = itemView.findViewById(R.id.header);
            calendarGrid = itemView.findViewById(R.id.calendar);
            calendarGrid.setOnClickListener(View::invalidate);
        }
    }
}
