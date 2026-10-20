package ua.govstack.digitalwallet.app.core.util.extensions.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import ua.govstack.digitalwallet.app.core.util.extensions.validateClassType

inline fun <T : Any> AppCompatActivity.registerForNavigationResultOnce(
    navController: NavController,
    key: String,
    graphId: Int,
    crossinline resultEvent: (T) -> Unit
) {
    val graphEntry = navController.getBackStackEntry(graphId)

    graphEntry.savedStateHandle
        .getLiveData<T>(key)
        .observe(this) {
            validateClassType(it)
            graphEntry.savedStateHandle.remove<T>(key)
            resultEvent(it)
        }
}