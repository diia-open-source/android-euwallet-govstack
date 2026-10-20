package ua.govstack.digitalwallet.app.ui_base.components.subatomic.loader

import androidx.compose.runtime.Composable
import ua.govstack.digitalwallet.app.core.util.state.Loader
import ua.govstack.digitalwallet.app.core.util.state.TridentDefault
import ua.govstack.digitalwallet.app.ui_base.components.molecule.loading.TridentLoaderMolecule

@Composable
fun TridentLoaderBlock(
    loader: Loader,
) {
    if (loader.isLoadingFullScreen(indicator = TridentDefault)) {
        TridentLoaderMolecule()
    }
}