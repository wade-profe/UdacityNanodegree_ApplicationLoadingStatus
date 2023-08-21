package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var defaultButtonColour = 0
    private var loadingColour = 0
    private var circleColour = 0
    private var loadingProgress = 0f
    private var buttonText: String = resources.getString(R.string.button_name)

    private var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->

        when (new) {
            ButtonState.Loading -> {
                isClickable = false
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.start()
            }

            ButtonState.Completed -> {
                valueAnimator.repeatCount = 0
                valueAnimator.doOnEnd {
                    buttonText = resources.getString(R.string.button_name)
                    loadingProgress = 0f
                    isClickable = true
                    invalidate()
                }
            }
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

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textHeight: Float = textPaint.descent() - textPaint.ascent()
    private val textOffset: Float = textHeight / 2 - textPaint.descent()


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultButtonColour = getColor(R.styleable.LoadingButton_defaultButtonColour, 0)
            loadingColour = getColor(R.styleable.LoadingButton_loadingColour, 0)
            circleColour = getColor(R.styleable.LoadingButton_circleColour, 0)
        }
        valueAnimator = ValueAnimator.ofFloat(0f, 100f)
        valueAnimator.duration = 2000
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
        circlePaint.color = circleColour
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), rectPaint)

        val textX = widthSize * 0.5f
        val textY = heightSize * 0.5f + textOffset

        if(loadingProgress > 0){
            canvas.drawRect(
                0f,
                0f,
                widthSize.toFloat() * loadingProgress / 100,
                heightSize.toFloat(),
                loadingRectPaint
            )

            val textLength = textPaint.measureText(buttonText, 0, buttonText.length - 1)
            val circleLeft = (textX + textLength / 1.8).toFloat()
            val circleTop = textY + textPaint.ascent()
            val circleRight = circleLeft + 75f
            val circleBottom = textY + textPaint.descent()

            canvas.drawArc(
                circleLeft,
                circleTop,
                circleRight,
                circleBottom,
                0f,
                360f * loadingProgress / 100,
                true,
                circlePaint
            )
        }

        canvas.drawText(buttonText, textX, textY, textPaint)

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

    fun changeState(newState: ButtonState) {
        buttonState = newState
    }

}