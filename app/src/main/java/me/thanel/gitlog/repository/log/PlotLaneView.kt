package me.thanel.gitlog.repository.log

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.dpToPx
import org.eclipse.jgit.revplot.PlotLane

class PlotLaneView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val childLanes = mutableListOf<Int>()
    private val parentLanes = mutableListOf<Int>()
    private val passingLanes = mutableSetOf<Int>()

    private val circleRadius: Float
    private val drawableBorderSize: Float
    private val drawableSize: Int
    private val laneSpacing: Float

    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

    var mainLane = 0

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PlotLaneView)
        circleRadius = a.getDimension(R.styleable.PlotLaneView_circleRadius, context.dpToPx(12f))
        drawableBorderSize = a.getDimension(R.styleable.PlotLaneView_drawableBorderSize,
                context.dpToPx(3f))
        paint.strokeWidth = a.getDimension(R.styleable.PlotLaneView_lineWidth, context.dpToPx(3f))
        laneSpacing = a.getDimension(R.styleable.PlotLaneView_laneSpacing, context.dpToPx(8f))
        a.recycle()

        drawableSize = ((circleRadius - drawableBorderSize) * 2).toInt()

        if (isInEditMode) {
            passingLanes.add(0)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxLane = (childLanes + parentLanes + passingLanes).max() ?: 0
        val lane = Math.max(mainLane, maxLane) + 1
        val minWidth = circleRadius * lane + circleRadius + (laneSpacing * (lane - 1))
        val width = View.resolveSizeAndState(minWidth.toInt(), widthMeasureSpec, 0)

        setMeasuredDimension(width, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        val mainLaneX = getLaneX(mainLane)
        val centerY = (height / 2).toFloat()

        for (lanePosition in childLanes) {
            val targetLanePosition = if (passingLanes.contains(lanePosition)) mainLane
            else lanePosition
            val laneX = getLaneX(targetLanePosition)
            paint.color = getColor(targetLanePosition)
            canvas.drawLine(mainLaneX, centerY, laneX, 0f, paint)
        }

        for (lanePosition in parentLanes) {
            val targetLanePosition = if (passingLanes.contains(lanePosition)) mainLane
            else lanePosition
            val laneX = getLaneX(targetLanePosition)
            paint.color = getColor(targetLanePosition)
            canvas.drawLine(mainLaneX, centerY, laneX, height.toFloat(), paint)
        }

        for (lanePosition in passingLanes) {
            val laneX = getLaneX(lanePosition)
            paint.color = getColor(lanePosition)
            canvas.drawLine(laneX, 0f, laneX, height.toFloat(), paint)
        }

        paint.color = getColor(mainLane)
        canvas.drawCircle(mainLaneX, centerY, circleRadius, paint)

        if (drawable != null) {
            val saveCount = canvas.save()
            canvas.translate(mainLaneX - circleRadius + drawableBorderSize,
                    centerY - circleRadius + drawableBorderSize)
            drawable.setBounds(0, 0, drawableSize, drawableSize)
            drawable.draw(canvas)
            canvas.restoreToCount(saveCount)
        }
    }

    fun addChildLane(lane: Int) {
        childLanes.add(0, lane)
    }

    fun addParentLane(lane: Int) {
        parentLanes.add(0, lane)
    }

    fun clearLines() {
        childLanes.clear()
        parentLanes.clear()
    }

    fun setPassing(newPassing: List<PlotLane>) {
        passingLanes.clear()
        passingLanes.addAll(newPassing.map { it.position })
    }

    private fun getLaneX(lanePosition: Int) = (lanePosition + 1) * circleRadius +
            (lanePosition * laneSpacing)

    companion object {
        private val COLORS = intArrayOf(
                0xff0099cc.toInt(),
                0xff9933cc.toInt(),
                0xff669900.toInt(),
                0xffff8800.toInt(),
                0xffcc0000.toInt())

        private fun getColor(position: Int) = COLORS[position % COLORS.size]
    }
}