# RequestUriConfig is serialized to Base64 with Gson, and PresentationMode
# stores concrete subtype class names via SerializableTypeAdapter.
-keep class ua.govstack.digitalwallet.app.wallet_sharing.domain.model.RequestUriConfig { *; }
-keep class ua.govstack.digitalwallet.app.wallet_sharing.domain.model.PresentationMode$* { *; }
