package me.thanel.gitlog.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.view.children
import kotlinx.android.synthetic.main.item_path_bar_entry.view.*
import kotlinx.android.synthetic.main.view_path_bar.view.*
import me.thanel.gitlog.R

class PathBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {
    private var onPathEntryClickListener: ((path: String) -> Unit)? = null
    private var onPathEntryLongClickListener: ((view: View, path: String) -> Unit)? = null

    init {
        View.inflate(context, R.layout.view_path_bar, this)
        isFillViewport = true
        setPath("")
    }

    fun setOnPathEntryClickListener(listener: ((path: String) -> Unit)? = null) {
        onPathEntryClickListener = listener
    }

    fun setOnPathEntryLongClickListener(listener: ((view: View, path: String) -> Unit)? = null) {
        onPathEntryLongClickListener = listener
    }

    fun setPath(path: String) {
        pathEntryContainer.removeAllViews()
        addEntry("/", "", path.isBlank())
        if (path.isNotBlank()) {
            val pathSegments = path.split("/")
            var fullPath = ""
            for (title in pathSegments) {
                addSeparator()
                fullPath += title
                addEntry(title, fullPath, false)
                fullPath += "/"
            }
            pathEntryContainer.children.last().isSelected = true
        }
        post {
            fullScroll(ScrollView.FOCUS_RIGHT)
        }
    }

    private fun addEntry(title: String, fullPath: String, isSelected: Boolean) {
        val entry = View.inflate(context, R.layout.item_path_bar_entry, null)
        entry.setOnClickListener {
            val path = it.tag as String
            onPathEntryClickListener?.invoke(path)
        }
        entry.setOnLongClickListener {
            val path = it.tag as String
            onPathEntryLongClickListener?.invoke(it, path)
            true
        }
        entry.tag = fullPath
        entry.titleView.text = title
        entry.titleView.isSelected = isSelected
        pathEntryContainer.addView(entry)
    }

    private fun addSeparator() {
        val separator = View.inflate(context, R.layout.item_path_bar_separator, null)
        pathEntryContainer.addView(separator)
    }
}
