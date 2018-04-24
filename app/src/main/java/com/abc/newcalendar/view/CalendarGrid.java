package com.abc.newcalendar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
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
    private Paint backgroundLinePaint;
    private Paint headerTextPaint;
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
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd EE");
    private String[] headerDates = new String[daysPerView * 3]; // 3 strings per day (months, day, day of week)

    public CalendarGrid(Context context) {
        super(context);
        init(context, null);
    }

    public CalendarGrid(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void forDates(Date startDate) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(startDate);
        initHeaderDates(instance);
        invalidate();
    }

    private void init(Context c, AttributeSet attributeSet) {
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        TypedArray attrs = c.obtainStyledAttributes(attributeSet, R.styleable.CalendarGrid, 0, 0);
        backgroundLinePaint = new Paint();
        backgroundLinePaint.setAntiAlias(true);
        backgroundLinePaint.setStyle(Paint.Style.FILL);
        headerTextPaint = new TextPaint();
        headerTextPaint.setAntiAlias(true);
        headerTextPaint.setTextAlign(Paint.Align.CENTER);
        headerTextPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, displayMetrics));
        gestureDetectorCompat = new GestureDetectorCompat(c, new CalendarGestureDetector());
        try {
            fillSectors();
            strokeWidth = attrs.getDimensionPixelSize(R.styleable.CalendarGrid_lineWidth, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics));
            dayColor = attrs.getColor(R.styleable.CalendarGrid_dayColor, ContextCompat.getColor(c, R.color.dayColor));
            weekendColor = attrs.getColor(R.styleable.CalendarGrid_weekendColor, ContextCompat.getColor(c, R.color.weekendColor));
            marginColor = attrs.getColor(R.styleable.CalendarGrid_marginColor, Color.WHITE);
            headerMargin = attrs.getDimensionPixelOffset(R.styleable.CalendarGrid_headerMargin, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics));
            initialRooms = attrs.getInt(R.styleable.CalendarGrid_initialRooms, 5);
            headerHeight = attrs.getDimensionPixelSize(R.styleable.CalendarGrid_headerHeight, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, displayMetrics));
            backGroundColor = attrs.getColor(R.styleable.CalendarGrid_backgroundColor, Color.GREEN);
            headerTextColor = attrs.getColor(R.styleable.CalendarGrid_headerTextColor, Color.BLACK);
            lineColor = attrs.getColor(R.styleable.CalendarGrid_lineColor, Color.WHITE);
            backgroundLinePaint.setColor(lineColor);
            backgroundLinePaint.setStrokeWidth(strokeWidth);
            headerTextPaint.setColor(headerTextColor);
            Calendar instance = Calendar.getInstance();
            initHeaderDates(instance);
            topSectors = new ArrayList<>(daysPerView);
            daySectors = new ArrayList<>(daysPerView * initialRooms);
        } finally {
            attrs.recycle();
        }
    }

    private void initHeaderDates(Calendar instance) {
        for (int i = 0; i < daysPerView; i++) {
            String date;
            if (i == 0) {
                date = dateFormat.format(instance.getTime());
            } else {
                instance.add(Calendar.DAY_OF_YEAR, 1);
                date = dateFormat.format(instance.getTime());
            }
            String[] split = date.split(" ");
            int splitIndex = 0;
            int inner = i * 3 + 3;
            for (int k = i * 3; k < inner; k++) {
                headerDates[k] = split[splitIndex++];
            }
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
                    for (int vertical = 0; vertical < daysPerView; vertical++) {
                        float topX = vertical * xOffset;
                        float topY = horizontal * yOffset + topOffset;
                        float bottomX = (vertical + 1) * xOffset;
                        float bottomY = (horizontal + 1) * yOffset + topOffset;
                        RectF rectF = new RectF(topX, topY, bottomX, bottomY);
                        daySectors.add(rectF);
                    }
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
        backgroundLinePaint.setStyle(Paint.Style.FILL);
        drawMargin(canvas);
        backgroundLinePaint.setColor(lineColor);
        backgroundLinePaint.setStyle(Paint.Style.STROKE);
        for (RectF daySector : daySectors) {
            canvas.drawRect(daySector, backgroundLinePaint);
        }
        if (shouldHighlight && !highlightRect.isEmpty()) {
            backgroundLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            backgroundLinePaint.setColor(Color.BLACK);
            canvas.drawRect(highlightRect.get(0), backgroundLinePaint);
            highlightRect.clear();
            backgroundLinePaint.setStyle(Paint.Style.FILL);
            backgroundLinePaint.setColor(lineColor);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean consumed = gestureDetectorCompat.onTouchEvent(event);
        if (!consumed) {
            int action = event.getAction();
        }
        return consumed;
    }

    private void drawMargin(Canvas canvas) {
        backgroundLinePaint.setColor(marginColor);
        canvas.drawRect(0, headerHeight, getWidth(), headerHeight + headerMargin, backgroundLinePaint);
    }

    private void drawHeader(Canvas canvas) {
        backgroundLinePaint.setStyle(Paint.Style.STROKE);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        for (int i = 0; i < topSectors.size(); i++) {
            RectF rect = topSectors.get(i);
            canvas.drawRect(rect, backgroundLinePaint);
            Log.d(TAG, "info for view: " + i);
            int inner = i * 3 + 3;
            int position = 0;
            for (int k = i * 3; k < inner; k++) {
                String headerDate = headerDates[k];
                float x = 0;
                float y = 0;
                float textSize = 0;
                Typeface typeface = Typeface.DEFAULT;
                switch (position) {
                    case 0:
                        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, displayMetrics);
                        x = rect.centerX();
                        y = rect.height() / 8;
                        typeface = Typeface.DEFAULT;
                        break;
                    case 1:
                        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 26, displayMetrics);
                        x = rect.centerX();
                        typeface = Typeface.DEFAULT;
                        y = rect.centerY();
                        break;
                    case 2:
                        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, displayMetrics);
                        x = rect.centerX();
                        typeface = Typeface.DEFAULT_BOLD;
                        y = (rect.height() / 8) * 7;
                        break;
                }
                position++;
                headerTextPaint.setTypeface(typeface);
                headerTextPaint.setTextSize(textSize);
                canvas.drawText(headerDate, x, y, headerTextPaint);
                Log.d(TAG, headerDate);
            }
        }
    }

    private class CalendarGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
//            Log.d(TAG, "onDown " + e);
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
