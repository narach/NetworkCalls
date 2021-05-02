package com.example.networkcalls.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.networkcalls.databinding.ActivityImageUploadBinding
import com.example.networkcalls.network.*
import com.example.networkcalls.repositories.CarsRepository
import com.example.networkcalls.repositories.PostRepository
import com.example.networkcalls.viewmodels.CarsViewModel
import com.example.networkcalls.viewmodels.PostsViewModel
import com.example.networkcalls.viewmodels.factories.CarsViewModelFactory
import com.example.networkcalls.viewmodels.factories.PostsViewModelFactory
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ImageUploadActivity : UploadRequestBody.UploadCallback, AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    private lateinit var viewModel: CarsViewModel

    private lateinit var binding: ActivityImageUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = CarsRepository()
        val viewModelFactory = CarsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CarsViewModel::class.java)

        viewModel.getCars()
        viewModel.carsResponse.observe(this, { response ->
            Log.d("Headers", response.headers().toString())
            response.body()?.forEach { car ->
                Log.d("CarsActivity", car.toString())
            }
        })

        with(binding) {
            ivPicture.setOnClickListener {
                openImageChooser()
            }

            btnUpload.setOnClickListener {
                uploadImage()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_PICK_IMAGE -> {
                    selectedImageUri = data?.data
                    binding.ivPicture.setImageURI(selectedImageUri)
                }
            }
        }
    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
            val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }

    private fun uploadImage() {
        if (selectedImageUri == null) {
            binding.layoutRoot.snackbar("Select an Image First")
            return
        }

        val parcelFileDescriptor =
            contentResolver.openFileDescriptor(selectedImageUri!!, "r", null) ?: return

        val inputStream = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val file = File(cacheDir, contentResolver.getFileName(selectedImageUri!!))
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        binding.pbUploadStatus.progress = 0
//        val body = UploadRequestBody(file, "multipart/form-data", this)
        val body = UploadRequestBody(file, "photo", this)
        UploadPhotoApi().uploadImage(
            MultipartBody.Part.createFormData(
                "photo",
                file.name,
                body
            )
        ).enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                if(response.code() != 200) {
                    binding.layoutRoot.snackbar("Failed when uploading file: ${response.errorBody()}")
                    Log.e("ImageUpload", response.toString())
                } else {
                    response.body()?.let {
                        binding.layoutRoot.snackbar("File was upload successfully!")
                        binding.pbUploadStatus.progress = 100
                    }
                }
            }

            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                binding.layoutRoot.snackbar(t.message!!)
                binding.pbUploadStatus.progress = 0
            }

        })
    }

    companion object {
        const val REQUEST_CODE_PICK_IMAGE = 101
    }

    override fun onProgressUpdate(percentage: Int) {
        binding.pbUploadStatus.progress = percentage
    }
}