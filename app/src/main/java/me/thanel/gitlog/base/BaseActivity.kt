package me.thanel.gitlog.base

import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import me.thanel.gitlog.R

abstract class BaseActivity : AppCompatActivity() {

    protected open val title: String? = null

    protected open val subtitle: String? = null

    protected open val canNavigateUp = true

    protected abstract val layoutResId: Int
        @LayoutRes get

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        title?.let { supportActionBar!!.title = it }
        subtitle?.let { supportActionBar!!.subtitle = it }
        if (canNavigateUp) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    protected fun <T : Parcelable> parcelableExtra(name: String) = lazy {
        intent.getParcelableExtra<T>(name)
    }

}