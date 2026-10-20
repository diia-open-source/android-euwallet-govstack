package ua.govstack.digitalwallet.app.wallet_core.security.certificate

import android.content.Context
import android.util.Log
import java.security.cert.X509Certificate

interface CertificateResourceHelper {
    /**
     * Loads every parseable certificate file from the dedicated certificate assets directory.
     *
     * Certificates are intentionally stored under assets/certificates.
     */
    fun getCertListFromAssetsDirectory(): List<X509Certificate>
}

class CertificateResourceHelperImpl(
    private val context: Context
) : CertificateResourceHelper {

    override fun getCertListFromAssetsDirectory(): List<X509Certificate> {
        val certificateFiles = listAssetFilesRecursively(CERTIFICATES_ASSET_DIR)
        val certList = mutableListOf<X509Certificate>()

        certificateFiles.forEach { assetPath ->
            val assetContents = readAssetText(assetPath)
            if (assetContents.contains(BEGIN_CERTIFICATE) && assetContents.contains(END_CERTIFICATE)) {
                try {
                    val cert = convertStringToX509Cert(assetContents)
                    certList.add(cert)
                } catch (e: Exception) {
                    Log.d(
                        "CertificateResourceHelper",
                        "Unable to parse asset $assetPath to X509 cert $e"
                    )
                }
            }
        }

        return certList
    }

    private fun readAssetText(assetPath: String): String =
        try {
            context.assets.open(assetPath).bufferedReader().use { it.readText() }
        } catch (_: Exception) {
            ""
        }

    private fun listAssetFilesRecursively(path: String): List<String> {
        val children = context.assets.list(path).orEmpty()
        if (children.isEmpty()) {
            return listOf(path)
        }

        return children.flatMap { child ->
            val childPath = "$path/$child"
            val nestedChildren = context.assets.list(childPath).orEmpty()
            if (nestedChildren.isEmpty()) {
                listOf(childPath)
            } else {
                listAssetFilesRecursively(childPath)
            }
        }
    }

    private companion object {
        const val CERTIFICATES_ASSET_DIR = "certificates"
        const val BEGIN_CERTIFICATE = "-----BEGIN CERTIFICATE-----"
        const val END_CERTIFICATE = "-----END CERTIFICATE-----"
    }
}
