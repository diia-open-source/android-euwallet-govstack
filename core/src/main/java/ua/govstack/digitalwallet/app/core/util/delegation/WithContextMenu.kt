package ua.govstack.digitalwallet.app.core.util.delegation

import androidx.lifecycle.LiveData
import ua.govstack.digitalwallet.app.core.models.ContextMenuField
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent

interface WithContextMenu<T : ContextMenuField> {

    val openContextMenu: LiveData<UiDataEvent<Array<T>>>

    val showContextMenu: LiveData<Boolean>

    fun openContextMenu()

    fun setContextMenu(contextMenu: Array<T>?)

    fun getMenu(): Array<T>?
}