package ua.govstack.digitalwallet.app.wallet_core.issuance.deferred

import android.content.Context
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssueDeferredDocumentResult

interface DeferredIssuingResultDispatcher {
    fun dispatch(
        context: Context,
        result: IssueDeferredDocumentResult,
    )
}

interface DeferredIssuingResultHandler {
    fun handle(
        context: Context,
        result: IssueDeferredDocumentResult,
    )
}

class DeferredIssuingResultDispatcherImpl(
    private val handlers: Set<@JvmSuppressWildcards DeferredIssuingResultHandler>,
) : DeferredIssuingResultDispatcher {
    override fun dispatch(
        context: Context,
        result: IssueDeferredDocumentResult,
    ) {
        handlers.forEach { it.handle(context, result) }
    }
}
