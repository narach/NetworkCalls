package com.example.networkcalls.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networkcalls.entities.cardiary.Car
import com.example.networkcalls.repositories.CarsRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class CarsViewModel(private val repository: CarsRepository) : ViewModel() {

    var carsResponse: MutableLiveData<Response<List<Car>>> = MutableLiveData()

    fun getCars() {
        viewModelScope.launch {
            val response = repository.getCars()
            carsResponse.value = response
        }
    }
}