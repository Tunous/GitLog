package me.thanel.gitlog.repository.log

import android.content.Context
import android.graphics.Canvas
import android.support.v4.math.MathUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.HorizontalScrollView

/**
 * HorizontalScrollView with added zooming feature, which allows to scale children until their
 * width fits the whole view.
 */
class ZoomHorizontalScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {
    private val scaleDetector = ScaleGestureDetector(context, ScaleListener())
    private var scaleFactor = 1f

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (getScrollRange() == 0) return false
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (getScrollRange() == 0) return false
        return super.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(ev)
        if (scaleDetector.isInProgress) {
            ev.action = MotionEvent.ACTION_CANCEL
            super.dispatchTouchEvent(ev)
            return false
        }
        ev.setLocation(ev.x * (1 / scaleFactor), ev.y * (1 / scaleFactor))
        return super.dispatchTouchEvent(ev)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        canvas.scale(scaleFactor, scaleFactor)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(saveCount)
        invalidate()
    }

    override fun overScrollBy(
        deltaX: Int, deltaY: Int, scrollX: Int, scrollY: Int,
        scrollRangeX: Int, scrollRangeY: Int, maxOverScrollX: Int, maxOverScrollY: Int,
        isTouchEvent: Boolean
    ): Boolean {
        val maxScrollRange = getScrollRange()
        val scrollRange = Math.min(scrollRangeX, maxScrollRange)
        return super.overScrollBy(
            deltaX, deltaY, scrollX, scrollY, scrollRange, scrollRangeY,
            maxOverScrollX, maxOverScrollY, isTouchEvent
        )
    }

    private fun getScrollRange(): Int {
        if (childCount > 0) {
            val child = getChildAt(0)
            val childWidth = (child.width * scaleFactor).toInt()
            return Math.max(0, childWidth - (width - paddingLeft - paddingRight))
        }
        return 0
    }

    override fun computeHorizontalScrollRange() =
        (super.computeHorizontalScrollRange() * scaleFactor).toInt()

    override fun computeHorizontalScrollOffset() =
        (super.computeHorizontalScrollOffset() * scaleFactor).toInt()

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val child: View? = getChildAt(0)
            val minScale = if (child != null) width / child.width.toFloat() else 1f

            scaleFactor *= detector.scaleFactor
            scaleFactor = MathUtils.clamp(scaleFactor, minScale, 1f)

            child?.layoutParams = child?.layoutParams?.apply {
                height = (this@ZoomHorizontalScrollView.height * (1 / scaleFactor)).toInt()
            }

            scrollTo(Math.min(scrollX, getScrollRange()), 0)
            awakenScrollBars()

            invalidate()
            return true
        }
    }
}
