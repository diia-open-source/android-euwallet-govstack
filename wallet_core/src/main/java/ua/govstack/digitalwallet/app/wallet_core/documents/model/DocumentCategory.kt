package ua.govstack.digitalwallet.app.wallet_core.documents.model

/**
 * Represents a collection of document categories and their associated document identifiers.
 *
 * This class is a value class, meaning it provides a type-safe wrapper around a `Map<DocumentCategory, List<DocumentIdentifier>>`.
 * It allows you to organize documents into categories, where each category is associated with a list of unique document identifiers.
 *
 * The primary purpose of this class is to provide a structured and type-safe way to manage documents categorized by different criteria.
 * It ensures that each category is associated with a distinct list of document identifiers, avoiding potential conflicts or ambiguities.
 *
 * @property value A list lists of [DocumentIdentifier] instances, representing the documents belonging to each category.
 * Each list contains unique identifiers.
 *
 * @constructor Creates a [DocumentCategories] instance from a list of [DocumentIdentifier].
 * It is recommended to ensure that document identifiers within each list are unique, though this is not enforced by the class itself.
 *
 */
@JvmInline
value class DocumentCategories(
    val value: List<DocumentIdentifier>,
)
