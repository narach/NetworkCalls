package com.example.networkcalls.adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.networkcalls.ICommunication
import com.example.networkcalls.TAG
import com.example.networkcalls.databinding.ItemTodoBinding
import com.example.networkcalls.entities.Todo
import com.example.networkcalls.network.RetrofitInstance
import kotlinx.coroutines.*
import retrofit2.HttpException
import java.io.IOException

class TodoAdapter(val parentActivity: ICommunication) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    private var alertDialog: AlertDialog? = null
    private lateinit var context: Context

    inner class TodoViewHolder(val binding: ItemTodoBinding) : RecyclerView.ViewHolder(binding.root)

    /*
    Утилита для сравнения объектов в списке - чтобы определить, добавить ли
    новый элемент или он уже существует
     */
    private val diffCallback = object : DiffUtil.ItemCallback<Todo>() {

        // Сравниваем два объекта - обычно по ID или по другому ключевому полю
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.id == newItem.id
        }

        // Сравниваем содержимое объектов - проверяем, если что-то изменилось
        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }

    }

    // Собственно сама утилита, что будет мониторить изменение списка.
    private val differ = AsyncListDiffer(this, diffCallback)
    var todos: List<Todo>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    // Создание View для каждого объекта
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        this.context = parent.context
        return TodoViewHolder(ItemTodoBinding.inflate(
            LayoutInflater.from(parent.context), // Получаем inflater из контекста
            parent,
            false // Не присоединяем к rootView, т.к. это будет сделано RecyclerView адаптером автоматически
        ))
    }

    // Привязываем данные из объекта к TodoItem (графическому отображению элемента списка)
    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.binding.apply {
            val todo = todos[position]
            tvTitle.text = todo.title
            cbDone.isChecked = todo.completed
        }

        holder.itemView.setOnClickListener {
            parentActivity.openPostActivity()
        }

        holder.itemView.setOnLongClickListener {
            val delPostId = 1
            showDeleteDialog(delPostId)
            true
        }
    }

    // Количество элементов в списке
    override fun getItemCount() = todos.size

    private fun showDeleteDialog(postId: Int) {
        val builder = AlertDialog.Builder(context)
        builder.run {
            setTitle("Delete post")
            setMessage("Are you really want to delete a post #$postId?")
            setPositiveButton("Yes") { _, _ ->
                runBlocking {
                    deletePost(postId)
                }
            }
            setNegativeButton("Cancel") {_, _ ->
            }
        }
        alertDialog = builder.create()
        alertDialog?.show()
    }

    private suspend fun deletePost(postId: Int) {
        coroutineScope {
            launch {
                val response = try {
                    RetrofitInstance.postsApi.deletePost(postId)
                } catch (e: IOException) { // Для No Internet Connection
                    Log.e(TAG, "IOException, you might not have internet connection")
                    return@launch
                }
                catch (e: HttpException) {
                    Log.e(TAG, "HttpException, unexpected response")
                    return@launch
                }
                if (response.isSuccessful && response.body() != null) {
                    Log.d(TAG, "Post #$postId was deleted!")
                } else {
                    Log.e(TAG, "Response was not successful. Error code: ${response.code()}")
                }
            }
        }
    }
}