package ua.govstack.digitalwallet.app.wallet_core.documents.helper

import android.util.Base64
import eu.europa.ec.eudi.wallet.document.format.DocumentClaim
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcClaim
import eu.europa.ec.eudi.wallet.document.metadata.IssuerMetadata
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.R
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.decodeFromBase64
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.encodeToBase64String
import ua.govstack.digitalwallet.app.wallet_core.utils.extensions.safeLet
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimPathDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.IssuedDocumentClaimDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.isNotBlank
import ua.govstack.digitalwallet.app.wallet_core.documents.util.DocumentJsonKeys
import ua.govstack.digitalwallet.app.wallet_core.utils.toDateFormatted
import java.time.LocalDate
import java.util.Locale

private fun keyIsUserPseudonym(key: String): Boolean {
    return key == DocumentJsonKeys.USER_PSEUDONYM
}

private fun keyIsGender(key: String): Boolean {
    return DocumentJsonKeys.GENDER_KEYS.contains(key)
}

fun getGenderDisplayValue(value: String, resourceProvider: ResourceProvider): String =
    when (value) {
        "0" -> resourceProvider.getString(R.string.request_gender_not_known)
        "1" -> resourceProvider.getString(R.string.request_gender_male)
        "2" -> resourceProvider.getString(R.string.request_gender_female)
        "9" -> resourceProvider.getString(R.string.request_gender_not_applicable)
        else -> value
    }

fun getBooleanValue(value: Boolean, resourceProvider: ResourceProvider): String =
    when(value) {
        true -> resourceProvider.getString(R.string.document_details_boolean_item_true_readable_value)
        false -> resourceProvider.getString(R.string.document_details_boolean_item_false_readable_value)
    }

fun createIssuedDocumentClaim(
    item: Any,
    groupKey: String,
    childKey: String = "",
    claimMetaData: IssuerMetadata.Claim?,
    allItems: MutableList<IssuedDocumentClaimDomain>,
) {

    fun addFlatOrGroupedChildren(
        allItems: MutableList<IssuedDocumentClaimDomain>,
        children: List<IssuedDocumentClaimDomain>,
        groupKey: String,
        displayTitle: String,
        predicate: () -> Boolean
    ) {

        val groupIsAlreadyPresent = children.any { child ->
            child.identifier == groupKey && child.children.isNotEmpty()
        }

        if (predicate() && !groupIsAlreadyPresent) {
            allItems.add(
                IssuedDocumentClaimDomain(
                    identifier = groupKey,
                    value = null,
                    displayTitle = displayTitle,
                    children = children
                )
            )
        } else {
            allItems.addAll(children)
        }
    }

    when (item) {

        is Map<*, *> -> {

            val children: MutableList<IssuedDocumentClaimDomain> = mutableListOf()
            val childKeys: MutableList<String> = mutableListOf()

            item.forEach { (key, value) ->
                safeLet(key as? String, value) { key, value ->

                    val newGroupKey = if (value is Collection<*>) key else groupKey
                    val newChildKey = if (value is Collection<*>) "" else key

                    childKeys.add(newChildKey)

                    createIssuedDocumentClaim(
                        item = value,
                        groupKey = newGroupKey,
                        childKey = newChildKey,
                        claimMetaData = null,
                        allItems = children
                    )
                }
            }

            addFlatOrGroupedChildren(
                allItems = allItems,
                children = children,
                groupKey = groupKey,
                displayTitle = getReadableNameFromIdentifier(
                    claimMetaData = claimMetaData,
                    userLocale = Locale.getDefault(),
                    fallback = groupKey
                )
            ) {
                childKeys.none { it.isEmpty() }
            }
        }

        is Collection<*> -> {

            val children: MutableList<IssuedDocumentClaimDomain> = mutableListOf()

            item.forEach { value ->
                value?.let {
                    createIssuedDocumentClaim(
                        item = it,
                        groupKey = groupKey,
                        claimMetaData = claimMetaData,
                        allItems = children
                    )
                }
            }

            addFlatOrGroupedChildren(
                allItems = allItems,
                children = children,
                groupKey = groupKey,
                displayTitle = getReadableNameFromIdentifier(
                    claimMetaData = claimMetaData,
                    userLocale = Locale.getDefault(),
                    fallback = groupKey
                )
            ) {
                childKey.isEmpty()
            }
        }

        else -> {
            allItems.add(
                IssuedDocumentClaimDomain(
                    identifier = childKey.ifEmpty { groupKey },
                    value = item.toIssuedDocumentClaimValue(groupKey),
                    displayTitle = childKey.ifEmpty {
                        getReadableNameFromIdentifier(
                            claimMetaData = claimMetaData,
                            userLocale = Locale.getDefault(),
                            fallback = groupKey
                        )
                    },
                    children = emptyList()
                )
            )
        }
    }
}

