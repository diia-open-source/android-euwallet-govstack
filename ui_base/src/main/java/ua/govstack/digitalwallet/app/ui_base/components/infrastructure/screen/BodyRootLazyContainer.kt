package ua.govstack.digitalwallet.app.ui_base.components.infrastructure.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnAddOptionAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnAddOptionAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPlainAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryDefaultAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryDefaultAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryWideAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnPrimaryWideAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnStrokeDefaultAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnStrokeDefaultAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnWhiteLargeIconAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.BtnWhiteLargeIconAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.ButtonIconCircledLargeAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.button.ButtonIconCircledLargeAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.divider.DividerWithSpace
import ua.govstack.digitalwallet.app.ui_base.components.atom.divider.DividerWithSpaceData
import ua.govstack.digitalwallet.app.ui_base.components.atom.divider.GradientDividerAtom
import ua.govstack.digitalwallet.app.ui_base.components.atom.divider.TableDividerAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.divider.TableDividerAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.media.ArticlePicAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.media.ArticlePicAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.space.SpacerAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.ChipStatusAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.status.ChipStatusAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.GreyTitleAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.GreyTitleAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.LargeTickerAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.LargeTickerAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.SectionTitleAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.SectionTitleAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.SlideBarAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.SlideBarAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TextLabelAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TextLabelAtmData
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TickerAtm
import ua.govstack.digitalwallet.app.ui_base.components.atom.text.TickerAtomData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.UIElementData
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.ContainerType
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.loadItem
import ua.govstack.digitalwallet.app.ui_base.components.molecule.button.BtnLoadIconPlainGroupMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.button.BtnLoadIconPlainGroupMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.button.BtnSlideMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.button.BtnSlideMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.button.SmallButtonPanelMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.button.SmallButtonPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.AlertCardMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.AlertCardMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.AlertCardMlcV2
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.AlertCardMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardImageMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardImageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardMlcV2
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardMlcV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardProgressMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.CardProgressMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.DashboardCardMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.DashboardCardMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.UserCardMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.card.UserCardMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox.CheckboxBtnOrg
import ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox.CheckboxBtnOrgData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox.CheckboxBtnWhiteOrg
import ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox.CheckboxBtnWhiteOrgData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox.CheckboxSquareMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox.CheckboxSquareMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox.TableItemCheckboxMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.checkbox.TableItemCheckboxMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.divider.DividerLineMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.divider.DividerLineMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.doc.DocCoverMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.doc.DocCoverMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.NavigationPanelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.SheetNavigationBarMolecule
import ua.govstack.digitalwallet.app.ui_base.components.molecule.header.SheetNavigationBarMoleculeData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.InputNumberLargeMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.InputNumberLargeMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.InputNumberMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.InputNumberMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.SearchInputV2
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.SearchInputV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.SelectorOrg
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.SelectorOrgData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.SelectorOrgV2
import ua.govstack.digitalwallet.app.ui_base.components.molecule.input.SelectorOrgV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.BtnIconPlainGroupMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.BtnIconPlainGroupMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.ItemReadMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.ItemReadMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.radio.RadioBtnGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.radio.RadioBtnGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.radio.RadioBtnGroupOrgV2
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.radio.RadioBtnGroupOrgV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.radio.SingleChoiceMlcl
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.radio.SingleChoiceMlclData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.ContentGroupMolecule
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.ContentGroupMoleculeData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.contentgroup.TableBlockOrgV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock.TableSecondaryHeadingMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.tableblock.TableSecondaryHeadingMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.loading.CircularLoaderBodyBlock
import ua.govstack.digitalwallet.app.ui_base.components.molecule.loading.LinearLoadingMolecule
import ua.govstack.digitalwallet.app.ui_base.components.molecule.media.ArticleVideoMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.media.ArticleVideoMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.AttentionIconMessageMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.AttentionIconMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.AttentionMessageMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.AttentionMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.FinalScreenBlockMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.FinalScreenBlockMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.MessageMoleculeData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.PaginationMessageMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.PaginationMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.PaymentStatusMessageMolecule
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.PaymentStatusMessageMoleculeData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.StatusMessageMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.StatusMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.StubInfoMessageMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.StubInfoMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.StubMessageMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.message.StubMessageMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.sharing.LinkSharingMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.sharing.LinkSharingMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.DetailsTextDescriptionMolecule
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.DetailsTextDescriptionMoleculeData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.PaymentInfoOrg
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.PaymentInfoOrgData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.PaymentInfoOrgV2
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.PaymentInfoOrgV2Data
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.PlainDetailsBlockMolecule
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.PlainDetailsBlockMoleculeData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubTitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubTitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubtitleLabelMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.SubtitleLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TextLabelContainerMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TextLabelContainerMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TextLabelMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TextLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TimerMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TimerMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleCentralizedMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleLabelIconMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleLabelIconMlcData
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleLabelMlc
import ua.govstack.digitalwallet.app.ui_base.components.molecule.text.TitleLabelMlcData
import ua.govstack.digitalwallet.app.ui_base.components.organism.FileUploadGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.FileUploadGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.FullScreenVideoOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.FullScreenVideoOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.GroupFilesAddOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.GroupFilesAddOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.MediaUploadGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.MediaUploadGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.SingleMediaUploadGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.SingleMediaUploadGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.accordion.AccordionOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.accordion.AccordionOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.block.AttachmentsOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.block.AttachmentsOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.block.TextBlockOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.block.TextBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.BottomGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.BottomGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.calendar.CalendarOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.calendar.CalendarOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.ArticlePicCarouselOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.ArticlePicCarouselOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.BankingCardCarouselOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.BankingCardCarouselOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.CardHorizontalScrollOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.CardHorizontalScrollOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.CardImageCarouselOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.CardImageCarouselOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.ImageCardCarouselOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.carousel.ImageCardCarouselOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.checkbox.SmallCheckIconOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.checkbox.SmallCheckIconOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.chip.CenterChipBlackTabsOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.chip.CenterChipBlackTabsOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.chip.ChipTabsOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.chip.ChipTabsOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.BackgroundWhiteOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.BackgroundWhiteOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.InputBlockOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.InputBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.UpdatedContainerOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.container.UpdatedContainerOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocActivateCardOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocActivateCardOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocHeadingOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.DocHeadingOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.TableBlockOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.TableBlockOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.TableBlockPlaneOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.TableBlockPlaneOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.TableBlockTwoColumnsOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.document.TableBlockTwoColumnsOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.MediaTitleOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.MediaTitleOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.header.TopGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.input.EditAutomaticallyDeterminedValueOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.input.QuestionFormsOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.input.QuestionFormsOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.input.QuestionFormsOrgDataLocal
import ua.govstack.digitalwallet.app.ui_base.components.organism.input.QuestionFormsOrgLocal
import ua.govstack.digitalwallet.app.ui_base.components.organism.input.RecursiveContainerOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.input.RecursiveContainerOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.CardsListOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.CheckboxRoundGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.CheckboxRoundGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.CheckboxRoundGroupOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.CheckboxRoundGroupOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.CheckboxRoundSeeAllOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.CheckboxRoundSeeAllOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ChipBlackGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ChipBlackGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.DownloadListGroupOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.DownloadListGroupOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ItemListViewOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ItemListViewOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.ListItemGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.MessageListOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.MessageListState
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.MultipleChoiceGroupOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.MultipleChoiceGroupOrganismDataData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.OutlineButtonOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.OutlineButtonOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.PaginatedCardListOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.PlainListWithSearchOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.PlainListWithSearchOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.SingleChoiceWithAdditionalInputOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.SingleChoiceWithAdditionalInputOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.SingleChoiceWithAltOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.SingleChoiceWithAltOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.SingleChoiceWithButtonOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.SingleChoiceWithButtonOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.SingleChoiceWithSearchOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.SingleChoiceWithSearchOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.loadItems
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.loadListItemGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.loadMessageListOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.loadPaginatedCardListOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.pagination.PaginationListOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.pagination.PaginationListWhiteOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.pagination.SimplePagination
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.pagination.loadPaginationListOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.list.pagination.loadPaginationListWhiteOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.media.MediaGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.media.MediaGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.pager.DocCarouselOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.pager.DocCarouselOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.photo.PhotoGroupOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.photo.PhotoGroupOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.qr.LinkQrShareOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.qr.LinkQrShareOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.qr.QrCodeOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.qr.QrCodeOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.radio.RadioBtnWithAltOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.radio.RadioBtnWithAltOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.sharing.LinkSharingOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.sharing.LinkSharingOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.sharing.SharingCodesOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.sharing.SharingCodesOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.table.ContentTableOrganism
import ua.govstack.digitalwallet.app.ui_base.components.organism.table.ContentTableOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.organism.table.TableAccordionOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.table.TableAccordionOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.table.TableBlockAccordionOrg
import ua.govstack.digitalwallet.app.ui_base.components.organism.table.TableBlockAccordionOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.DashboardCardTileOrgData
import ua.govstack.digitalwallet.app.ui_base.components.organism.tile.loadTileItems
import ua.govstack.digitalwallet.app.ui_base.components.theme.ColumbiaBlue
import ua.govstack.digitalwallet.app.ui_base.mappers.loader.mapToLoader

