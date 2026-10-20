package ua.govstack.digitalwallet.app.wallet_issuing.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.govstack.digitalwallet.app.core.util.extensions.safeAsync
import ua.govstack.digitalwallet.app.wallet_issuing.data.repository.IssuingRepository
import ua.govstack.digitalwallet.app.wallet_issuing.data.transformer.toInteractorResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.IssueDocumentOfferResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.ResolveDocumentOfferResult
import java.util.Locale

interface DocumentOfferInteractor {
    fun resolveDocumentOffer(offerUri: String): Flow<ResolveDocumentOfferResult>

    fun issueDocumentsOffer(
        offerUri: String,
        txCode: String? = null
    ): Flow<IssueDocumentOfferResult>

    fun resumeOpenId4VciWithAuthorization(uri: String)
}

internal class DocumentOfferInteractorImpl(
    private val issuingRepository: IssuingRepository,
) : DocumentOfferInteractor {

    override fun resolveDocumentOffer(offerUri: String): Flow<ResolveDocumentOfferResult> =
        issuingRepository.resolveDocumentOffer(offerUri)
            .map { it.toInteractorResult(Locale.getDefault()) }
            .safeAsync {
                ResolveDocumentOfferResult.Failure(it.message.orEmpty())
            }

    override fun issueDocumentsOffer(
        offerUri: String,
        txCode: String?
    ): Flow<IssueDocumentOfferResult> =
        issuingRepository.issueDocumentsByOffer(offerUri, txCode).map { it.toInteractorResult() }

    override fun resumeOpenId4VciWithAuthorization(uri: String) {
        issuingRepository.resumeOpenId4VciWithAuthorization(uri)
    }
}
