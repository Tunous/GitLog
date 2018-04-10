package me.thanel.gitlog.ui.repository.filelist.search

import activitystarter.Arg
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.marcinmoskala.activitystarter.argExtra
import kotlinx.android.synthetic.main.view_toolbar.*
import kotlinx.android.synthetic.main.view_toolbar_search.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.activity.BaseFragmentActivity
import me.thanel.gitlog.ui.repository.filelist.GitFileListActivityStarter

class SearchActivity : BaseFragmentActivity() {
    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val refName: String by argExtra()

    override fun createFragment(): SearchFragment =
        SearchFragmentStarter.newInstance(repositoryId, refName)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        toolbarItemStub.layoutResource = R.layout.view_toolbar_search
        toolbarItemStub.inflate().apply {
            toolbarSearch.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) = Unit

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) = Unit

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
                    (getFragment() as SearchListener).onTextChanged(s)
            })
        }
    }

    override fun getSupportParentActivityIntent(): Intent =
        GitFileListActivityStarter.getIntent(this, repositoryId, refName)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    interface SearchListener {
        fun onTextChanged(text: CharSequence?)
    }
}
