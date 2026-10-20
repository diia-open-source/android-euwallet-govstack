package ua.govstack.digitalwallet.app.wallet_gallery.ui.actions

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.govstack.digitalwallet.app.core.models.ConsumableItem
import ua.govstack.digitalwallet.app.core.models.document.Document
import ua.govstack.digitalwallet.app.core.util.extensions.fragment.setNavigationResult
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.DocAction
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ActivityViewOrg
import ua.govstack.digitalwallet.app.ui_base.fragments.BaseBottomDialog
import ua.govstack.digitalwallet.app.wallet_core.ui.conts.DocsConst
import javax.inject.Inject

@AndroidEntryPoint
class DocActionsDFCompose : BaseBottomDialog() {

    @Inject
    lateinit var docActionsNavigationHandler: DocActionsNavigationHandler
    private val vm: DocActionsVMCompose by viewModels()
    private var composeView: ComposeView? = null
    private val args: DocActionsDFComposeArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(true)
            window?.setWindowAnimations(R.style.BottomDialogAnimation)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        composeView = ComposeView(requireContext())
        return composeView
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        (view?.parent as View).setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView?.setContent {

            vm.apply {
                docAction.collectAsEffect { docAction ->
                    handleAction(
                        this@DocActionsDFCompose,
                        docAction,
                        args
                    )
                }
                dismiss.collectAsEffect {
                    dismiss()
                }
            }

//            LaunchedEffect(Unit) {
//                vm.getData(
//                    args.doc as DiiaDocument,
//                    args.manualDocs
//                )
//            }
            val data by vm.body.collectAsState()

            data?.let { orgData ->
                ActivityViewOrg(
                    modifier = Modifier,
                    data = orgData,
                    onUIAction = {
                        vm.onUIAction(it)
                    }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    private fun handleAction(
        fragment: DocActionsDFCompose,
        action: DocAction,
        args: DocActionsDFComposeArgs
    ) {
        with(fragment) {
            when (action) {
                is DocActionsVMCompose.DocActions.RemoveDoc -> {
                    val doc = args.doc
                    if (doc is Document) {
                        dismiss()
                        setNavigationResult(
                            arbitraryDestination = args.resultDestinationId,
                            key = DocsConst.RESULT_KEY_REMOVE_DOCUMENT,
                            data = ConsumableItem(doc)
                        )
                    }
                }

                is DocActionsVMCompose.DocActions.UpdateDoc -> {
                    val doc = args.doc
                    if (doc is Document) {
                        dismiss()
                        setNavigationResult(
                            arbitraryDestination = args.resultDestinationId,
                            key = DocsConst.RESULT_KEY_UPDATE_DOCUMENT,
                            data = ConsumableItem(doc)
                        )
                    }
                }

                is DocActionsVMCompose.DocActions.NavigateByDocAction -> {
                    docActionsNavigationHandler.handleNavigation(
                        this@DocActionsDFCompose,
                        action,
                        args
                    )
                }
            }

        }
    }
}
