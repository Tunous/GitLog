package me.thanel.gitlog.base

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import me.thanel.gitlog.R
import java.io.Serializable

abstract class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {
    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }
    private val appBarLayout by lazy { findViewById<AppBarLayout>(R.id.appBarLayout) }

    protected var title: String? = null
        set(value) {
            field = value
            supportActionBar?.title = value
        }

    var subtitle: String? = null
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

    fun addHeaderView(view: View) =
            appBarLayout.addView(view, appBarLayout.childCount, AppBarLayout.LayoutParams(
                    AppBarLayout.LayoutParams.MATCH_PARENT,
                    AppBarLayout.LayoutParams.WRAP_CONTENT
            ))

    fun removeHeaderView(view: View) = appBarLayout.removeView(view)

    protected fun <T : Parcelable> parcelableExtra(name: String) = lazy {
        intent.getParcelableExtra<T>(name)
    }

    protected fun intExtra(name: String) = lazy {
        intent.getIntExtra(name, 0)
    }

    protected fun stringExtra(name: String) = lazy {
        intent.getStringExtra(name)
    }

    protected fun stringArrayExtra(name: String) = lazy {
        intent.getStringArrayExtra(name)
    }

    protected fun <T : Serializable> serializableExtra(name: String) = lazy {
        @Suppress("UNCHECKED_CAST")
        intent.getSerializableExtra(name) as T
    }
}