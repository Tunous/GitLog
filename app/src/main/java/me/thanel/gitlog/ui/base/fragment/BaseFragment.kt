package me.thanel.gitlog.ui.base.fragment

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.thanel.gitlog.ui.base.activity.BaseActivity

abstract class BaseFragment : Fragment() {
    protected lateinit var baseActivity: BaseActivity

    protected abstract val layoutResId: Int
        @LayoutRes get

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        baseActivity = context as? BaseActivity ?:
                throw IllegalStateException("Parent activity must extend BaseActivity")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(layoutResId, container, false)

    fun addHeaderView(view: View) = baseActivity.addHeaderView(view)

    fun removeHeaderView(view: View) = baseActivity.removeHeaderView(view)

    open fun onBackPressed(): Boolean = false
}
