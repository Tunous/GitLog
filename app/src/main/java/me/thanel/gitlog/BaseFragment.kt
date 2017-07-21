package me.thanel.gitlog

import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : Fragment() {

    protected abstract val layoutResId: Int
        @LayoutRes get

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        return inflater.inflate(layoutResId, container, false)
    }

    protected fun <T : Parcelable> parcelableArg(name: String) = lazy {
        arguments.getParcelable<T>(name)
    }

}