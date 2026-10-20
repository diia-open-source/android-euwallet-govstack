package ua.govstack.digitalwallet.app.core.models.deeplink

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DeeplinkOpenFlowParams(
    val flowId: String,
    val resourceId: String,
    val resourceSubtypeId: String?
) : Parcelable