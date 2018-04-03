package me.thanel.gitlog.commit.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import kotlinx.android.synthetic.main.view_stacked_avatar.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.loadAvatar
import me.thanel.gitlog.view.SmallCircleDrawable
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit

class AvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.view_stacked_avatar, this)
        if (isInEditMode) {
            bigAvatarView.setImageResource(R.mipmap.ic_launcher_round)
            smallAvatarView.setImageResource(R.mipmap.ic_launcher_round)
            smallAvatarView.visibility = View.VISIBLE
        }
    }

    fun setFromIdent(ident: PersonIdent) {
        loadAvatar(ident, bigAvatarView)
        smallAvatarView.visibility = View.GONE
    }

    fun setFromCommit(revCommit: RevCommit, allowMergeIndicator: Boolean = false) {
        if (allowMergeIndicator && revCommit.parentCount > 1) {
            bigAvatarView.setImageDrawable(SmallCircleDrawable())
            smallAvatarView.visibility = View.GONE
            return
        }

        val author: PersonIdent? = revCommit.authorIdent
        loadAvatar(author, bigAvatarView)

        val committer: PersonIdent? = revCommit.committerIdent
        if (committer != null && committer.emailAddress != author?.emailAddress) {
            loadAvatar(committer, smallAvatarView)
            smallAvatarView.visibility = View.VISIBLE
        } else {
            smallAvatarView.visibility = View.GONE
        }
    }

    private fun loadAvatar(ident: PersonIdent?, view: ImageView) {
        if (ident == null) {
            view.setImageDrawable(null)
        } else {
            context.loadAvatar(ident.emailAddress, view)
        }
    }
}
