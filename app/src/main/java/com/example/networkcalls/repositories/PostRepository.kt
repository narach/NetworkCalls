package com.example.networkcalls.repositories

import com.example.networkcalls.entities.Post
import com.example.networkcalls.network.RetrofitInstance
import retrofit2.Response
import retrofit2.http.Body

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

    suspend fun getCustomPosts(userId: Int, sort: String, order: String): Response<List<Post>> {
        return RetrofitInstance.postsApi.getCustomPosts(userId, sort, order)
    }

    suspend fun getCustomPosts2(userId: Int, options: Map<String, String>): Response<List<Post>> {
        return RetrofitInstance.postsApi.getCustomPosts2(userId, options)
    }

    suspend fun pushPost(post: Post): Response<Post> {
        return RetrofitInstance.postsApi.pushPost(post)
    }

    suspend fun pushPost2(id: Int, userId: Int, title: String, body: String) : Response<Post> {
        return RetrofitInstance.postsApi.pushPost2(id, userId, title, body)
    }
}