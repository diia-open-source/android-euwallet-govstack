package ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation

import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.MutableSharedFlow
import ua.govstack.digitalwallet.app.core.models.common.BackStackEvent

interface DocGalleryNavigationHelper {

    /**
     * subscribes for navigation events and handles navigation
     */
    fun subscribeForNavigationEvents(
        fragment: Fragment,
        navigationBackStackEventFlow: MutableSharedFlow<BackStackEvent>
    )

}