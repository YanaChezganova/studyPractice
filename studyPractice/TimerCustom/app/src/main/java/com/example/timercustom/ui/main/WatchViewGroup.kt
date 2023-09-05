package com.example.timercustom.ui.main

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.example.timercustom.R
import com.example.timercustom.databinding.ViewGroupWatchBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.properties.Delegates


class WatchViewGroup
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    val viewModel = MainViewModel()
    val binding = ViewGroupWatchBinding.inflate(LayoutInflater.from(context))
    private var isInitialized = false

    private val darkGreenWatchPaint = Paint().apply {
        reset()
        color = resources.getColor(R.color.green_500)
        Paint.Style.FILL
    }
    private val yellowWatchPaint = Paint().apply {
        reset()
        color = resources.getColor(R.color.yellow_300)
        Paint.Style.STROKE
        strokeWidth = 20F
        isAntiAlias = true
    }
    private val fontSize =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18F, resources.displayMetrics)
    private val textPaint = Paint().apply {
        reset()
        textSize = fontSize
        color = resources.getColor(R.color.yellow_300)
    }
    private val handPaint = Paint().apply {
        reset()
        color = resources.getColor(R.color.white)
        style = Paint.Style.STROKE
        strokeWidth = 4F
        isAntiAlias = true
    }
    private var padding = 0
    private var numeralSpacing = 12
    private var radius = 0
    private var heightScreen: Int = 0
    private var widthScreen = 0
    private var handTruncation = 0
    private var hourHandTruncation = 0

    private var callOnDraw = false

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (!isInitialized) {
            height.also { heightScreen = it }
            width.also { widthScreen = it }
            padding = numeralSpacing + 40  // spacing from the circle border
            val minAttr = min(height, width)
            radius = minAttr / 2 - padding
            // for maintaining different heights among the clock-hands
            handTruncation = minAttr / 17
            hourHandTruncation = minAttr / 12
            isInitialized = true
            canvas?.drawColor(resources.getColor(R.color.green_200))
        }
        canvas?.drawCircle(
            (widthScreen / 2).toFloat(),
            (heightScreen / 2).toFloat(), (radius + padding).toFloat(), yellowWatchPaint
        )
        canvas?.drawCircle(
            (widthScreen / 2).toFloat(),
            (heightScreen / 2).toFloat(), (radius + padding - 11).toFloat(), darkGreenWatchPaint
        )

        canvas?.drawCircle(
            (widthScreen / 2).toFloat(),
            (heightScreen / 2).toFloat(), 12F, yellowWatchPaint
        )
        for (i in 1..12) {
            val numberValue = (i).toString()
            textPaint.getTextBounds(numberValue, 0, numberValue.length, Rect())
            val angle = Math.PI / 6 * (i - 3)
            val x = widthScreen / 2 + cos(angle) * radius + Rect().width() - 10
            val y = heightScreen / 2 + sin(angle) * radius - Rect().height() + 10
            canvas?.drawText(i.toString(), x.toFloat(), y.toFloat(), textPaint)
        }

        val hours = viewModel.timerPlaying.hour
        val minutes = viewModel.timerPlaying.minute
        val seconds = viewModel.timerPlaying.second
        println("Draw hour $hours, min $minutes, sec $seconds, onDraw $callOnDraw")
        drawHandLine(
            canvas!!,
            (hours + (minutes / 60).toDouble() / 60.0) * 5.0,
            isHour = true,
            isSecond = false
        ) // draw hours

        drawHandLine(
            canvas!!, minutes.toDouble(),
            isHour = false, isSecond = false
        ) // draw minutes

        drawHandLine(
            canvas!!, seconds.toDouble(),
            isHour = false, isSecond = true
        ) // draw seconds
        // postInvalidateDelayed(1000)
    }

    private fun drawHandLine(canvas: Canvas, moment: Double, isHour: Boolean, isSecond: Boolean) {
        val angle = Math.PI * moment / 30 - Math.PI / 2
        val handRadius =
            if (isHour) (radius - handTruncation - hourHandTruncation) else (radius - handTruncation)
        if (isSecond) handPaint.color = Color.YELLOW else handPaint.color = Color.WHITE
        canvas.drawLine(
            (widthScreen / 2).toFloat(),
            (heightScreen / 2).toFloat(), (widthScreen / 2 + cos(angle) * handRadius).toFloat(),
            (heightScreen / 2 + sin(angle) * handRadius).toFloat(), handPaint
        )
    }

    fun stopTimer() {
        println("stop")
        callOnDraw = false
        val startTime = viewModel.timerState.timeInMillis
        val hour = viewModel.timerPlaying.hour
        val minutes = viewModel.timerPlaying.minute
        val seconds = viewModel.timerPlaying.second
        viewModel.changeTimerState(startTime, hour, minutes, seconds, callOnDraw)
    }

    suspend fun startTimer() {
        callOnDraw = true
        var additionalHour = 0
        var additionalMinute = 0
        var additionalSeconds = 0

        if (!viewModel.timerState.isPlayed) {
            additionalHour = viewModel.timerState.hour
            additionalMinute = viewModel.timerState.minute
            additionalSeconds = viewModel.timerState.second
        }

        val startTime = Calendar.getInstance().timeInMillis
        viewModel.changeTimerState(
            startTime,
            additionalHour,
            additionalMinute,
            additionalMinute,
            callOnDraw
        )
        var counter = 0
        while (viewModel.timerState.isPlayed) {
            val currentProgress =
                (Calendar.getInstance().timeInMillis - viewModel.timerState.timeInMillis) / 1000
            var hour = currentProgress / 3600
            hour = if (hour < 0) 12 + hour else hour
            hour += additionalHour
            hour = if (hour > 12) hour - 12 else hour

            var minutes = (currentProgress - hour * 3600) / 60
            minutes = if (minutes < 0) 60 + minutes else minutes
            minutes += additionalMinute
            if (minutes > 60) {
                minutes -= 60
                hour += 1
            }

            var seconds = currentProgress - hour * 3600 - minutes * 60
            seconds = if (seconds < 0) 60 + seconds else seconds
            seconds += additionalSeconds
            if (seconds > 60) {
                seconds -= 60
                minutes += 1
            }
            if (minutes > 60) {
                minutes -= 60
                hour += 1
            }
            hour = if (hour > 12) hour - 12 else hour
            viewModel.changeTimerPlaying(hour.toInt(), minutes.toInt(), seconds.toInt(), callOnDraw)
            invalidate()
            if (counter > 2) {
                delay(995)
            }
            counter++
        }
    }

    fun resetTimer() {
        callOnDraw = false
        viewModel.changeTimerState(0, 0, 0, 0, callOnDraw)
        viewModel.changeTimerPlaying(0, 0, 0, callOnDraw)
        invalidate()
    }
}
