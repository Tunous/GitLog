package me.thanel.gitlog.ui.base.activity

import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View

abstract class BaseActivity : AppCompatActivity() {
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
        // TODO
    }

    fun removeHeaderView(view: View) = Unit//TODO
}
