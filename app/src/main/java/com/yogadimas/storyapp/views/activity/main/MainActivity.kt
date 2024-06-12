package com.yogadimas.storyapp.views.activity.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yogadimas.storyapp.R
import com.yogadimas.storyapp.data.remote.model.ListStoryItem
import com.yogadimas.storyapp.databinding.ActivityMainBinding
import com.yogadimas.storyapp.utils.Result
import com.yogadimas.storyapp.views.activity.adapter.StoryAdapter
import com.yogadimas.storyapp.views.activity.detail.DetailActivity
import com.yogadimas.storyapp.views.activity.intrfaces.OnItemClickCallback
import com.yogadimas.storyapp.views.activity.login.LoginActivity
import com.yogadimas.storyapp.views.activity.login.LoginViewModel
import com.yogadimas.storyapp.views.activity.upload.UploadStoryActivity
import com.yogadimas.storyapp.views.factory.ViewModelAuthFactory
import com.yogadimas.storyapp.views.factory.ViewModelStoryFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private val viewModel: MainViewModel by viewModels {
        ViewModelStoryFactory.getInstance(applicationContext)
    }

    private val viewModelLogin: LoginViewModel by viewModels {
        ViewModelAuthFactory.getInstance(applicationContext)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            toolbar.title = getString(R.string.app_name)
            toolbar.inflateMenu(R.menu.main_menu)

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {

                    R.id.action_camera -> {
                        startActivity(Intent(this@MainActivity, UploadStoryActivity::class.java))
                    }

                    R.id.action_language -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    }

                    R.id.action_logout -> {
                        showAlertDialog()
                    }
                }
                false
            }
        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.getStories().observe(this) {
            when (it) {

                is Result.Loading -> showLoading(true)

                is Result.Success -> {
                    showLoading(false)
                    val result = it.data
                    if (result?.error != true) {
                        val listStory = result?.listStory.orEmpty()
                        if (listStory.isNotEmpty()) {
                            populateData(listStory)
                        }
                    }

                }

                is Result.Error -> {
                    showLoading(false)
                    Toast.makeText(
                        this@MainActivity,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()


                }
            }
        }
    }

    private fun populateData(listStory: List<ListStoryItem>) {
        val storyAdapter = StoryAdapter(listStory, object : OnItemClickCallback {
            override fun onItemClicked(data: ListStoryItem) {
                val story = ListStoryItem(
                    photoUrl = data.photoUrl,
                    name = data.name,
                    description = data.description
                )
                val intent = Intent(this@MainActivity, DetailActivity::class.java)
                intent.putExtra(DetailActivity.EXTRA_DETAIL, story)
                startActivity(intent)
            }


        })
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = storyAdapter
        }
    }

    private fun showAlertDialog() {
        MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setIcon(ContextCompat.getDrawable(this, R.drawable.ic_logout))
            .setTitle(getString(R.string.logout))
            .setMessage(getString(R.string.dialog_logout))
            .setNegativeButton(getString(R.string.no)) { _, _ ->
                return@setNegativeButton
            }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModelLogin.getAuthToken().observe(this) {
                    viewModelLogin.clearAuthToken()
                    if (it == null) {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
            .show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}