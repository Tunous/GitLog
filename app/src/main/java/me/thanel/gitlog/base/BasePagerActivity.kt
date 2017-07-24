package me.thanel.gitlog.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_base_pager.*
import kotlinx.android.synthetic.main.view_toolbar_with_tabs.*
import me.thanel.gitlog.R

abstract class BasePagerActivity : BaseActivity() {

    override val layoutResId: Int
        get() = R.layout.activity_base_pager

    protected abstract val pageTitles: Array<CharSequence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentViewPager.adapter = object : FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int) = createFragment(position)

            override fun getCount() = pageTitles.size

            override fun getPageTitle(position: Int) = pageTitles[position]
        }
        tabLayout.setupWithViewPager(fragmentViewPager)
    }

    protected abstract fun createFragment(position: Int): Fragment

}