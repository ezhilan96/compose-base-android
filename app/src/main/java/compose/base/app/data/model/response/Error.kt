package compose.base.app.data.model.response

import compose.base.app.config.Constants.DEFAULT_INT


data class ErrorResponse(
    var error: Error = Error()
)

data class Error(
    var statusCode: Int = DEFAULT_INT,
    var name: String = "Unknown error",
    var message: String = "Something went wrong!"
)