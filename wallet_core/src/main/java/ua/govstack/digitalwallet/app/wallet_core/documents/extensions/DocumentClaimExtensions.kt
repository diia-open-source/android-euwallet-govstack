package ua.govstack.digitalwallet.app.wallet_core.documents.extensions

import eu.europa.ec.eudi.wallet.document.format.DocumentClaim
import eu.europa.ec.eudi.wallet.document.format.MsoMdocClaim
import eu.europa.ec.eudi.wallet.document.format.SdJwtVcClaim
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimPathDomain

/**
 * Converts a [DocumentClaim] to a list of [ClaimPathDomain]s.
 *
 * This function recursively traverses the claim structure to generate a list of paths,
 * where each path leads to a specific claim.
 * Each path is represented as a list of strings, with each string being an identifier of a claim in the hierarchy.
 *
 * @param parentPath The path of the parent claim, used for recursive calls to build up the full path. Defaults to an empty list.
 * @return A list of [ClaimPathDomain]s representing the paths to each claim in the [DocumentClaim] structure.
 *
 * For [SdJwtVcClaim]s, the function will:
 *  - Append the current claim's identifier to the `parentPath` to form the `currentPath`.
 *  - If the claim has no children or its value is a collection, a single [ClaimPathDomain] is created with the `currentPath`.
 *  - If the claim has children and its value is not a collection, it recursively calls `toClaimPaths` on each child,
 *    passing the `currentPath`, and flattens the resulting lists of paths.
 *
 * For [MsoMdocClaim]s, the function will:
 * - Create a [ClaimPathDomain] with a list containing only the claim's identifier.
 */
fun DocumentClaim.toClaimPaths(
    parentPath: List<String> = emptyList()
): List<ClaimPathDomain> {
    return when (this) {
        is SdJwtVcClaim -> {
            val currentPath: List<String> = parentPath + this.identifier
            if (children.isEmpty() || this.value is Collection<*>) {
                listOf(ClaimPathDomain(currentPath))
            } else {
                children.flatMap { child ->
                    child.toClaimPaths(currentPath)
                }
            }
        }

        is MsoMdocClaim -> {
            listOf(ClaimPathDomain(listOf(this.identifier)))
        }
    }
}