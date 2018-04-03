package me.thanel.gitlog.ui.base.activity

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_base_pager.*
import kotlinx.android.synthetic.main.view_toolbar_with_tabs.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import java.util.*

abstract class BasePagerActivity : BaseActivity() {
    private lateinit var adapter: Adapter

    final override val layoutResId: Int
        get() = R.layout.activity_base_pager

    protected abstract val pageTitles: Array<CharSequence>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = Adapter()
        fragmentViewPager.adapter = adapter
        tabLayout.setupWithViewPager(fragmentViewPager)
    }

    override fun onBackPressed() {
        val fragment = adapter.getFragment(fragmentViewPager.currentItem)
        val baseFragment = fragment as? BaseFragment<*>
        val handled = baseFragment?.onBackPressed() ?: false
        if (!handled) {
            super.onBackPressed()
        }
    }

    protected abstract fun createFragment(position: Int): Fragment

    private inner class Adapter : FragmentStatePagerAdapter(supportFragmentManager) {
        private val fragments = WeakHashMap<Int, Fragment>()

        override fun getItem(position: Int): Fragment {
            val fragment = createFragment(position)
            fragments[position] = fragment
            return fragment
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            super.destroyItem(container, position, obj)
            fragments.remove(position)
        }

        override fun getCount() = pageTitles.size

        override fun getPageTitle(position: Int) = pageTitles[position]

        fun getFragment(position: Int): Fragment? = fragments[position]
    }
}
