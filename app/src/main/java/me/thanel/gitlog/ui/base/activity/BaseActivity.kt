package me.thanel.gitlog.ui.base.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import me.thanel.gitlog.R

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var appBarLayout: AppBarLayout

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
        appBarLayout = findViewById(R.id.appBarLayout)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (canNavigateUp) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = supportParentActivityIntent
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                } else {
                    finish()
                }
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

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
