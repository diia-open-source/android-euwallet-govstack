package ua.govstack.digitalwallet.app.opensource.util.documents

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.core.models.ConsumableItem
import ua.govstack.digitalwallet.app.core.models.ConsumableString
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.openLink
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.previousDestinationId
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForNavigationResult
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.registerForTemplateDialogNavResult
import ua.govstack.digitalwallet.app.opensource.R
import ua.govstack.digitalwallet.app.opensource.ui.AppConst
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.DocsConst
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.ConfirmRemoveDocBackStackResult
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.DocGalleryNavigationHelper
import javax.inject.Inject

class DocGalleryNavigationHelperImpl @Inject constructor() : DocGalleryNavigationHelper {

    override fun subscribeForNavigationEvents(
        fragment: Fragment,
        navigationBackStackEventFlow: MutableSharedFlow<BackStackEvent>,
    ) {
        with(fragment) {
            galleryUniqRegistration(navigationBackStackEventFlow)
            registerFromRemoveDocument(navigationBackStackEventFlow)
            registerFromOpenLink()
        }
    }

    private fun Fragment.galleryUniqRegistration(navigationBackStackEventFlow: MutableSharedFlow<BackStackEvent>) {
        registerForTemplateDialogNavResult { action ->
            findNavController().popBackStack()
            val event = BackStackEvent.UserActionResult(ConsumableString(action))

            navigationBackStackEventFlow.tryEmit(event)
        }
    }

    private fun Fragment.registerFromRemoveDocument(
        navigationBackStackEventFlow: MutableSharedFlow<BackStackEvent>
    ) {
        registerForNavigationResult<ConsumableItem>(DocsConst.RESULT_KEY_REMOVE_DOCUMENT) { key ->
            key.consumeEvent<Document> { doc ->
                if (previousDestinationId == R.id.homeF) {
                    findNavController().popBackStack()
                }
                navigationBackStackEventFlow.tryEmit(
                    ConfirmRemoveDocBackStackResult(ConsumableItem(doc))
                )
            }
        }
    }

    private fun Fragment.registerFromOpenLink() {
        registerForNavigationResult<ConsumableString>(AppConst.RESULT_KEY_OPEN_LINK) { key ->
            key.consumeEvent { link ->
                if (previousDestinationId == R.id.homeF) {
                    findNavController().popBackStack()
                }
                openLink(link)
            }
        }
    }
}
