package com.example.client

import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import android.provider.MediaStore
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.OutputStream

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.client/save_image"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "saveImageToGallery") {
                val imagePath = call.argument<String>("imagePath")

                if (imagePath != null) {
                    val success = saveImageToGallery(imagePath)
                    if (success) {
                        result.success("Image saved successfully")
                    } else {
                        result.error("UNAVAILABLE", "Image could not be saved", null)
                    }
                } else {
                    result.error("ERROR", "Invalid image path", null)
                }
            } else {
                result.notImplemented()
            }
        }
    }

    private fun saveImageToGallery(imagePath: String): Boolean {
        val file = File(imagePath)
        return if (file.exists()) {
            try {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                }

                val resolver = applicationContext.contentResolver
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                if (imageUri != null) {
                    val outputStream: OutputStream? = resolver.openOutputStream(imageUri)
                    val inputStream = FileInputStream(file)

                    inputStream.copyTo(outputStream!!)

                    outputStream.close()
                    inputStream.close()

                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        } else {
            false
        }
    }
}
