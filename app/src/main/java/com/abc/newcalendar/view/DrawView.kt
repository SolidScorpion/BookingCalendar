package com.abc.newcalendar.view

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * Created by Anton P. on 25.04.2018.
 */
class DrawView : View {
    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).also {
        it.style = Paint.Style.STROKE
        it.color = Color.CYAN
        it.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context.resources.displayMetrics)
    }
    private val text = "Hello!"
    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).also {
        it.color = Color.BLACK
        it.textAlign = Paint.Align.CENTER
        it.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16f, context.resources.displayMetrics)
    }
    private val measureRect = Rect()
    private val rectsF = arrayOf(RectF(), RectF())
    private val leftRects = ArrayList<RectF>(3)
    private val rightRects = ArrayList<RectF>(3)

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet? = null) {
        addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
                removeOnLayoutChangeListener(this)
                val leftRect = rectsF[0]
                val rightRect = rectsF[1]
                leftRect.left = 0f
                leftRect.top = 0f
                leftRect.right = width / 2f
                leftRect.bottom = height.toFloat()
                rightRect.left = width / 2f
                rightRect.top = 0f
                rightRect.right = width.toFloat()
                rightRect.bottom = height.toFloat()
                textPaint.getTextBounds(text, 0, text.length, measureRect)
                rectsF.withIndex().forEach {
                    val part = it.value.height() / 4
                    for (i in 0..2) {
                        val topX = it.value.left
                        val topY = when (i) {
                            0, 1 -> i * part
                            else -> (i + 1) * part
                        }
                        val rightX = it.value.left + it.value.width()
                        val bottomY = when (i) {
                            1, 2 -> (i + 2) * part
                            else -> (i + 1) * part
                        }
                        val element = RectF(topX, topY, rightX, bottomY)
                        if (it.index == 0) {
                            leftRects.add(element)
                        } else {
                            rightRects.add(element)
                        }
                    }
                }
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (rectF in rectsF) {
            canvas.drawRect(rectF, rectPaint)
            drawTextInRect(rectF, canvas)
        }
    }

    private fun drawTextInRect(rectF: RectF, canvas: Canvas) {
        val rectS = if (rectF.left == 0f) leftRects else rightRects
        for (rect in rectS) {
            rectPaint.color = Color.RED
            canvas.drawCircle(rect.centerX(), rect.centerY(), 2f, rectPaint)
            rectPaint.color = Color.CYAN
            canvas.drawText(text, rect.centerX(), rect.centerY(), textPaint)
            canvas.drawRect(rect, rectPaint)
        }
    }
}