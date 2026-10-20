# UI Base

Gradle path: `:libs:ui_base`  
Type: shared UI library module

---

## Description

`ui_base` provides the shared UI foundation used across Diia GovStack Wallet. It contains reusable Compose and View components, shared fragments, UI infrastructure, visual building blocks, mappers, adapters, and helper utilities used by application and feature screens.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide shared UI components for Compose and View-based screens;
- provide reusable fragments, dialog infrastructure, and navigation helpers;
- provide common card, message, loader, header, checkbox, barcode, and document-related UI molecules;
- provide shared UI mappers and helper utilities used by feature and wallet modules;
- provide shared permission, media, paging, and accessibility-related UI support.

---

## Out of Scope

- business logic should stay in feature, wallet, or domain-specific modules;
- wallet runtime configuration belongs to `:wallet:wallet_core`;
- storage concerns belong to storage or data modules rather than to UI infrastructure.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.libs.uiBase)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:libs:core` | Shared contracts, utilities, and platform-level helpers used by UI infrastructure |

---

## Public Contract

This module exposes shared UI building blocks intended to be consumed by app, feature, and wallet modules.

That includes:

- reusable Compose molecules, atoms, and infrastructure components;
- shared dialogs, bottom-sheet fragments, and base fragment helpers;
- shared document-related UI mappers;
- common UI constants, navigation helpers, and view extensions.

---

## How to Use

1. Add the module dependency.
2. Reuse shared UI components from this module before creating project-local duplicates.
3. Keep module-specific business rules outside `ui_base`, even when using its components.

---

## Internal Structure

```text
ui_base/src/main/java/ua/govstack/digitalwallet/app/ui_base/
├── adapters       # Shared UI adapters and decorations
├── components     # Compose/UI building blocks and infrastructure
├── fragments      # Shared dialogs, sheets, and fragment helpers
├── mappers        # Shared UI and document-oriented mappers
├── models         # Shared UI-facing support models
├── navigation     # Shared navigation helpers
├── provider       # Resource and permission providers
├── util           # View, fragment, accessibility, paging, and other UI helpers
└── views          # Shared custom Views and legacy UI widgets
```

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `ui_base` were modified or extended in order to support:

- wallet-specific document presentation patterns;
- wallet-specific action and full-information mapping flows;
- project-specific dialog, screen, and interaction requirements;
- shared UI behavior needed by the current Android wallet implementation.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Guidelines

- add new shared UI primitives here only when they are reusable across multiple modules;
- do not move feature-specific or wallet-specific business logic into `ui_base`;
- when a UI change is needed only for one flow, prefer keeping it local unless it clearly belongs to shared infrastructure;
- keep adaptations compatible with the Diia-style shared UI direction whenever possible.

---

## Related Modules

- `:apps:opensource` - application composition root using the shared UI foundation
- `:features:*` - reusable user flows built on top of shared UI components
- `:wallet:*` - wallet-specific flows that consume shared UI building blocks

---

## Notes and Limitations

- this module contains both Compose-based and View-based UI infrastructure;
- some parts of the module are broad because it serves as shared UI foundation for multiple flows;
- project-specific adaptations should be kept understandable and scoped, so the module does not drift into a generic dumping ground for unrelated UI logic.
