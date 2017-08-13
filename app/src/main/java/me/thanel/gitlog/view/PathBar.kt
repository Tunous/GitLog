package me.thanel.gitlog.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.HorizontalScrollView
import android.widget.Toast
import kotlinx.android.synthetic.main.item_path_bar_entry.view.*
import kotlinx.android.synthetic.main.view_path_bar.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.resolveColor

class PathBar @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr), View.OnClickListener {
    private val pathEntries = mutableListOf<String>()

    init {
        View.inflate(context, R.layout.view_path_bar, this)
        setBackgroundColor(context.resolveColor(R.attr.colorPrimary))
        isHorizontalScrollBarEnabled = true
        isFillViewport = true
        setPath("/")
    }

    fun setPath(path: String) {
        pathEntryContainer.removeAllViews()
        pathEntries.clear()
        pathEntries.addAll(path.split("/"))
        for ((index, entry) in pathEntries.withIndex()) {
            val title = if (entry.isEmpty()) "/" else entry
            val fullPath = pathEntries.take(index + 1).joinToString("/")
            addEntry(title, if (fullPath.isEmpty()) "/" else fullPath)
        }
    }

    override fun onClick(view: View) {
        val path = view.tag as String
        Toast.makeText(context, "Should navigate to: $path", Toast.LENGTH_SHORT).show()
    }

    private fun addEntry(title: String, fullPath: String) {
        val entry = View.inflate(context, R.layout.item_path_bar_entry, null)
        entry.setOnClickListener(this)
        entry.tag = fullPath
        entry.titleView.text = title
        pathEntryContainer.addView(entry)
    }
}