package ua.govstack.digitalwallet.app.wallet_core.ui.alertDialogsFactory

import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogButton
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogData
import ua.govstack.digitalwallet.app.core.models.dialogs.TemplateDialogModel
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.ui_base.components.DiiaResourceIcon
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.WalletConst
import javax.inject.Inject

class WalletAlertDialogFactoryImpl @Inject constructor(
) : WalletAlertDialogFactory {

    /**
     * For debug purposes to open verify user person at any point of app
     */
    override fun docIssued(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ELLIPSE_CHECK.code,
            title = "The document has been created",
            description = "The document has been successfully added to your wallet.",
            mainButton = TemplateDialogButton(
                name = "Done",
                action = WalletConst.TO_DOCUMENT_GALLERY_ACTION,
            )
        ),
    )

    override fun deferredDocRequest(key: String)= TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ELLIPSE_CHECK.code,
            title = "Document requested",
            description = "Your document has been requested and is being processed. This may take some time.",
            mainButton = TemplateDialogButton(
                name = "Done",
                action = WalletConst.TO_DOCUMENT_GALLERY_ACTION,
            )
        ),
    )

    override fun docFailToReceive(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Couldn’t create the document",
            description = "Something went wrong while creating the document. Try again or try later.",
            mainButton = TemplateDialogButton(
                name = "Cancel",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun failToShareNoDocFound(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "The required document is missing",
            description = "Failed to process the data sharing request. Please activate the necessary document",
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun failToHandleSharingRequest(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Failed to process the data sharing request",
            description = "Please try again later",
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun serviceDisconnected(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Request processing error",
            description = "The connection was interrupted.",
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun errorOccurred(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Unfortunately, an error occurred",
            description = "Please try again later",
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun bluetoothLost(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Bluetooth connection lost",
            description = "Reshare your data to restore the session",
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun somethingGoesWrong(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Unfortunately, an error occurred",
            description = "Please try again later",
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun dataSharedSuccessfully(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ELLIPSE_CHECK.code,
            title = "Data sent successfully",
            description = null,
            mainButton = TemplateDialogButton(
                name = "Done",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun documentsDeleted(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ELLIPSE_CHECK.code,
            title = "Document successfully deleted",
            description = null,
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_DEAL_WITH_IT
            ),
        )
    )

    override fun confirmDeleteDocument(key: String) = TemplateDialogModel(
        key = key,
        type = "middleCenterIconBlackButtonAlert",
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Delete document?",
            description = "This will delete your digital document. You can add it again from the document gallery.",
            mainButton = TemplateDialogButton(
                name = "Delete document",
                action = ActionsConst.DIALOG_ACTION_DOC_CONFIRM_REMOVE
            ),
            alternativeButton = TemplateDialogButton(
                name = "Cancel",
                action = ActionsConst.DIALOG_ACTION_DOC_CANCEL_REMOVE
            )
        )
    )


    override fun documentAlreadyExist(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "This document has already been added",
            description = "The scanned document matches one that is already in your wallet.",
            mainButton = TemplateDialogButton(
                name = "Cancel",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun pidNotFound(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "PID not found",
            description = "Failed to process the document request. Please create your PID to add other documents.",
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_ACTION_CANCEL,
            )
        ),
    )

    override fun untrustedIssuer(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Untrusted issuer",
            description = "We couldn't verify this issuer. For your security, documents from untrusted sources cannot be added to your wallet.",
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun untrustedVerifier(key: String) = TemplateDialogModel(
        type = "middleCenterIconBlackButtonAlert",
        key = key,
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Untrusted verifier",
            description = "We couldn't verify this requester. For your security, data cannot be shared with untrusted verifiers.",
            mainButton = TemplateDialogButton(
                name = "Got it",
                action = ActionsConst.DIALOG_ACTION_PREV_SCREEN,
            )
        ),
    )

    override fun deferredDocIssued(key: String) = TemplateDialogModel(
        key = key,
        type = "middleCenterIconBlackButtonAlert",
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ELLIPSE_CHECK.code,
            title = "The deferred document is now available",
            description = "The document has been successfully added to your wallet.",
            mainButton = TemplateDialogButton(
                name = "Done",
                action = ActionsConst.DIALOG_DEAL_WITH_IT
            )
        )
    )

    override fun deferredDocFailed(message: String?, key: String) = TemplateDialogModel(
        key = key,
        type = "middleCenterIconBlackButtonAlert",
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Document request failed",
            description = message
                ?: "For some reason, your document request couldn't be processed.",
            mainButton = TemplateDialogButton(
                name = "Cancel",
                action = ActionsConst.DIALOG_DEAL_WITH_IT
            )
        )
    )

    override fun deferredDocExpired(key: String) = TemplateDialogModel(
        key = key,
        type = "middleCenterIconBlackButtonAlert",
        isClosable = false,
        data = TemplateDialogData(
            icon = DiiaResourceIcon.ATTENTION_BLACK_ROUND_SMALL.code,
            title = "Document expired",
            description = "This document is no longer valid. Please request a new one from the document gallery.",
            mainButton = TemplateDialogButton(
                name = "Cancel",
                action = ActionsConst.DIALOG_DEAL_WITH_IT
            )
        )
    )
}
