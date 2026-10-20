package ua.govstack.digitalwallet.app.wallet_core.security.certificate

import eu.europa.ec.eudi.iso18013.transfer.readerauth.ReaderTrustStore
import java.security.cert.X509Certificate

class CustomReaderTrustStore(
    private val trustedCertificates: List<X509Certificate>
) : ReaderTrustStore {

    private val certificateChainValidator = CertificateChainValidator(trustedCertificates)

    override fun createCertificationTrustPath(chain: List<X509Certificate>): List<X509Certificate>? =
        certificateChainValidator.createTrustPath(chain)

    override fun validateCertificationTrustPath(chainToDocumentSigner: List<X509Certificate>): Boolean =
        certificateChainValidator.isTrusted(chainToDocumentSigner)
}
