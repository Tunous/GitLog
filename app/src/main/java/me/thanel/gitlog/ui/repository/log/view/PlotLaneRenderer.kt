package me.thanel.gitlog.ui.repository.log.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import me.thanel.gitlog.ui.repository.log.ColorCommitList
import me.thanel.gitlog.ui.utils.dpToPx
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revplot.AbstractPlotRenderer
import org.eclipse.jgit.revplot.PlotCommit

class PlotLaneRenderer(context: Context) : AbstractPlotRenderer<ColorCommitList.ColorLane, Int>() {
    private lateinit var canvas: Canvas
    private var paddingStart = 0
    private var paddingEnd = 0

    private val paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = context.dpToPx(2f)
    }

    fun draw(
        canvas: Canvas,
        commit: PlotCommit<ColorCommitList.ColorLane>,
        paddingStart: Int,
        paddingEnd: Int
    ) {
        this.canvas = canvas
        this.paddingStart = paddingStart
        this.paddingEnd = paddingEnd
        paintCommit(commit, canvas.height)
    }

    override fun drawBoundaryDot(x: Int, y: Int, w: Int, h: Int) {
        paint.style = Paint.Style.STROKE
        drawCommitDot(x, y, w, h)
        paint.style = Paint.Style.FILL
    }

    override fun drawText(msg: String, x: Int, y: Int) {
    }

    override fun drawCommitDot(x: Int, y: Int, w: Int, h: Int) {
        val cx = (paddingStart + x + (w / 2)).toFloat()
        val cy = (y + (h / 2)).toFloat()
        val radius = (w + 2).toFloat()
        canvas.drawCircle(cx, cy, radius, paint)
    }

    override fun drawLabel(x: Int, y: Int, ref: Ref?) = 0

    override fun drawLine(color: Int, x1: Int, y1: Int, x2: Int, y2: Int, width: Int) {
        paint.color = color
        val startX = (paddingStart + x1).toFloat()
        val endX = (paddingStart + x2).toFloat()
        canvas.drawLine(startX, y1.toFloat(), endX, y2.toFloat(), paint)
    }

    override fun laneColor(myLane: ColorCommitList.ColorLane) = myLane.color
}

