package ua.govstack.digitalwallet.app.opensource.helper

import androidx.fragment.app.Fragment
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.models.document.ManualDocs
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.currentDestinationId
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.findNavControllerById
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigateOnce
import ua.govstack.digitalwallet.app.opensource.NavMainDirections
import ua.govstack.digitalwallet.app.opensource.R
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.DocumentsHelper
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.DocsConst
import ua.govstack.digitalwallet.app.wallet_gallery.NavDocActionsDirections
import ua.govstack.digitalwallet.app.wallet_gallery.NavStackOrderDirections

class DocumentsHelperImpl: DocumentsHelper {

    override fun navigateToAddDocument(fragment: Fragment) {
        fragment.navigate(
            NavMainDirections.actionHomeFToAddDocument(),
            fragment.findNavControllerById(R.id.nav_host)
        )
    }

    override fun navigateToQrScan(fragment: Fragment) {
        fragment.navigate(
            NavMainDirections.actionGlobalToQrScanF(),
            fragment.findNavControllerById(R.id.nav_host)
        )
    }

    override fun navigateToDocActions(
        fragment: Fragment,
        doc: Document,
        position: Int,
        manualDocs: ManualDocs?
    ) {
        fragment.apply {
            navigateOnce(
                destination = NavDocActionsDirections.actionGlobalDestinationDocActionsCompose(
                    doc = doc,
                    position = position,
                    enableStackActions = false,
                    currentlyDisplayedOdcTypes = DocsConst.DOCUMENT_TYPE_ALL,
                    manualDocs = manualDocs,
                    resultDestinationId = currentDestinationId ?: return
                ),
                targetDestinationId = R.id.destination_docActionsCompose
            )
        }
    }

    override fun navigateToDocsOrder(fragment: Fragment) {
        fragment.apply {
            navigateOnce(
                destination = NavStackOrderDirections.actionGlobalToStackOrder()
            )
        }
    }

    override fun navigateToPinInput(fragment: Fragment, verification: Boolean) {
        fragment.apply {
            navigateOnce(
                destination = NavMainDirections.actionGlobalDestinationPinInput(verification = verification)
            )
        }
    }
}
