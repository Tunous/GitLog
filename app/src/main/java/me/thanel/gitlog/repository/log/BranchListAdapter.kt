package me.thanel.gitlog.repository.log

import android.view.View
import android.widget.TextView
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository

class BranchListAdapter(
        onItemClickListener: (Ref) -> Unit
) : ItemAdapter<Ref, BranchListAdapter.ViewHolder>(onItemClickListener) {
    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getLayoutResId(viewType: Int) = R.layout.item_branch

    class ViewHolder(itemView: View) : ItemAdapter.ViewHolder<Ref>(itemView) {
        private val branchNameView = itemView.findViewById<TextView>(R.id.branchNameView)

        override fun bind(item: Ref) {
            super.bind(item)
            branchNameView.text = Repository.shortenRefName(item.name)
        }
    }
}