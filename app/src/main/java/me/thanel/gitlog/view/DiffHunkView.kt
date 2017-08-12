package me.thanel.gitlog.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatTextView
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.LineBackgroundSpan
import android.text.style.LineHeightSpan
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import me.thanel.gitlog.R

class DiffHunkView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {
    private val diffTextView = AppCompatTextView(context).apply {
        typeface = Typeface.MONOSPACE
        setTextIsSelectable(true)
    }
    init {
        addView(diffTextView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ))
    }
    init {
        isFillViewport = true

        if (isInEditMode) {
            setDiff("+ Added Line\n  Regular line\n- Removed line")
        }
    }

    fun setDiff(diff: String) {
        val lines = diff.trim().split("\n").drop(4)
        val builder = SpannableStringBuilder()

        val bgAdd = ContextCompat.getColor(context, R.color.diffAddBackground)
        val bgRemove = ContextCompat.getColor(context, R.color.diffRemoveBackground)
        val bgMarker = ContextCompat.getColor(context, R.color.diffMarkerBackground)
        val bgRegular = Color.WHITE

        for ((index, line) in lines.withIndex()) {
            val spanStart = builder.length
            builder.append("  ").append(line).append("  ")
            if (index < lines.size - 1) {
                builder.append("\n")
            }

            val bg = when {
                line.startsWith("+") -> bgAdd
                line.startsWith("-") -> bgRemove
                line.startsWith("@@") && line.endsWith("@@") -> bgMarker
                else -> bgRegular
            }

            val span = DiffLineSpan(bg, bg, 32, index == 0, index == lines.size - 1, 0)
            builder.setSpan(span, spanStart, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val padding = context.resources.getDimensionPixelSize(R.dimen.diff_line_padding)
        val lineHeightSpan = DiffLineHeightSpan(padding)
        builder.setSpan(lineHeightSpan, 0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        diffTextView.text = builder
    }

    private class DiffLineHeightSpan(private val padding: Int) : LineHeightSpan {
        override fun chooseHeight(text: CharSequence, start: Int, end: Int, spanStartV: Int, v: Int,
                fm: Paint.FontMetricsInt) {
            val topOffset = padding / 2
            fm.top -= topOffset
            fm.ascent -= topOffset
            fm.bottom += padding
            fm.descent += padding
        }
    }

    private class DiffLineSpan(
            private @ColorInt val backgroundColor: Int,
            private @ColorInt val numberBackgroundColor: Int,
            private val padding: Int,
            private val isFirstLine: Boolean,
            private val isLastLine: Boolean,
            private val lineNumberLength: Int
    ) : LineBackgroundSpan {
        override fun drawBackground(c: Canvas, p: Paint, left: Int, right: Int, top: Int, baseline: Int,
                bottom: Int, text: CharSequence, start: Int, end: Int, lineNumber: Int) {
            val paintColor = p.color
            val width = p.measureText(text, start, start + lineNumberLength)
            val paddingTop = (top - if (isFirstLine) padding else 0).toFloat()
            val paddingBottom = (bottom + if (isLastLine) padding else 0).toFloat()

            p.color = numberBackgroundColor
            c.drawRect(left.toFloat(), paddingTop, left + width, paddingBottom, p)

            p.color = backgroundColor
            c.drawRect(left + width, paddingTop, right.toFloat(), paddingBottom, p)

            p.color = paintColor
        }
    }
}
