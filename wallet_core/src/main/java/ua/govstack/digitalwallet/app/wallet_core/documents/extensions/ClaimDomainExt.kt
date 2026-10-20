package ua.govstack.digitalwallet.app.wallet_core.documents.extensions

import androidx.compose.ui.util.fastJoinToString
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimDomain

fun List<ClaimDomain>.findClaim(
    key: String,
    recursive: Boolean = true
): ClaimDomain? = findFirstMatchingClaim(recursive) { it.key == key }

fun List<ClaimDomain>.findGroup(
    key: String,
    recursive: Boolean = true
): ClaimDomain.Group? = findFirstMatchingClaim(recursive) {
    it is ClaimDomain.Group && it.key == key
} as? ClaimDomain.Group

fun List<ClaimDomain>.findPrimitive(
    vararg keys: String,
    recursive: Boolean = true
): ClaimDomain.Primitive? {
    keys.forEach { key ->
        val primitive = findFirstMatchingClaim(recursive) {
            it is ClaimDomain.Primitive && it.key == key
        } as? ClaimDomain.Primitive
        if (primitive != null) {
            return primitive
        }
    }
    return null
}

fun List<ClaimDomain>.findPrimitiveValue(
    vararg keys: String,
    recursive: Boolean = true
): String? = findPrimitive(*keys, recursive = recursive)?.value

fun List<ClaimDomain>.findPrimitiveClaims(
    key: String,
    recursive: Boolean = true
): List<ClaimDomain.Primitive> {
    return flatMap { claim ->
        when {
            claim.key == key && claim is ClaimDomain.Primitive -> listOf(claim)
            recursive && claim is ClaimDomain.Group -> claim.items.findPrimitiveClaims(key)
            else -> emptyList()
        }
    }
}


fun List<ClaimDomain>.joinPrimitiveValues(
    vararg keys: String,
    separator: String = " "
): String? = keys.mapNotNull { key -> findPrimitiveValue(key) }
    .joinToString(separator)
    .ifEmpty { null }

fun ClaimDomain.asDisplayValue(separator: String = ", "): String =
    when (this) {
        is ClaimDomain.Primitive -> value
        is ClaimDomain.Group -> collectPrimitiveValues().fastJoinToString(separator)
    }

fun String.normalizeBase64(): String =
    trim()
        .substringAfter(";base64,", this)
        .replace("_", "/")
        .replace("-", "+")

private fun List<ClaimDomain>.findFirstMatchingClaim(
    recursive: Boolean,
    predicate: (ClaimDomain) -> Boolean
): ClaimDomain? {
    return firstNotNullOfOrNull { claim ->
        when {
            predicate(claim) -> claim
            recursive && claim is ClaimDomain.Group -> claim.items.findFirstMatchingClaim(
                recursive = true,
                predicate = predicate
            )

            else -> null
        }
    }
}

private fun ClaimDomain.collectPrimitiveValues(): List<String> =
    when (this) {
        is ClaimDomain.Primitive -> listOf(value)
        is ClaimDomain.Group -> items.flatMap { it.collectPrimitiveValues() }
    }

fun ClaimDomain.displayTitleLabel() = "$displayTitle:"
