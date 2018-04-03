package me.thanel.gitlog.ui.base.fragment

import android.arch.lifecycle.ViewModel
import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.thanel.gitlog.ui.base.activity.BaseActivity

abstract class BaseFragment<T : ViewModel> : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutResId, container, false)

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
}