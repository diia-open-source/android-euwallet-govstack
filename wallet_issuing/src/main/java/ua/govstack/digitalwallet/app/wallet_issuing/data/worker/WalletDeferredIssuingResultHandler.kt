package ua.govstack.digitalwallet.app.wallet_issuing.data.worker

import android.content.Context
import android.content.Intent
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssueDeferredDocumentResult
import ua.govstack.digitalwallet.app.wallet_core.issuance.deferred.DeferredIssuingResultHandler
import ua.govstack.digitalwallet.app.wallet_issuing.util.IssuingActions
import javax.inject.Inject

class WalletDeferredIssuingResultHandler @Inject constructor() : DeferredIssuingResultHandler {
    override fun handle(
        context: Context,
        result: IssueDeferredDocumentResult,
    ) {
        val intent = Intent(IssuingActions.DEFERRED_ISSUING_RESULT_ACTION)
            .setPackage(context.packageName)

        when (result) {
            is IssueDeferredDocumentResult.Issued -> {
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_STATUS_EXTRA,
                    IssuingActions.DEFERRED_ISSUING_STATUS_ISSUED
                )
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_DOC_ID_EXTRA,
                    result.deferredDocumentData.documentId
                )
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_DOC_NAME_EXTRA,
                    result.deferredDocumentData.docName
                )
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_DOC_TYPE_EXTRA,
                    result.deferredDocumentData.formatType
                )
            }

            is IssueDeferredDocumentResult.NotReady -> {
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_STATUS_EXTRA,
                    IssuingActions.DEFERRED_ISSUING_STATUS_NOT_READY
                )
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_DOC_ID_EXTRA,
                    result.deferredDocumentData.documentId
                )
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_DOC_NAME_EXTRA,
                    result.deferredDocumentData.docName
                )
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_DOC_TYPE_EXTRA,
                    result.deferredDocumentData.formatType
                )
            }

            is IssueDeferredDocumentResult.Failed -> {
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_STATUS_EXTRA,
                    IssuingActions.DEFERRED_ISSUING_STATUS_FAILED
                )
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_DOC_ID_EXTRA,
                    result.documentId
                )
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_ERROR_EXTRA,
                    result.errorMessage
                )
            }

            is IssueDeferredDocumentResult.Expired -> {
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_STATUS_EXTRA,
                    IssuingActions.DEFERRED_ISSUING_STATUS_EXPIRED
                )
                intent.putExtra(
                    IssuingActions.DEFERRED_ISSUING_RESULT_DOC_ID_EXTRA,
                    result.documentId
                )
            }
        }

        context.sendBroadcast(intent)
    }
}
