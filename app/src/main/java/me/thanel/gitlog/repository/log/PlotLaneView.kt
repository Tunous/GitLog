package me.thanel.gitlog.repository.log

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
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
    private val radius = context.dpToPx(12f)
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = context.dpToPx(3f)
    }
    private val oneDp = context.dpToPx(1f)
    private val drawableSize = (radius * 2 - oneDp * 6).toInt()

    var mainLane = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxLane = (childLanes + parentLanes + passingLanes).max() ?: 0
        val lane = Math.max(mainLane, maxLane) + 1
        val minWidth = radius * lane + radius
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
        canvas.drawCircle(mainLaneX, centerY, radius, paint)

        if (drawable != null) {
            val saveCount = canvas.save()
            canvas.translate(mainLaneX - radius + oneDp * 3, centerY - radius + oneDp * 3)
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

    private fun getLaneX(lanePosition: Int) = (lanePosition + 1) * radius

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