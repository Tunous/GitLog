package me.thanel.gitlog.base

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.Serializable

abstract class BaseFragment<T : ViewModel> : LifecycleFragment() {
    protected lateinit var viewModel: T

    protected abstract val layoutResId: Int
        @LayoutRes get

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutResId, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = onCreateViewModel()
        observeViewModel(viewModel)
    }

    abstract fun onCreateViewModel(): T

    abstract fun observeViewModel(viewModel: T)

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