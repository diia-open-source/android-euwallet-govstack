package ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer

import ua.govstack.digitalwallet.app.core.models.document.DocumentCard
import ua.govstack.digitalwallet.app.core.models.document.DocumentWithMetadata

fun List<DocumentWithMetadata>.filterValid(): List<DocumentWithMetadata> {
    return filter { it.document != null }
}
fun List<DocumentWithMetadata>.toDocumentCards(): List<DocumentCard> {
    return this
        .sortedBy { it.getDocOrder() }
        .map { createSingleCard(it) }
}

fun createSingleCard(doc: DocumentWithMetadata): DocumentCard {
    return DocumentCard(
        doc = doc.copy(
            document = doc.document?.makeCopy(),
            status = doc.status
        ),
    )
}