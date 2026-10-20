package ua.govstack.digitalwallet.app.wallet_gallery.ui.details

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import ua.govstack.digitalwallet.app.core.models.ConsumableItem
import ua.govstack.digitalwallet.app.core.ui.dynamicdialog.ActionsConst
import ua.govstack.digitalwallet.app.core.util.extensions.activity.setWindowBrightness
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.DataActionWrapper
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.collectAsEffect
import ua.govstack.digitalwallet.app.ui_base.models.orientation.Orientation
import ua.govstack.digitalwallet.app.wallet_core.ui.extensions.showCopyDocIdClippedSnackBar
import ua.govstack.digitalwallet.app.wallet_gallery.ui.details.compose.FullInfoBottomSheet

@AndroidEntryPoint
class FullInfoFCompose : BottomDoc() {

    private var composeView: ComposeView? = null
    private val args: FullInfoFComposeArgs by navArgs()
    private val viewModel: FullInfoFComposeVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.configureBody(args.doc)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        composeView = ComposeView(requireContext())
        return composeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView?.setContent {
            val body = viewModel.bodyData
            val progressIndicator =
                viewModel.progressIndicator.collectAsState(initial = Pair("", true))
            val configuration = LocalConfiguration.current
            val orientation =
                if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    Orientation.Landscape
                } else {
                    Orientation.Portrait
                }
            viewModel.apply {
                docAction.collectAsEffect { docAction ->
                    when (docAction) {
                        is FullInfoFComposeVM.DocActions.DocNumberCopy -> {
                            composeView?.rootView?.showCopyDocIdClippedSnackBar(
                                docAction.value, 40f
                            )
                        }

                        is FullInfoFComposeVM.DocActions.ItemVerticalValueCopy -> {
                            composeView?.rootView?.showCopyDocIdClippedSnackBar(
                                docAction.value, 40f
                            )
                        }

                        is FullInfoFComposeVM.DocActions.ItemHorizontalValueCopy -> {
                            composeView?.rootView?.showCopyDocIdClippedSnackBar(
                                docAction.value, 40f
                            )
                        }

                        is FullInfoFComposeVM.DocActions.ItemPrimaryValueCopy -> {
                            composeView?.rootView?.showCopyDocIdClippedSnackBar(
                                docAction.value, 40f
                            )
                        }

                        is FullInfoFComposeVM.DocActions.DismissDoc -> {
                            dismiss()
                        }
                        is FullInfoFComposeVM.DocActions.HighBrightness -> {
                            activity?.setWindowBrightness()
                        }

                        is FullInfoFComposeVM.DocActions.DefaultBrightness -> {
                            activity?.setWindowBrightness(true)
                        }

                        is FullInfoFComposeVM.DocActions.FullInfoAction -> {
                            saveBottomSheetResult(docAction.action)
                            dismiss()
                        }
                    }
                }
            }

            FullInfoBottomSheet(
                progressIndicator = progressIndicator.value,
                data = body,
                orientation = orientation,
                onUIAction = {
                    viewModel.onUIAction(it)
                })
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.setWindowBrightness(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        composeView = null
    }

    private fun saveBottomSheetResult(action: DataActionWrapper) {
        val resultKey = ActionsConst.BOTTOM_SHEET_RESULT_KEY
        findNavController().currentBackStackEntry?.savedStateHandle?.set(
            key = resultKey,
            ConsumableItem(action)
        )
    }
}