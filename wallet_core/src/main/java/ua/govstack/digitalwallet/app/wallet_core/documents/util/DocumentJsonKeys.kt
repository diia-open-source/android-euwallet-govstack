package ua.govstack.digitalwallet.app.wallet_core.documents.util

object DocumentJsonKeys {
    const val NATIONALITY = "nationality"
    const val NATIONALITY_FOR_DISPLAY = "nationality_for_display"
    const val PORTRAIT = "portrait"
    const val SIGNATURE = "signature_usual_mark"
    const val USER_PSEUDONYM = "user_pseudonym"
    const val GENDER = "gender"
    const val SEX = "sex"
    val GENDER_KEYS: List<String> = listOf(GENDER, SEX)
    val BASE64_IMAGE_KEYS: List<String> = listOf(PORTRAIT, SIGNATURE, PICTURE)
    val TECHNICAL_KEYS = listOf("exp", "iat", "cnf", "status", "vct")
    const val PICTURE = "picture"
}