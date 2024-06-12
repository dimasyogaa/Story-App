package com.yogadimas.storyapp.views.activity.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogadimas.storyapp.R
import com.yogadimas.storyapp.data.remote.model.ListStoryItem
import com.yogadimas.storyapp.databinding.ItemRowStoryBinding
import com.yogadimas.storyapp.views.activity.intrfaces.OnItemClickCallback

class StoryAdapter(
    private val listStory: List<ListStoryItem>,
    private val onItemClickCallback: OnItemClickCallback,
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        return StoryViewHolder(
            ItemRowStoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val getItem = listStory[position]
        val photo = getItem.photoUrl
        val name = getItem.name
        val desc = getItem.description


        Glide.with(holder.itemView.context)
            .load(photo)
            .apply(
                RequestOptions.placeholderOf(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
            )
            .into(holder.binding.ivPhoto)

        holder.binding.tvName.text = name
        holder.binding.tvDesc.text = desc



        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(getItem) }
    }

    class StoryViewHolder(var binding: ItemRowStoryBinding) : RecyclerView.ViewHolder(binding.root)
}