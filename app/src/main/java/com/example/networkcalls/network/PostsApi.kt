package com.example.networkcalls.network

import com.example.networkcalls.entities.NewPost
import com.example.networkcalls.entities.Post
import retrofit2.Response
import retrofit2.http.*

interface PostsApi {

    @GET("/posts")
    suspend fun getPosts(): Response<List<Post>>

    @GET("/posts")
    suspend fun getPostsByUser(
        @Query("userId") userId: Int
    ): Response<List<Post>>

    @GET("/posts/{postId}")
    suspend fun getPostById(
        @Path("postId") postId: Int
    ) : Response<Post>

    @POST("/posts")
    suspend fun createPost(@Body Post: NewPost): Response<Post>

    @PUT("/posts/{postId}")
    suspend fun rewritePost(
        @Path("postId") postId: Int,
        @Body updatedPost: Post
    ) : Response<Post>

    @DELETE("/posts/{postId}")
    suspend fun deletePost(
        @Path("postId") postId: Int
    ) : Response<Any>

    @PATCH("/posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: Int
    )
    // Если надо передавать параметры fun getTodos(@Query("key") key: String): Response<List<Todo>>
}