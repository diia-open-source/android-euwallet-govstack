package ua.govstack.digitalwallet.app.opensource.util.extensions.fragment

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

fun Fragment.isPermissionGranted(permission: String): Boolean =
    ContextCompat.checkSelfPermission(
        requireActivity(),
        permission
    ) == PackageManager.PERMISSION_GRANTED