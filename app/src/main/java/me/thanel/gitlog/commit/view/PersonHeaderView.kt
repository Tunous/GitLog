package me.thanel.gitlog.commit.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.view_person_header.view.*
import me.thanel.gitlog.R
import org.eclipse.jgit.lib.PersonIdent

class PersonHeaderView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val root = View.inflate(context, R.layout.view_person_header, this)
    private val avatarView = root.avatarView
    private val nameView = root.nameView

    fun bind(ident: PersonIdent) {
        avatarView.setFromIdent(ident)
        nameView.text = ident.name
    }
}