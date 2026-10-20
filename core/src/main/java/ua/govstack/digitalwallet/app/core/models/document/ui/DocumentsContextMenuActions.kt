package ua.govstack.digitalwallet.app.core.models.document.ui

import ua.govstack.digitalwallet.app.core.models.document.DocumentContextMenuAction

enum class DocumentsContextMenuActions(override val action: String) : DocumentContextMenuAction {
    FULL_DOC("fullDoc"),
    CHANGE_DISPLAY_ORDER("changeDisplayOrder"),
    CHANGE_DOC_ORDERING("changeDocOrdering"),
    REMOVE_DOC("removeDoc"),
    UPDATE_DOC("updateDoc"),
}