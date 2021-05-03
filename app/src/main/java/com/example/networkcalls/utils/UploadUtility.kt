package com.example.networkcalls.utils

import android.app.Activity
import android.app.ProgressDialog
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadUtility(activity: Activity) {

    var activity = activity
    var dialog: ProgressDialog? = null
    var serverURL: String = "https://cardiary.herokuapp.com/api/v1/cars/1/photos"
    val client = OkHttpClient()

    fun uploadFile(sourceFilePath: String, uploadFileName: String? = null) {
        uploadFile(File(sourceFilePath), uploadFileName)
    }

    fun uploadFile(sourceFileUri: Uri, uploadFileName: String? = null) {
        val pathFromUri = URIPathHelper().getPath(activity, sourceFileUri)
        uploadFile(File(pathFromUri), uploadFileName)
    }

    fun uploadFile(sourceFile: File, uploadFileName: String? = null) {
        Thread {
            val mimeType = getMimeType(sourceFile)
            if (mimeType == null) {
                Log.e("file error", "Not able to get mime type")
                return@Thread
            }
            val fileName: String = uploadFileName ?: sourceFile.name
            toggleProgressDialog(true)
            try {
                val requestBody: RequestBody =
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("photo", fileName, sourceFile.asRequestBody(mimeType.toMediaTypeOrNull()))
                        .build()

                val request: Request = Request.Builder().url(serverURL).patch(requestBody).build()

                val response: Response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    Log.d("File upload", "Success for uploading $fileName")
                    showToast("File $fileName uploaded successfully!")
                } else {
                    Log.e("File upload", "Failed uploading $fileName. Reason: ${response.message}")
                    showToast("Fail to upload $fileName")
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                Log.e("File upload", "failed")
                showToast("File uploading failed")
            }
            toggleProgressDialog(false)
        }.start()
    }

    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun showToast(message: String) {
        activity.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun toggleProgressDialog(show: Boolean) {
        activity.runOnUiThread {
            if (show) {
                dialog = ProgressDialog.show(activity, "", "Uploading file...", true)
            } else {
                dialog?.dismiss()
            }
        }
    }
}