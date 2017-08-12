package me.thanel.gitlog.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.item_path_bar_entry.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.resolveColor

class PathBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {
    private val list = LinearLayout(context)//View.inflate(context, R.layout.view_path_bar, this) as ViewGroup
    init {
        setBackgroundColor(context.resolveColor(R.attr.colorPrimary))
        list.setPadding(context.resources.getDimensionPixelSize(R.dimen.toolbar_inset), 0, 0, 0)
        addView(list, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    fun setPath(path: String) {
        list.removeAllViews()
        val entries = path.split("/")
        for (entry in entries) {
            val title = if (entry.isEmpty()) "/"
            else entry
            addEntry(title)
        }
    }

    private fun addEntry(title: String) {
        val entry = View.inflate(context, R.layout.item_path_bar_entry, null)
        entry.titleView.text = title
        list.addView(entry)
    }
}