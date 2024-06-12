package com.yogadimas.storyapp.views.activity.detail

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yogadimas.storyapp.R
import com.yogadimas.storyapp.data.remote.model.ListStoryItem
import com.yogadimas.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val story: ListStoryItem? = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra(EXTRA_DETAIL, ListStoryItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_DETAIL)
        }
        if (story != null) {
            binding.apply {
                toolbar.title = "Detail"
                toolbar.setNavigationOnClickListener { finish() }


                Glide.with(this@DetailActivity)
                    .load(story.photoUrl)
                    .apply(
                        RequestOptions.placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                    )
                    .into(ivPhoto)

                tvName.text = story.name
                tvDesc.text = story.description

            }
        }
    }

    companion object {
        const val EXTRA_DETAIL = "extra_detail"
    }
}