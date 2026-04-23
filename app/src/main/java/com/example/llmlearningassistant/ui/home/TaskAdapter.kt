package com.example.llmlearningassistant.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.llmlearningassistant.data.LearningTask
import com.example.llmlearningassistant.databinding.ItemTaskCardBinding

class TaskAdapter(
    private val tasks: List<LearningTask>,
    private val onClick: (LearningTask) -> Unit
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    inner class VH(val b: ItemTaskCardBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemTaskCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return VH(b)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val task = tasks[position]
        holder.b.tvTitle.text = task.title
        holder.b.tvDescription.text = task.description
        holder.b.root.contentDescription = "Open ${task.title}"
        holder.b.root.setOnClickListener { onClick(task) }
    }
}
