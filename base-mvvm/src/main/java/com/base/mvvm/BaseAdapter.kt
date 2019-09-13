package com.base.mvvm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Common BaseAdapter for simple ListAdapter.
 * T: Model's type.
 * layout data binding xml: variable name = "item", type = T.
 */
abstract class BaseAdapter<T>(listener: ClickListener?, diffCallback: DiffUtil.ItemCallback<T>) :
    ListAdapter<T, BaseAdapter.BaseViewHolder<T>>(diffCallback) {
    private val mListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            mListener?.onItemClick(position, getItem(position))
        }
    }


    class BaseViewHolder<T>(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T) {
            //TODO: Uncomment this line after build succeed
            //binding.setVariable(BR.item, item)
            binding.executePendingBindings()
        }
    }

    interface ClickListener {
        fun <T> onItemClick(position: Int, item: T)
    }
}