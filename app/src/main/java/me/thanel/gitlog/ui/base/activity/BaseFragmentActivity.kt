package me.thanel.gitlog.ui.base.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.fragment.BaseFragment

abstract class BaseFragmentActivity : BaseActivity() {
    final override val layoutResId: Int
        get() = R.layout.activity_base

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, createFragment())
                .commit()
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val baseFragment = fragment as? BaseFragment
        val handled = baseFragment?.onBackPressed() ?: false
        if (!handled) {
            super.onBackPressed()
        }
    }

    protected abstract fun createFragment(): Fragment
}
