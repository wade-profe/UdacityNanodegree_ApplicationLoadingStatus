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
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var defaultButtonColour = 0
    private var loadingColour = 0
    private var loadingProgress = 0f
    private var buttonText: String = resources.getString(R.string.button_name)

    private var valueAnimator: ValueAnimator

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
    }

    private val textPaint = TextPaint(TextPaint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        color = Color.WHITE
    }

    private val loadingRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    val textHeight: Float = textPaint.descent() - textPaint.ascent()
    val textOffset: Float = textHeight / 2 - textPaint.descent()


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            defaultButtonColour = getColor(R.styleable.LoadingButton_defaultButtonColour, 0)
            loadingColour = getColor(R.styleable.LoadingButton_loadingColour, 0)
        }
        valueAnimator = ValueAnimator.ofFloat(0f,100f)
        valueAnimator.duration = 2000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.addUpdateListener {
            loadingProgress = valueAnimator.animatedValue as Float
            invalidate()
        }

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        rectPaint.color = defaultButtonColour
        loadingRectPaint.color = loadingColour
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), rectPaint)
        canvas.drawText(buttonText, widthSize * 0.5f, heightSize * 0.5f + textOffset, textPaint)
        canvas.drawRect(0f, 0f, widthSize.toFloat()*loadingProgress/100, heightSize.toFloat(), loadingRectPaint)
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