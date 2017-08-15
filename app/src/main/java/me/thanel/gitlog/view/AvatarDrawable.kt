package me.thanel.gitlog.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.text.TextUtils

class AvatarDrawable(userName: String, identifier: Any?) : Drawable() {
    @ColorInt private val COLOR_PALETTE = intArrayOf(
            0xffdb4437.toInt(), 0xffe91e63.toInt(), 0xff9c27b0.toInt(), 0xff673ab7.toInt(),
            0xff3f51b5.toInt(), 0xff4285f4.toInt(), 0xff039be5.toInt(), 0xff0097a7.toInt(),
            0xff009688.toInt(), 0xff0f9d58.toInt(), 0xff689f38.toInt(), 0xffef6c00.toInt(),
            0xffff5722.toInt(), 0xff757575.toInt()
    )

    private val LETTER_TO_TILE_RATIO = 0.67f

    private var paint = Paint().apply {
        typeface = Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    @ColorInt private var color: Int
    private val letter = CharArray(1)
    private var state = UserNameState(userName, identifier)
    private val rect = Rect()
    private val rectF = RectF()

    init {
        val colorIndex: Int
        if (TextUtils.isEmpty(userName)) {
            letter[0] = '?'
            colorIndex = if (state.identifier != null) {
                Math.abs((state.identifier as Any).hashCode()) % COLOR_PALETTE.size
            } else {
                (Math.random() * COLOR_PALETTE.size).toInt()
            }
        } else {
            letter[0] = Character.toUpperCase(userName[0])
            colorIndex = Math.abs(userName.hashCode()) % COLOR_PALETTE.size
        }

        color = COLOR_PALETTE[colorIndex]
    }

    override fun getConstantState(): Drawable.ConstantState? = state

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        if (!isVisible || bounds.isEmpty) {
            return
        }

        val minDimension = Math.min(bounds.width(), bounds.height())
        val centerX = bounds.centerX().toFloat()

        // Draw rounded rectangle
        paint.color = color
        rectF.set(bounds)
        canvas.drawRoundRect(rectF, 10f, 10f, paint)

        // Draw name letter
        paint.textSize = LETTER_TO_TILE_RATIO * minDimension
        paint.getTextBounds(letter, 0, 1, rect)
        paint.color = Color.WHITE
        canvas.drawText(letter, 0, 1, centerX, bounds.centerY() - rect.exactCenterY(), paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        paint.colorFilter = cf
    }

    override fun getOpacity() = PixelFormat.OPAQUE

    private class UserNameState(
            val userName: String,
            val identifier: Any?
    ) : Drawable.ConstantState() {
        override fun newDrawable() = AvatarDrawable(userName, identifier)

        override fun getChangingConfigurations() = 0
    }
}