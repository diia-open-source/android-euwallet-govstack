package ua.govstack.digitalwallet.app.wallet_core.documents.extensions

import android.util.Base64
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.transactionLogging.presentation.PresentedDocument
import ua.govstack.digitalwallet.app.wallet_core.documents.model.IssuedDocumentClaimDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.asDisplayString
import ua.govstack.digitalwallet.app.wallet_core.documents.model.isNotBlank
import ua.govstack.digitalwallet.app.wallet_core.documents.util.DocumentJsonKeys
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.transformPathsToIssuedDocumentClaims
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.encodeToBase64String
import ua.govstack.digitalwallet.app.wallet_core.utils.toDateFormatted
import java.time.LocalDate

fun IssuedDocument.toTransactionClaimDomains(): List<IssuedDocumentClaimDomain> {
    val claims = data.claims

    return transformPathsToIssuedDocumentClaims(
        paths = claims.flatMap { it.toClaimPaths() },
        claims = claims
    ).prepareForTransaction()
}

fun PresentedDocument.toTransactionClaimDomains(): List<IssuedDocumentClaimDomain> {
    val displayNamesByIdentifier = metadata
        ?.claims
        .orEmpty()
        .mapNotNull { claim ->
            val identifier = claim.path.lastOrNull()?.takeIf { it.isNotBlank() }
            val displayTitle = claim.display.firstOrNull()?.name?.takeIf { it.isNotBlank() }
            if (identifier != null && displayTitle != null) identifier to displayTitle else null
        }
        .toMap()

    val claimsMap = claims
        .mapNotNull { claim ->
            val identifier = claim.path.lastOrNull()?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val value = claim.value?.takeIf { it.isMeaningfulPresentedValue() } ?: return@mapNotNull null
            identifier to value
        }
        .toMap()

    val nestedPaths = claims
        .map { it.path.filter(String::isNotBlank) }
        .filter { it.size > 1 }

    val shouldDropTechnicalRootNesting = nestedPaths
        .map { it.first() }
        .distinct()
        .singleOrNull() != null

    val nestedSections = linkedMapOf<String, IssuedDocumentClaimDomain>()

    val transactionClaims = buildList {
        claims.forEach { claim ->
            val rawPath = claim.path.filter(String::isNotBlank)
            val path = rawPath.dropRootIfNeeded(shouldDropTechnicalRootNesting)
            if (path.isEmpty()) return@forEach

            val leafValue = claimsMap[path.last()] ?: return@forEach
            val children = leafValue.toIssuedDocumentChildren(displayNamesByIdentifier)
            val value = leafValue.toDisplayValueOrNull()

            if (path.size == 1) {
                val identifier = path.first()
                val claimDomain = IssuedDocumentClaimDomain(
                    identifier = identifier,
                    value = value,
                    displayTitle = displayNamesByIdentifier[identifier] ?: identifier,
                    children = children,
                )
                if (children.isEmpty()) {
                    add(claimDomain)
                } else {
                    nestedSections[identifier] = nestedSections[identifier]?.mergeWith(claimDomain)
                        ?: claimDomain
                }
                return@forEach
            }

            val rootIdentifier = path.first()
            val rootSection = nestedSections[rootIdentifier]
                ?: IssuedDocumentClaimDomain(
                    identifier = rootIdentifier,
                    value = null,
                    displayTitle = displayNamesByIdentifier[rootIdentifier] ?: rootIdentifier,
                    children = emptyList(),
                )

            nestedSections[rootIdentifier] = rootSection.insertPath(
                path = path.drop(1),
                value = value,
                children = children,
                displayNamesByIdentifier = displayNamesByIdentifier,
            )
        }

        addAll(nestedSections.values)
    }

    return transactionClaims.prepareForTransaction()
}

private fun List<IssuedDocumentClaimDomain>.normalizePortrait(): List<IssuedDocumentClaimDomain> {
    return filter { it.identifier !in DocumentJsonKeys.BASE64_IMAGE_KEYS }
}

private fun List<IssuedDocumentClaimDomain>.prepareForTransaction(): List<IssuedDocumentClaimDomain> {
    return map { it.prepareForTransaction() }
        .normalizePortrait()
        .removeTechnicalClaims()
        .filter { it.hasContent() }
        .sortForTransaction()
}

private fun IssuedDocumentClaimDomain.prepareForTransaction(): IssuedDocumentClaimDomain {
    return normalizeNationalityClaim()
        .normalizeClaimDisplayName()
        .flattenChildrenToSingleLevel()
        .removeEmptyClaims()
}

