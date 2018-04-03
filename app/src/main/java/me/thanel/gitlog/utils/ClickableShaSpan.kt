package me.thanel.gitlog.utils

import android.content.Intent
import android.text.style.ClickableSpan
import android.view.View
import me.thanel.gitlog.commit.CommitActivityStarter

class ClickableShaSpan(
    private val repositoryId: Int,
    private val sha: String
) : ClickableSpan() {
    override fun onClick(view: View) {
        CommitActivityStarter.startWithFlags(
            view.context,
            sha,
            repositoryId,
            Intent.FLAG_ACTIVITY_NEW_TASK
        )
    }
}
