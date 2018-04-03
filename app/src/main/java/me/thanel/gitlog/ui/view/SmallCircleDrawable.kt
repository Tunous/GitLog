package me.thanel.gitlog.ui.view

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

class SmallCircleDrawable : Drawable() {
    private val paint = Paint()

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        if (!isVisible || bounds.isEmpty) {
            return
        }

        paint.color = 0xff3f51b5.toInt()
        canvas.drawCircle(bounds.centerX().toFloat(), bounds.centerY().toFloat(), 20f, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity() = PixelFormat.OPAQUE
}
