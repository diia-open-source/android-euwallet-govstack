package ua.govstack.digitalwallet.app.core.util.extensions

import ua.govstack.digitalwallet.app.core.util.throwExceptionInDebug
import java.io.Serializable

fun validateClassType(it: Any) {
    if (it is Serializable) {
        if (it is java.lang.String || it is java.lang.Boolean || it is java.lang.Integer) {
            //in some cases we receive java String here. Ignore this case
            return
        }
        throwExceptionInDebug("Serializable is not supported. Please use Parcelable")
    }
}