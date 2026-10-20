# Context Menu

Gradle path: `:features:context_menu`  
Type: shared feature module

---

## Description

`context_menu` provides the reusable context-menu dialog flow used across Diia GovStack Wallet. It renders a modal action list from `ContextMenuField` items and routes the selected action back into the host or calling feature flow.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide a reusable context-menu dialog destination;
- map `ContextMenuField` items into UI menu entries;
- emit selected menu actions back into the calling flow;
- expose a helper contract for opening context menu, FAQ, support, and rating-related destinations.

---

## Out of Scope

- wallet runtime configuration belongs to `:wallet:wallet_core`;
- feature-specific action handling belongs to the caller;
- broader host navigation orchestration belongs to the application or parent feature module.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.features.contextMenu)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:libs:core` | Shared `ContextMenuField`, action constants, and navigation-related contracts |
| `:libs:ui_base` | Shared bottom-dialog and UI component infrastructure used by the menu dialog |
| AndroidX browser and paging support | Supporting infrastructure used by linked auxiliary flows |

---

## Public Contract

The host application should include the context menu navigation graph:

```xml
<include app:graph="@navigation/nav_context_menu" />
```

The graph is provided in:

```text
src/main/res/navigation/nav_context_menu.xml
```

The dialog destination expects:

```text
items: ua.govstack.digitalwallet.app.core.models.ContextMenuField[]
```

The module also exposes a code-level helper contract:

- `ContextMenuNavigationHelper`

This helper defines navigation entry points for:

- context menu;
- FAQ;
- support;
- rating service flows.

---

## How to Use

1. Add the module dependency.
2. Include `nav_context_menu.xml` in the host navigation setup.
3. Open the dialog by navigating with an `items` argument built from `ContextMenuField` values.
4. Handle the selected action in the calling feature or host flow.

---

## Internal Structure

```text
context_menu/src/main/java/ua/govstack/digitalwallet/app/context_menu/
├── helper       # Navigation helper contracts for context-menu related flows
└── ui           # Bottom dialog fragment and ViewModel for menu rendering and action emission
```

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `context_menu` were modified or extended in order to support:

- wallet-oriented action sheets and document-related context menu flows;
- project-specific modal action handling and navigation behavior;
- reuse of shared action-menu patterns across the wallet app.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Guidelines

- keep this module focused on reusable modal action-menu behavior;
- keep action interpretation in the caller when the behavior belongs to a specific feature;
- avoid moving wallet-specific business logic into the shared dialog module;
- keep helper contracts narrow and reusable across multiple feature flows.

---

## Related Modules

- `:libs:core` - shared menu item contracts and action constants
- `:libs:ui_base` - bottom dialog and reusable UI component infrastructure
- `:wallet:wallet_gallery` - one of the wallet modules that can trigger context-menu flows
- `:apps:opensource` - host application composing navigation graphs and top-level destinations

---

## Notes and Limitations

- this module provides a reusable action-sheet mechanism, not business behavior by itself;
- menu item meaning depends on the caller, so action contracts should stay aligned between the producer and the consumer of `ContextMenuField`.
