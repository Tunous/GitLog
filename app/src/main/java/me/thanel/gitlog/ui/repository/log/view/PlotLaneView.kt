package me.thanel.gitlog.ui.repository.log.view

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import me.thanel.gitlog.ui.repository.log.ColorCommitList
import org.eclipse.jgit.revplot.PlotCommit

class PlotLaneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val renderer = PlotLaneRenderer(context)

    lateinit var commit: PlotCommit<ColorCommitList.ColorLane>

    override fun onDraw(canvas: Canvas) {
        renderer.draw(canvas, commit, paddingStart, paddingEnd)
    }
}
