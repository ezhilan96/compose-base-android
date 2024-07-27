package com.compose.base.presentation.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.webkit.MimeTypeMap
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
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

/**
 * Compresses an image file to a target size in bytes.
 *
 * This function attempts to compress a JPEG image file by reducing its quality until it
 * reaches a specified target size. It utilizes a `do-while` loop to iteratively compress the
 * image and check its size. The image quality is reduced by 10% in each iteration.
 *
 * If the compression fails to achieve the target size or an exception occurs, the original
 * file remains unchanged.
 *
 * @param targetSizeInBytes The desired size of the compressed image in bytes (default: 2MB).
 */
fun File.compressImage(targetSizeInBytes: Int = Constants.TWO_MB) {
    try {
        // Decode the image file into a Bitmap and return if it's null
        val bitmap = BitmapFactory.decodeFile(absolutePath, BitmapFactory.Options()) ?: return
        var outputBytes: ByteArray
        var imageQuality = 100
        val outputStream = ByteArrayOutputStream()
        do {
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, outputStream)
            outputBytes = outputStream.toByteArray()
            // Reduce the quality by 10% for the next iteration
            imageQuality -= (imageQuality * 0.1).toInt()
        } while (
        // Check if the compressed image size is greater than the target size
        // and the image quality is greater than 5
            outputBytes.size > targetSizeInBytes && imageQuality > 5
        )
        outputStream.close()
        bitmap.recycle()
        writeBytes(outputBytes)
    } catch (e: Exception) {
        Firebase.crashlytics.recordException(e)
    }
}

/**
 * Retrieves the file extension from a Uri (if available).
 *
 * This function utilizes the MimeTypeMap to determine the file extension associated with a Uri.
 * It first gets a MimeTypeMap instance (singleton) and then attempts to retrieve the MIME type
 * for the Uri using the context's ContentResolver. Finally, it uses the MIME type to get the
 * corresponding file extension from the MimeTypeMap.
 *
 * If the MIME type cannot be determined or there's no corresponding extension, the function
 * returns null.
 *
 * @receiver The Uri to retrieve the file extension from.
 * @param context The application context required to access the ContentResolver.
 * @return The file extension associated with the Uri (or null if not available).
 */
fun Uri.getFileExtension(context: Context): String? {
    val mimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(context.contentResolver.getType(this))
}

/**
 * Caches the content of an external Uri to the app's cache directory.
 *
 * This function attempts to cache the content of a provided Uri to a file in the application's
 * cache directory. It performs the following steps:
 *  1. Opens an InputStream for the Uri using the context's ContentResolver.
 *  2. Creates a new file in the cache directory with a unique name based on current timestamp
 *     and the original file extension (obtained using getFileExtension).
 *  3. Opens a BufferedOutputStream for the newly created file.
 *  4. Uses a loop to read data from the original Uri in chunks using a buffer, write it to the
 *     cache file, and flush the output stream.
 *  5. Closes all input and output streams.
 *  6. If successful, returns a Uri for the cached file. Otherwise, logs the exception with
 *     Firebase Crashlytics and returns null.
 *
 * **Important Notes:**
 *  - This function uses the cache directory, which means the cached files might be deleted
 *    by the system to reclaim storage space.
 *  - Consider permission handling for accessing the external Uri's content.
 *
 * @receiver The Uri to be cached.
 * @param context The application context required for accessing content resolver and cache directory.
 * @return A Uri for the cached file on success, null on failure.
 *
 * @see Uri.getFileExtension
 */
fun Uri.cacheExternalUri(context: Context): Uri? {
    return try {

        // Open an InputStream for the Uri using the context's ContentResolver
        val inputStream = context.contentResolver
            .openInputStream(this)
        val bufferedInputStream = BufferedInputStream(inputStream)

        // Create a new file in the cache directory with a unique name based on current timestamp
        // and the original file extension
        val outputFile = File(
            context.cacheDir,
            "${System.currentTimeMillis()}.${this.getFileExtension(context)}"
        )

        // Open a BufferedOutputStream for the newly created file
        val bufferedOutputStream = BufferedOutputStream(FileOutputStream(outputFile))
        val buffer = ByteArray(1024 * 4)// 4KB buffer
        var readBytes: Int

        // Read data from the original Uri in chunks using a buffer, write it to the cache file,
        // and flush the output stream.
        while (true) {
            readBytes = bufferedInputStream.read(buffer)
            if (readBytes == -1) {
                bufferedOutputStream.flush()
                break
            }
            bufferedOutputStream.write(buffer)
            bufferedOutputStream.flush()
        }

        // Close all input and output streams.
        inputStream?.close()
        bufferedInputStream.close()
        bufferedOutputStream.close()

        Uri.fromFile(outputFile)
    } catch (e: Exception) {
        Firebase.crashlytics.recordException(e)
        null
    }
}

/**
 * Converts a Uri to a MultipartBody.Part suitable for uploading an image.
 *
 * This function attempts to convert a Uri representing an image to a MultipartBody.Part
 * which can be used for uploading the image data in a multipart request. It performs the following:
 *  1. Extracts the file path from the Uri (if available).
 *  2. Checks if the file size exceeds a threshold (defined in Constants.TWO_MB).
 *  3. If the file size is too large, it attempts to compress the image using the compressImage() function
 *     (assuming it's defined elsewhere).
 *  4. Creates a RequestBody for the image file using the file itself and a media type [Constants.JSON_MULTIPART].
 *  5. Creates a MultipartBody.Part with the following details:
 *     - name: [Constants.JSON_IMAGE]
 *     - filename: original filename of the image file
 *     - body: the created RequestBody for the image data
 *  6. If the Uri path is unavailable or there are errors, the function throws an exception.
 *
 * @return A MultipartBody.Part for uploading the image data.
 *
 * @see compressImage
 * @see Constants.TWO_MB
 * @see Constants.JSON_IMAGE
 */
fun Uri.convertToMultipartData(): MultipartBody.Part? {
    // Extract the file path from the Uri
    val file = path?.let { File(it) } ?: return null

    // Check if the file size exceeds a threshold
    if (file.length() > Constants.TWO_MB) {
        file.compressImage()
    }

    // Create a RequestBody for the image file using the file itself and a media type derived from the file extension
    val requestFile = file.asRequestBody(Constants.JSON_MULTIPART.toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(Constants.JSON_IMAGE, file.name, requestFile)
}