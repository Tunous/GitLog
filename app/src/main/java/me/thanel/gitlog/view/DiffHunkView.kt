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
import android.util.TypedValue
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
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
    }

    var displayLineNumbers: Boolean = true

    init {
        addView(diffTextView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        isFillViewport = true

        if (isInEditMode) {
            setDiff("@@ -6,7 +6,7 @@\n+ Added Line\n  Regular line\n- Removed line")
        }
    }

    fun setDiff(diff: String) {
        val lines = diff.trim().split("\n").dropWhile { !it.startsWith("@@") }
        val builder = SpannableStringBuilder()

        val bgAdd = ContextCompat.getColor(context, R.color.diffAddBackground)
        val bgRemove = ContextCompat.getColor(context, R.color.diffRemoveBackground)
        val bgMarker = ContextCompat.getColor(context, R.color.diffMarkerBackground)
        val bgLineAdd = ContextCompat.getColor(context, R.color.diffLineAddBackground)
        val bgLineRemove = ContextCompat.getColor(context, R.color.diffLineRemoveBackground)
        val bgLineMarker = ContextCompat.getColor(context, R.color.diffLineMarkerBackground)
        val bgRegular = Color.WHITE

        var fromLineNumber = 0
        var toLineNumber = 0
        var numberLength = 1
        for ((index, line) in lines.withIndex()) {
            val spanStart = builder.length

            if (displayLineNumbers) {
                if (line.startsWith("@@")) {
                    val regex = Regex("""^@@ -(\d+),\d+ \+(\d+),\d+ @@.*""")
                    val result = regex.find(line)
                    if (result != null) {
                        fromLineNumber = result.groupValues[1].toInt() - 1
                        toLineNumber = result.groupValues[2].toInt() - 1

                        val maxLineNumber = Math.max(fromLineNumber, toLineNumber)
                        numberLength = (maxLineNumber + lines.size).toString().length
                    }
                } else if (line.startsWith("+")) {
                    toLineNumber += 1
                } else if (line.startsWith("-")) {
                    fromLineNumber += 1
                } else {
                    toLineNumber += 1
                    fromLineNumber += 1
                }

                builder.appendLineNumbers(line, fromLineNumber, toLineNumber, numberLength)
            }

            val lineNumberLength = builder.length - spanStart

            builder.append("  ").append(line).append("  ")
            if (index < lines.size - 1) {
                builder.append("\n")
            }

            val bg = when {
                line.startsWith("+") -> bgAdd
                line.startsWith("-") -> bgRemove
                line.startsWith("@@") -> bgMarker
                else -> bgRegular
            }
            val lineBg = when {
                line.startsWith("+") -> bgLineAdd
                line.startsWith("-") -> bgLineRemove
                line.startsWith("@@") -> bgLineMarker
                else -> bgRegular
            }

            val span = DiffLineSpan(bg, lineBg, 32, index == 0, index == lines.size - 1, lineNumberLength)
            builder.setSpan(span, spanStart, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val padding = context.resources.getDimensionPixelSize(R.dimen.diff_line_padding)
        val lineHeightSpan = DiffLineHeightSpan(padding)
        builder.setSpan(lineHeightSpan, 0, builder.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        diffTextView.text = builder
    }

    private fun SpannableStringBuilder.appendLineNumbers(line: String, fromLineNumber: Int,
            toLineNumber: Int, numberLength: Int) {
        val isMarker = line.startsWith("@@")
        val isAdded = line.startsWith("+")
        val isRemoved = line.startsWith("-")
        append("  ")
        if (!isMarker && !isAdded) {
            appendSingleNumber(fromLineNumber, numberLength)
        } else {
            for (i in 0 until numberLength) {
                append(" ")
            }
        }
        append("   ")
        if (!isMarker && !isRemoved) {
            appendSingleNumber(toLineNumber, numberLength)
        } else {
            for (i in 0 until numberLength) {
                append(" ")
            }
        }
        append(" ")
    }

    private fun SpannableStringBuilder.appendSingleNumber(number: Int, numberLength: Int) {
        val numberText = number.toString()
        for (i in numberText.length until numberLength) {
            append(" ")
        }
        append(numberText)
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
