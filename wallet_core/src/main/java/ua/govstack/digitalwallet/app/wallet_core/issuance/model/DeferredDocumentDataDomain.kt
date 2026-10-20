package ua.govstack.digitalwallet.app.wallet_core.issuance.model

import eu.europa.ec.eudi.wallet.document.DocumentId
import ua.govstack.digitalwallet.app.wallet_core.documents.model.FormatType

data class DeferredDocumentDataDomain(
    val documentId: DocumentId,
    val formatType: FormatType,
    val docName: String,
)