private fun Any.toIssuedDocumentClaimValue(
    groupKey: String,
): IssuedDocumentClaimDomain.Value {
    val base64Image = (this as? ByteArray)?.encodeToBase64String(Base64.URL_SAFE)

    val date: String? = (this as? String)?.toDateFormatted()
        ?: (this as? LocalDate)?.toDateFormatted()

    return when {
        base64Image != null -> IssuedDocumentClaimDomain.Value.Base64Image(base64Image)
        keyIsGender(groupKey) -> IssuedDocumentClaimDomain.Value.Gender(this.toString())
        keyIsUserPseudonym(groupKey) -> IssuedDocumentClaimDomain.Value.Text(
            toString().decodeFromBase64()
        )
        date != null -> IssuedDocumentClaimDomain.Value.Date(date)
        this is Boolean || this.toString()
            .toBooleanStrictOrNull() != null -> IssuedDocumentClaimDomain.Value.BooleanValue(
            this.toString().toBoolean()
        )
        else -> IssuedDocumentClaimDomain.Value.Text(toString())
    }
}

private fun insertIssuedDocumentClaimPath(
    tree: List<IssuedDocumentClaimDomain>,
    path: ClaimPathDomain,
    disclosurePath: ClaimPathDomain,
    claims: List<DocumentClaim>
): List<IssuedDocumentClaimDomain> {
    if (path.value.isEmpty()) return tree

    val userLocale = Locale.getDefault()

    val key = path.value.first()

    val existingNode = tree.find { it.identifier == key }

    val currentClaim: DocumentClaim? = claims.find { it.identifier == key }

    return if (path.value.size == 1) {
        if (existingNode == null && currentClaim?.value != null) {
            val accumulatedClaims: MutableList<IssuedDocumentClaimDomain> = mutableListOf()
            createIssuedDocumentClaim(
                item = currentClaim.value!!,
                groupKey = currentClaim.identifier,
                claimMetaData = currentClaim.issuerMetadata,
                allItems = accumulatedClaims,
            )
            tree + accumulatedClaims
        } else {
            tree
        }
    } else {
        val childClaims =
            (claims.find { key == it.identifier } as? SdJwtVcClaim)?.children ?: claims
        val updatedNode = if (existingNode?.children?.isNotEmpty() == true) {
            existingNode.copy(
                children = insertIssuedDocumentClaimPath(
                    tree = existingNode.children,
                    path = ClaimPathDomain(path.value.drop(1)),
                    disclosurePath = disclosurePath,
                    claims = childClaims,
                )
            )
        } else {
            IssuedDocumentClaimDomain(
                identifier = currentClaim?.identifier ?: key,
                value = null,
                displayTitle = getReadableNameFromIdentifier(
                    claimMetaData = currentClaim?.issuerMetadata,
                    userLocale = userLocale,
                    fallback = currentClaim?.identifier ?: key
                ),
                children = insertIssuedDocumentClaimPath(
                    tree = emptyList(),
                    path = ClaimPathDomain(path.value.drop(1)),
                    disclosurePath = disclosurePath,
                    claims = childClaims,
                )
            )
        }

        tree.filter { it.identifier != key } + updatedNode
    }
}

fun transformPathsToIssuedDocumentClaims(
    paths: List<ClaimPathDomain>,
    claims: List<DocumentClaim>,
): List<IssuedDocumentClaimDomain> {
    return paths.fold<ClaimPathDomain, List<IssuedDocumentClaimDomain>>(initial = emptyList()) { acc, path ->
        insertIssuedDocumentClaimPath(
            tree = acc,
            path = path,
            disclosurePath = path,
            claims = claims,
        )
    }.removeEmptyIssuedDocumentClaimGroups()
        .sortIssuedDocumentClaimsRecursivelyBy {
            it.displayTitle.lowercase()
        }
}

private fun List<IssuedDocumentClaimDomain>.removeEmptyIssuedDocumentClaimGroups(): List<IssuedDocumentClaimDomain> {
    return mapNotNull { claim ->
        val children = claim.children.removeEmptyIssuedDocumentClaimGroups()
        when {
            claim.value?.isNotBlank() == true -> claim.copy(children = children)
            children.isNotEmpty() -> claim.copy(children = children)
            else -> null
        }
    }
}

private fun <T : Comparable<T>> List<IssuedDocumentClaimDomain>.sortIssuedDocumentClaimsRecursivelyBy(
    selector: (IssuedDocumentClaimDomain) -> T
): List<IssuedDocumentClaimDomain> {
    return map { claim ->
        claim.copy(
            children = claim.children.sortIssuedDocumentClaimsRecursivelyBy(selector)
        )
    }.sortedBy(selector)
}
