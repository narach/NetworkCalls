package com.example.networkcalls.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.networkcalls.repositories.PostRepository
import com.example.networkcalls.viewmodels.PostsViewModel

class PostsViewModelFactory(private val repository: PostRepository) :
        ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostsViewModel(repository) as T
    }
}