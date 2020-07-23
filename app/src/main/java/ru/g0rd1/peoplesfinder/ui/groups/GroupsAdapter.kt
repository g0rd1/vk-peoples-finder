package ru.g0rd1.peoplesfinder.ui.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.g0rd1.peoplesfinder.databinding.ItemGroupBinding
import javax.inject.Inject

class GroupsAdapter @Inject constructor() : RecyclerView.Adapter<GroupsAdapter.GroupViewHolder>() {

    private var groupViews: List<GroupView> = emptyList()

    private var inSelectMode: Boolean = false

    private lateinit var onGroupClickListener: (groupView: GroupView) -> Unit

    class GroupViewHolder(val binding: ItemGroupBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.root.setOnClickListener {
            if (inSelectMode) {
                selectOrUnselectGroup(binding.root.tag as GroupView)
            } else {
                onGroupClickListener(binding.root.tag as GroupView)
            }
        }
        binding.root.setOnLongClickListener {
            selectOrUnselectGroup(binding.root.tag as GroupView)
            true
        }
        return GroupViewHolder(binding)
    }

    override fun getItemCount(): Int = groupViews.size

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.binding.groupView = groupViews[position]
        holder.binding.root.tag = groupViews[position]
    }

    private fun selectOrUnselectGroup(groupView: GroupView) {
        groupView.let { it.selected = !it.selected }
        inSelectMode = groupViews.any { it.selected }
    }

    fun setGroupViews(groupViews: List<GroupView>) {
        notifyItemRangeRemoved(0, itemCount)
        this.groupViews = groupViews
        notifyItemRangeInserted(0, itemCount)
    }

    fun setOnClickListener(onGroupClickListener: (groupView: GroupView) -> Unit) {
        this.onGroupClickListener = onGroupClickListener
    }

    fun isInSelectMode(): Boolean = inSelectMode

}