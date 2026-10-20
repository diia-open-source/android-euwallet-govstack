package ua.govstack.digitalwallet.app.wallet_core.wallet.configs

import eu.europa.ec.eudi.wallet.document.CreateDocumentSettings.CredentialPolicy
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier
import kotlin.collections.get

/**
 * Represents the configuration for document issuance.
 *
 * This class defines the rules for issuing documents, including a default rule and
 * specific rules for individual document types.
 *
 * @property defaultRule The default [DocumentIssuanceRule] to be applied when no specific rule
 *                       is defined for a document type.
 * @property documentSpecificRules A map where keys are [ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentIdentifier]s and values are
 *                                  [DocumentIssuanceRule]s, defining specific rules for
 *                                  particular document types.
 */
data class DocumentIssuanceConfig(
    val defaultRule: DocumentIssuanceRule,
    val documentSpecificRules: Map<DocumentIdentifier, DocumentIssuanceRule>
) {

    /**
     * Retrieves the [DocumentIssuanceRule] for a given [DocumentIdentifier].
     *
     * If a specific rule is defined for the provided [documentIdentifier], that rule is returned.
     * Otherwise, the [defaultRule] is returned.
     *
     * @param documentIdentifier The identifier of the document for which to retrieve the rule.
     *                           If null, the [defaultRule] will be returned.
     * @return The [DocumentIssuanceRule] applicable to the given [documentIdentifier], or the
     *         [defaultRule] if no specific rule is found.
     */
    fun getRuleForDocument(documentIdentifier: DocumentIdentifier?): DocumentIssuanceRule =
        documentSpecificRules[documentIdentifier] ?: defaultRule
}

/**
 * Represents a rule for issuing a document.
 *
 * This class encapsulates the policy and the number of credentials associated with a document
 * issuance.
 *
 * @property policy The [CredentialPolicy] to be applied during document issuance.
 * @property numberOfCredentials The number of credentials to be issued for the document.
 */
data class DocumentIssuanceRule(
    val policy: CredentialPolicy,
    val numberOfCredentials: Int,
)