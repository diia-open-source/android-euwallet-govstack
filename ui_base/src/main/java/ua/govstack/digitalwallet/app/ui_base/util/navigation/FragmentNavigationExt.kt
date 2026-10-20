package ua.govstack.digitalwallet.app.ui_base.util.navigation

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.currentDestinationId
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.navigate
import ua.govstack.digitalwallet.app.ui_base.NavTemplateDialogDirections


/////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////// Global navigation //////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////

fun Fragment.openTemplateDialog(
    templateDialogModel: TemplateDialogModel,
    @IdRes destinationId: Int = -1
) {
    if (currentDestinationId != R.id.template_dialog) {
        navigate(
            NavTemplateDialogDirections.actionGlobalToTemplateDialog(
                templateDialogModel,
                destinationId
            )
        )
    }
}

