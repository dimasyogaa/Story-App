package com.yogadimas.storyapp.views.activity.upload

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.yogadimas.storyapp.R
import com.yogadimas.storyapp.databinding.ActivityUploadStoryBinding
import com.yogadimas.storyapp.utils.Result
import com.yogadimas.storyapp.utils.reduceFileImage
import com.yogadimas.storyapp.utils.rotateFile
import com.yogadimas.storyapp.utils.uriToFile
import com.yogadimas.storyapp.views.activity.upload.camera.CameraActivity
import com.yogadimas.storyapp.views.factory.ViewModelStoryFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class UploadStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding

    private var getFile: File? = null

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this, "Tidak mendapatkan permission.", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val viewModel: UploadStoryViewModel by viewModels {
        ViewModelStoryFactory.getInstance(applicationContext)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            toolbar.setNavigationOnClickListener { finish() }
            descEditText.setOnTouchListener(OnTouchListener { v, event ->
                if (descEditText.hasFocus()) {
                    v.parent.requestDisallowInterceptTouchEvent(true)
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_SCROLL -> {
                            v.parent.requestDisallowInterceptTouchEvent(false)
                            return@OnTouchListener true
                        }
                    }
                }
                false
            })

            btnCamera.setOnClickListener { startCameraX() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener {
                uploadImage()
            }

        }

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }


    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }


    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION") it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }


    private fun uploadImage() {
        if (getFile != null) {
            lifecycleScope.launch(Dispatchers.Main) {

                showLoading(true)

                withContext(Dispatchers.Default) {

                    val file = reduceFileImage(getFile as File)

                    withContext(Dispatchers.Main) {

                        val description =
                            binding.descEditText.text.toString()
                                .toRequestBody("text/plain".toMediaType())

                        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())

                        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                            "photo", file.name, requestImageFile
                        )


                        viewModel.setStory(description, imageMultipart)
                            .observe(this@UploadStoryActivity) {
                                when (it) {
                                    is Result.Loading,
                                    -> showLoading(true)

                                    is Result.Success -> {
                                        showLoading(false)
                                        val result = it.data
                                        if (result?.error != true) {
                                            finish()
                                        }
                                    }

                                    is Result.Error -> {
                                        showLoading(false)
                                        Toast.makeText(
                                            this@UploadStoryActivity,
                                            it.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }


                    }


                }
            }


        } else {
            Toast.makeText(
                this@UploadStoryActivity,
                getString(R.string.empty_image),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Pilih gambar")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@UploadStoryActivity)
                getFile = myFile
                binding.ivPreview.setImageURI(uri)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}