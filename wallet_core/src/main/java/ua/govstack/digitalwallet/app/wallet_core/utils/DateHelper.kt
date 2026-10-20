package ua.govstack.digitalwallet.app.wallet_core.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

private const val DAY_MONTH_YEAR_SHORT_PATTERN = "dd MMM yyyy"
const val FULL_DATETIME_PATTERN = "dd MMM yyyy HH:mm"
const val TRANSACTION_FULL_DATETIME_PATTERN = "dd MMM yyyy, HH:mm"

private val dtoDateFormatters = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSS",
    "yyyy-MM-dd'T'HH:mm:ss",
    "yyyy-MM-dd'T'HH:mm:ss.SSSz",
    "yyyy-MM-dd'T'HH:mm:ssz",
    "yyyy-MM-dd"
)

fun String.toDateFormatted(
    selectedLanguage: String = LocaleUtils.DEFAULT_LOCALE,
    dateFormatStyle: Int = DateFormat.MEDIUM
): String? {
    var formattedDate: Date? = null
    val dateFormat = SimpleDateFormat.getDateInstance(
        dateFormatStyle,
        LocaleUtils.getLocaleFromSelectedLanguage(selectedLanguage)
    )
    for (formatter in dtoDateFormatters) {
        try {
            formattedDate = SimpleDateFormat(formatter, Locale.ENGLISH).parse(this)
            break
        } catch (_: Exception) {
            continue
        }
    }
    return formattedDate?.let { dateFormat.format(it) }
}

fun LocalDate.toDateFormatted(
    selectedLanguage: String = LocaleUtils.DEFAULT_LOCALE,
    dateFormatStyle: Int = DateFormat.MEDIUM
): String? {
    return try {
        val locale = LocaleUtils.getLocaleFromSelectedLanguage(selectedLanguage)
        val dateFormat = DateFormat.getDateInstance(dateFormatStyle, locale)
        dateFormat.format(Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant()))
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Instant.formatInstant(
    zoneId: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.ENGLISH
): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", locale)
        .withZone(zoneId)
    return formatter.format(this)
}

fun LocalDateTime.formatLocalDateTime(
    pattern: String = DAY_MONTH_YEAR_SHORT_PATTERN,
    selectedLanguage: String = LocaleUtils.DEFAULT_LOCALE,
): String {
    val formatter = DateTimeFormatter.ofPattern(
        pattern,
        LocaleUtils.getLocaleFromSelectedLanguage(selectedLanguage)
    )
    return this.format(formatter)
}