private fun IssuedDocumentClaimDomain.hasContent(): Boolean {
    return value?.isNotBlank() == true || children.isNotEmpty()
}

private fun List<String>.dropRootIfNeeded(shouldDropRoot: Boolean): List<String> {
    return drop(1.takeIf { shouldDropRoot && size > 1 } ?: 0)
}

private fun IssuedDocumentClaimDomain.mergeWith(
    claim: IssuedDocumentClaimDomain,
): IssuedDocumentClaimDomain {
    return copy(
        value = claim.value ?: value,
        children = (children + claim.children).distinctBy { it.identifier }
    )
}

private fun List<IssuedDocumentClaimDomain>.removeTechnicalClaims(): List<IssuedDocumentClaimDomain> {
    return buildList {
        for (item in this@removeTechnicalClaims) {
            if (item.identifier !in DocumentJsonKeys.TECHNICAL_KEYS) {
                add(
                    item.copy(
                        children = item.children.removeTechnicalClaims()
                    )
                )
            }
        }
    }
}

private fun List<IssuedDocumentClaimDomain>.sortedByDisplayTitle(): List<IssuedDocumentClaimDomain> {
    return sortedBy { it.displayTitle.lowercase() }
}

private fun List<IssuedDocumentClaimDomain>.sortForTransaction(): List<IssuedDocumentClaimDomain> {
    val (claimsWithoutChildren, claimsWithChildren) = partition { it.children.isEmpty() }
    return claimsWithoutChildren.sortedByDisplayTitle() + claimsWithChildren
        .map { it.withSortedChildren() }
        .sortedByDisplayTitle()
}

private fun IssuedDocumentClaimDomain.withSortedChildren(): IssuedDocumentClaimDomain {
    if (children.isEmpty()) return this
    return copy(
        children = children
            .map { child -> child.withSortedChildren() }
            .sortedByDisplayTitle()
    )
}

private fun IssuedDocumentClaimDomain.normalizeNationalityClaim(): IssuedDocumentClaimDomain {
    val normalizedChildren = children.map { it.normalizeNationalityClaim() }
    val nationalityIdentifiers = setOf(
        DocumentJsonKeys.NATIONALITY,
        DocumentJsonKeys.NATIONALITY_FOR_DISPLAY
    )
    return if (identifier in nationalityIdentifiers) {
        copy(
            value = collectPrimitiveValues()
                .joinToString(", ")
                .takeIf { it.isNotBlank() }
                ?.let { IssuedDocumentClaimDomain.Value.Text(it) },
            children = emptyList()
        )
    } else {
        copy(children = normalizedChildren)
    }
}

private fun IssuedDocumentClaimDomain.normalizeClaimDisplayName(): IssuedDocumentClaimDomain {
    val normalizedChildren = children.map { it.normalizeClaimDisplayName() }
    val hasDefaultDisplayTitle =
        displayTitle.isBlank() || displayTitle == identifier

    val normalizedTitle = when {
        hasDefaultDisplayTitle -> identifier.humanizeIdentifier()
        else -> displayTitle
    }

    return copy(
        displayTitle = normalizedTitle,
        children = normalizedChildren.filter { child ->
            child.value?.isNotBlank() == true || child.children.isNotEmpty()
        }
    )
}

private fun IssuedDocumentClaimDomain.insertPath(
    path: List<String>,
    value: IssuedDocumentClaimDomain.Value?,
    children: List<IssuedDocumentClaimDomain>,
    displayNamesByIdentifier: Map<String, String>,
): IssuedDocumentClaimDomain {
    if (path.isEmpty()) return this

    val currentIdentifier = path.first()
    val existingChild = children.firstOrNull { it.identifier == currentIdentifier }
    val currentNode = existingChild ?: IssuedDocumentClaimDomain(
        identifier = currentIdentifier,
        value = null,
        displayTitle = displayNamesByIdentifier[currentIdentifier] ?: currentIdentifier,
        children = emptyList(),
    )

    val updatedNode = if (path.size == 1) {
        currentNode.copy(
            value = value,
            children = children
        )
    } else {
        currentNode.insertPath(
            path = path.drop(1),
            value = value,
            children = children,
            displayNamesByIdentifier = displayNamesByIdentifier,
        )
    }

    return copy(
        children = children
            .filterNot { it.identifier == currentIdentifier } + updatedNode
    )
}

private fun Any.isMeaningfulPresentedValue(): Boolean {
    return when (this) {
        is String -> isNotBlank()
        is Collection<*> -> isNotEmpty()
        is Map<*, *> -> isNotEmpty()
        else -> true
    }
}

