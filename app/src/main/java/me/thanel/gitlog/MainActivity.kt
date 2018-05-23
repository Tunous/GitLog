package me.thanel.gitlog

import androidx.navigation.findNavController
import me.thanel.gitlog.ui.base.activity.BaseActivity

class MainActivity : BaseActivity() {

    override val layoutResId: Int
        get() = R.layout.activity_main

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp()
}
