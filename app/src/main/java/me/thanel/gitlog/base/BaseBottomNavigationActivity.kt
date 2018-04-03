package me.thanel.gitlog.base

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.MenuRes
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_base_bottom_navigation.*
import me.thanel.gitlog.R

abstract class BaseBottomNavigationActivity
    : BaseActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    final override val layoutResId: Int
        get() = R.layout.activity_base_bottom_navigation

    protected abstract val menuResId: Int
        @MenuRes get

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bottomNavigationView.inflateMenu(menuResId)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            displayFragment(bottomNavigationView.selectedItemId)
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        val baseFragment = fragment as? BaseFragment<*>
        val handled = baseFragment?.onBackPressed() ?: false
        if (!handled) {
            super.onBackPressed()
        }
    }

    final override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (bottomNavigationView.selectedItemId == item.itemId) return false
        displayFragment(item.itemId)
        return true
    }

    protected abstract fun createFragment(@IdRes itemId: Int): Fragment

    private fun displayFragment(@IdRes itemId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, createFragment(itemId))
            .commit()
    }
}
