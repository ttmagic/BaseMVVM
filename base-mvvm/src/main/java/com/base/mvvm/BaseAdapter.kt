package com.base.mvvm

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


/**
 * Base Entity for Adapter's list item data.
 */
abstract class Model {
    abstract val uniqueId: Any
}

/**
 * Common BaseAdapter for simple ListAdapter.
 * T: Model's type.
 * layout data binding xml: variable name = "item", type = T.
 */
abstract class BaseAdapter<T : Model>(@LayoutRes private val layoutRes: Int, listener: DefaultClickListener? = null) :
    ListAdapter<T, BaseAdapter.BaseViewHolder<T>>(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem.uniqueId == newItem.uniqueId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem
    }) {
    private val mListener = listener

    override fun getItemViewType(position: Int): Int = layoutRes

    override fun submitList(list: List<T>?) {
        super.submitList(list)
        notifyDataSetChanged()
    }

    /**
     * Set BR.variable ID for data binding.
     */
    abstract fun brVariableId(): Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<T> {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding =
            DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, viewType, parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<T>, position: Int) {
        holder.bind(getItem(position), brVariableId())
        mListener?.let {
            holder.itemView.setOnClickListener {
                mListener.onItemClick(position, getItem(position))
            }
        }
        customBind(holder.binding, position, getItem(position))
    }

    open fun customBind(binding: ViewDataBinding, position: Int, item: T) {

    }


    class BaseViewHolder<T>(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: T, brVariableId: Int) {
            binding.setVariable(brVariableId, item)
            binding.executePendingBindings()
        }
    }

    interface DefaultClickListener {
        fun <T> onItemClick(position: Int, item: T)
    }
}