package ru.g0rd1.peoplesfinder.common

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import ru.g0rd1.peoplesfinder.databinding.ItemSpinnerBinding


class AppSpinnerAdapter(
    context: Context,
    private val elements: List<String>,
) : ArrayAdapter<String>(context, 0, elements) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
        binding.text = elements[position]
        return binding.root
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding = ItemSpinnerBinding.inflate(LayoutInflater.from(context), parent, false)
        binding.text = elements[position]
        return binding.root
    }

}