package me.thanel.gitlog.utils

import android.content.Intent
import android.text.style.ClickableSpan
import android.view.View
import me.thanel.gitlog.commit.CommitActivity

class ClickableShaSpan(private val repositoryId: Int, private val sha: String) : ClickableSpan() {
    override fun onClick(view: View) {
        val context = view.context
        val intent = CommitActivity.newIntent(context, repositoryId, sha)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
}