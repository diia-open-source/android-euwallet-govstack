# Menu

Gradle path: `:features:menu`  
Type: shared feature module

---

## Description

`menu` provides the application menu flow used by Diia GovStack Wallet. It contains menu UI, menu actions, menu navigation wiring, and the host integration contract required to route users into settings, support, Diia ID, app sessions, logout, and transaction-related destinations.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide the main menu UI flow;
- expose menu actions and menu content composition;
- define menu navigation contracts and required navigation IDs;
- define the host integration contract through `MenuHelper`;
- connect menu flows with app-level destinations such as settings, support, Diia ID, app sessions, and transactions.

---

## Out of Scope

- wallet runtime configuration belongs to `:wallet:wallet_core`;
- actual destination implementations for support, FAQ, settings, or app sessions belong to the host application or other feature modules;
- generic shared UI primitives belong to `:libs:ui_base`.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.features.menu)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:libs:core` | Shared platform contracts and utilities |
| `:libs:diia_storage` | Shared storage integration used by menu-related flows |
| `:libs:ui_base` | Shared UI building blocks used by menu screens |
| `:wallet:wallet_core` | Wallet-related menu integration points used by the current project |

---

## Public Contract

The host application should include the menu navigation graph:

```xml
<include app:graph="@navigation/nav_menu_actions" />
```

The graph is provided in:

```text
src/main/res/navigation/nav_menu_actions.xml
```

The host application must also expose required navigation IDs declared in:

```text
src/main/res/values/nav_ids.xml
```

Current declared IDs:

```text
nav_host
supportBF
helpBaseF
settingsF
nav_faq
nav_diia_id
nav_app_sessions
```

The host application should also provide a `MenuHelper` implementation for menu-driven routing:

- `navigateToSettings(...)`
- `navigateToLogout(...)`
- `navigateToDiiaId(...)`
- `navigateToAppSessions(...)`
- `navigateToSettingsSystemDialog(...)`
- `navigateToTransaction(...)`

---

## How to Use

1. Add the module dependency.
2. Include `nav_menu_actions.xml` in the host navigation setup.
3. Provide required navigation IDs from `nav_ids.xml`.
4. Provide a host-level `MenuHelper` implementation for menu-driven routing.
5. Route users into the menu flow from the app shell or relevant host screen.

---

## Internal Structure

```text
menu/src/main/java/ua/govstack/digitalwallet/app/menu/
├── helper        # Host integration contract for menu navigation
├── models        # Menu-related models and event types
├── navigation    # Menu navigation helpers
├── ui            # Menu screens, route definitions, and ViewModel
└── MenuContentController.kt   # Menu content composition/controller logic
```

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `menu` were modified or extended in order to support:

- wallet-related menu actions and routing;
- project-specific app navigation structure;
- integration with transaction and wallet-oriented destinations;
- current host application requirements and menu composition needs.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Guidelines

- keep reusable menu flow behavior here when it is shared across the app;
- keep app-specific destination implementation in the host layer instead of hardcoding it into the module;
- prefer host integration through `MenuHelper` and navigation contracts rather than direct coupling to application screens;
- keep wallet-specific menu additions understandable and scoped.

---

## Related Modules

- `:apps:opensource` - host application that provides concrete menu routing
- `:libs:ui_base` - shared UI foundation used by menu screens
- `:wallet:wallet_transaction` - transaction destination reached from menu actions
- `:wallet:wallet_core` - wallet-related integration points used by the current project

---

## Notes and Limitations

- this module defines menu flow and routing contracts, but the host application owns several concrete destinations;
- project-specific adaptations should stay focused on menu and host integration requirements rather than becoming a general routing layer.
