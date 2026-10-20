package ua.govstack.digitalwallet.app.core.util.service.decoder.media_item

import android.net.Uri
import ua.govstack.digitalwallet.app.core.models.media.MediaItem

interface MediaServiceDecoder {

    suspend fun decode(item: MediaItem, maxFileSize: Long?): Uri?
}