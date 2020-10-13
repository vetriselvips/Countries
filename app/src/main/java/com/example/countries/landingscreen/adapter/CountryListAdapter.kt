package com.example.countries.landingscreen.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.ahmadrosid.svgloader.SvgLoader
import com.example.countries.R
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

        return CountryListViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_countrylist,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return filteredCountryList.size
    }

    override fun onBindViewHolder(holder: CountryListAdapter.CountryListViewHolder, position: Int) {

        val eachItem = filteredCountryList.get(position)
        holder.bind(eachItem)
    }

    inner class CountryListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(model: CountryDetailsResp) = with(itemView) {
            itemView.tvCountryName.text = model.name
            SvgLoader.pluck()
                .with(context as Activity?)
                .setPlaceHolder(
                    R.drawable.ic_no_image_placeholder,
                    R.drawable.ic_no_image_placeholder
                )
                .load(model.flag, itemView.ivFlag)

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