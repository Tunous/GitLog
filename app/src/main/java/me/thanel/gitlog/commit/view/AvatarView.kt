package me.thanel.gitlog.commit.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_stacked_avatar.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.md5
import me.thanel.gitlog.view.AvatarDrawable
import me.thanel.gitlog.view.SmallCircleDrawable
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit

class AvatarView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val root = View.inflate(context, R.layout.view_stacked_avatar, this)
    private val bigAvatarView = root.bigAvatarView
    private val smallAvatarView = root.smallAvatarView

    init {
        if (isInEditMode) {
            bigAvatarView.setImageResource(R.mipmap.ic_launcher_round)
            smallAvatarView.setImageResource(R.mipmap.ic_launcher_round)
            smallAvatarView.visibility = View.VISIBLE
        }
    }

    fun setFromIdent(ident: PersonIdent) {
        val drawable = AvatarDrawable(ident.name, ident)
        loadAvatar(ident, drawable, bigAvatarView)
        smallAvatarView.visibility = View.GONE
    }

    fun setFromCommit(revCommit: RevCommit, allowMergeIndicator: Boolean = false) {
        if (allowMergeIndicator && revCommit.parentCount > 1) {
            bigAvatarView.setImageDrawable(SmallCircleDrawable())
            smallAvatarView.visibility = View.GONE
            return
        }

        val author: PersonIdent? = revCommit.authorIdent
        val authorName = author?.name ?: "UNKNOWN AUTHOR"
        val authorDrawable = AvatarDrawable(authorName, author)
        loadAvatar(author, authorDrawable, bigAvatarView)

        val committer: PersonIdent? = revCommit.committerIdent
        if (committer != null && committer.emailAddress != author?.emailAddress) {
            val committerDrawable = AvatarDrawable(committer.name, committer)
            loadAvatar(committer, committerDrawable, smallAvatarView)
            smallAvatarView.visibility = View.VISIBLE
        } else {
            smallAvatarView.visibility = View.GONE
        }
    }

    private fun loadAvatar(ident: PersonIdent?, defaultDrawable: AvatarDrawable, view: ImageView) {
        if (ident == null) {
            view.setImageDrawable(defaultDrawable)
            return
        }

        val hash = ident.emailAddress.trim().toLowerCase().md5()
        val url = Uri.parse(GRAVATAR_URL).buildUpon()
                .appendPath(hash)
                .appendQueryParameter("d", "identicon")
                .toString()

        Picasso.with(context)
                .load(url)
                .placeholder(defaultDrawable)
                .into(view)
    }

    companion object {
        private const val GRAVATAR_URL = "https://www.gravatar.com/avatar"
    }
}