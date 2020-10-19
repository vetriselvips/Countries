package com.example.countries.landingscreen.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.example.countries.R
import com.example.countries.databinding.ItemCountrylistBinding
import com.example.countries.landingscreen.listener.CountryItemClickListener
import com.example.countries.landingscreen.model.country.CountryDetailsResp
import kotlinx.android.synthetic.main.item_countrylist.view.*

class CountryListAdapter(
    val countryListModel: ArrayList<CountryDetailsResp>,
    val countryItemClick: CountryItemClickListener
) : RecyclerView.Adapter<CountryListAdapter.CountryListViewHolder>(), Filterable {
    var filteredCountryList: ArrayList<CountryDetailsResp>

    init {
        filteredCountryList = countryListModel
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryListAdapter.CountryListViewHolder {

        var inflater = LayoutInflater.from(parent.context)
        var binding : ItemCountrylistBinding = DataBindingUtil.inflate(inflater,R.layout.item_countrylist,parent,false)
        return CountryListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredCountryList.size
    }

    override fun onBindViewHolder(holder: CountryListAdapter.CountryListViewHolder, position: Int) {

        val eachItem = filteredCountryList.get(position)
        holder.bind(eachItem)
    }

    inner class CountryListViewHolder(val binding: ItemCountrylistBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: CountryDetailsResp) = with(itemView) {
            binding.countries = model
            binding.executePendingBindings()
            itemView.setOnClickListener {
                countryItemClick.onCountryClickListener(model)
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString: String = constraint.toString()
                if (charString.isEmpty()) {
                    filteredCountryList = countryListModel
                } else {
                    val filteredList: ArrayList<CountryDetailsResp> = ArrayList()
                    for (row in countryListModel) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    filteredCountryList = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filteredCountryList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredCountryList = results?.values as ArrayList<CountryDetailsResp>
                notifyDataSetChanged()
            }
        }
    }
}