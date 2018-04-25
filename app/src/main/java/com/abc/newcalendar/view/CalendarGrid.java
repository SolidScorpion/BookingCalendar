package com.abc.newcalendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.abc.newcalendar.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Anton P. on 20.04.2018.
 */
public class CalendarGrid extends View {
    private static final String TAG = "CalendarGrid";
    private Paint commonDayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint weekendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint monthTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint dayTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint dayWeekTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private int daysPerView = 5;
    private int initialRooms = 6;
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
    private List<RectF> topSectors;
    private List<RectF> daySectors;
    private boolean shouldHighlight = false;
    private List<RectF> highlightRect = new ArrayList<>();
    private Rect measureRect = new Rect();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd EE");
    private List<CalendarDay> calendarDays;
    private Calendar startCalendar;
    private String[] headerDates = new String[daysPerView * 3]; // 3 strings per day (months, day, day of week)
    private float[] headerDatesHeights = new float[headerDates.length];
    private SparseArray<List<RectF>> rectsByRoom;

    public CalendarGrid(Context context) {
        super(context);
        init(context, null);
    }

    public CalendarGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void forDates(Date startDate) {
        startCalendar.setTime(startDate);
        initHeaderDates();
        invalidate();
    }

    private void init(Context c, AttributeSet attributeSet) {
        DisplayMetrics display = c.getResources().getDisplayMetrics();
        TypedArray attr = c.obtainStyledAttributes(attributeSet, R.styleable.CalendarGrid, 0, 0);
        gestureDetectorCompat = new GestureDetectorCompat(c, new CalendarGestureDetector());
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
            startCalendar = Calendar.getInstance();
            topSectors = new ArrayList<>(daysPerView);
            daySectors = new ArrayList<>(daysPerView * initialRooms);
            calendarDays = new ArrayList<>(daysPerView);
            rectsByRoom = new SparseArray<>(initialRooms);
            initHeaderDates();
            fillSectors();
            initPaint();
        } finally {
            attr.recycle();
        }
    }

    private void initPaint() {
        DisplayMetrics display = getContext().getResources().getDisplayMetrics();

        commonDayPaint.setStyle(Paint.Style.FILL);
        commonDayPaint.setStrokeWidth(strokeWidth);
        commonDayPaint.setColor(lineColor);

        weekendPaint.setStyle(Paint.Style.FILL);
        weekendPaint.setStrokeWidth(strokeWidth);
        weekendPaint.setColor(weekendColor);

        monthTextPaint.setTextAlign(Paint.Align.CENTER);
        monthTextPaint.setColor(headerTextColor);
        monthTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                12, display));

        dayTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 26, display));
        dayTextPaint.setColor(headerTextColor);
        dayTextPaint.setTextAlign(Paint.Align.CENTER);

        dayWeekTextPaint.setColor(headerTextColor);
        dayWeekTextPaint.setTextAlign(Paint.Align.CENTER);
        dayWeekTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, display));
        dayWeekTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    private boolean isWeekend(Calendar cal) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY;
    }

    private void initHeaderDates() {
        calendarDays.clear();
        for (int i = 0; i < daysPerView; i++) {
            if (i != 0) {
                startCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            Date time = startCalendar.getTime();
            String date = dateFormat.format(time);
            String[] split = date.split(" ");
            CalendarDay day = new CalendarDay(isWeekend(startCalendar), time, split);
            calendarDays.add(day);
            int splitIndex = 0;
            int inner = i * 3 + 3;
            for (int k = i * 3; k < inner; k++) {
                Paint paint = getPaint(splitIndex);
                String splitDate = split[splitIndex++];
                paint.getTextBounds(splitDate, 0, splitDate.length(), measureRect);
                headerDatesHeights[k] = measureRect.height();
                headerDates[k] = splitDate;
            }
        }
    }

    private Paint getPaint(int splitIndex) {
        switch (splitIndex) {
            case 0:
                return monthTextPaint;
            case 1:
                return dayTextPaint;
            case 2:
                return dayWeekTextPaint;
            default:
                return monthTextPaint;
        }
    }

    private void fillSectors() {
        addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                removeOnLayoutChangeListener(this);
                float xOffset = getWidth() / daysPerView;
                float topOffset = headerHeight + headerMargin;
                float yOffset = (getHeight() - topOffset) / initialRooms;
                for (int i = 0; i < daysPerView; i++) {
                    float x = i * xOffset;
                    RectF rectF = new RectF(x, 0, x + xOffset, headerHeight);
                    topSectors.add(rectF);
                }
                for (int horizontal = 0; horizontal < initialRooms; horizontal++) {
                    ArrayList<RectF> sectorsByRow = new ArrayList<>();
                    for (int vertical = 0; vertical < daysPerView; vertical++) {
                        float topX = vertical * xOffset;
                        float topY = horizontal * yOffset + topOffset;
                        float bottomX = (vertical + 1) * xOffset;
                        float bottomY = (horizontal + 1) * yOffset + topOffset;
                        RectF rectF = new RectF(topX, topY, bottomX, bottomY);
                        sectorsByRow.add(rectF);
                        daySectors.add(rectF);
                    }
                    rectsByRoom.append(horizontal, sectorsByRow);
                }
            }
        });
    }

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backGroundColor);
        drawHeader(canvas);
        commonDayPaint.setStyle(Paint.Style.FILL);
        drawMargin(canvas);
        commonDayPaint.setColor(lineColor);
        commonDayPaint.setStyle(Paint.Style.STROKE);
        drawRoomSectors(canvas);
        if (shouldHighlight && !highlightRect.isEmpty()) {
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
            List<RectF> rectFS = rectsByRoom.get(i);
            for (int rectIndex = 0; rectIndex < rectFS.size(); rectIndex++) {
                CalendarDay calendarDay = calendarDays.get(rectIndex);
                RectF rectF = rectFS.get(rectIndex);
                drawSectorBackground(canvas, calendarDay, rectF);
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

    private void drawMargin(Canvas canvas) {
        commonDayPaint.setColor(marginColor);
        canvas.drawRect(0, headerHeight, getWidth(), headerHeight + headerMargin, commonDayPaint);
    }

    private void drawHeader(Canvas canvas) {
        commonDayPaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < initialRooms; i++) {
            RectF rect = topSectors.get(i);
            CalendarDay calendarDay = calendarDays.get(i);
            drawSectorBackground(canvas, calendarDay, rect);
            Log.d(TAG, "info for view: " + i);
            int inner = i * 3 + 3;
            int position = 0;
            for (int k = i * 3; k < inner; k++) {
                String headerDate = headerDates[k];
                float x = 0;
                float y = 0;
                Paint textPaint = getPaint(position);
                float paintHeight = headerDatesHeights[k];
                switch (position) {
                    case 0:
                        x = rect.centerX();
                        y = (rect.height() / 8) + (paintHeight / 2);
                        break;
                    case 1:
                        y = rect.centerY() + paintHeight / 3;
                        x = rect.centerX();
                        break;
                    case 2:
                        x = rect.centerX();
                        y = (rect.height() / 8) * 7;
                        break;
                }
                position++;
                canvas.drawText(headerDate, x, y, textPaint);
                Log.d(TAG, headerDate);
            }
        }
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
                        shouldHighlight = true;
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
}
