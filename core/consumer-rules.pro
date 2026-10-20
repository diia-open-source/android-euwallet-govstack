# Core models are serialized and deserialized across module boundaries and may
# also be persisted locally. Keep field names and structure stable for app
# upgrades and reflective adapters.
-keep class ua.govstack.digitalwallet.app.core.models.** { *; }
