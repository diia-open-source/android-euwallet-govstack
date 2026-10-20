package ua.govstack.digitalwallet.app.core.util.date

import ua.govstack.digitalwallet.app.core.util.extensions.date_time.getCurrentDateUtc
import java.util.Date


interface CurrentDateProvider {

    fun getDate(): Date
}

internal class CurrentDateProviderImpl : CurrentDateProvider {

    override fun getDate(): Date {
        return getCurrentDateUtc()
    }
}
