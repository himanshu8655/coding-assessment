package com.example.codingtest

import android.content.Context
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
import com.example.codingtest.adapters.CountryAdapter
import com.example.codingtest.models.Country
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView

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

        fetchCountryData(this)
    }

    private fun fetchCountryData(context:Context) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://gist.githubusercontent.com/peymano-wmt/32dcb892b06648910ddd40406e37fdab/raw/db25946fd77c5873b0303b858e861ce724e0dcd0/countries.json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val gson = Gson()
                    val countryListType = object : TypeToken<ArrayList<Country>>() {}.type
                    val countryList: ArrayList<Country> = gson.fromJson(responseBody, countryListType)

                    for (country in countryList) {
                        country.name = country.name.takeIf { it.isNotEmpty() } ?: "Not available"
                        country.region = country.region.takeIf { it.isNotEmpty() } ?: "Not available"
                        country.code = country.code.takeIf { it.isNotEmpty() } ?: "N/A"
                        country.capital = country.capital.takeIf { it.isNotEmpty() } ?: "Not available"
                    }

                    runOnUiThread {
                        recyclerView.adapter = CountryAdapter(countryList)
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(context, "Failed to fetch data", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(context, "Failed to fetch data, ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
