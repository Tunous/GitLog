package me.thanel.gitlog.base

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import me.thanel.gitlog.R

abstract class BaseActivity : AppCompatActivity(), LifecycleRegistryOwner {

    private val lifecycleRegistry by lazy { LifecycleRegistry(this) }

    protected open val title: String? = null

    protected open var subtitle: String? = null
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

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        title?.let { supportActionBar!!.title = it }
        subtitle?.let { supportActionBar!!.subtitle = it }
        if (canNavigateUp) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun getLifecycle() = lifecycleRegistry

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
}