package com.example.simpleweatherapp.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.simpleweatherapp.R
import com.example.simpleweatherapp.databinding.ActivityMainBinding
import com.example.simpleweatherapp.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"
    lateinit var viewModel: WeatherViewModel
    lateinit var get: SharedPreferences
    lateinit var set: SharedPreferences.Editor
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        get = getSharedPreferences("cityName", MODE_PRIVATE)
        set = get.edit()

        viewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)

        var cName = get.getString("cityname", "baku")?.lowercase()
        binding.edtCityName.setText(cName)
        if (cName != null) {
            viewModel.fetchData(cName)
        }

        getLiveData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.pbLoading.visibility = View.GONE
            binding.tvError.visibility = View.GONE
            binding.llData.visibility = View.GONE

            var cName = get.getString("cityname", cName)?.lowercase()
            binding.edtCityName.setText(cName)
            if (cName != null) {
                viewModel.fetchData(cName)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.imgSearchCity.setOnClickListener {
            val cityName = binding.edtCityName.text.toString()
            set.putString("cityName", cityName)
            set.apply()
            viewModel.fetchData(cityName)
            getLiveData()
            Log.i(TAG, "Oncreate: " + cityName)
        }

    }

    private fun getLiveData() {
        viewModel.responseWeather.observe(this, Observer { data ->
            data?.let {
                binding.tvError.visibility = View.GONE
                binding.llData.visibility = View.VISIBLE
                binding.tvCityCode.text = data.sys.country.toString()
                binding.tvCityName.text = data.name.toString()
                binding.tvDegree.text = data.main.temp.toString() + "°C"
                binding.tvHumidity.text = data.main.humidity.toString() + "%"
                binding.tvWindSpeed.text = data.wind.speed.toString()
                binding.tvLat.text = data.coord.lat.toString()
                binding.tvLon.text = data.coord.lon.toString()

                // Get Weather Icon with Glide
                val imgWeatherPictures = findViewById<ImageView>(R.id.img_weather_pictures)
                Glide.with(this)
                    .load("https://openweathermap.org/img/wn/" + data.weather.get(0).icon + "@2x.png")
                    .into(imgWeatherPictures)



            }
        })

        viewModel.loading.observe(this, Observer { loading ->
            loading?.let {
                if (loading) {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.tvError.visibility = View.GONE
                    binding.llData.visibility = View.GONE
                } else {
                    binding.pbLoading.visibility = View.GONE
                }
            }
        })

        viewModel.error.observe(this, Observer { error ->
            error?.let {
                if (error) {
                    binding.tvError.visibility = View.VISIBLE
                    binding.pbLoading.visibility = View.GONE
                    binding.llData.visibility = View.GONE
                } else {
                    binding.tvError.visibility = View.GONE
                }
            }
        })
    }
}
