package me.thanel.gitlog.commit.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.view_stacked_avatar.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.view.AvatarDrawable
import me.thanel.gitlog.view.SmallCircleDrawable
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit

class StackedAvatarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val root = View.inflate(context, R.layout.view_stacked_avatar, this)
    private val bigAvatarView = root.bigAvatarView
    private val smallAvatarView = root.smallAvatarView

    fun setFromCommit(revCommit: RevCommit, allowMergeIndicator: Boolean = false) {
        if (allowMergeIndicator && revCommit.parentCount > 1) {
            setBigDrawable(SmallCircleDrawable())
            setSmallDrawable(null)
            return
        }

        val author: PersonIdent? = revCommit.authorIdent
        val authorName = author?.name ?: "UNKNOWN AUTHOR"
        val authorDrawable = AvatarDrawable(authorName, author)
        setBigDrawable(authorDrawable)

        val committer: PersonIdent? = revCommit.committerIdent
        if (committer != null && committer.emailAddress != author?.emailAddress) {
            val committerDrawable = AvatarDrawable(committer.name, committer)
            setSmallDrawable(committerDrawable)
        } else {
            setSmallDrawable(null)
        }
    }

    private fun setBigDrawable(drawable: Drawable) {
        bigAvatarView.setImageDrawable(drawable)
    }

    private fun setSmallDrawable(drawable: Drawable?) {
        smallAvatarView.setImageDrawable(drawable)
        smallAvatarView.visibility = if (drawable != null) View.VISIBLE else View.GONE
    }
}