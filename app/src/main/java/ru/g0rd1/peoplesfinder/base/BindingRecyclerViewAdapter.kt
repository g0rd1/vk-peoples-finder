package ru.g0rd1.peoplesfinder.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.g0rd1.peoplesfinder.common.AppDiffCallbackFactory

abstract class BindingRecyclerViewAdapter<VBD : ViewDataBinding, V>(
    @LayoutRes private val layoutRes: Int,
    private val diffCallbackFactory: AppDiffCallbackFactory<V>
) : RecyclerView.Adapter<BindingRecyclerViewAdapter<VBD, V>.BindingViewHolder>() {

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
        getSetViewModelToBindingFunction(holder.binding).invoke(items[position])
    }

    protected abstract fun getSetViewModelToBindingFunction(holderBinding: VBD): (V) -> Unit

    fun setOnItemClickListener(onItemClickListener: (position: V) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setItems(items: List<V>) {
        val oldItems = this.items.toList()
        this.items.clear()
        this.items.addAll(items)
        val diffResult = DiffUtil.calculateDiff(diffCallbackFactory.create(oldItems, items))
        diffResult.dispatchUpdatesTo(this)
    }


}