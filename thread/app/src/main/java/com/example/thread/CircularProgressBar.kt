package com.example.thread

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var progress: Int = 0
        set(value) {
            field = value.coerceIn(0, 100)
            invalidate()
        }

    var centerText: String? = null
        set(value) {
            field = value
            invalidate()
        }

    var showPercentageInCenter: Boolean = true
        set(value) {
            field = value
            invalidate()
        }

    private val strokeWidthPx = 32f

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        color = Color.parseColor("#E0E0E0")
        strokeCap = Paint.Cap.ROUND
    }

    private val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        color = Color.parseColor("#6750A4")
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1C1B1F")
        textSize = 56f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val oval = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val padding = strokeWidthPx / 2f + 10f
        val w = width.toFloat()
        val h = height.toFloat()

        val size = min(w, h)
        val left = (w - size) / 2f + padding
        val top = (h - size) / 2f + padding
        val right = (w + size) / 2f - padding
        val bottom = (h + size) / 2f - padding

        oval.set(left, top, right, bottom)

        // Draw background circle
        canvas.drawArc(oval, 0f, 360f, false, backgroundPaint)

        // Draw progress arc starting at top (-90 deg)
        val sweepAngle = (progress / 100f) * 360f
        if (sweepAngle > 0) {
            canvas.drawArc(oval, -90f, sweepAngle, false, progressPaint)
        }

        // Draw center text
        val textToDraw = centerText ?: if (showPercentageInCenter) "$progress%" else ""
        if (textToDraw.isNotEmpty()) {
            val xPos = w / 2f
            val yPos = (h / 2f) - ((textPaint.descent() + textPaint.ascent()) / 2f)
            canvas.drawText(textToDraw, xPos, yPos, textPaint)
        }
    }
}
