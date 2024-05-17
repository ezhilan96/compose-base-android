package com.compose.base.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import com.compose.base.core.Constants
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

val File.size get() = if (!exists()) 0.0 else length().toDouble()
val File.sizeInKb get() = size / 1024
val File.sizeInMb get() = sizeInKb / 1024
val File.sizeInGb get() = sizeInMb / 1024
val File.sizeInTb get() = sizeInGb / 1024

fun File.compressFile(maxSizeInBytes: Int = Constants.TWO_MB) {
    if (length() > maxSizeInBytes) {
        var streamLength = maxSizeInBytes
        var compressQuality = 100
        val arrayOutputStream = ByteArrayOutputStream()
        while (streamLength >= maxSizeInBytes && compressQuality > 5) {
            arrayOutputStream.use {
                it.flush()
                it.reset()
            }
            compressQuality -= 5
            val bitmap = BitmapFactory.decodeFile(absolutePath, BitmapFactory.Options())
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, arrayOutputStream)
            val bmpPicByteArray = arrayOutputStream.toByteArray()
            streamLength = bmpPicByteArray.size
        }
        FileOutputStream(this).use {
            it.write(arrayOutputStream.toByteArray())
        }
    }
}

fun Uri.getFileExtension(context: Context): String? {
    val mimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(context.contentResolver.getType(this))
}

fun Uri.cacheExternalUri(context: Context): Uri? {
    return try {
        val inputStream = context.contentResolver.openInputStream(this)
        val bufferedInputStream = BufferedInputStream(inputStream)
        val outputFile = File(
            context.cacheDir, "${System.currentTimeMillis()}.${this.getFileExtension(context)}"
        )
        val bufferedOutputStream = BufferedOutputStream(FileOutputStream(outputFile))
        val buffer = ByteArray(1024 * 4)
        var readBytes: Int
        while (true) {
            readBytes = bufferedInputStream.read(buffer)
            if (readBytes == -1) {
                bufferedOutputStream.flush()
                break
            }
            bufferedOutputStream.write(buffer)
            bufferedOutputStream.flush()
        }
        inputStream?.close()
        bufferedInputStream.close()
        bufferedOutputStream.close()
        Uri.fromFile(outputFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Uri.convertToMultipartData(): MultipartBody.Part? {
    val file = path?.let { File(it) } ?: return null
    if (file.length() > Constants.TWO_MB) file.compressFile()
    val requestFile = file.asRequestBody(Constants.JSON_MULTIPART.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(Constants.JSON_IMAGE, file.name, requestFile)
}