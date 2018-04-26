package com.abc.newcalendar.view.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.abc.newcalendar.R;
import com.abc.newcalendar.view.DataUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Anton P. on 20.04.2018.
 */
public class CalendarGrid extends View {
    private static final String TAG = "CalendarGrid";
    private Paint commonDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint weekendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int daysPerView = 5;
    private int initialRooms;
    private int backGroundColor;
    private int headerHeight;
    private int strokeWidth;
    private int headerMargin;
    private int marginColor;
    private int lineColor;
    private int dayColor;
    private int weekendColor;
    private int headerTextColor;
    private GestureDetectorCompat gestureDetectorCompat;
    private List<RectF> daySectors;
    private List<RectF> highlightRect = new ArrayList<>(1);
    private List<CalendarDay> calendarDays;
    private SparseArray<List<RectF>> rectsByRoom;

    public CalendarGrid(Context context) {
        super(context);
        init(context, null);
    }

    public CalendarGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void forDates(List<CalendarDay> calendarDays) {
        if (calendarDays != null) {
            this.calendarDays.clear();
            this.calendarDays.addAll(calendarDays);
        }
        invalidate();
    }

    private void init(Context c, AttributeSet attributeSet) {
        DisplayMetrics display = c.getResources().getDisplayMetrics();
        TypedArray attr = c.obtainStyledAttributes(attributeSet, R.styleable.CalendarGrid, 0, 0);
        try {
            strokeWidth = attr.getDimensionPixelSize(R.styleable.CalendarGrid_lineWidth,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, display));
            dayColor = attr.getColor(R.styleable.CalendarGrid_dayColor, ContextCompat.getColor(c, R.color.dayColor));
            weekendColor = attr.getColor(R.styleable.CalendarGrid_weekendColor, ContextCompat.getColor(c, R.color.weekendColor));
            marginColor = attr.getColor(R.styleable.CalendarGrid_marginColor, Color.WHITE);
            headerMargin = attr.getDimensionPixelOffset(R.styleable.CalendarGrid_headerMargin,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, display));
            initialRooms = attr.getInt(R.styleable.CalendarGrid_initialRooms, 5);
            headerHeight = attr.getDimensionPixelSize(R.styleable.CalendarGrid_headerHeight,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, display));
            backGroundColor = attr.getColor(R.styleable.CalendarGrid_backgroundColor, Color.GREEN);
            headerTextColor = attr.getColor(R.styleable.CalendarGrid_headerTextColor, Color.BLACK);
            lineColor = attr.getColor(R.styleable.CalendarGrid_lineColor, Color.WHITE);
            daySectors = new ArrayList<>(daysPerView * initialRooms);
            calendarDays = new ArrayList<>(daysPerView);
            rectsByRoom = new SparseArray<>(initialRooms);
            gestureDetectorCompat = new GestureDetectorCompat(c, new CalendarGestureDetector());
            float totalRoomHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, display) * initialRooms;
            setMinimumHeight(Math.round(totalRoomHeight));
            initPaint();
            forDates(DataUtils.getStubDataForDates(1, Calendar.getInstance()).get(0));
        } finally {
            attr.recycle();
        }
    }

    private void initPaint() {
        commonDayPaint.setStyle(Paint.Style.FILL);
        commonDayPaint.setStrokeWidth(strokeWidth);
        commonDayPaint.setColor(lineColor);

        weekendPaint.setStyle(Paint.Style.FILL);
        weekendPaint.setStrokeWidth(strokeWidth);
        weekendPaint.setColor(weekendColor);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            float xOffset = getWidth() / daysPerView;
            float yOffset = getHeight() / initialRooms;
            for (int horizontal = 0; horizontal < initialRooms; horizontal++) {
                ArrayList<RectF> sectorsByRow = new ArrayList<>();
                for (int vertical = 0; vertical < daysPerView; vertical++) {
                    float topX = vertical * xOffset;
                    float topY = horizontal * yOffset;
                    float bottomX = (vertical + 1) * xOffset;
                    float bottomY = (horizontal + 1) * yOffset;
                    RectF rectF = new RectF(topX, topY, bottomX, bottomY);
                    sectorsByRow.add(rectF);
                    daySectors.add(rectF);
                }
                rectsByRoom.append(horizontal, sectorsByRow);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backGroundColor);
        commonDayPaint.setColor(lineColor);
        commonDayPaint.setStyle(Paint.Style.STROKE);
        drawRoomSectors(canvas);
        if (!highlightRect.isEmpty()) {
            commonDayPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            commonDayPaint.setColor(Color.BLACK);
            canvas.drawRect(highlightRect.get(0), commonDayPaint);
            highlightRect.clear();
            commonDayPaint.setStyle(Paint.Style.FILL);
            commonDayPaint.setColor(lineColor);
        }
    }

    private void drawRoomSectors(Canvas canvas) {
        for (int i = 0; i < initialRooms; i++) {
            List<RectF> roomSectors = rectsByRoom.get(i);
            for (int day = 0; day < daysPerView; day++) {
                CalendarDay calendarDay = calendarDays.get(day);
                RectF roomSector = roomSectors.get(day);
                drawSectorBackground(canvas, calendarDay, roomSector);
            }
        }
    }

    private void drawSectorBackground(Canvas canvas, CalendarDay calendarDay, RectF rectF) {
        if (calendarDay.isWeekend()) {
            canvas.drawRect(rectF, weekendPaint);
            canvas.drawRect(rectF, commonDayPaint);
        } else {
            canvas.drawRect(rectF, commonDayPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetectorCompat.onTouchEvent(event);
    }

    private class CalendarGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp " + e);
            float x = e.getX();
            float y = e.getY();
            if (y > headerHeight + headerMargin) {
                for (RectF daySector : daySectors) {
                    if (daySector.contains(x, y)) {
                        highlightRect.add(daySector);
                        invalidate();
                        return true;
                    }
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.d(TAG, "onDoubleTap " + e);
            return true;
        }
    }

    // Public methods

    public int getMarginColor() {
        return marginColor;
    }

    public void setMarginColor(int marginColor) {
        this.marginColor = marginColor;
        invalidate();
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
        invalidate();
    }

    public int getDayColor() {
        return dayColor;
    }

    public void setDayColor(int dayColor) {
        this.dayColor = dayColor;
        invalidate();
    }

    public int getWeekendColor() {
        return weekendColor;
    }

    public void setWeekendColor(int weekendColor) {
        this.weekendColor = weekendColor;
        invalidate();
    }

    public int getHeaderTextColor() {
        return headerTextColor;
    }

    public void setHeaderTextColor(int headerTextColor) {
        this.headerTextColor = headerTextColor;
        invalidate();
    }
}
