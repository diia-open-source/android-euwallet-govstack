package ua.govstack.digitalwallet.app.home.ui

import org.junit.Before
import org.junit.Test
import org.mockito.MockitoAnnotations
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.tab.TabItemMoleculeData

class HomeScreenComposeMapperImplTest {
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `test mapping of TabItemMoleculeData list to TabBarOrganismData`() {
        val mutableList = mutableListOf<TabItemMoleculeData>()
        val firstObj = TabItemMoleculeData(
            actionKey = "action_first",
            id = "first_item_id",
            label = "First Item",
            iconSelected = UiText.DynamicString("iconSelected"),
            iconUnselected = UiText.DynamicString("iconUnselected"),
            iconSelectedWithBadge = UiText.DynamicString("iconSelectedWithBadge"),
            iconUnselectedWithBadge = UiText.DynamicString("iconUnselectedWithBadge"),
            showBadge = true,
            selectionState = UIState.Selection.Selected
        )
        mutableList.add(firstObj)
    }
}