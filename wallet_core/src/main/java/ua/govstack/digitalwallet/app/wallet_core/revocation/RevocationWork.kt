package ua.govstack.digitalwallet.app.wallet_core.revocation

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import eu.europa.ec.eudi.statium.Status
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import ua.govstack.digitalwallet.app.wallet_core.database.dao.RevokedDocumentDao
import ua.govstack.digitalwallet.app.wallet_core.database.model.RevokedDocument
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStatusStore
import ua.govstack.digitalwallet.app.wallet_core.documents.store.DocumentStore
import ua.govstack.digitalwallet.app.wallet_core.revocation.model.RevokedDocumentDataDomain
import java.util.concurrent.TimeUnit

@HiltWorker
class RevocationWork @AssistedInject constructor(
    private val revokedDocumentDao: RevokedDocumentDao,
    private val documentStore: DocumentStore,
    private val documentStatusStore: DocumentStatusStore,
    @Assisted context: Context,
    @Assisted workParams: WorkerParameters,
) : CoroutineWorker(context, workParams) {

    companion object {
        const val REVOCATION_WORK_NAME = "revocationWorker"
        const val REVOCATION_WORK_REFRESH_ACTION = "revocation.refresh.action"
        const val REVOCATION_WORK_REFRESH_DETAILS_ACTION = "revocation.refresh_details.action"
        const val REVOCATION_WORK_MESSAGE_ACTION = "revocation.message.action"
        const val REVOCATION_IDS_EXTRA = "revocation.ids.extra"
        const val REVOCATION_IDS_DETAILS_EXTRA = "revocation.ids_details.extra"

        fun enqueue(
            workManager: WorkManager
        ) {
            workManager.enqueueUniquePeriodicWork(
                REVOCATION_WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                PeriodicWorkRequestBuilder<RevocationWork>(15, TimeUnit.MINUTES)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        5L,
                        TimeUnit.MILLISECONDS
                    )
                    .build()
            )
        }

    }

    override suspend fun doWork(): Result {
        try {
            Log.d("RevocationWorkManager", "doWork: RevocationWorkManager")
            val storedRevokedDocuments = documentStatusStore.getRevokedDocumentIds()
            val fromRevokedToValid = mutableListOf<String>()
            val revokedDocuments = mutableListOf<IssuedDocument>()

            documentStore
                .getAllIssuedDocuments()
                .forEach { document ->
                    documentStatusStore.resolveDocumentStatus(document).fold(
                        onSuccess = { status ->
                            Log.d(
                                "RevocationWorkManager",
                                "onSuccess: ${status}, id = ${document.id}"
                            )
                            when (status) {
                                is Status.Invalid, Status.Suspended -> {
                                    if (!storedRevokedDocuments.any { it == document.id }) {
                                        revokedDocuments.add(document)
                                    }
                                }

                                is Status.Valid -> {
                                    if (storedRevokedDocuments.any { it == document.id }) {
                                        fromRevokedToValid.add(document.id)
                                    }
                                }

                                else -> {
                                }
                            }
                        },
                        onFailure = { exception ->
                            Log.d("TAG", "Document status resolving failure ${exception.message}")
                        }
                    )
                }

            if (fromRevokedToValid.isNotEmpty()) {
                removeRevokedDocumentsFromStorage(fromRevokedToValid)
            }

            if (revokedDocuments.isNotEmpty()) {
                storeRevokedDocuments(revokedDocuments)
                sendRevocationBroadcasts(revokedDocuments)
            }

            if (fromRevokedToValid.isNotEmpty() || revokedDocuments.isNotEmpty()) {
                notifyDocumentsList()
            }

            return Result.success()
        } catch (_: IllegalArgumentException) {
            return Result.failure()
        }
    }

    @Throws(IllegalArgumentException::class)
    private suspend fun storeRevokedDocuments(revokedDocuments: List<IssuedDocument>) {
        revokedDocumentDao.storeAll(
            revokedDocuments.map { RevokedDocument(identifier = it.id) }
        )
    }

    @Throws(IllegalArgumentException::class)
    private suspend fun removeRevokedDocumentsFromStorage(ids: List<String>) {
        ids.forEach {
            revokedDocumentDao.delete(it)
        }
    }

    private fun sendRevocationBroadcasts(revokedDocuments: List<IssuedDocument>) {

        val messageIntent = Intent(REVOCATION_WORK_MESSAGE_ACTION).apply {
            putParcelableArrayListExtra(
                REVOCATION_IDS_EXTRA,
                ArrayList(
                    revokedDocuments.map {
                        RevokedDocumentDataDomain(name = it.name, id = it.id)
                    }
                )
            )
        }

        val detailsIntent = Intent(REVOCATION_WORK_REFRESH_DETAILS_ACTION).apply {
            putStringArrayListExtra(
                REVOCATION_IDS_DETAILS_EXTRA,
                ArrayList(
                    revokedDocuments.map { it.id }
                )
            )
        }

        applicationContext.sendBroadcast(messageIntent)
        applicationContext.sendBroadcast(detailsIntent)
    }

    private fun notifyDocumentsList() {
        val refreshIntent = Intent(REVOCATION_WORK_REFRESH_ACTION)
        applicationContext.sendBroadcast(refreshIntent)
    }
}