package ua.govstack.digitalwallet.app.wallet_gallery.domain.transformer

import ua.govstack.digitalwallet.app.core.models.common_compose.table.Item
import ua.govstack.digitalwallet.app.core.models.common_compose.table.TableItemHorizontalMlc
import ua.govstack.digitalwallet.app.core.models.common_compose.table.TableItemVerticalMlc
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.normalizeBase64

object TableItemFactory {

    fun createTableItemVertical(
        componentId: String, displayTitle: String? = null, value: String
    ): Item = Item(
        tableItemVerticalMlc = TableItemVerticalMlc(
            componentId = componentId, label = displayTitle, value = value
        )
    )

    fun createTableItemHorizontal(
        componentId: String, displayTitle: String? = null, value: String
    ): Item = Item(
        tableItemHorizontalMlc = TableItemHorizontalMlc(
            componentId = componentId, label = displayTitle, value = value
        )
    )

    fun createImageItem(id: String, value: String) =
        Item(
            tableItemVerticalMlc = TableItemVerticalMlc(
                componentId = id,
                valueImage = value.normalizeBase64()
            )
        )

}