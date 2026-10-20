package ua.govstack.digitalwallet.app.wallet_issuing.data.transformer

import ua.govstack.digitalwallet.app.wallet_issuing.data.repository.GetAddDocumentOptionsRepositoryResult
import ua.govstack.digitalwallet.app.wallet_issuing.data.repository.IssueDocumentOfferRepositoryResult
import ua.govstack.digitalwallet.app.wallet_issuing.data.repository.IssueDocumentsRepositoryResult
import ua.govstack.digitalwallet.app.wallet_issuing.data.repository.ResolveDocumentOfferRepositoryResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.DocumentOffer
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.GetAddDocumentOptionsResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.IssueDocumentOfferResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.IssueDocumentsResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.ResolveDocumentOfferResult
import java.util.Locale

internal fun GetAddDocumentOptionsRepositoryResult.toInteractorResult(): GetAddDocumentOptionsResult =
    when (this) {
        is GetAddDocumentOptionsRepositoryResult.Failure -> {
            GetAddDocumentOptionsResult.Failure(error)
        }

        is GetAddDocumentOptionsRepositoryResult.NoOptions -> {
            GetAddDocumentOptionsResult.NoOptions(errorMsg)
        }

        is GetAddDocumentOptionsRepositoryResult.Success -> {
            GetAddDocumentOptionsResult.Success(options)
        }
    }

internal fun IssueDocumentsRepositoryResult.toInteractorResult(): IssueDocumentsResult =
    when (this) {
        is IssueDocumentsRepositoryResult.DeferredSuccess -> {
            IssueDocumentsResult.DeferredSuccess
        }

        is IssueDocumentsRepositoryResult.Failure -> {
            IssueDocumentsResult.Failure(errorMessage)
        }

        is IssueDocumentsRepositoryResult.Success -> {
            IssueDocumentsResult.Success(documentIds)
        }
    }

internal fun ResolveDocumentOfferRepositoryResult.toInteractorResult(
    userLocale: Locale
): ResolveDocumentOfferResult =
    when (this) {
        is ResolveDocumentOfferRepositoryResult.UntrustedIssuer -> {
            ResolveDocumentOfferResult.UntrustedIssuer(issuerId)
        }

        is ResolveDocumentOfferRepositoryResult.Failure -> {
            ResolveDocumentOfferResult.Failure(errorMessage)
        }

        is ResolveDocumentOfferRepositoryResult.Success -> {
            val issuer = offer.getIssuerUrl()
            ResolveDocumentOfferResult.Success(
                documents = offer.offeredDocuments.map {
                    DocumentOffer(
                        title = it.getName(userLocale).orEmpty(),
                        issuer = issuer
                    )
                },
                issuerName = offer.getIssuerName(userLocale),
                issuerLogo = offer.getIssuerLogo(userLocale),
                txCodeLength = offer.txCodeSpec?.length
            )
        }

        is ResolveDocumentOfferRepositoryResult.NoDocument -> {
            ResolveDocumentOfferResult.NoDocument(
                issuerName = issuerName,
                issuerLogo = issuerLogo
            )
        }

        is ResolveDocumentOfferRepositoryResult.PIDRequired -> {
            ResolveDocumentOfferResult.PIDRequired
        }
    }

internal fun IssueDocumentOfferRepositoryResult.toInteractorResult(): IssueDocumentOfferResult =
    when (this) {
        is IssueDocumentOfferRepositoryResult.Failure -> {
            IssueDocumentOfferResult.Failure(errorMessage = errorMessage)
        }

        is IssueDocumentOfferRepositoryResult.Success -> {
            IssueDocumentOfferResult.Success(documentIds = documentIds)
        }

        is IssueDocumentOfferRepositoryResult.DeferredSuccess -> {
            IssueDocumentOfferResult.DeferredSuccess
        }

        is IssueDocumentOfferRepositoryResult.AlreadyExists -> {
            IssueDocumentOfferResult.AlreadyExists(type)
        }
    }
