package me.thanel.gitlog

import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.text.TextUtils

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

class AvatarDrawable(userName: String, identifier: Any?) : Drawable() {
    @ColorInt private val COLOR_PALETTE = intArrayOf(
            0xffdb4437.toInt(), 0xffe91e63.toInt(), 0xff9c27b0.toInt(), 0xff673ab7.toInt(),
            0xff3f51b5.toInt(), 0xff4285f4.toInt(), 0xff039be5.toInt(), 0xff0097a7.toInt(),
            0xff009688.toInt(), 0xff0f9d58.toInt(), 0xff689f38.toInt(), 0xffef6c00.toInt(),
            0xffff5722.toInt(), 0xff757575.toInt()
    )

    private val LETTER_TO_TILE_RATIO = 0.67f

    private var mPaint: Paint
    @ColorInt private var mColor: Int
    private val mLetter = CharArray(1)
    private var mState: UserNameState
    private val sRect = Rect()

    init {
        mState = UserNameState(userName, identifier)

        mPaint = Paint()
        mPaint.typeface = Typeface.DEFAULT_BOLD
        mPaint.textAlign = Paint.Align.CENTER
        mPaint.isAntiAlias = true

        val colorIndex: Int
        if (TextUtils.isEmpty(userName)) {
            mLetter[0] = '?'
            if (mState.identifier != null) {
                colorIndex = Math.abs((mState.identifier as Any).hashCode()) % COLOR_PALETTE.size
            } else {
                colorIndex = (Math.random() * COLOR_PALETTE.size).toInt()
            }
        } else {
            mLetter[0] = Character.toUpperCase(userName[0])
            colorIndex = Math.abs(userName.hashCode()) % COLOR_PALETTE.size
        }

        mColor = COLOR_PALETTE[colorIndex]
    }

    override fun getConstantState(): Drawable.ConstantState? {
        return mState
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        if (!isVisible || bounds.isEmpty) {
            return
        }

        val minDimension = Math.min(bounds.width(), bounds.height())
        val centerX = bounds.centerX().toFloat()
        val centerY = bounds.centerY().toFloat()
        val radius = (minDimension / 2).toFloat()

//        mPaint.color = 0xff3f51b5.toInt()
//        canvas.drawRect(centerX - 5, bounds.top.toFloat(), centerX + 5, bounds.bottom.toFloat(), mPaint)
//        canvas.drawCircle(centerX, centerY, radius, mPaint)

        mPaint.color = mColor

        canvas.drawCircle(centerX, centerY, radius /*- 10*/, mPaint)

        mPaint.textSize = LETTER_TO_TILE_RATIO * minDimension
        mPaint.getTextBounds(mLetter, 0, 1, sRect)
        mPaint.color = Color.WHITE

        canvas.drawText(mLetter, 0, 1, centerX,
                bounds.centerY() - sRect.exactCenterY(),
                mPaint)
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return android.graphics.PixelFormat.OPAQUE
    }

    private class UserNameState(
            val userName: String,
            val identifier: Any?
    ) : Drawable.ConstantState() {

        override fun newDrawable() = AvatarDrawable(userName, identifier)

        override fun getChangingConfigurations() = 0
    }
}