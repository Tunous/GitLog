package me.thanel.gitlog.commit

import activitystarter.Arg
import android.content.Intent
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.repository.RepositoryActivityStarter
import me.thanel.gitlog.utils.SHORT_SHA_LENGTH
import me.thanel.gitlog.utils.StyleableTag
import me.thanel.gitlog.utils.formatTags

class CommitActivity : BaseFragmentActivity() {
    @get:Arg
    val commitSha: String by argExtra()

    @get:Arg
    val repositoryId: Int by argExtra()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val shortSha = commitSha.substring(0, SHORT_SHA_LENGTH)
        toolbarTitle = "Commit [sha]".formatTags(StyleableTag("sha", shortSha))
    }

    override fun createFragment(): CommitFragment =
        CommitFragmentStarter.newInstance(commitSha, repositoryId)

    override fun getSupportParentActivityIntent(): Intent =
        RepositoryActivityStarter.getIntent(this, repositoryId)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
