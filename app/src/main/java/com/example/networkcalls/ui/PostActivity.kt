package com.example.networkcalls.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.networkcalls.databinding.ActivityPostBinding
import com.example.networkcalls.entities.NewPost
import com.example.networkcalls.entities.Post
import com.example.networkcalls.network.RetrofitInstance
import com.example.networkcalls.repositories.PostRepository
import com.example.networkcalls.viewmodels.PostsViewModel
import com.example.networkcalls.viewmodels.factories.PostsViewModelFactory
import retrofit2.HttpException
import java.io.IOException

const val POST_TAG = "PostActivity"

class PostActivity : AppCompatActivity() {

    private lateinit var viewModel: PostsViewModel

    private lateinit var binding: ActivityPostBinding

    private var postId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = PostRepository()
        val viewModelFactory = PostsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PostsViewModel::class.java)

        postId = intent.getIntExtra("postId", -1)

        binding.apply {
            btnSavePost.setOnClickListener {
                if(postId == -1) {
                    var newPost = NewPost(
                        etPostTitle.text.toString(),
                        etPostBody.text.toString(),
                        1
                    )
                    var postId = placeNewPost(newPost)
                    Log.d(POST_TAG, "Post ID: $postId")
                } else {
                    var post = Post(
                        postId,
                        etPostTitle.text.toString(),
                        etPostBody.text.toString(),
                        1
                    )
                    rewritePost(postId!!, post)
                }
            }

            btnGetCustomPosts.setOnClickListener {
//                viewModel.getCustomPosts(1, "id", "desc")

                val options: HashMap<String, String> = HashMap()
                options["_sort"] = "id"
                options["_order"] = "desc"
                viewModel.getCustomPosts(3, options)
                viewModel.myCustomPosts2.observe(this@PostActivity, { response ->
                    Log.d("Headers", response.headers().toString())
                    response.body()?.forEach {
                        Log.d("Response", it.userId.toString())
                        Log.d("Response", it.id.toString())
                        Log.d("Response", it.title!!)
                        Log.d("Response", it.body!!)
                        Log.d("Response", "--------------------")
                    }
                })

                val myPost = Post(-1, "Narach1988", "Some pst body", 2)
                viewModel.pushPost(myPost)
                viewModel.myResponse.observe(this@PostActivity, { response ->
                    // Do something with response here
                })
            }

            btnReturn.setOnClickListener {
                finish()
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
                Log.e(POST_TAG, "IOException, you might not have internet connection")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(POST_TAG, "HttpException, unexpected response")
                return@launchWhenCreated
            }
            if (response.isSuccessful && response.body() != null) {
                binding.etLogs.setText(response.toString())
                createdPost = response.body()!!
                newPostId = createdPost!!.id
                Log.d(POST_TAG, "Newly created post: $createdPost")
            } else {
                Log.e(POST_TAG, "Response was not successful. Error code: ${response.code()}")
            }
        }
        return newPostId
    }

    private fun rewritePost(postId: Int, updatedPost: Post) {
        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitInstance.postsApi.rewritePost(postId, updatedPost)
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