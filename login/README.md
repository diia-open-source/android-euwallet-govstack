# Login

Gradle path: `:features:login`  
Type: shared authentication feature module

---

## Description

`login` provides the user authorization entry flow used by Diia GovStack Wallet. In the current project shape, it acts as a lightweight authentication-start module that routes the user into the PIN setup or authorized flow and connects the login entry point with host navigation.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide the login entry screen and login ViewModel;
- route the user into the PIN creation flow;
- mark the user as authorized through the PIN-related repository flow;
- provide the initial transition from login into the main app flow.

---

## Out of Scope

- PIN creation and PIN input logic belong to `:features:pin`;
- wallet runtime configuration belongs to `:wallet:wallet_core`;
- broader app routing and composition belong to the host application module.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.features.login)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:libs:core` | Shared platform contracts and utilities used by the login flow |
| `:features:pin` | PIN setup and authorization continuation flow |
| `:libs:ui_base` | Shared UI building blocks used by the login screen |
| `:libs:diia_storage` | Local persistence and authorization state support |

---

## Public Contract

The host application should include the login navigation graph:

```xml
<include app:graph="@navigation/nav_login" />
```

The graph is provided in:

```text
src/main/res/navigation/nav_login.xml
```

The host application must also expose required navigation IDs declared in:

```text
src/main/res/values/nav_ids.xml
```

Current declared ID:

```text
nav_home
```

---

## How to Use

1. Add the module dependency.
2. Include `nav_login.xml` in the host navigation setup.
3. Provide required navigation IDs from `nav_ids.xml`.
4. Ensure `:features:pin` is part of the app composition, because the login flow routes into PIN setup.

---

## Internal Structure

```text
login/src/main/java/ua/govstack/digitalwallet/app/login/
└── ui           # Login fragment, ViewModel, constants, and Compose screen
```

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `login` were modified or extended in order to support:

- the current authentication entry flow of the wallet application;
- navigation from login into PIN setup and then into the app shell;
- project-specific authorization and startup requirements.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Guidelines

- keep this module focused on authentication entry and transition into PIN-related flows;
- keep reusable PIN behavior inside `:features:pin` rather than duplicating it here;
- avoid turning `login` into a general-purpose authentication orchestration module if the behavior belongs in the host application or PIN module.

---

## Related Modules

- `:features:pin` - PIN setup and authorization continuation
- `:apps:opensource` - host application providing top-level composition and routing
- `:libs:ui_base` - shared UI foundation used by the login screen

---

## Notes and Limitations

- the module is intentionally lightweight in the current project structure;
- project-specific adaptations should stay centered on authentication entry and startup flow requirements.
