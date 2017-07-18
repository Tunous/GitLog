package me.thanel.gitlog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_commit.*
import org.eclipse.jgit.util.RelativeDateFormatter
import java.text.SimpleDateFormat
import java.util.*

class CommitActivity : AppCompatActivity() {

    private val commit by lazy { intent.getParcelableExtra<Commit>(EXTRA_COMMIT) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commit)

        commitAuthorAvatarView.setImageDrawable(AvatarDrawable(commit.authorName, commit))
        commitAuthorView.text = commit.authorName
        commitShaView.text = commit.sha.substring(0, 6)
        commitMessageView.text = commit.fullMessage.trim()

        val date = Date(commit.commitTime * 1000L)
        commitDateView.text = RelativeDateFormatter.format(date)
        val format = SimpleDateFormat("'authored' yyyy-MM-dd '@' HH:mm", Locale.getDefault())
        val longDate = format.format(date)
        commitDateView.setOnClickListener {
            Toast.makeText(this, longDate, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val EXTRA_COMMIT = "extra.commit"

        fun newIntent(context: Context, commit: Commit): Intent {
            return Intent(context, CommitActivity::class.java).apply {
                putExtra(EXTRA_COMMIT, commit)
            }
        }
    }
}
