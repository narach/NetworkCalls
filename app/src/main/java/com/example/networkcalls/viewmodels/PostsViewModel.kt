package com.example.networkcalls.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networkcalls.entities.Post
import com.example.networkcalls.repositories.PostRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class PostsViewModel(private val repository: PostRepository) : ViewModel() {

    var postsResponse: MutableLiveData<Response<List<Post>>> = MutableLiveData()
    var selectedPost: MutableLiveData<Response<Post>> = MutableLiveData()

    var userPostsResponse: MutableLiveData<Response<List<Post>>> = MutableLiveData()

    fun getPosts() {
        viewModelScope.launch {
            val response = repository.getPosts()
            postsResponse.value = response
        }
    }

    fun getPostById(postId: Int) {
        viewModelScope.launch {
            val response = repository.getPostById(postId)
            selectedPost.value = response
        }
    }

    fun getPostsByUser(userId: Int) {
        viewModelScope.launch {
            val response = repository.getPostsByUser(userId)
            userPostsResponse.value = response
        }
    }
}