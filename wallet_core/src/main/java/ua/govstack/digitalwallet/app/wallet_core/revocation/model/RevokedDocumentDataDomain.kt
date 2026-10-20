package ua.govstack.digitalwallet.app.wallet_core.revocation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RevokedDocumentDataDomain(
    val name: String,
    val id: String,
) : Parcelable