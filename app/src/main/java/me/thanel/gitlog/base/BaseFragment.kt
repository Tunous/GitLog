package me.thanel.gitlog.base

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.Serializable

abstract class BaseFragment<T : ViewModel> : LifecycleFragment() {
    protected lateinit var baseActivity: BaseActivity
    protected lateinit var viewModel: T

    protected abstract val layoutResId: Int
        @LayoutRes get

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        if (context !is BaseActivity) {
            throw IllegalStateException("Parent activity must extend BaseActivity")
        }
        baseActivity = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View = inflater.inflate(layoutResId, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = onCreateViewModel()
        observeViewModel(viewModel)
    }

    fun addHeaderView(view: View) = baseActivity.addHeaderView(view)

    fun removeHeaderView(view: View) = baseActivity.removeHeaderView(view)

    abstract fun onCreateViewModel(): T

    open fun observeViewModel(viewModel: T) = Unit

    open fun onBackPressed(): Boolean = false

    protected fun <T : Parcelable> parcelableArg(name: String) = lazy {
        arguments.getParcelable<T>(name)
    }

    protected fun intArg(name: String) = lazy {
        arguments.getInt(name)
    }

    protected fun stringArg(name: String) = lazy {
        arguments.getString(name)
    }

    protected fun stringArrayArg(name: String) = lazy {
        arguments.getStringArray(name)
    }

    protected fun <T : Serializable> serializableArg(name: String) = lazy {
        @Suppress("UNCHECKED_CAST")
        arguments.getSerializable(name) as T
    }
}