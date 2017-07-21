package me.thanel.gitlog.repositorylist

import me.thanel.gitlog.BaseActivity

class RepositoryListActivity : BaseActivity() {

    override val canNavigateUp = false

    override fun createFragment() = RepositoryListFragment()
}
