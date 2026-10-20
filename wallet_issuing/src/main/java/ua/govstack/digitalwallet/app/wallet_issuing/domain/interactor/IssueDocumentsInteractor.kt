package ua.govstack.digitalwallet.app.wallet_issuing.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ua.govstack.digitalwallet.app.core.util.extensions.safeAsync
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssuanceMethod
import ua.govstack.digitalwallet.app.wallet_issuing.data.repository.IssuingRepository
import ua.govstack.digitalwallet.app.wallet_issuing.data.transformer.toInteractorResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.GetAddDocumentOptionsResult
import ua.govstack.digitalwallet.app.wallet_issuing.domain.model.result.IssueDocumentsResult

interface IssueDocumentsInteractor {
    fun getAddDocumentOption(): Flow<GetAddDocumentOptionsResult>

    fun issueDocuments(
        issuanceMethod: IssuanceMethod,
        configIds: List<String>,
        issuerId: String
    ): Flow<IssueDocumentsResult>

    fun resumeOpenId4VciWithAuthorization(uri: String)
}

internal class IssueDocumentsInteractorImpl(
    private val issuingRepository: IssuingRepository,
) : IssueDocumentsInteractor {

    override fun getAddDocumentOption(): Flow<GetAddDocumentOptionsResult> =
        issuingRepository.getAddDocumentOption().map { it.toInteractorResult() }.safeAsync {
            GetAddDocumentOptionsResult.Failure(
                error = it.message.orEmpty()
            )
        }

    override fun issueDocuments(
        issuanceMethod: IssuanceMethod,
        configIds: List<String>,
        issuerId: String
    ): Flow<IssueDocumentsResult> =
        issuingRepository.issueDocuments(
            issuanceMethod = issuanceMethod,
            configIds = configIds,
            issuerId = issuerId
        ).map { it.toInteractorResult() }.safeAsync {
            IssueDocumentsResult.Failure(
                errorMessage = it.message.orEmpty()
            )
        }

    override fun resumeOpenId4VciWithAuthorization(uri: String) {
        issuingRepository.resumeOpenId4VciWithAuthorization(uri)
    }
}
