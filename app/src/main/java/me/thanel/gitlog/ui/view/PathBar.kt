package me.thanel.gitlog.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.HorizontalScrollView
import kotlinx.android.synthetic.main.item_path_bar_entry.view.*
import kotlinx.android.synthetic.main.view_path_bar.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.utils.resolveColor

class PathBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr), View.OnClickListener {
    private var onPathEntryClicked: ((String) -> Unit)? = null

    init {
        View.inflate(context, R.layout.view_path_bar, this)
        setBackgroundColor(context.resolveColor(R.attr.colorPrimary))
        isHorizontalScrollBarEnabled = true
        isFillViewport = true
        setPath(emptyList())
    }

    fun onPathEntryClicked(listener: ((String) -> Unit)? = null) {
        onPathEntryClicked = listener
    }

    fun setPath(path: List<String>) {
        pathEntryContainer.removeAllViews()
        addEntry("/", "")
        for ((index, title) in path.withIndex()) {
            addEntry(title, path.take(index + 1).joinToString("/"))
        }
    }

    override fun onClick(view: View) {
        val path = view.tag as String
        onPathEntryClicked?.invoke(path)
    }

    private fun addEntry(title: String, fullPath: String) {
        val entry = View.inflate(context, R.layout.item_path_bar_entry, null)
        entry.setOnClickListener(this)
        entry.tag = fullPath
        entry.titleView.text = title
        pathEntryContainer.addView(entry)
    }
}
