package com.abc.newcalendar;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.abc.newcalendar.view.CalendarGrid;

import java.util.Calendar;

/**
 * Created by Anton P. on 24.04.2018.
 */
public class SimpleWeekAdapter extends RecyclerView.Adapter<SimpleWeekAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_week, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calendar instance = Calendar.getInstance();
        if (position > 0) {
            instance.add(Calendar.DAY_OF_YEAR, position * 5);
        }
        ((CalendarGrid) holder.itemView).forDates(instance.getTime());
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
