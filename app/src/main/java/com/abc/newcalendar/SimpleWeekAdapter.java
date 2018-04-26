package com.abc.newcalendar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abc.newcalendar.view.calendar.CalendarGrid;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Anton P. on 24.04.2018.
 */
public class SimpleWeekAdapter extends RecyclerView.Adapter<SimpleWeekAdapter.ViewHolder> {
    private Calendar calendar;
    private Date startDate;

    SimpleWeekAdapter() {
        calendar = Calendar.getInstance();
        startDate = calendar.getTime();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_week, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        calendar.setTime(startDate);
        if (position > 0) {
            calendar.add(Calendar.DAY_OF_YEAR, position * 5);
        }
        holder.calendarGrid.forDates(calendar.getTime());
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CalendarGrid calendarGrid;

        ViewHolder(View itemView) {
            super(itemView);
            calendarGrid = itemView.findViewById(R.id.calendar);
            calendarGrid.setOnClickListener(View::invalidate);
        }
    }
}
