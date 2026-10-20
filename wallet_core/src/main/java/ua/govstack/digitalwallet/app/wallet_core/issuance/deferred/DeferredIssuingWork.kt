package ua.govstack.digitalwallet.app.wallet_core.issuance.deferred

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import eu.europa.ec.eudi.wallet.document.DeferredDocument
import kotlinx.coroutines.flow.first
import ua.govstack.digitalwallet.app.wallet_core.issuance.model.IssueDeferredDocumentResult
import ua.govstack.digitalwallet.app.wallet_core.issuance.DocumentIssuanceManager
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import java.util.concurrent.TimeUnit

@HiltWorker
class DeferredIssuingWork @AssistedInject constructor(
    private val documentStore: DocumentStore,
    private val documentIssuanceManager: DocumentIssuanceManager,
    private val deferredIssuingResultDispatcher: DeferredIssuingResultDispatcher,
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        return runCatching {
            val deferredDocuments = documentStore
                .getAllDocuments()
                .filterIsInstance<DeferredDocument>()

            if (deferredDocuments.isEmpty()) {
                return Result.success()
            }

            deferredDocuments.forEach { document ->
                val result = documentIssuanceManager.issueDeferredDocument(document.id).first()
                when (result) {
                    is IssueDeferredDocumentResult.Expired -> {
                        documentStore.deleteDocument(result.documentId).first()
                    }

                    is IssueDeferredDocumentResult.Failed -> {
                        documentStore.deleteDocument(result.documentId).first()
                    }


                    is IssueDeferredDocumentResult.Issued,
                    is IssueDeferredDocumentResult.NotReady -> Unit
                }
                deferredIssuingResultDispatcher.dispatch(
                    context = applicationContext,
                    result = result
                )

                when (result) {
                    is IssueDeferredDocumentResult.Failed,
                    is IssueDeferredDocumentResult.Issued,
                    is IssueDeferredDocumentResult.Expired -> documentStore.refreshDocuments()
                    is IssueDeferredDocumentResult.NotReady -> Unit
                }
            }

            if (documentStore.getAllDocuments().any { it is DeferredDocument }) {
                enqueue(
                    workManager = WorkManager.getInstance(applicationContext),
                    delaySeconds = RETRY_DELAY_SECONDS
                )
            }

            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }

    companion object {
        const val DEFERRED_ISSUING_WORK_NAME = "deferredIssuingWork"
        private const val DEFAULT_DELAY_SECONDS = 60L
        private const val RETRY_DELAY_SECONDS = 60L

        fun enqueue(
            workManager: WorkManager,
            delaySeconds: Long = DEFAULT_DELAY_SECONDS,
        ) {
            val request = OneTimeWorkRequestBuilder<DeferredIssuingWork>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    DEFAULT_DELAY_SECONDS,
                    TimeUnit.SECONDS
                )
                .build()

            workManager.enqueueUniqueWork(
                DEFERRED_ISSUING_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }
}
