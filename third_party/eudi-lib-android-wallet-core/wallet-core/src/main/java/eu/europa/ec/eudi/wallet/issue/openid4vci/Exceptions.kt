/*
 * Copyright (c) 2024-2025 European Commission
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:JvmMultifileClass

package eu.europa.ec.eudi.wallet.issue.openid4vci

import eu.europa.ec.eudi.openid4vci.CredentialOfferRequestError
import eu.europa.ec.eudi.openid4vci.CredentialOfferRequestException
import eu.europa.ec.eudi.openid4vci.SubmissionOutcome
import org.multipaz.crypto.Algorithm
import org.multipaz.securearea.KeyUnlockData
import org.multipaz.securearea.SecureArea

/**
 * Exception thrown when user authentication is required.
 */
internal class UserAuthRequiredException(
    val signingAlgorithm: Algorithm,
    val keysAndSecureAreas: Map<KeyAlias, SecureArea>,
    val resume: suspend (Map<KeyAlias, KeyUnlockData?>) -> SubmitRequest.ResponseResult<SubmissionOutcome>,
    override val cause: Throwable? = null,
) : Throwable()

/**
 * Exception thrown when an issuer cannot be trusted based on signed metadata verification.
 */
class UntrustedIssuerException(
    message: String = "Issuer metadata is not trusted",
    cause: Throwable? = null,
) : Exception(message, cause)

internal fun Throwable.asUntrustedIssuerIfNeeded(): Throwable {
    if (this is UntrustedIssuerException) return this
    if (isSignedMetadataValidationFailure() || isRequireSignedFailure()) {
        return UntrustedIssuerException(cause = this)
    }
    return this
}

private fun Throwable.isSignedMetadataValidationFailure(): Boolean {
    val offerException = this as? CredentialOfferRequestException ?: return false
    val metadataError =
        offerException.error as? CredentialOfferRequestError.UnableToResolveCredentialIssuerMetadata
            ?: return false
    return metadataError.reason.isRequireSignedFailure()
}

private fun Throwable.isRequireSignedFailure(): Boolean {
    var current: Throwable? = this
    while (current != null) {
        val className = current::class.java.name
        val message = current.message.orEmpty()

        if (
            className.contains("CredentialIssuerMetadataError") ||
            className.endsWith("MissingSignedMetadata") ||
            className.endsWith("InvalidSignedMetadata") ||
            message.contains("missing signed_metadata", ignoreCase = true) ||
            message.contains("missing 'x5c' header claim", ignoreCase = true) ||
            message.contains(
                "certificate chain in 'x5c' header claim is not trusted",
                ignoreCase = true
            )
        ) {
            return true
        }
        current = current.cause
    }
    return false
}
