package me.thanel.gitlog.base

import android.arch.lifecycle.LifecycleFragment
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : LifecycleFragment() {

    protected abstract val layoutResId: Int
        @LayoutRes get

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutResId, container, false)
    }

    protected fun <T : Parcelable> parcelableArg(name: String) = lazy {
        arguments.getParcelable<T>(name)
    }

    protected fun intArg(name: String) = lazy {
        arguments.getInt(name)
    }

    protected fun stringArg(name: String) = lazy {
        arguments.getString(name)
    }

}