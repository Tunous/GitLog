package me.thanel.gitlog

import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_commit.*
import org.eclipse.jgit.util.RelativeDateFormatter
import java.text.SimpleDateFormat
import java.util.*

class CommitFragment : BaseFragment() {

    private val commit by lazy { arguments.getParcelable<Commit>(ARG_COMMIT) }

    override val layoutResId: Int
        get() = R.layout.fragment_commit

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        commitAuthorAvatarView.setImageDrawable(AvatarDrawable(commit.authorName, commit))
        commitAuthorView.text = commit.authorName
        commitShaView.text = commit.shortSha
        commitMessageView.text = commit.fullMessage.trim()

        val date = Date(commit.commitTime * 1000L)
        commitDateView.text = RelativeDateFormatter.format(date)
        val format = SimpleDateFormat("'authored' yyyy-MM-dd '@' HH:mm", Locale.getDefault())
        val longDate = format.format(date)
        commitDateView.setOnClickListener {
            Toast.makeText(context, longDate, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARG_COMMIT = "arg.commit"

        fun newInstance(commit: Commit): CommitFragment {
            return CommitFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_COMMIT, commit)
                }
            }
        }
    }

}