package com.abc.newcalendar.view.calendar;

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
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.abc.newcalendar.R;
import com.abc.newcalendar.view.DataUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Anton P. on 26.04.2018.
 */
public class CalendarHeader extends View {
    private static final String TAG = "CalendarHeader";
    private Paint weekendPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint monthTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint dayTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint dayWeekTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint dayPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int daysPerView = 5;
    private int headerHeight;
    private int headerMargin;
    private int weekendColor;
    private int marginColor;
    private int backGroundColor;
    private int headerTextColor;
    private int lineColor;
    private int lineWidth;
    private List<CalendarDay> calendarDays = new ArrayList<>(daysPerView);
    private String[] headerDates = new String[daysPerView * 3]; // 3 strings per day (months, day, day of week)
    private float[] headerDatesHeights = new float[headerDates.length];
    private Rect measureRect = new Rect();
    private List<RectF> topSectors = new ArrayList<>(daysPerView);

    public CalendarHeader(Context context) {
        super(context);
        init(context, null);
    }

    public CalendarHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context c, AttributeSet attrs) {
        DisplayMetrics display = c.getResources().getDisplayMetrics();
        TypedArray attr = c.obtainStyledAttributes(attrs, R.styleable.CalendarHeader, 0, 0);
        try {
            lineWidth = attr.getDimensionPixelSize(R.styleable.CalendarHeader_header_line_width,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, display));
            headerHeight = attr.getDimensionPixelSize(R.styleable.CalendarHeader_header_Height,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, display));
            weekendColor = attr.getColor(R.styleable.CalendarHeader_header_weekendColor, ContextCompat.getColor(c, R.color.weekendColor));
            marginColor = attr.getColor(R.styleable.CalendarHeader_header_marginColor, Color.WHITE);
            backGroundColor = attr.getColor(R.styleable.CalendarHeader_header_backgroundColor, Color.GREEN);
            headerTextColor = attr.getColor(R.styleable.CalendarGrid_headerTextColor, Color.BLACK);
            lineColor = attr.getColor(R.styleable.CalendarHeader_header_lineColor, Color.WHITE);
            headerMargin = attr.getDimensionPixelOffset(R.styleable.CalendarHeader_header_headerMargin,
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, display));
            setMinimumHeight(headerHeight + headerMargin);
            initPaint();
            setCalendarDays(DataUtils.getStubDataForDates(1, Calendar.getInstance()).get(0));
        } finally {
            attr.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int resultWidth = MeasureSpec.getSize(widthMeasureSpec);
        int resultHeight = Math.round(headerHeight + headerMargin);
        switch (mode) {
            case MeasureSpec.AT_MOST:
                resultHeight = Math.min(height, resultHeight);
                break;
            case MeasureSpec.EXACTLY:
                resultHeight = height;
                break;
            case MeasureSpec.UNSPECIFIED:
                resultHeight = height;
                break;

        }
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(backGroundColor);
        drawHeader(canvas);
        drawMargin(canvas);
    }

    private void drawHeader(Canvas canvas) {
        dayPaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < daysPerView; i++) {
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
                        y = (rect.height() / 8) * 3.5f + (paintHeight / 2);
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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            float xOffset = getWidth() / daysPerView;
            for (int i = 0; i < daysPerView; i++) {
                float x = i * xOffset;
                RectF rectF = new RectF(x, 0, x + xOffset, headerHeight);
                topSectors.add(rectF);
            }
        }
    }

    private void drawMargin(Canvas canvas) {
        dayPaint.setStyle(Paint.Style.FILL);
        dayPaint.setColor(marginColor);
        canvas.drawRect(0, headerHeight, getWidth(), headerHeight + headerMargin, dayPaint);
    }

    private void drawSectorBackground(Canvas canvas, CalendarDay calendarDay, RectF rectF) {
        if (calendarDay.isWeekend()) {
            canvas.drawRect(rectF, weekendPaint);
            canvas.drawRect(rectF, dayPaint);
        } else {
            canvas.drawRect(rectF, dayPaint);
        }
    }

    public void setCalendarDays(List<CalendarDay> calendarDays) {
        if (calendarDays != null) {
            this.calendarDays.clear();
            this.calendarDays.addAll(calendarDays);
        }
        initHeaderDates();
    }

    private void initHeaderDates() {
        for (int i = 0; i < daysPerView; i++) {
            String[] split = calendarDays.get(i).getFormattedData();
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

    private void initPaint() {
        DisplayMetrics display = getContext().getResources().getDisplayMetrics();
        dayPaint.setColor(lineColor);
        dayPaint.setStrokeWidth(lineWidth);
        dayPaint.setStyle(Paint.Style.STROKE);

        weekendPaint.setStyle(Paint.Style.FILL);
        weekendPaint.setStrokeWidth(lineWidth);
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
}
