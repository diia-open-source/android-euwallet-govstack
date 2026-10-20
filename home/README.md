# Home

Gradle path: `:features:home`  
Type: shared feature module

---

## Description

`home` provides the main in-app shell of Diia GovStack Wallet after authorization. It hosts the wallet landing experience, bottom-level tab navigation, and the transition layer between wallet document flows, menu flows, QR-based sharing entry, and app-level authorized navigation.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide the authorized home entry screen;
- host the in-app shell used after login and PIN setup;
- coordinate bottom navigation between wallet document and menu flows;
- bridge home-level actions into QR scanning, document presentation, and app-level destinations;
- render wallet-specific home visuals and state-driven entry behavior.

---

## Out of Scope

- wallet runtime configuration belongs to `:wallet:wallet_core`;
- QR scanning implementation belongs to `:features:scanner_qr`;
- menu-specific logic belongs to `:features:menu`;
- document gallery and document action flows belong to `:wallet:wallet_gallery`.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.features.home)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:wallet:wallet_core` | Wallet state, document flows, and shared wallet runtime behavior |
| `:wallet:wallet_gallery` | Embedded wallet document gallery flow shown inside the home shell |
| `:features:menu` | Embedded menu flow shown inside the home shell |
| `:libs:diia_storage` | Local storage-backed state used by the authorized app shell |

---

## Public Contract

The host application should include the home navigation graph:

```xml
<include app:graph="@navigation/nav_home" />
```

The graph is provided in:

```text
src/main/res/navigation/nav_home.xml
```

The home graph starts at:

```text
homeF
```

The root navigation graph should also expose the global QR scanner action expected by the module:

```xml
<action
    android:id="@+id/action_global_to_QrScanF"
    app:destination="@id/qrScanF" />
```

Inside the home shell, the module composes child navigation from:

- `:wallet:wallet_gallery`
- `:features:menu`

---

## How to Use

1. Add the module dependency.
2. Include `nav_home.xml` in the host navigation setup.
3. Add the global `action_global_to_QrScanF` action to the root graph.
4. Ensure `:wallet:wallet_gallery`, `:features:menu`, and `:features:scanner_qr` are part of the app composition.
5. Route the authorized app flow into `homeF`.

---

## Internal Structure

```text
home/src/main/java/ua/govstack/digitalwallet/app/home/
├── di           # DI bindings for home-related UI mapping
├── model        # Home menu items and background models
└── ui           # Home fragment, ViewModel, actions, mappers, and views
```

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `home` were modified or extended in order to support:

- the wallet-specific authorized shell experience;
- integration with wallet document gallery and sharing flows;
- project-specific navigation and startup behavior after authentication.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Guidelines

- keep the module focused on the authorized home shell and top-level feature coordination;
- keep wallet document logic inside `:wallet:wallet_gallery` and `:wallet:wallet_core` rather than duplicating it here;
- keep menu-specific behavior inside `:features:menu`;
- avoid moving app-wide composition concerns into `home` when they belong to the host application module.

---

## Related Modules

- `:apps:opensource` - host application and root navigation composition
- `:wallet:wallet_gallery` - wallet document gallery integrated into the home shell
- `:features:menu` - menu flow integrated into the home shell
- `:features:scanner_qr` - QR scanner destination used by home-level actions
- `:wallet:wallet_core` - wallet runtime foundation used by the authorized experience

---

## Notes and Limitations

- the module acts as a composition point for child flows and should stay thin where deeper logic already belongs to child modules;
- home-level navigation contracts must remain aligned with the root navigation graph, especially the global QR scanner action.
