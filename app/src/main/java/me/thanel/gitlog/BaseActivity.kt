package me.thanel.gitlog

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.view_toolbar.*

abstract class BaseActivity : AppCompatActivity() {

    protected open val title: String? = null

    protected open val subtitle: String? = null

    protected open val canNavigateUp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        setSupportActionBar(toolbar)
        title?.let { supportActionBar!!.title = it }
        subtitle?.let { supportActionBar!!.subtitle = it }
        if (canNavigateUp) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, createFragment())
                    .commit()
        }
    }

    protected abstract fun createFragment(): Fragment
}