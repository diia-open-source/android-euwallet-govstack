package ua.govstack.digitalwallet.app.wallet_core.security.certificate

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

@Throws(Exception::class)
fun convertStringToX509Cert(certificate: String): X509Certificate {
    val targetStream: InputStream = ByteArrayInputStream(certificate.toByteArray())
    return CertificateFactory
        .getInstance("X509")
        .generateCertificate(targetStream) as X509Certificate
}