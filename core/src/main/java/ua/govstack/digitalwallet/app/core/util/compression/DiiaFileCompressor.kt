package ua.govstack.digitalwallet.app.core.util.compression

import android.net.Uri
import java.io.File

interface DiiaFileCompressor {

    suspend fun compressPicture(
        pictureUri: Uri,
        maxSize: Long,
        verifyForBase64: Boolean
    ): File

}