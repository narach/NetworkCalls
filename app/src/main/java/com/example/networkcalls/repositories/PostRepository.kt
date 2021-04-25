package com.example.networkcalls.repositories

import com.example.networkcalls.entities.Post
import com.example.networkcalls.network.RetrofitInstance
import retrofit2.Response

class PostRepository {
    suspend fun getPosts(): Response<List<Post>> {
        return RetrofitInstance.postsApi.getPosts()
    }

    suspend fun getPostById(postId: Int) : Response<Post> {
        return RetrofitInstance.postsApi.getPostById(postId)
    }

    suspend fun getPostsByUser(userId: Int) : Response<List<Post>> {
        return RetrofitInstance.postsApi.getPostsByUser(userId)
    }
}