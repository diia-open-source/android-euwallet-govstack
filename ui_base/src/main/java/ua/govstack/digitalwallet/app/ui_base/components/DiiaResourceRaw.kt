package ua.govstack.digitalwallet.app.ui_base.components

import ua.govstack.digitalwallet.app.ui_base.R

enum class DiiaResourceRaw(
    val code: String,
    val rawResourceId: Int
) {

    DEFAULT_GLOBAL("default-global", R.raw.loader),
    CARD("card", R.raw.card),
    PASSPORT("passport", R.raw.passport),
    LOADER("loader", R.raw.loader_nfc);

    companion object {
        fun getResourceId(code: String?): Int {
            return DiiaResourceRaw.entries.firstOrNull { code == it.code }?.rawResourceId
                ?: DEFAULT_GLOBAL.rawResourceId
        }
    }

}