@Composable
fun ColumnScope.BodyRootLazyContainer(
    modifier: Modifier = Modifier,
    bodyViews: SnapshotStateList<UIElementData>,
    displayBlockDivider: Boolean = false,
    connectivityState: Boolean = true,
    containerType: ContainerType = ContainerType.PUBLIC_SERVICE,
    progressIndicator: Pair<String, Boolean> = Pair("", false),
    contentLoaded: Pair<String, Boolean> = Pair("", false),
    lazyListState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(bottom = 16.dp),
    useGradientBg: Boolean = false,
    onUIAction: (UIAction) -> Unit
) {
    var displayBottomGradient by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val messageListState = remember { MessageListState() }

    LaunchedEffect(
        key1 = lazyListState.canScrollBackward,
        key2 = lazyListState.canScrollForward
    ) {
        displayBottomGradient = displayBlockDivider && lazyListState.canScrollForward
    }

    val withDividers = bodyViews.firstOrNull {
        it is PaginationListWhiteOrgData
    }?.let {
        return@let when (it) {
            is PaginationListWhiteOrgData -> it.withDividers
            else -> null
        }
    }

    val paddingModeTop = bodyViews.firstOrNull {
        it is PaginationListWhiteOrgData
    }?.let {
        return@let when (it) {
            is PaginationListWhiteOrgData -> it.paddingTop
            else -> null
        }
    }
    val paddingModeHorizontal = bodyViews.firstOrNull {
        it is PaginationListWhiteOrgData
    }?.let {
        return@let when (it) {
            is PaginationListWhiteOrgData -> it.paddingHorizontal
            else -> null
        }
    }
    val paginationItems = bodyViews.firstOrNull {
        it is PaginatedCardListOrgData ||
            it is PaginationListOrgData ||
            it is PaginationListWhiteOrgData ||
            it is MessageListOrganismData
    }?.let {
        return@let when (it) {
            is PaginatedCardListOrgData -> it.items.collectAsLazyPagingItems()
            is PaginationListOrgData -> it.pagedItems.collectAsLazyPagingItems()
            is PaginationListWhiteOrgData -> it.items.collectAsLazyPagingItems()
            is MessageListOrganismData -> it.items.collectAsLazyPagingItems()
            else -> null
        }
    }

    val context = LocalContext.current

    Box(
        modifier = modifier
            .weight(1f)
            .semantics {
                isTraversalGroup = true
            },
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .semantics(mergeDescendants = true) {  },
            state = lazyListState,
            contentPadding = contentPadding,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bodyViews.forEachIndexed { index, element ->
                val elementModifier =
                    Modifier.padding(top = getTopPadding(index == 0, element))
                when (element) {
                    is SheetNavigationBarMoleculeData -> {
                        loadItem(contentType = SheetNavigationBarMoleculeData::class) {
                            SheetNavigationBarMolecule(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TitleLabelMlcData -> {
                        loadItem(TitleLabelMlcData::class) {
                            TitleLabelMlc(data = element)
                        }
                    }

                    is TitleCentralizedMlcData -> {
                        loadItem(TitleCentralizedMlcData::class) {
                            TitleCentralizedMlc(data = element)
                        }
                    }

                    is SubTitleCentralizedMlcData -> {
                        loadItem(SubTitleCentralizedMlcData::class) {
                            SubTitleCentralizedMlc(data = element)
                        }
                    }

                    is GreyTitleAtmData -> {
                        loadItem(GreyTitleAtmData::class) {
                            GreyTitleAtm(
                                data = element
                            )
                        }
                    }

                    is ItemReadMlcData -> {
                        loadItem(ItemReadMlcData::class) {
                            ItemReadMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SubtitleLabelMlcData -> {
                        loadItem(SubtitleLabelMlcData::class) {
                            SubtitleLabelMlc(
                                modifier = modifier,
                                data = element,
                                existForBody = true
                            )
                        }
                    }

                    is SectionTitleAtmData -> {
                        loadItem(SectionTitleAtmData::class) {
                            SectionTitleAtm(
                                modifier = modifier,
                                data = element
                            )
                        }
                    }

                    is PlainListWithSearchOrganismData -> {
                        loadItem(PlainListWithSearchOrganismData::class) {
                            PlainListWithSearchOrganism(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TextLabelMlcData -> {
                        loadItem(TextLabelMlcData::class) {
                            TextLabelMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is DocHeadingOrgData -> {
                        loadItem(DocHeadingOrgData::class) {
                            DocHeadingOrg(
                                modifier = modifier.padding(horizontal = 8.dp),
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is DocActivateCardOrgData -> {
                        loadItem(DocActivateCardOrgData::class) {
                            DocActivateCardOrg(
                                modifier = modifier.padding(horizontal = 8.dp),
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is FileUploadGroupOrgData -> {
                        loadItem(FileUploadGroupOrgData::class) {
                            FileUploadGroupOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is GroupFilesAddOrgData -> {
                        loadItem(GroupFilesAddOrgData::class) {
                            GroupFilesAddOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CalendarOrgData -> {
                        loadItem(CalendarOrgData::class) {
                            CalendarOrg(
                                data = element,
                                onUIAction = onUIAction,
                                progressIndicator = progressIndicator
                            )
                        }
                    }

                    is TextLabelContainerMlcData -> {
                        loadItem(TextLabelContainerMlcData::class) {
                            TextLabelContainerMlc(
                                data = element,
                                onUIAction = onUIAction,
                                isFirstAtBody = index == 0
                            )
                        }
                    }

                    is ArticlePicAtmData -> {
                        loadItem(ArticlePicAtmData::class) {
                            ArticlePicAtm(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is ArticlePicCarouselOrgData -> {
                        loadItem(ArticlePicCarouselOrgData::class) {
                            ArticlePicCarouselOrg(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BankingCardCarouselOrgData -> {
                        loadItem(BankingCardCarouselOrgData::class) {
                            BankingCardCarouselOrg(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is ImageCardCarouselOrgData -> {
                        loadItem(ImageCardCarouselOrgData::class) {
                            ImageCardCarouselOrg(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CardImageCarouselOrgData -> {
                        loadItem(CardImageCarouselOrgData::class) {
                            CardImageCarouselOrg(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CardImageMlcData -> {
                        loadItem(CardImageMlcData::class) {
                            CardImageMlc(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is ArticleVideoMlcData -> {
                        loadItem(ArticleVideoMlcData::class) {
                            ArticleVideoMlc(
                                modifier = modifier,
                                data = element,
                                connectivityState = connectivityState,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TickerAtomData -> {
                        loadItem(TickerAtomData::class) {
                            TickerAtm(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SingleMediaUploadGroupOrgData -> {
                        loadItem(SingleMediaUploadGroupOrgData::class) {
                            SingleMediaUploadGroupOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is MediaUploadGroupOrgData -> {
                        loadItem(MediaUploadGroupOrgData::class) {
                            MediaUploadGroupOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is MediaGroupOrgData -> {
                        loadItem(MediaGroupOrgData::class) {
                            MediaGroupOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is FullScreenVideoOrgData -> {
                        loadItem(FullScreenVideoOrgData::class) {
                            FullScreenVideoOrg(
                                data = element,
                                loader = mapToLoader(progressIndicator, contentLoaded),
                                connectivityState = connectivityState,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CheckboxBtnOrgData -> {
                        loadItem(CheckboxBtnOrgData::class) {
                            CheckboxBtnOrg(
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is AttentionMessageMlcData -> {
                        loadItem(AttentionMessageMlcData::class) {
                            AttentionMessageMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is AttentionIconMessageMlcData -> {
                        loadItem(AttentionIconMessageMlcData::class) {
                            AttentionIconMessageMlc(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SelectorOrgData -> {
                        loadItem(SelectorOrgData::class) {
                            SelectorOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SelectorOrgV2Data -> {
                        loadItem(SelectorOrgV2Data::class) {
                            SelectorOrgV2(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is StubInfoMessageMlcData -> {
                        loadItem(StubInfoMessageMlcData::class) {
                            StubInfoMessageMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is FinalScreenBlockMlcData -> {
                        loadItem(FinalScreenBlockMlcData::class) {
                            FinalScreenBlockMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SingleChoiceWithSearchOrganismData -> {
                        loadItem(SingleChoiceWithSearchOrganismData::class) {
                            SingleChoiceWithSearchOrganism(
                                modifier = elementModifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SingleChoiceWithAltOrganismData -> {
                        loadItem(SingleChoiceWithAltOrganismData::class) {
                            SingleChoiceWithAltOrganism(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SingleChoiceWithAdditionalInputOrgData -> {
                        loadItem(SingleChoiceWithAdditionalInputOrgData::class) {
                            SingleChoiceWithAdditionalInputOrg(
                                data = element,
                                onUIAction = onUIAction,
                            )
                        }
                    }

                    is MultipleChoiceGroupOrganismDataData -> {
                        loadItem(MultipleChoiceGroupOrganismDataData::class) {
                            MultipleChoiceGroupOrganism(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is QuestionFormsOrgData -> {
                        loadItem(QuestionFormsOrgData::class) {
                            QuestionFormsOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is QuestionFormsOrgDataLocal -> {
                        loadItem(QuestionFormsOrgDataLocal::class) {
                            QuestionFormsOrgLocal(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SearchInputV2Data -> {
                        loadItem(SearchInputV2Data::class) {
                            SearchInputV2(
                                data = element,
                                focused = true,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is ItemListViewOrgData -> {
                        loadItem(ItemListViewOrgData::class) {
                            ItemListViewOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is DownloadListGroupOrganismData -> {
                        loadItem(DownloadListGroupOrganismData::class) {
                            DownloadListGroupOrganism(
                                modifier = elementModifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is OutlineButtonOrgData -> {
                        loadItem(OutlineButtonOrgData::class) {
                            OutlineButtonOrg(
                                data = element,
                                onUIAction = onUIAction,
                                progressIndicator = progressIndicator,
                            )
                        }
                    }

                    is ContentTableOrganismData -> {
                        loadItem(ContentTableOrganismData::class) {
                            ContentTableOrganism(
                                modifier = elementModifier,
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is ContentGroupMoleculeData -> {
                        loadItem(ContentGroupMoleculeData::class) {
                            ContentGroupMolecule(
                                modifier = elementModifier,
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BtnPrimaryDefaultAtmData -> {
                        loadItem(BtnPrimaryDefaultAtmData::class) {
                            BtnPrimaryDefaultAtm(
                                data = element,
                                loader = mapToLoader(progress = progressIndicator),
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BtnPrimaryWideAtmData -> {
                        loadItem(BtnPrimaryWideAtmData::class) {
                            BtnPrimaryWideAtm(
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BtnStrokeDefaultAtmData -> {
                        loadItem(BtnPrimaryDefaultAtmData::class) {
                            BtnStrokeDefaultAtm(
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BtnPlainAtmData -> {
                        loadItem(BtnPlainAtmData::class) {
                            BtnPlainAtm(
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is StatusMessageMlcData -> {
                        loadItem(StatusMessageMlcData::class) {
                            StatusMessageMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is DetailsTextDescriptionMoleculeData -> {
                        loadItem(DetailsTextDescriptionMoleculeData::class) {
                            DetailsTextDescriptionMolecule(
                                modifier = elementModifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is PaymentInfoOrgData -> {
                        loadItem(PaymentInfoOrgData::class) {
                            PaymentInfoOrg(
                                data = element
                            )
                        }
                    }

                    is PaymentInfoOrgV2Data -> {
                        loadItem(PaymentInfoOrgData::class) {
                            PaymentInfoOrgV2(
                                data = element
                            )
                        }
                    }

                    is StubMessageMlcData -> {
                        loadItem(StubMessageMlcData::class) {
                            StubMessageMlc(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is PaginationMessageMlcData -> {
                        loadItem(PaginationMessageMlcData::class) {
                            PaginationMessageMlc(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is DocCarouselOrgData -> {
                        loadItem(DocCarouselOrgData::class) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                DocCarouselOrg(
                                    data = element,
                                    progressIndicator = progressIndicator,
                                    onUIAction = onUIAction
                                )
                            }
                        }
                    }

                    is PaymentStatusMessageMoleculeData -> {
                        loadItem(PaymentStatusMessageMoleculeData::class) {
                            PaymentStatusMessageMolecule(
                                modifier = elementModifier,
                                data = element
                            )
                        }
                    }

                    is BtnIconPlainGroupMlcData -> {
                        loadItem(BtnIconPlainGroupMlcData::class) {
                            BtnIconPlainGroupMlc(
                                modifier = elementModifier,
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BtnLoadIconPlainGroupMlcData -> {
                        loadItem(BtnLoadIconPlainGroupMlcData::class) {
                            BtnLoadIconPlainGroupMlc(
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is ua.govstack.digitalwallet.app.ui_base.components.molecule.button.BtnIconPlainGroupMlcData -> {
                        loadItem(ua.govstack.digitalwallet.app.ui_base.components.molecule.button.BtnIconPlainGroupMlcData::class) {
                            ua.govstack.digitalwallet.app.ui_base.components.molecule.button.BtnIconPlainGroupMlc(
                                data = element,
                                loader = mapToLoader(progressIndicator),
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is PlainDetailsBlockMoleculeData -> {
                        loadItem(PlainDetailsBlockMoleculeData::class) {
                            PlainDetailsBlockMolecule(
                                modifier = elementModifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SingleChoiceWithButtonOrganismData -> {
                        loadItem(SingleChoiceWithButtonOrganismData::class) {
                            SingleChoiceWithButtonOrganism(
                                modifier = elementModifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SingleChoiceMlclData -> {
                        loadItem(SingleChoiceMlclData::class) {
                            SingleChoiceMlcl(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is RadioBtnGroupOrgData -> {
                        loadItem(RadioBtnGroupOrgData::class) {
                            RadioBtnGroupOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is RadioBtnGroupOrgV2Data -> {
                        loadItem(RadioBtnGroupOrgV2Data::class) {
                            RadioBtnGroupOrgV2(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CardMlcData -> {
                        loadItem(CardMlcData::class) {
                            CardMlc(
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TableBlockOrgData -> {
                        loadItem(TableBlockOrgData::class) {
                            TableBlockOrg(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TextBlockOrgData -> {
                        loadItem(TextBlockOrgData::class) {
                            TextBlockOrg(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CheckboxRoundGroupOrgData -> {
                        loadItem(CheckboxRoundGroupOrgData::class) {
                            CheckboxRoundGroupOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SpacerAtmData -> {
                        loadItem(SpacerAtmData::class) {
                            SpacerAtm(data = element)
                        }
                    }

                    is ChipStatusAtmData -> {
                        loadItem(ChipStatusAtmData::class) {
                            ChipStatusAtm(data = element)
                        }
                    }

                    is TableBlockPlaneOrgData -> {
                        loadItem(TableBlockPlaneOrgData::class) {
                            TableBlockPlaneOrg(
                                modifier = Modifier.padding(horizontal = 8.dp),
                                data = element,
                                existForBody = true,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TableBlockAccordionOrgData -> {
                        loadItem(TableBlockAccordionOrgData::class) {
                            TableBlockAccordionOrg(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TableDividerAtmData -> {
                        loadItem(TableDividerAtmData::class) {
                            TableDividerAtm(
                                data = element
                            )
                        }
                    }

                    is TextBlockOrgData -> {
                        loadItem(TextBlockOrgData::class) {
                            TextBlockOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is DividerLineMlcData -> {
                        loadItem(DividerLineMlcData::class) {
                            DividerLineMlc(
                                data = element
                            )
                        }
                    }

                    is BottomGroupOrgData -> {
                        loadItem(BottomGroupOrgData::class) {
                            BottomGroupOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is RadioBtnWithAltOrgData -> {
                        loadItem(RadioBtnWithAltOrgData::class) {
                            RadioBtnWithAltOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is InputNumberLargeMlcData -> {
                        loadItem(InputNumberLargeMlcData::class) {
                            InputNumberLargeMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is InputNumberMlcData -> {
                        loadItem(InputNumberMlcData::class) {
                            InputNumberMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is MediaTitleOrgData -> {
                        loadItem(MediaTitleOrgData::class) {
                            MediaTitleOrg(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CheckboxRoundGroupOrganismData -> {
                        loadItem(CheckboxRoundGroupOrganismData::class) {
                            CheckboxRoundGroupOrganism(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CheckboxRoundSeeAllOrgData -> {
                        loadItem(CheckboxRoundSeeAllOrgData::class) {
                            CheckboxRoundSeeAllOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SmallButtonPanelMlcData -> {
                        loadItem(SmallButtonPanelMlcData::class) {
                            SmallButtonPanelMlc(data = element, onUIAction = onUIAction)
                        }
                    }

                    is AlertCardMlcData -> {
                        loadItem(AlertCardMlcData::class) {
                            AlertCardMlc(modifier, element, onUIAction)
                        }
                    }

                    is AlertCardMlcV2Data -> {
                        loadItem(AlertCardMlcData::class) {
                            AlertCardMlcV2(modifier, element, onUIAction)
                        }
                    }

                    is DashboardCardMlcData -> {
                        loadItem(DashboardCardMlcData::class) {
                            DashboardCardMlc(modifier, element, onUIAction)
                        }
                    }

                    is TableAccordionOrgData -> {
                        loadItem(TableAccordionOrgData::class) {
                            TableAccordionOrg(modifier, element, onUIAction)
                        }
                    }

                    is AccordionOrgData -> {
                        loadItem(AccordionOrgData::class) {
                            AccordionOrg(modifier, element, onUIAction)
                        }
                    }

                    is DashboardCardTileOrgData -> {
                        loadTileItems(element, configuration.screenWidthDp, onUIAction)
                    }

                    is ListItemGroupOrgData -> {
                        loadListItemGroupOrg(
                            data = element,
                            onUIAction = onUIAction,
                            progressIndicator = progressIndicator
                        )
                    }

                    is RecursiveContainerOrgData -> {
                        loadItem(ListItemGroupOrgData::class) {
                            RecursiveContainerOrg(
                                data = element,
                                onUIAction = onUIAction,
                            )
                        }
                    }

                    is DividerWithSpaceData -> {
                        loadItem(DividerWithSpaceData::class) {
                            DividerWithSpace(
                                data = element
                            )
                        }
                    }

                    is SharingCodesOrgData -> {
                        loadItem(SharingCodesOrgData::class) {
                            SharingCodesOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is UserCardMlcData -> {
                        loadItem(UserCardMlcData::class) {
                            UserCardMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TextLabelAtmData -> {
                        loadItem(TextLabelAtmData::class) {
                            TextLabelAtm(
                                data = element
                            )
                        }
                    }

                    is TableItemCheckboxMlcData -> {
                        loadItem(TableItemCheckboxMlcData::class) {
                            TableItemCheckboxMlc(
                                modifier = modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CardsListOrgData -> {
                        loadItems(
                            item = element,
                            progressIndicator = progressIndicator,
                            onUIAction = onUIAction
                        )
                    }

                    is PaginatedCardListOrgData -> {
                        paginationItems?.let {
                            loadPaginatedCardListOrg(
                                items = paginationItems as LazyPagingItems<CardMlcData>,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is PaginationListOrgData -> {
                        paginationItems?.let {
                            loadPaginationListOrg(
                                modifier = Modifier,
                                items = paginationItems as LazyPagingItems<SimplePagination>,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is PaginationListWhiteOrgData -> {
                        paginationItems?.let {
                            loadPaginationListWhiteOrg(
                                modifier = Modifier,
                                items = paginationItems as LazyPagingItems<SimplePagination>,
                                state = lazyListState,
                                withDividers = withDividers,
                                paddingTop = paddingModeTop,
                                paddingHorizontal = paddingModeHorizontal,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is MessageListOrganismData -> {
                        paginationItems?.let {
                            loadMessageListOrganism(
                                items = paginationItems as LazyPagingItems<MessageMoleculeData>,
                                state = messageListState,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is EditAutomaticallyDeterminedValueOrgData -> {
                        loadItem(EditAutomaticallyDeterminedValueOrgData::class) {
                            ua.govstack.digitalwallet.app.ui_base.components.organism.input.EditAutomaticallyDeterminedValueOrg(
                                data = element,
                                onUIAction = onUIAction,
                            )
                        }
                    }

                    is ChipBlackGroupOrgData -> {
                        loadItem(ChipBlackGroupOrgData::class) {
                            ChipBlackGroupOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is ChipTabsOrgData -> {
                        loadItem(ChipBlackGroupOrgData::class) {
                            ChipTabsOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SmallCheckIconOrgData -> {
                        loadItem(SmallCheckIconOrgData::class) {
                            SmallCheckIconOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is PhotoGroupOrgData -> {
                        loadItem(PhotoGroupOrgData::class) {
                            PhotoGroupOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CardHorizontalScrollOrgData -> {
                        loadItem(CardHorizontalScrollOrgData::class) {
                            CardHorizontalScrollOrg(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CheckboxBtnWhiteOrgData -> {
                        loadItem(CheckboxBtnWhiteOrgData::class) {
                            CheckboxBtnWhiteOrg(
                                data = element,
                                onUIAction = onUIAction,
                                progressIndicator = progressIndicator
                            )
                        }
                    }

                    is CheckboxSquareMlcData -> {
                        loadItem(CheckboxSquareMlcData::class) {
                            CheckboxSquareMlc(
                                data = element,
                                onUIAction = onUIAction,
                            )
                        }
                    }

                    is LargeTickerAtmData -> {
                        loadItem(LargeTickerAtmData::class) {
                            LargeTickerAtm(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is DocCoverMlcData -> {
                        loadItem(DocCoverMlcData::class) {
                            DocCoverMlc(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BackgroundWhiteOrgData -> {
                        loadItem(BackgroundWhiteOrgData::class) {
                            BackgroundWhiteOrg(
                                modifier = Modifier,
                                data = element,
                                loader = mapToLoader(progress = progressIndicator),
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is InputBlockOrgData -> {
                        loadItem(InputBlockOrgData::class) {
                            InputBlockOrg(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is UpdatedContainerOrgData -> {
                        loadItem(UpdatedContainerOrgData::class) {
                            UpdatedContainerOrg(
                                modifier = Modifier,
                                data = element,
                                progressIndicator = progressIndicator,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is SlideBarAtmData -> {
                        loadItem(SlideBarAtmData::class) {
                            SlideBarAtm(
                                modifier = Modifier,
                                data = element,
                            )
                        }
                    }

                    is CardMlcV2Data -> {
                        loadItem(CardMlcV2Data::class) {
                            CardMlcV2(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BtnWhiteLargeIconAtmData -> {
                        loadItem(BtnWhiteLargeIconAtmData::class) {
                            BtnWhiteLargeIconAtm(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TimerMlcData -> {
                        loadItem(TimerMlcData::class) {
                            TimerMlc(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TitleLabelIconMlcData -> {
                        loadItem(TitleLabelIconMlcData::class) {
                            TitleLabelIconMlc(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TableBlockOrgV2Data -> {
                        loadItem(TableBlockOrgV2Data::class) {
                            ua.govstack.digitalwallet.app.ui_base.components.molecule.list.table.items.contentgroup.TableBlockOrgV2(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TableSecondaryHeadingMlcData -> {
                        loadItem(TableSecondaryHeadingMlcData::class) {
                            TableSecondaryHeadingMlc(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is QrCodeOrgData -> {
                        loadItem(QrCodeOrgData::class) {
                            QrCodeOrg(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CenterChipBlackTabsOrgData -> {
                        loadItem(CenterChipBlackTabsOrgData::class) {
                            CenterChipBlackTabsOrg(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is LinkSharingMlcData -> {
                        loadItem(LinkSharingMlcData::class) {
                            LinkSharingMlc(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is LinkSharingOrgData -> {
                        loadItem(LinkSharingOrgData::class) {
                            LinkSharingOrg(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is CardProgressMlcData -> {
                        loadItem(CardProgressMlcData::class) {
                            CardProgressMlc(
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is TableBlockTwoColumnsOrgData -> {
                        loadItem(TableBlockTwoColumnsOrgData::class) {
                            TableBlockTwoColumnsOrg(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BtnSlideMlcData -> {
                        loadItem(BtnSlideMlcData::class) {
                            BtnSlideMlc(data = element, onUIAction = onUIAction)
                        }
                    }

                    is LinkQrShareOrgData -> {
                        loadItem(LinkQrShareOrgData::class) {
                            LinkQrShareOrg(
                                modifier = Modifier,
                                data = element,
                                loader = mapToLoader(progressIndicator),
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is AttachmentsOrgData -> {
                        loadItem(AttachmentsOrgData::class) {
                            AttachmentsOrg(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }

                    is BtnAddOptionAtmData -> {
                        loadItem(BtnAddOptionAtmData::class){
                            BtnAddOptionAtm(
                                modifier = Modifier,
                                data = element,
                                onUIAction = onUIAction
                            )
                        }
                    }
                }
            }
        }
        bodyViews.firstOrNull { it is ButtonIconCircledLargeAtmData }?.let {
            ButtonIconCircledLargeAtm(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 32.dp)
                    .semantics {
                        traversalIndex = -1f
                        contentDescription =
                            context.getString(R.string.accessibility_scan_barcode_with_camera)
                        role = Role.Button
                    },
                data = it as ButtonIconCircledLargeAtmData,
                onUIAction = onUIAction
            )
        }
        GradientDividerContentBlock(displayBottomGradient, containerType, useGradientBg)
        LinearLoaderContentBlock(contentLoaded, onUIAction)
    }
}

private fun getTopPadding(isFirst: Boolean, element: UIElementData): Dp {
    if (isFirst) return 0.dp
    return when (element) {
        is BtnPlainAtmData -> 8.dp
        is DownloadListGroupOrganismData -> 12.dp
        else -> 24.dp
    }
}

@Composable
private fun LinearLoaderContentBlock(
    contentLoaded: Pair<String, Boolean>,
    onUIAction: (UIAction) -> Unit
) {
    if (!contentLoaded.second) {
        if (contentLoaded.first == UIActionKeysCompose.PAGE_LOADING_LINEAR_WITH_LABEL) {
            Column(modifier = Modifier.fillMaxWidth()) {
                TopGroupOrg(
                    data = TopGroupOrgData(
                        navigationPanelMlcData = NavigationPanelMlcData(
                            title = UiText.DynamicString(""),
                            isContextMenuExist = false
                        )
                    ), onUIAction = onUIAction
                )
                LinearLoadingMolecule(
                    labelDisplayed = true
                )
            }

        }
        if (contentLoaded.first == UIActionKeysCompose.PAGE_LOADING_LINEAR_PAGINATION) {
            LinearLoadingMolecule(
                labelDisplayed = false
            )
        }
        if (contentLoaded.first == UIActionKeysCompose.PAGE_LOADING_CIRCULAR_BODY) {
            CircularLoaderBodyBlock()
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun BoxScope.GradientDividerContentBlock(
    displayBottomGradient: Boolean,
    containerType: ContainerType,
    useGradientBg: Boolean = false
) {
    val density = LocalDensity.current
    val isImeVisible = WindowInsets.ime.getBottom(density) > 0

    if (displayBottomGradient && !isImeVisible) {
        if (containerType == ContainerType.PUBLIC_SERVICE) {
            GradientDividerAtom(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter),
                color = if (useGradientBg) Color.White else ColumbiaBlue
            )
        } else {
            GradientDividerAtom(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.BottomCenter),
                color = Color.White
            )
        }
    }
}
