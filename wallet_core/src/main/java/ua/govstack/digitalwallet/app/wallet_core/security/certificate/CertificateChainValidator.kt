package ua.govstack.digitalwallet.app.wallet_core.security.certificate

import java.security.cert.CertPathBuilder
import java.security.cert.CertStore
import java.security.cert.CollectionCertStoreParameters
import java.security.cert.PKIXBuilderParameters
import java.security.cert.PKIXCertPathBuilderResult
import java.security.cert.TrustAnchor
import java.security.cert.X509CertSelector
import java.security.cert.X509Certificate

internal class CertificateChainValidator(
    private val trustedCertificates: List<X509Certificate>
) {

    fun createTrustPath(chain: List<X509Certificate>): List<X509Certificate>? {
        return buildTrustPath(chain)?.let { result ->
            buildList {
                addAll(result.certPath.certificates.filterIsInstance<X509Certificate>())
                result.trustAnchor.trustedCert?.let(::add)
            }
        }
    }

    fun isTrusted(chain: List<X509Certificate>): Boolean {
        return buildTrustPath(chain) != null
    }

    private fun buildTrustPath(chain: List<X509Certificate>): PKIXCertPathBuilderResult? {
        if (chain.isEmpty() || trustedCertificates.isEmpty()) return null

        val leafCertificate = chain.findLeafCertificate() ?: return null
        val trustAnchors = trustedCertificates.map { certificate ->
            TrustAnchor(certificate, null)
        }.toSet()

        return runCatching {
            val selector = X509CertSelector().apply {
                certificate = leafCertificate
            }

            val certStore = CertStore.getInstance(
                "Collection",
                CollectionCertStoreParameters((chain + trustedCertificates).distinct())
            )

            val parameters = PKIXBuilderParameters(trustAnchors, selector).apply {
                isRevocationEnabled = false
                addCertStore(certStore)
            }

            CertPathBuilder.getInstance("PKIX")
                .build(parameters) as PKIXCertPathBuilderResult
        }.getOrNull()
    }

    private fun List<X509Certificate>.findLeafCertificate(): X509Certificate? {
        return firstOrNull { candidate ->
            candidate.basicConstraints < 0 || none { other ->
                other != candidate &&
                    other.subjectX500Principal == candidate.issuerX500Principal
            }
        }
    }
}
