package com.example.networkcalls.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networkcalls.entities.cardiary.Car
import com.example.networkcalls.network.utils.Resource
import com.example.networkcalls.repositories.CarsRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class CarsViewModel @ViewModelInject constructor(
    private val carsRepository: CarsRepository
):ViewModel(){

    private val _res = MutableLiveData<Resource<List<Car>>>()

    val res : LiveData<Resource<List<Car>>>
        get() = _res

    init {
        getEmployees()
    }

    private fun getEmployees()  = viewModelScope.launch {
        _res.postValue(Resource.loading(null))
        carsRepository.getCars().let {
            if (it.isSuccessful){
                _res.postValue(Resource.success(it.body()))
            }else{
                _res.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

}