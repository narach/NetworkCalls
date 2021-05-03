package com.example.networkcalls.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.networkcalls.entities.Post
import com.example.networkcalls.network.RetrofitInstance
import com.example.networkcalls.repositories.PostRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class PostsViewModel(private val repository: PostRepository) : ViewModel() {

    private var postsResponse: MutableLiveData<Response<List<Post>>> = MutableLiveData()
    var selectedPost: MutableLiveData<Response<Post>> = MutableLiveData()

    private var userPostsResponse: MutableLiveData<Response<List<Post>>> = MutableLiveData()

    private val myCustomPosts: MutableLiveData<Response<List<Post>>> = MutableLiveData()
    val myCustomPosts2: MutableLiveData<Response<List<Post>>> = MutableLiveData()

    val myResponse: MutableLiveData<Response<Post>> = MutableLiveData()

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

    fun getCustomPosts(userId: Int, sort: String, order: String) {
        viewModelScope.launch {
            val response = repository.getCustomPosts(userId, sort, order)
            myCustomPosts.value = response
        }
    }

    fun getCustomPosts(userId: Int, options: Map<String, String>) {
        viewModelScope.launch {
            val response = repository.getCustomPosts2(userId, options)
            myCustomPosts2.value = response
        }
    }

    fun pushPost(post: Post) {
        viewModelScope.launch {
            val response = RetrofitInstance.postsApi.pushPost(post)
            myResponse.value = response
        }
    }
}