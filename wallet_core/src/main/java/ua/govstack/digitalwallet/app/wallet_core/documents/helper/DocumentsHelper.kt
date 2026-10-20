package ua.govstack.digitalwallet.app.wallet_core.documents.helper

import androidx.fragment.app.Fragment
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.ManualDocs

interface DocumentsHelper {

    fun navigateToAddDocument(fragment: Fragment)

    fun navigateToDocActions(
        fragment: Fragment,
        doc: Document,
        position: Int,
        manualDocs: ManualDocs?
    )

    fun navigateToDocsOrder(fragment: Fragment)

    fun navigateToPinInput(fragment: Fragment, verification: Boolean)

    fun navigateToQrScan(fragment: Fragment)
}