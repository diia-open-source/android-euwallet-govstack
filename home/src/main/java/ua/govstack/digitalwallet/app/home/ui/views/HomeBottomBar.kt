package ua.govstack.digitalwallet.app.home.ui.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ua.govstack.digitalwallet.app.menu.ui.ScreenMenuRoute
import ua.govstack.digitalwallet.app.ui_base.R
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIAction
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.event.UIActionKeysCompose
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.state.UIState
import ua.govstack.digitalwallet.app.ui_base.components.infrastructure.utils.resource.UiText
import ua.govstack.digitalwallet.app.ui_base.components.molecule.tab.TabItemMoleculeData
import ua.govstack.digitalwallet.app.ui_base.components.organism.bottom.TabBarOrganismData
import ua.govstack.digitalwallet.app.ui_base.components.subatomic.icon.IconWithBadge
import ua.govstack.digitalwallet.app.ui_base.components.theme.Black
import ua.govstack.digitalwallet.app.ui_base.components.theme.DiiaTextStyle
import ua.govstack.digitalwallet.app.ui_base.components.theme.White
import ua.govstack.digitalwallet.app.wallet_gallery.ui.navigation.ScreenDocGalleryRoute


@Composable
fun HomeBottomBar(
    modifier: Modifier = Modifier,
    data: TabBarOrganismData,
    onTabClick: (action: UIAction) -> Unit
) {
    BottomAppBar(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        containerColor = Black
    ) {
        data.tabs.forEach {
            HomeBottomBarItem(
                item = it,
                onTabClick = onTabClick
            )
        }
    }
}

@Composable
fun RowScope.HomeBottomBarItem(
    item: TabItemMoleculeData,
    onTabClick: (action: UIAction) -> Unit
) {
    BottomNavigationItem(
        modifier = Modifier,
        selected = item.selectionState == UIState.Selection.Selected,
        icon = {
           BadgeIconPortrait(item)
        },
        alwaysShowLabel = false,
        onClick = {
            onTabClick(UIAction(actionKey = UIActionKeysCompose.MENU_ITEM_CLICK, data = item.id))
        }
    )
}

@Composable
fun BadgeIconPortrait(item: TabItemMoleculeData) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .semantics {
                val textDescriptions = item.label ?: ""
                val iconDescriptions =
                    if (item.showBadge) context.getString(R.string.accessibility_there_are_new_messages) else ""
                contentDescription = textDescriptions + iconDescriptions
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgeIcon(item)
        Text(
            modifier = Modifier
                .padding(top = 2.dp)
                .clearAndSetSemantics { },
            text = item.label ?: "",
            style = DiiaTextStyle.t5TextSmallDescription,
            color = White
        )
    }
}

@Composable
private fun BadgeIcon(item: TabItemMoleculeData) {
    if (item.showBadge) {
        IconWithBadge(
            modifier = Modifier.size(24.dp),
            image = when (item.selectionState) {
                UIState.Selection.Selected -> item.iconSelectedWithBadge
                    ?: UiText.StringResource(resId = R.drawable.ic_tab_menu_selected_badge)

                UIState.Selection.Unselected -> item.iconUnselectedWithBadge
                    ?: UiText.StringResource(resId = R.drawable.ic_tab_menu_unselected_badge)
            },
            contentDescription = null
        )
    } else {
        IconWithBadge(
            modifier = Modifier.size(24.dp),
            image = when (item.selectionState) {
                UIState.Selection.Selected -> item.iconSelected
                    ?: UiText.StringResource(resId = R.drawable.ic_tab_menu_selected)

                UIState.Selection.Unselected -> item.iconUnselected
                    ?: UiText.StringResource(
                        resId = R.drawable.ic_tab_menu_unselected
                    )
            },
            contentDescription = null
        )
    }
}

private object HomeActions {
    const val HOME_DOCUMENTS = 1
    const val HOME_MENU = 2
}


@Preview
@Composable
fun HomeBottomBarPreview() {

    val tabDocuments = TabItemMoleculeData(
        label = "Documents",
        iconSelected = UiText.StringResource(R.drawable.ic_tab_documents_selected),
        iconUnselected = UiText.StringResource(R.drawable.ic_tab_documents_unselected),
        actionKey = HomeActions.HOME_DOCUMENTS.toString(),
        id = HomeActions.HOME_DOCUMENTS.toString(),
        componentId = UiText.StringResource(R.string.home_tab_documents_test_tag)
    )
    val tabMenu = TabItemMoleculeData(
        label = "Menu",
        iconSelected = UiText.StringResource(R.drawable.ic_tab_menu_selected),
        iconUnselected = UiText.StringResource(R.drawable.ic_tab_menu_unselected),
        iconSelectedWithBadge = UiText.StringResource(R.drawable.ic_tab_menu_selected_badge),
        iconUnselectedWithBadge = UiText.StringResource(R.drawable.ic_tab_menu_unselected_badge),
        actionKey = HomeActions.HOME_MENU.toString(),
        id = HomeActions.HOME_MENU.toString(),
        showBadge = true,
        componentId = UiText.StringResource(R.string.home_tab_menu_test_tag)
    )
    val tabs = listOf(tabDocuments, tabMenu)
    val tabBarData = TabBarOrganismData(
        tabs = SnapshotStateList<TabItemMoleculeData>().apply {
            tabs.forEach {
                add(it)
            }
        }
    )
    val tabBarState = remember { mutableStateOf(tabBarData) }
    val navController = rememberNavController()

    Scaffold(bottomBar = {
        HomeBottomBar(
            data = tabBarState.value,
            onTabClick = { action ->
                tabBarState.value = tabBarState.value.onTabClicked(action.data)
                navController.navigate(
                    when (action.data) {
                        "${HomeActions.HOME_DOCUMENTS}" -> ScreenDocGalleryRoute
                        "${HomeActions.HOME_MENU}" -> ScreenMenuRoute
                        else -> ScreenDocGalleryRoute
                    }
                ) {
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            })
    }, content = { padding ->
        NavHost(
            modifier = Modifier.padding(padding),
            navController = navController,
            startDestination = ScreenDocGalleryRoute
        ) {
            composable<ScreenDocGalleryRoute> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        var text by rememberSaveable { mutableStateOf("Hello") }

                        TextField(
                            value = text,
                            onValueChange = { text = it },
                            label = { Text("Label") }
                        )
                    }


                }

            }
            composable<ScreenMenuRoute> {
                TabPresentation("Menu")
            }
        }
    })
}

@Composable
fun TabPresentation(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(name)
    }
}
