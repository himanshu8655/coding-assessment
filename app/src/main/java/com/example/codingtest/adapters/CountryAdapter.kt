package com.example.codingtest.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.codingtest.R
import com.example.codingtest.models.Country

class CountryAdapter(private val countries : List<Country>): RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {
    inner class CountryViewHolder(countryView : View):RecyclerView.ViewHolder(countryView){
        val nameRegionTextView: TextView = itemView.findViewById(R.id.nameRegionTextView)
        val codeTextView: TextView = itemView.findViewById(R.id.codeTextView)
        val capitalTextView: TextView = itemView.findViewById(R.id.capitalTextView)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.country_item, parent, false)
        return CountryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return countries.size
    }

    override fun onBindViewHolder(
        holder: CountryAdapter.CountryViewHolder,
        position: Int
    ) {
        val country = countries[position]
        holder.nameRegionTextView.text = "${country.name}, ${country.region}"
        holder.codeTextView.text = country.code
        holder.capitalTextView.text = country.capital
    }
}