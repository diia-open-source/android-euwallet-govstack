package ua.govstack.digitalwallet.app.core.models.scanner

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScannedDocument(
    val scannedDocType: ScannedDocType,
    val docNumber: String,
    val birthDate: String? = null,
    val expirationDate: String? = null,
    val recordNumber: String? = null
) : Parcelable