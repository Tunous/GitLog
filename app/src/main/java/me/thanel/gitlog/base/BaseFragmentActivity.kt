package me.thanel.gitlog.base

import android.os.Bundle
import android.support.v4.app.Fragment
import me.thanel.gitlog.R

abstract class BaseFragmentActivity : BaseActivity() {

    override val layoutResId: Int
        get() = R.layout.activity_base

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, createFragment())
                    .commit()
        }
    }

    protected abstract fun createFragment(): Fragment

}
