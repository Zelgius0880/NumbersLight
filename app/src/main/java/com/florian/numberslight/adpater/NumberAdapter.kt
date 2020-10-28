package com.florian.numberslight.adpater

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.florian.numberslight.BuildConfig
import com.florian.numberslight.context
import com.florian.numberslight.databinding.AdapterNumberBinding
import com.florian.numberslight.model.INumber
import com.squareup.picasso.Picasso

class NumberAdapter(
    private var list: List<INumber>,
    private val listener: OnItemClickedListener? = null
) : RecyclerView.Adapter<NumberAdapter.ViewHolder>() {
    interface OnItemClickedListener {
        fun onItemClicked(item: INumber)
    }

    var keepSelection: Boolean = false
    var selectedItem : INumber? = null

    inner class ViewHolder(val binding: AdapterNumberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(AdapterNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        with(holder.binding) {
            name.text = item.name
            Picasso.with(context).apply {
                isLoggingEnabled = BuildConfig.LOG_NETWORK
            }
                .load(item.image)
                .into(image)

            cardView.setOnClickListener {
                selectedItem = item
                listener?.onItemClicked(item)
                notifyDataSetChanged()
            }

            cardView.isChecked = item.name == selectedItem?.name && keepSelection

        }
    }

    fun setList(list: List<INumber>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size
}