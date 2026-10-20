package ua.govstack.digitalwallet.app.wallet_sharing.domain.transformer

import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocument
import eu.europa.ec.eudi.iso18013.transfer.response.DisclosedDocuments
import eu.europa.ec.eudi.iso18013.transfer.response.RequestedDocument
import eu.europa.ec.eudi.iso18013.transfer.response.device.MsoMdocItem
import eu.europa.ec.eudi.wallet.document.IssuedDocument
import eu.europa.ec.eudi.wallet.transfer.openId4vp.SdJwtVcItem
import ua.govstack.digitalwallet.app.core.util.UuidProvider
import ua.govstack.digitalwallet.app.ui_base.provider.ResourceProvider
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.toClaimPath
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.toClaimPaths
import ua.govstack.digitalwallet.app.wallet_core.documents.extensions.toSelectiveExpandableListItems
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.docNamespace
import ua.govstack.digitalwallet.app.wallet_core.documents.helper.transformPathsToDomainClaims
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimPathDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ClaimPathDomain.Companion.isPrefixOf
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DocumentPayloadDomain
import ua.govstack.digitalwallet.app.wallet_core.documents.model.DomainDocumentFormat
import ua.govstack.digitalwallet.app.wallet_core.documents.model.ExpandableListItemUi
import ua.govstack.digitalwallet.app.wallet_core.documents.model.Header
import ua.govstack.digitalwallet.app.wallet_core.documents.model.RequestDocumentItemUi
import ua.govstack.digitalwallet.app.wallet_core.documents.util.MdlJsonKeys

object RequestTransformer {

    fun transformToDomainItems(
        storageDocuments: List<IssuedDocument>,
        resourceProvider: ResourceProvider,
        uuidProvider: UuidProvider,
        requestDocuments: List<RequestedDocument>,
    ): Result<List<DocumentPayloadDomain>> = runCatching {
        val resultList = mutableListOf<DocumentPayloadDomain>()

        requestDocuments.forEach { requestDocument ->

            val storageDocument =
                storageDocuments.first { it.id == requestDocument.documentId }

            val claimsPaths = storageDocument.data.claims.flatMap { claim ->
                claim.toClaimPaths()
            }

            val requestedItemsPaths = requestDocument.requestedItems.keys
                .map {
                    it.toClaimPath()
                }

            val filteredPaths = claimsPaths.filter { available ->
                requestedItemsPaths.any { requested ->
                    requested.isPrefixOf(available)
                }
            }

            val domainClaims = transformPathsToDomainClaims(
                paths = filteredPaths,
                claims = storageDocument.data.claims,
                resourceProvider = resourceProvider,
                uuidProvider = uuidProvider
            ).filterDrivingPrivileges()

            if (domainClaims.isNotEmpty()) {
                resultList.add(
                    DocumentPayloadDomain(
                        docName = storageDocument.name,
                        docId = storageDocument.id,
                        domainDocFormat = DomainDocumentFormat.getFormat(
                            format = storageDocument.format,
                            namespace = storageDocument.docNamespace
                        ),
                        docClaimsDomain = domainClaims
                    )
                )
            }
        }

        resultList
    }


    fun transformToUiItems(
        documentsDomain: List<DocumentPayloadDomain>,
    ): List<RequestDocumentItemUi> {
        return documentsDomain.map {
            RequestDocumentItemUi(
                domainPayload = it,
                headerUi = ExpandableListItemUi.NestedListItem(
                    header = Header(headerId = it.docId, it.docName, isSelected = true),
                    nestedItems = it.toSelectiveExpandableListItems(),
                    isExpanded = false,
                ),
            )
        }
    }

    fun createDisclosedDocuments(items: List<RequestDocumentItemUi>): DisclosedDocuments {
        // Collect all selected expanded items from the list
        fun ExpandableListItemUi.collectSingles(): List<ExpandableListItemUi.SingleListItem> =
            when (this) {
                is ExpandableListItemUi.SingleListItem -> {
                    val isSelected = header.isSelected
                    if (isSelected) listOf(this) else emptyList()
                }

                is ExpandableListItemUi.NestedListItem -> nestedItems.flatMap { it.collectSingles() }
            }

        val groupedByDocument = items.map { document ->
            document.domainPayload to document.headerUi.nestedItems.flatMap { uiItem ->
                uiItem.collectSingles()
                    .distinctBy { listItemDataUi ->
                        listItemDataUi.header.headerId // Distinct by item ID to prevent duplicate sending of the same group
                    }
            }
        }

        // Convert to the format required by DisclosedDocuments
        val disclosedDocuments: List<DisclosedDocument> =
            groupedByDocument.mapNotNull { (documentPayload, selectedItemsForDocument) ->

                val mDocItems: MutableList<MsoMdocItem> = mutableListOf()
                val sdJwtItems: MutableList<SdJwtVcItem> = mutableListOf()

                selectedItemsForDocument.forEach { selectedItem ->

                    val selectedItemId = selectedItem.header.headerId

                    when (documentPayload.domainDocFormat) {

                        is DomainDocumentFormat.SdJwtVc -> sdJwtItems.add(
                            SdJwtVcItem(
                                path = ClaimPathDomain.toSdJwtVcPath(selectedItemId)
                            )
                        )

                        is DomainDocumentFormat.MsoMdoc -> mDocItems.add(
                            MsoMdocItem(
                                namespace = (documentPayload.domainDocFormat as DomainDocumentFormat.MsoMdoc).namespace,
                                elementIdentifier = ClaimPathDomain.toElementIdentifier(
                                    selectedItemId
                                )
                            )
                        )
                    }
                }

                val disclosedItems = mDocItems.distinctBy { it.elementIdentifier } + sdJwtItems

                return@mapNotNull if (disclosedItems.isNotEmpty()) {
                    DisclosedDocument(
                        documentId = documentPayload.docId,
                        disclosedItems = disclosedItems,
                        keyUnlockData = null
                    )
                } else {
                    null
                }
            }

        return DisclosedDocuments(disclosedDocuments)
    }

    private fun List<ClaimDomain>.filterDrivingPrivileges(): List<ClaimDomain> {
        return map { item ->
            if(item is ClaimDomain.Group && item.key == MdlJsonKeys.DRIVING_PRIVILEGES) {
                item.copy(items = item.items.filter { it.key == MdlJsonKeys.VEHICLE_CATEGORY_CODE })
            } else item
        }
    }
}