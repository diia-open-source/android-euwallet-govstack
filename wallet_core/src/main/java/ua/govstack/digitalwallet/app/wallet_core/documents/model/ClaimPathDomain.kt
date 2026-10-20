package ua.govstack.digitalwallet.app.wallet_core.documents.model

data class ClaimPathDomain(val value: List<String>) {

    companion object {
        const val PATH_SEPARATOR = ","

        fun toElementIdentifier(itemId: String): String {
            return itemId
                .split(PATH_SEPARATOR)
                .drop(1)
                .first()
        }

        fun toSdJwtVcPath(itemId: String): List<String> {
            return itemId
                .split(PATH_SEPARATOR)
                .drop(1)
        }

        fun List<String>.toClaimPathDomain(): ClaimPathDomain {
            return ClaimPathDomain(value = this)
        }

        /**
         * Checks whether this [ClaimPathDomain] is a prefix of another [ClaimPathDomain].
         *
         * A prefix match means that all elements of this path must appear in the same order
         * and at the beginning of the [other] path. This allows partial matches useful in scenarios
         * where the verifier requests a parent claim (e.g., `"address"`) and the wallet contains
         * deeper nested claims (e.g., `"address.street"`, `"address.city"`).
         *
         * Examples:
         * ```
         * ClaimPath(listOf("address")).isPrefixOf(ClaimPath(listOf("address", "city"))) == true
         * ClaimPath(listOf("claim1", "a")).isPrefixOf(ClaimPath(listOf("claim1", "a", "b"))) == true
         * ClaimPath(listOf("name")).isPrefixOf(ClaimPath(listOf("name_birth"))) == false
         * ```
         *
         * @param other The [ClaimPathDomain] to compare against.
         * @return `true` if this path is a prefix of [other]; `false` otherwise.
         */
        fun ClaimPathDomain.isPrefixOf(other: ClaimPathDomain): Boolean {
            return this.value.size <= other.value.size &&
                    this.value.zip(other.value).all { (a, b) -> a == b }
        }
    }

    fun toId(docId: String): String =
        (listOf(docId) + value).joinToString(separator = PATH_SEPARATOR)
}