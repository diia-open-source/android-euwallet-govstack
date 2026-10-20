package ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer

import eu.europa.ec.eudi.wallet.document.IssuedDocument
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.getIssuerUrl
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.toClaimPaths
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.transformPathsToDomainClaims
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentDetailsDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.toDocumentIdentifier
import ua.govstack.digitalwallet.app.wallet_core.utils.formatInstant
import java.time.Instant

object DocumentDetailsTransformer {

    suspend fun transformToDocumentDetailsDomain(
        document: IssuedDocument,
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider
    ): Result<DocumentDetailsDomain> = runCatching {
        val claimsPaths = document.data.claims.flatMap { claim ->
            claim.toClaimPaths()
        }

        val domainClaims = transformPathsToDomainClaims(
            paths = claimsPaths,
            claims = document.data.claims,
            resourceProvider = resourceProvider,
            uuidProvider = uuidProvider
        )

        val validUntil = document.getValidUntil().getOrNull()
        val docHasExpired = document.findCredential(Instant.now()) == null

        return@runCatching DocumentDetailsDomain(
            docName = document.name,
            docId = document.id,
            documentIdentifier = document.toDocumentIdentifier(),
            issuerUrl = document.getIssuerUrl(),
            documentExpirationDateFormatted = validUntil?.formatInstant(),
            documentHasExpired = docHasExpired,
            documentClaims = domainClaims
        )
    }
}
