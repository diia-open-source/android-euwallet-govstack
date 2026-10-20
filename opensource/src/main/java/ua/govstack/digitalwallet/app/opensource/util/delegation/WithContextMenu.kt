package ua.govstack.digitalwallet.app.opensource.util.delegation

import androidx.lifecycle.MutableLiveData
import ua.govstack.digitalwallet.app.core.models.ContextMenuField
import ua.govstack.digitalwallet.app.core.util.delegation.WithContextMenu
import ua.govstack.digitalwallet.app.core.util.event.UiDataEvent
import ua.govstack.digitalwallet.app.core.util.extensions.lifecycle.asLiveData
import javax.inject.Inject

class DefaultWithContextMenuBehaviour<T : ContextMenuField> @Inject constructor():
    WithContextMenu<T> {

    private var contextMenu: Array<T>? = null

    private val _openContextMenu = MutableLiveData<UiDataEvent<Array<T>>>()
    override val openContextMenu = _openContextMenu.asLiveData()

    private val _showContextMenu = MutableLiveData(false)
    override val showContextMenu = _showContextMenu.asLiveData()

    override fun openContextMenu() {
        contextMenu?.let {
            _openContextMenu.postValue(UiDataEvent(it))
        }
    }

    override fun setContextMenu(contextMenu: Array<T>?) {
        this.contextMenu = contextMenu

        val isVisible = !contextMenu.isNullOrEmpty()
        _showContextMenu.postValue(isVisible)
    }

    override fun getMenu(): Array<T>?  = contextMenu

}