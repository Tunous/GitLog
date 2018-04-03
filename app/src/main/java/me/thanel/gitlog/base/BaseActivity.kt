package me.thanel.gitlog.base

import android.arch.lifecycle.LifecycleRegistry
import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import me.thanel.gitlog.R

abstract class BaseActivity : AppCompatActivity() {
    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }
    private val appBarLayout by lazy { findViewById<AppBarLayout>(R.id.appBarLayout) }
    val headerContext: Context get() = appBarLayout.context

    protected var toolbarTitle: CharSequence? = null
        set(value) {
            field = value
            supportActionBar?.title = value
        }

    var toolbarSubtitle: CharSequence? = null
        set(value) {
            field = value
            supportActionBar?.subtitle = value
        }

    protected open val canNavigateUp = true

    protected abstract val layoutResId: Int
        @LayoutRes get

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (canNavigateUp) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun getLifecycle() = lifecycleRegistry

    fun addHeaderView(view: View, addToTop: Boolean = false) {
        val position = if (addToTop) 0 else appBarLayout.childCount
        val layoutParams = AppBarLayout.LayoutParams(
            AppBarLayout.LayoutParams.MATCH_PARENT,
            AppBarLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
        }
        appBarLayout.addView(view, position, layoutParams)
    }

    fun removeHeaderView(view: View) = appBarLayout.removeView(view)
}
