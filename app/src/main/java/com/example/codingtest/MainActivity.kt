package com.example.codingtest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Callback
import okhttp3.Call
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.io.IOException
import org.json.JSONArray

data class Country(val name: String, val region: String, val code: String, val capital: String)

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var countryList: ArrayList<Country>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fetchCountryData()
    }

    private fun fetchCountryData() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://gist.githubusercontent.com/peymano-wmt/32dcb892b06648910ddd40406e37fdab/raw/db25946fd77c5873b0303b858e861ce724e0dcd0/countries.json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val countriesJsonArray = JSONArray(responseBody)

                    countryList = ArrayList()
                    for (i in 0 until countriesJsonArray.length()) {
                        val countryObject = countriesJsonArray.getJSONObject(i)
                        val country = Country(
                            countryObject.getString("name"),
                            countryObject.getString("region"),
                            countryObject.getString("code"),
                            countryObject.getString("capital")
                        )
                        countryList.add(country)
                    }

                    runOnUiThread {
                        recyclerView.adapter = CountryAdapter(countryList)
                    }
                } else {
                    showError("Failed to fetch data")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                showError("Error: ${e.message}")
            }
        })
    }

    private fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    class CountryAdapter(private val countries: List<Country>) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.country_item, parent, false)
            return CountryViewHolder(view)
        }

        override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
            val country = countries[position]
            holder.nameRegionTextView.text = "${country.name}, ${country.region}"
            holder.codeTextView.text = country.code
            holder.capitalTextView.text = country.capital
        }

        override fun getItemCount(): Int = countries.size

        class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameRegionTextView: TextView = itemView.findViewById(R.id.nameRegionTextView)
            val codeTextView: TextView = itemView.findViewById(R.id.codeTextView)
            val capitalTextView: TextView = itemView.findViewById(R.id.capitalTextView)
        }
    }
}
