package me.thanel.gitlog.repository.log

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.ImageView
import me.thanel.gitlog.utils.dpToPx
import org.eclipse.jgit.revplot.PlotLane

class PlotLaneView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    private val childLanes = mutableListOf<Int>()
    private val parentLanes = mutableListOf<Int>()
    private val passingLanes = mutableSetOf<Int>()
    private val strokeWidth = context.dpToPx(3f)
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = this@PlotLaneView.strokeWidth
    }

    var mainLane = 0

    override fun onDraw(canvas: Canvas) {
        val mainLaneX = getLaneX(mainLane)
        val centerY = (height / 2).toFloat()

        for (lanePosition in childLanes) {
            val targetLanePosition = if (passingLanes.contains(lanePosition)) mainLane else lanePosition
            val laneX = getLaneX(targetLanePosition)
            paint.color = getColor(targetLanePosition)
            canvas.drawLine(mainLaneX, centerY, laneX, 0f, paint)
        }

        for (lanePosition in parentLanes) {
            val targetLanePosition = if (passingLanes.contains(lanePosition)) mainLane else lanePosition
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
        canvas.drawCircle(mainLaneX, centerY, strokeWidth, paint)
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

    private fun getLaneX(lanePosition: Int) = (lanePosition + 1) * strokeWidth * 2

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