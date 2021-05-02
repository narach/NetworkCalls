package com.example.networkcalls.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.networkcalls.repositories.CarsRepository
import com.example.networkcalls.viewmodels.CarsViewModel

class CarsViewModelFactory(private val repository: CarsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CarsViewModel(repository) as T
    }
}