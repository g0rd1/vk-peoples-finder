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
    private val diffCallbackFactory: AppDiffCallbackFactory<V>,
    private val itemClickListener: ItemClickListener<V>? = null
) : RecyclerView.Adapter<BindingRecyclerViewAdapter<VBD, V>.BindingViewHolder>() {

    private var items: List<V> = listOf()

    inner class BindingViewHolder(val binding: VBD) : RecyclerView.ViewHolder(binding.root)

    @Suppress("UNCHECKED_CAST")
    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder {
        val binding: VBD = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), layoutRes, parent, false
        )
        binding.root.setOnClickListener {
            itemClickListener?.onItemClick(binding.root.tag as V)
        }
        return BindingViewHolder(binding)
    }

    final override fun getItemCount(): Int = items.size

    @CallSuper
    final override fun onBindViewHolder(holder: BindingViewHolder, position: Int) {
        holder.binding.root.tag = items[position]
        getSetViewModelToBindingFunction(holder.binding).invoke(items[position])
    }

    protected abstract fun getSetViewModelToBindingFunction(holderBinding: VBD): (V) -> Unit

    @Synchronized
    fun setItems(items: List<V>) {
        val oldItems = this.items.toList()
        this.items = items
        val diffResult = DiffUtil.calculateDiff(diffCallbackFactory.create(oldItems, items))
        diffResult.dispatchUpdatesTo(this)
    }

}