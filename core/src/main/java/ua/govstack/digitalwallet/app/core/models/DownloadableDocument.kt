package ua.govstack.digitalwallet.app.core.models

interface DownloadableDocument {
    val id: String
    val docName: String
    val downloadUrl: String
}