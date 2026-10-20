package ua.govstack.digitalwallet.app.wallet_core.security.certificate

import eu.europa.ec.eudi.openid4vci.CertificateChainTrust
import eu.europa.ec.eudi.openid4vci.IssuerMetadataPolicy
import eu.europa.ec.eudi.openid4vci.IssuerTrust
import java.security.cert.X509Certificate

class IssuerMetadataPolicyProvider(
    private val trustedCertificates: List<X509Certificate>
) {

    private val certificateChainValidator = CertificateChainValidator(trustedCertificates)

    fun create(allowUntrustedIssuers: Boolean): IssuerMetadataPolicy {
        if (allowUntrustedIssuers) {
            return IssuerMetadataPolicy.IgnoreSigned
        }

        val certificateChainTrust = CertificateChainTrust(certificateChainValidator::isTrusted)

        return IssuerMetadataPolicy.RequireSigned(
            issuerTrust = IssuerTrust.ByCertificateChain(certificateChainTrust)
        )
    }
}
