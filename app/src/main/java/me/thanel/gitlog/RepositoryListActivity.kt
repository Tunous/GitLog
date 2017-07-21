package me.thanel.gitlog

class RepositoryListActivity : BaseActivity() {

    override val canNavigateUp = false

    override fun createFragment() = RepositoryListFragment()
}
