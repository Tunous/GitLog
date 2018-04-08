package me.thanel.gitlog.ui.commit

import activitystarter.Arg
import android.content.Intent
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.activity.BaseFragmentActivity
import me.thanel.gitlog.ui.repository.RepositoryActivityStarter
import me.thanel.gitlog.ui.utils.SHORT_SHA_LENGTH
import me.thanel.gitlog.ui.utils.StyleableTag
import me.thanel.gitlog.ui.utils.formatTags
import kotlin.math.min

class CommitActivity : BaseFragmentActivity() {
    @get:Arg
    val commitSha: String by argExtra()

    @get:Arg
    val repositoryId: Int by argExtra()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val shortSha = commitSha.substring(0, min(commitSha.length, SHORT_SHA_LENGTH))
        toolbarTitle = getString(R.string.commit_sha).formatTags(StyleableTag("sha", shortSha))
    }

    override fun createFragment(): CommitFragment =
        CommitFragmentStarter.newInstance(commitSha, repositoryId)

    override fun getSupportParentActivityIntent(): Intent =
        RepositoryActivityStarter.getIntent(this, repositoryId)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
