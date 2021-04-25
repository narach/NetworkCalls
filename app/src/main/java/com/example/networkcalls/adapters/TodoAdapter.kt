package com.example.networkcalls.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.networkcalls.databinding.ItemTodoBinding
import com.example.networkcalls.entities.Todo

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

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
    }

    // Количество элементов в списке
    override fun getItemCount() = todos.size
}