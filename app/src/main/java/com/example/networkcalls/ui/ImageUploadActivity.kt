package com.example.networkcalls.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.toolbox.Volley
import com.example.networkcalls.databinding.ActivityImageUploadBinding
import com.example.networkcalls.network.*
import com.example.networkcalls.network.requests.FileDataPart
import com.example.networkcalls.network.requests.VolleyFileUploadRequest
import com.example.networkcalls.repositories.CarsRepository
import com.example.networkcalls.repositories.PostRepository
import com.example.networkcalls.utils.UploadUtility
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
import java.io.IOException

class ImageUploadActivity : UploadRequestBody.UploadCallback, AppCompatActivity() {

    private var selectedImageUri: Uri? = null

    // For Volley image upload
    private var imageData: ByteArray? = null
    private val uploadImageUrl: String = "https://cardiary.herokuapp.com/api/v1/cars/1/photos"

    private lateinit var viewModel: CarsViewModel

    private lateinit var binding: ActivityImageUploadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = CarsRepository()
        val viewModelFactory = CarsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CarsViewModel::class.java)

//        viewModel.getCars()
//        viewModel.carsResponse.observe(this, { response ->
//            Log.d("Headers", response.headers().toString())
//            response.body()?.forEach { car ->
//                Log.d("CarsActivity", car.toString())
//            }
//        })

        with(binding) {
            ivPicture.setOnClickListener {
                openImageChooser()
            }

            btnUpload.setOnClickListener {
                // Upload file - Retrofit
                // uploadImage()
                // Upload file - Volley
                // uploadImageVolley()
                // Upload file - HttpOk
                selectedImageUri?.let { imgUri ->
                    if(askForPermissions()) {
                        UploadUtility(this@ImageUploadActivity).uploadFile(imgUri)
                    }
                }
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PICK_IMAGE) {
//            val uri = data?.data
//            if (uri != null) {
//                binding.ivPicture.setImageURI(uri)
//                createImageData(uri)
//            }
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    private fun openImageChooser() {
        Intent(Intent.ACTION_PICK).also {
            it.type = "image/*"
//            val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
//            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            startActivityForResult(it, REQUEST_CODE_PICK_IMAGE)
        }
    }

    // Volley version of image upload function
    private fun uploadImageVolley() {
        imageData?: return
        val request = object : VolleyFileUploadRequest(
            Method.PATCH,
            uploadImageUrl,
            com.android.volley.Response.Listener {
                println("response is: $it")
            },
            com.android.volley.Response.ErrorListener {
                println("error is: $it")
            }
        ) {
            override fun getByteData(): Map<String, FileDataPart>? {
                var params = HashMap<String, FileDataPart>()
                params["photo"] = FileDataPart("image", imageData!!, "jpg")
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }

    @Throws(IOException::class)
    private fun createImageData(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        inputStream?.buffered()?.use {
            imageData = it.readBytes()
        }
    }

    // Upload image through Retrofit
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
        const val REQUEST_PERMISSIONS_CODE = 102
    }

    override fun onProgressUpdate(percentage: Int) {
        binding.pbUploadStatus.progress = percentage
    }

    // Permissions processing functions
    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this as Activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionDeniedDialog()
            } else {
                ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSIONS_CODE)
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Perform your operation here
                } else {
                    // permission is denied, you can ask for permission again, if you want
                    //  askForPermissions()
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings"
            ) { _, _ ->
                // send to app settings if permission is denied permanently
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel",null)
            .show()
    }
}