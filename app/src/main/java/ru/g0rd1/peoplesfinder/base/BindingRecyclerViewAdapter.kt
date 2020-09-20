package ru.g0rd1.peoplesfinder.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BindingRecyclerViewAdapter<VBD : ViewDataBinding, V : Any>(@LayoutRes val layoutRes: Int) :
    RecyclerView.Adapter<BindingRecyclerViewAdapter<VBD, V>.BindingViewHolder>() {

    private val items: MutableList<V> = mutableListOf()

    private var onItemClickListener: ((item: V) -> Unit)? = null

    inner class BindingViewHolder(val binding: VBD) : RecyclerView.ViewHolder(binding.root)

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding: VBD = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), layoutRes, parent, false
        )
        binding.root.setOnClickListener {
            onItemClickListener?.invoke(items[binding.root.tag as Int])
        }
        return BindingViewHolder(binding)
    }

    final override fun getItemCount(): Int = items.size

    @CallSuper
    final override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.binding.root.tag = position
        onBindViewHolder(holder.binding, position, items)
    }

    protected abstract fun onBindViewHolder(holderBinding: VBD, position: Int, items: List<V>)

    fun setOnItemClickListener(onItemClickListener: (position: V) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItems(items: List<V>) {
        val oldItemCount = itemCount
        this.items.clear()
        this.items.addAll(items)

        notifyItemRangeRemoved(0, oldItemCount)
        notifyItemRangeInserted(0, itemCount)
    }

    fun addItems(items: List<V>) {
        val oldCount = itemCount
        this.items.addAll(items)
        notifyItemRangeInserted(oldCount, items.size)
    }
}