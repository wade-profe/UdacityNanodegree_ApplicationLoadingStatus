package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var buttonText: String = resources.getString(R.string.button_name)

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when (new) {
            ButtonState.Loading -> {
                Log.d("WADE", "button state set to loading")
                buttonText = resources.getString(R.string.button_loading)
                invalidate()
            }

            ButtonState.Completed -> {
                Log.d("WADE", "button state set to completed")
                buttonText = resources.getString(R.string.button_name)
                invalidate()
            }

            else -> {}
        }

    }


    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = context.getColor(R.color.buttonUnclicked)
    }

    private val textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        color = Color.WHITE
    }

    val textHeight: Float = textPaint.descent() - textPaint.ascent()
    val textOffset: Float = textHeight / 2 - textPaint.descent()


    init {
        isClickable = true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), rectPaint)
        canvas.drawText(buttonText, widthSize * 0.5f, heightSize * 0.5f + textOffset, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun changeState(newState: ButtonState){
        buttonState = newState
    }

}