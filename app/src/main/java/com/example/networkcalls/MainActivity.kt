package com.example.networkcalls

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networkcalls.adapters.TodoAdapter
import com.example.networkcalls.databinding.ActivityMainBinding
import com.example.networkcalls.entities.NewPost
import com.example.networkcalls.entities.Post
import com.example.networkcalls.network.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Инициализируем RecyclerView
        setupRecyclerView()

        // Загружаем элементы с сервера в отдельном потоке
        lifecycleScope.launchWhenCreated {
            binding.progressBar.isVisible = true
            val response = try {
                RetrofitInstance.api.getTodos() // lazy объект API будет проинициализирован здесь!
            } catch(e: IOException) { // Для No Internet Connection
                Log.e(TAG, "IOException, you might not have internet connection")
                binding.progressBar.isVisible = false // Прячем ProgressBar
                return@launchWhenCreated // возвращаемся с ошибкой
            } catch(e: HttpException) { // Когда что-то упало на сервере
                Log.e(TAG, "HttpException, unexpected response")
                binding.progressBar.isVisible = false
                return@launchWhenCreated
            }
            if(response.isSuccessful && response.body() != null) { // Если запрос выполнился успешно - заполняем список
                todoAdapter.todos = response.body()!!
            } else {
                Log.e(TAG, "Response was not successful")
            }
            binding.progressBar.isVisible = false
        }

        getPosts()
        var newPostId = placeNewPost(
            NewPost(
                "Prepare materials and examples for Android course lesson",
                "Weekend task",
                1
            )
        )
        newPostId?.let { postId ->
            rewritePost(postId)
        }

    }

    private fun setupRecyclerView() = binding.rvTodos.apply {
        todoAdapter = TodoAdapter()
        adapter = todoAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
    }

    private fun getPosts() {
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitInstance.postsApi.getPosts()
            } catch(e: IOException) { // Для No Internet Connection
                Log.e(TAG, "IOException, you might not have internet connection")
                return@launchWhenCreated
            } catch(e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                return@launchWhenCreated
            }
            if(response.isSuccessful && response.body() != null) {
                val fetchedPosts = response.body()!!
                Log.d(TAG, "All posts: $fetchedPosts")
            } else {
                Log.e(TAG, "Response was not successful. Error code: ${response.code()}")
            }
        }
    }

    private fun placeNewPost(newPost: NewPost) : Int? {
        var createdPost: Post? = null
        var newPostId: Int? = null
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitInstance.postsApi.createPost(newPost)
            } catch (e: IOException) { // Для No Internet Connection
                Log.e(TAG, "IOException, you might not have internet connection")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                createdPost = response.body()!!
                newPostId = createdPost!!.id
                Log.d(TAG, "Newly created post: $createdPost")
            } else {
                Log.e(TAG, "Response was not successful. Error code: ${response.code()}")
            }
        }
        return newPostId
    }

    private fun rewritePost(postId: Int) {
        lifecycleScope.launchWhenCreated {
            val newPost = Post(postId,
                "Weekend Task",
                "Prepare new lection about Retrofit for Android course",
                1
            )
            val response = try {
                RetrofitInstance.postsApi.rewritePost(postId, newPost)
            } catch (e: IOException) { // Для No Internet Connection
                Log.e(TAG, "IOException, you might not have internet connection")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response")
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                var rewritePost = response.body()!!
                Log.d(TAG, "Newly created post: $rewritePost")
            } else {
                Log.e(TAG, "Response was not successful. Error code: ${response.code()}")
            }
        }
    }
}