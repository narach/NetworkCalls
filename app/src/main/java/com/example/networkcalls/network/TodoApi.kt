package com.example.networkcalls.network

import com.example.networkcalls.entities.CreateTodoResponse
import com.example.networkcalls.entities.Todo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Интерфейс для выполнения запросов к API
interface TodoApi {

    // Относительный путь к ресурсам, которые запрашиваем.
    @GET("/todos")
    // Помечаем как suspend - чтобы вызывать асинхронно
    suspend fun getTodos(): Response<List<Todo>>

    // Загрузить данные на сервер - новый объект.
    @POST("/createTodo")
    fun createTodo(@Body todo: Todo): Response<Todo>

    // Если надо передавать параметры fun getTodos(@Query("key") key: String): Response<List<Todo>>
}