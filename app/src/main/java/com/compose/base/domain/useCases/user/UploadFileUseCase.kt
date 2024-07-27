package com.compose.base.domain.useCases.user

import android.content.Context
import android.net.Uri
import com.compose.base.R
import com.compose.base.core.Constants
import com.compose.base.data.util.DataResponse
import com.compose.base.domain.repository.user.UserRepository
import com.compose.base.presentation.util.UiText
import com.compose.base.presentation.util.cacheExternalUri
import com.compose.base.presentation.util.convertToMultipartData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case class responsible for uploading a file to the server.
 *
 * This class interacts with the `UserRepository` to perform the upload and handles potential errors.
 * It also handles caching the uploaded file locally (if applicable) and extracting the image URL from the response.
 *
 * @param userRepository: The repository used to interact with the server for uploading files.
 * @param context: The application context used for accessing resources like cache directory.
 */
class UploadFileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context,
) {

    /**
     * Initiates the process to upload a file represented by the provided URI.
     *
     * @param uri: The URI of the file to be uploaded.
     * @return: A Flow of `DataResponse<String>` representing the upload result.
     *      - Success: Contains the uploaded image URL if available.
     *      - InProgress: Indicates the upload is ongoing.
     *      - Error: Indicates any errors encountered during upload or processing.
     */
    operator fun invoke(uri: Uri): Flow<DataResponse<String>> {
        val fileUri =
            // Check if URI scheme requires external storage caching
            if (uri.scheme == Constants.KEY_CONTENT) uri.cacheExternalUri(context) else uri

        // Convert the URI to a MultipartBody suitable for upload
        return fileUri?.convertToMultipartData()?.let { multipartBody ->
            userRepository.uploadFile(multipartBody).map { dataResponse ->
                when (dataResponse) {
                    is DataResponse.InProgress -> dataResponse

                    is DataResponse.Success -> {

                        // Clear and recreate cache directory
                        context.cacheDir.deleteRecursively()
                        context.cacheDir.mkdir()
                        val imageUrl = dataResponse.data.data?.image
                        if (!imageUrl.isNullOrEmpty()) {
                            // Success with extracted image URL
                            DataResponse.Success(imageUrl)
                        } else {
                            // Error: Invalid image URL in response
                            DataResponse.Error.Local(UiText.Resource(R.string.error_invalid_image_url))
                        }
                    }

                    is DataResponse.Error -> dataResponse // Pass through error response
                }
            }
        } ?: flowOf(DataResponse.Error.Local(UiText.Resource(R.string.error_file_not_found)))
    }
}