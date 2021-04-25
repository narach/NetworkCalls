package com.example.networkcalls

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networkcalls.adapters.TodoAdapter
import com.example.networkcalls.databinding.ActivityMainBinding
import com.example.networkcalls.network.RetrofitInstance
import retrofit2.HttpException
import java.io.IOException

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), ICommunication {

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

        with(binding) {
            btnNewPost.setOnClickListener {
                Intent(this@MainActivity, PostActivity::class.java).also {
                    startActivity(it)
                }
            }
        }

        getPosts()
    }

    private fun setupRecyclerView() = binding.rvTodos.apply {
        todoAdapter = TodoAdapter(this@MainActivity)
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

    override fun openPostActivity() {
        Intent(this@MainActivity, PostActivity::class.java).also {
            it.putExtra("postId", 1)
            startActivity(it)
        }
    }
}