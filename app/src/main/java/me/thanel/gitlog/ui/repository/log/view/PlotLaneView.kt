package me.thanel.gitlog.ui.repository.log.view

import android.content.Context
import android.graphics.*
import android.graphics.Shader.TileMode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.annotation.DrawableRes
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.utils.dpToPx
import org.eclipse.jgit.revplot.PlotLane

/**
 * Circular drawable code is based on https://github.com/hdodenhof/CircleImageView.
 * // TODO: Include license in about screen
 */
class PlotLaneView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val childLanes = mutableListOf<Int>()
    private val parentLanes = mutableListOf<Int>()
    private val passingLanes = mutableSetOf<Int>()
    private var highestLane = 0

    private val circleRadius: Float
    private val drawableSize: Float
    private val drawableBorderSize: Float
    private val laneSpacing: Float

    private var bitmap: Bitmap? = null
    private var bitmapShader: BitmapShader? = null
    private val shaderMatrix = Matrix()
    private val bitmapPaint = Paint()
    private val drawableRect = RectF()
    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }

    var mainLane = 0
        set(value) {
            field = value
            updateHighestLane(value)
        }

    var maxLanes = 0

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.PlotLaneView)
        drawableSize = a.getDimension(R.styleable.PlotLaneView_drawableSize, context.dpToPx(24f))
        drawableBorderSize = a.getDimension(
            R.styleable.PlotLaneView_drawableBorderSize,
            context.dpToPx(2f)
        )
        paint.strokeWidth = a.getDimension(R.styleable.PlotLaneView_lineWidth, context.dpToPx(3f))
        laneSpacing = a.getDimension(R.styleable.PlotLaneView_laneSpacing, context.dpToPx(8f))
        maxLanes = a.getInteger(R.styleable.PlotLaneView_maxLanes, 0)
        a.recycle()

        circleRadius = drawableSize / 2 + drawableBorderSize

        if (isInEditMode) {
            passingLanes.add(0)
        }
    }

    fun addChildLane(lane: Int) {
        childLanes.add(0, lane)
        updateHighestLane(lane)
        invalidate()
    }

    fun addParentLane(lane: Int) {
        parentLanes.add(0, lane)
        updateHighestLane(lane)
    }

    fun setPassing(newPassing: List<PlotLane>) {
        passingLanes.clear()
        passingLanes.addAll(newPassing.map { it.position })

        val highestPassingLane = passingLanes.max() ?: 0
        updateHighestLane(highestPassingLane)
    }

    fun clearLanes() {
        childLanes.clear()
        parentLanes.clear()
        passingLanes.clear()
        highestLane = mainLane
    }

    fun calculateMinimumGraphWidth(lane: Int) =
        (getLaneX(lane) + circleRadius + paddingRight).toInt()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minGraphWidth = calculateMinimumGraphWidth(highestLane)
        val minWidth = Math.max(suggestedMinimumWidth, minGraphWidth)
        val width = View.resolveSizeAndState(minWidth, widthMeasureSpec, 0)
        setMeasuredDimension(width, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        val mainLaneX = getLaneX(mainLane)
        val centerY = (height / 2).toFloat()

        for (lane in childLanes) {
            val targetLane = getTargetLane(lane)
            if (!canDrawConnections(targetLane)) continue

            val targetLaneX = getLaneX(targetLane)
            paint.color =
                    getColor(targetLane)
            canvas.drawLine(mainLaneX, centerY, targetLaneX, 0f, paint)
        }

        var isMerge = false

        for (lane in parentLanes) {
            isMerge = isMerge || !passingLanes.contains(lane) && lane != mainLane

            val targetLane = getTargetLane(lane)
            if (!canDrawConnections(targetLane)) continue

            val targetLaneX = getLaneX(targetLane)
            paint.color =
                    getColor(targetLane)
            canvas.drawLine(mainLaneX, centerY, targetLaneX, height.toFloat(), paint)
        }

        for (lane in passingLanes) {
            if (!canDrawConnections(lane)) continue

            val targetLaneX = getLaneX(lane)
            paint.color = getColor(lane)
            canvas.drawLine(targetLaneX, 0f, targetLaneX, height.toFloat(), paint)
        }

        val radius = if (isMerge) circleRadius / 2 else circleRadius

        paint.color = getColor(mainLane)
        canvas.drawCircle(mainLaneX, centerY, radius, paint)

        if (!isMerge && bitmap != null) {
            val saveCount = canvas.save()
            val drawableCenterX = drawableRect.centerX()
            val drawableCenterY = drawableRect.centerY()
            canvas.translate(mainLaneX - drawableCenterX, centerY - drawableCenterY)
            canvas.drawCircle(drawableCenterX, drawableCenterY, drawableSize / 2, bitmapPaint)
            canvas.restoreToCount(saveCount)
        }
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initializeBitmap()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setup()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setup()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        setup()
    }

    private fun initializeBitmap() {
        bitmap = getBitmapFromDrawable(drawable)
        setup()
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        if (drawable is BitmapDrawable) return drawable.bitmap

        return try {
            val bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(
                    COLORDRAWABLE_DIMENSION,
                    COLORDRAWABLE_DIMENSION,
                    BITMAP_CONFIG
                )
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth, drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun setup() {
        if (width == 0 && height == 0) return
        if (bitmap == null) {
            invalidate()
            return
        }

        bitmapShader = BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP)

        bitmapPaint.isAntiAlias = true
        bitmapPaint.shader = bitmapShader

        drawableRect.set(0f, 0f, drawableSize, drawableSize)

        updateShaderMatrix(bitmap!!)
        invalidate()
    }

    private fun updateShaderMatrix(bitmap: Bitmap) {
        val scale: Float
        var dx = 0f
        var dy = 0f

        shaderMatrix.set(null)

        if (bitmap.width * drawableRect.height() > bitmap.height * drawableRect.width()) {
            scale = drawableRect.height() / bitmap.height.toFloat()
            dx = (drawableRect.width() - bitmap.width * scale) * 0.5f
        } else {
            scale = drawableRect.width() / bitmap.width.toFloat()
            dy = (drawableRect.height() - bitmap.height * scale) * 0.5f
        }

        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(
            ((dx + 0.5f).toInt() + drawableRect.left),
            ((dy + 0.5f).toInt() + drawableRect.top)
        )

        bitmapShader!!.setLocalMatrix(shaderMatrix)
    }

    private fun updateHighestLane(newLane: Int) {
        highestLane = Math.max(highestLane, newLane)
    }

    private fun getLaneX(lanePosition: Int): Float {
        val position = if (maxLanes > 0) Math.min(lanePosition, maxLanes - 1) else lanePosition
        return (position + 1) * circleRadius + (position * laneSpacing) + paddingLeft
    }

    private fun canDrawConnections(lanePosition: Int) =
        maxLanes <= 0 || lanePosition < maxLanes - 1

    private fun getTargetLane(lane: Int) = if (passingLanes.contains(lane)) mainLane else lane

    companion object {
        private const val COLORDRAWABLE_DIMENSION = 2
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private val COLORS = intArrayOf(
            0xff0099cc.toInt(),
            0xff9933cc.toInt(),
            0xff669900.toInt(),
            0xffff8800.toInt(),
            0xffcc0000.toInt()
        )

        private fun getColor(position: Int) = COLORS[position % COLORS.size]
    }
}