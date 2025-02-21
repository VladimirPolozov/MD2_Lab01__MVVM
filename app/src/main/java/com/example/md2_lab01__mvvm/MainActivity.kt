package com.example.md2_lab01__mvvm

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.Serializable

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: WeatherViewModel
    private lateinit var adapter: Adapter
    private var weatherData: List<WeatherList>? = null
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        viewModel = WeatherViewModel(
            retrofit.create(RetrofitServices::class.java),
            resources
        )

        adapter = Adapter(DiffCallback(), viewModel)
        findViewById<RecyclerView>(R.id.rView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        viewModel.weatherData.observe(this) { data ->
            data?.let { adapter.submitList(it) }
        }

        viewModel.isCelsius.observe(this) { isCelsius ->
            findViewById<ToggleButton>(R.id.toggleTempUnit).isChecked = !isCelsius
            adapter.notifyDataSetChanged()
        }

        findViewById<ToggleButton>(R.id.toggleTempUnit).setOnCheckedChangeListener { _, _ ->
            viewModel.toggleTemperatureUnit()
        }

        viewModel.toastMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).apply {
                    setGravity(Gravity.CENTER, 0, 0)
                }.show()
                viewModel.onToastShow()
            }
        }

        findViewById<Button>(R.id.search_button).setOnClickListener {
            val city = findViewById<EditText>(R.id.city_input).text.toString()
            if (city.isNotEmpty()) {
                viewModel.fetchWeather(city)
            }
        }
    }
}