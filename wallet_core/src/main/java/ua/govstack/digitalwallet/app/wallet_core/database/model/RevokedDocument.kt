package ua.govstack.digitalwallet.app.wallet_core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "revokedDocuments")
data class RevokedDocument(
    @PrimaryKey
    val identifier: String
)