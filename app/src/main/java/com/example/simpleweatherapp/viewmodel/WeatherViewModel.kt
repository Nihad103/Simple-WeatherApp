package com.example.simpleweatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.simpleweatherapp.model.Model
import com.example.simpleweatherapp.service.RetrofitInstance
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class WeatherViewModel : ViewModel() {


    val TAG = "MainViewModel"
    var compositeDisposable = CompositeDisposable()
    var retrofitInstance = RetrofitInstance()

    var error = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()
    var responseWeather = MutableLiveData<Model>()

    fun fetchData(cityName: String) {
        getDataFromApi(cityName)
        // This line -> We can add: fun getDataFromDatabase
    }

    fun getDataFromApi(cityName: String) {

        loading.value = true

        compositeDisposable.add(
            retrofitInstance.loadData(cityName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Model>() {
                    override fun onSuccess(t: Model) {
                        loading.value = false
                        error.value = false
                        responseWeather.value = t
                    }

                    override fun onError(e: Throwable) {
                        loading.value = false
                        error.value = true
                        Log.e(TAG,
                            "Check your internet connection or Enter the city name correctly" , e)
                    }

                })
        )
    }
}