private fun Any.toDisplayValueOrNull(): IssuedDocumentClaimDomain.Value? {
    val base64Image = (this as? ByteArray)?.encodeToBase64String(Base64.URL_SAFE)

    val date: String? = (this as? String)?.toDateFormatted()
        ?: (this as? LocalDate)?.toDateFormatted()

    return when  {
        base64Image != null -> IssuedDocumentClaimDomain.Value.Base64Image(base64Image)
        date != null -> IssuedDocumentClaimDomain.Value.Date(date)
        this is Boolean || this.toString()
            .toBooleanStrictOrNull() != null -> IssuedDocumentClaimDomain.Value.BooleanValue(
            this.toString().toBoolean()
        )
        this is Collection<*> -> {
            if (all { item -> item == null || item is String || item is Number || item is Boolean }) {
                mapNotNull { item -> item?.toString()?.takeIf { it.isNotBlank() } }
                    .joinToString(", ")
                    .takeIf { it.isNotBlank() }
                    ?.let { IssuedDocumentClaimDomain.Value.Text(it) }
            } else {
                null
            }
        }
        this is Map<*, *> -> null
        this is String -> takeIf { it.isNotBlank() }?.let { IssuedDocumentClaimDomain.Value.Text(it) }
        else -> toString()
            .takeIf { it.isNotBlank() }
            ?.let { IssuedDocumentClaimDomain.Value.Text(it) }
    }
}

private fun Any.toIssuedDocumentChildren(
    displayNamesByIdentifier: Map<String, String>,
): List<IssuedDocumentClaimDomain> {
    return when (this) {
        is Map<*, *> -> this.entries.mapNotNull { (rawKey, rawValue) ->
            val identifier = (rawKey as? String)?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            val value = rawValue?.takeIf { it.isMeaningfulPresentedValue() } ?: return@mapNotNull null
            val children = value.toIssuedDocumentChildren(displayNamesByIdentifier)
            IssuedDocumentClaimDomain(
                identifier = identifier,
                value = value.toDisplayValueOrNull(),
                displayTitle = displayNamesByIdentifier[identifier] ?: identifier,
                children = children,
            )
        }.sortedByDisplayTitle()

        is Collection<*> -> {
            if (size == 1 && firstOrNull() is Map<*, *>) {
                return first()?.toIssuedDocumentChildren(displayNamesByIdentifier).orEmpty()
            }
            if (all { item -> item == null || item is String || item is Number || item is Boolean }) {
                return emptyList()
            }
            mapIndexedNotNull { index, rawValue ->
                val value = rawValue?.takeIf { it.isMeaningfulPresentedValue() } ?: return@mapIndexedNotNull null
                val identifier = "item_${index + 1}"
                IssuedDocumentClaimDomain(
                    identifier = identifier,
                    value = value.toDisplayValueOrNull(),
                    displayTitle = identifier,
                    children = value.toIssuedDocumentChildren(displayNamesByIdentifier),
                )
            }
        }

        else -> emptyList()
    }
}

private fun IssuedDocumentClaimDomain.collectPrimitiveValues(): List<String> {
    if (children.isEmpty()) {
        return value
            ?.takeIf { it.isNotBlank() }
            ?.let { listOf(it.asDisplayString()) }
            .orEmpty()
    }
    return children.flatMap { child -> child.collectPrimitiveValues() }
}

private fun IssuedDocumentClaimDomain.removeEmptyClaims(): IssuedDocumentClaimDomain {
    val normalizedChildren = children
        .map { it.removeEmptyClaims() }
        .filter { child -> child.value?.isNotBlank() == true || child.children.isNotEmpty() }

    return copy(children = normalizedChildren)
}

private fun IssuedDocumentClaimDomain.flattenChildrenToSingleLevel(): IssuedDocumentClaimDomain {
    if (children.isEmpty()) return this

    val flattenedChildren = children.flatMap { child ->
        child.flattenAsChildItems()
    }

    return copy(children = flattenedChildren)
}

private fun IssuedDocumentClaimDomain.flattenAsChildItems(): List<IssuedDocumentClaimDomain> {
    val currentItem = copy(children = emptyList())
    val nestedItems = children.flatMap { child -> child.flattenAsChildItems() }
    return listOf(currentItem) + nestedItems
}

fun String.humanizeIdentifier(): String {
    return split("_")
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.lowercase().replaceFirstChar { it.uppercase() }
        }
}
