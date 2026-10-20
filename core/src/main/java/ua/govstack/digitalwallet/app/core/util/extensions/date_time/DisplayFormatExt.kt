package ua.govstack.digitalwallet.app.core.util.extensions.date_time

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

fun LocalDate.toDisplayFormat(format: FormatStyle = FormatStyle.MEDIUM): String{
    val formatter = DateTimeFormatter.ofLocalizedDate(format)
    return format(formatter)
}