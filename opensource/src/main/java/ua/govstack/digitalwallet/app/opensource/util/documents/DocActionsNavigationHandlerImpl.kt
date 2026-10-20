package ua.govstack.digitalwallet.app.opensource.util.documents

import android.os.Parcelable
import ua.govstack.digitalwallet.app.core.models.document.ui.DocumentsContextMenuActions
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.opensource.NavMainDirections
import ua.govstack.digitalwallet.app.wallet_gallery.ui.actions.DocActionsDFCompose
import ua.govstack.digitalwallet.app.wallet_gallery.ui.actions.DocActionsDFComposeArgs
import ua.govstack.digitalwallet.app.wallet_gallery.ui.actions.DocActionsNavigationHandler
import ua.govstack.digitalwallet.app.wallet_gallery.ui.actions.DocActionsVMCompose
import ua.govstack.digitalwallet.app.wallet_gallery.ui.details.FullInfoFCompose
import ua.govstack.digitalwallet.app.wallet_gallery.ui.details.FullInfoFComposeArgs
import javax.inject.Inject

class DocActionsNavigationHandlerImpl @Inject constructor() :
    DocActionsNavigationHandler {
    override fun handleNavigation(
        fragment: DocActionsDFCompose,
        navigation: DocActionsVMCompose.DocActions,
        args: DocActionsDFComposeArgs
    ) {
        with(fragment) {
            when (navigation) {
                is DocActionsVMCompose.DocActions.NavigateByDocAction -> {
                    when (navigation.action) {

                        DocumentsContextMenuActions.FULL_DOC.action -> {
                            fullDocAction(args.doc)
                        }

                        DocumentsContextMenuActions.CHANGE_DOC_ORDERING.action -> {
                            dismiss()
                            navigate(NavMainDirections.actionGlobalToStackOrder())
                        }

                        DocumentsContextMenuActions.CHANGE_DISPLAY_ORDER.action -> {
                            dismiss()
                            navigate(NavMainDirections.actionGlobalToStackOrder())
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
        }
    }

    private fun DocActionsDFCompose.fullDocAction(document: Parcelable) {
        val fullDocFragment = FullInfoFCompose().apply {
            arguments = FullInfoFComposeArgs(document).toBundle()
        }
        dismiss()
        fullDocFragment.show(
            requireActivity().supportFragmentManager,
            FULL_DOC_INFO_TRANSACTION_TAG
        )
    }

    companion object {
        private const val FULL_DOC_INFO_TRANSACTION_TAG = "FULL_DOC_INF"
    }
}