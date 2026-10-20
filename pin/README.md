# PIN

Gradle path: `:features:pin`  
Type: shared authentication feature module

---

## Description

`pin` provides PIN creation, PIN confirmation, PIN input, and PIN reset flows used by Diia GovStack Wallet. It also defines the integration contract for alternative authentication support, such as biometric authorization, through a host-provided helper.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide PIN setup and confirmation flows;
- provide PIN input and unlock flow;
- provide PIN reset flow;
- integrate with alternative authentication entry points through a host-owned helper;
- expose reusable PIN-related UI and repository behavior used by app and wallet flows.

---

## Out of Scope

- wallet runtime configuration belongs to `:wallet:wallet_core`;
- biometric implementation details belong to the host integration layer or biometric-specific modules;
- application-level routing outside the PIN flow belongs to app or feature modules that consume this module.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.features.pin)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:libs:core` | Shared platform contracts and utilities used by the PIN flow |
| `:libs:ui_base` | Shared UI building blocks used by PIN screens |
| `:libs:diia_storage` | Local persistence for PIN-related state |

---

## Public Contract

The host application should include the PIN navigation graphs:

```xml
<include app:graph="@navigation/nav_pin_create" />
<include app:graph="@navigation/nav_pin_input" />
<include app:graph="@navigation/nav_pin_reset" />
```

These graphs are provided in:

```text
src/main/res/navigation/nav_pin_create.xml
src/main/res/navigation/nav_pin_input.xml
src/main/res/navigation/nav_pin_reset.xml
```

The host application must also expose required navigation IDs declared in:

```text
src/main/res/values/nav_ids.xml
```

Current declared ID:

```text
nav_home
```

The host application should also provide a `PinHelper` implementation for alternative authentication and related host-driven flows:

- `isAlternativeAuthAvailable()`
- `isAlternativeAuthEnabled()`
- `openAlternativeAuth(...)`
- `navigateToAlternativeAuthSetup(...)`
- `navigateToQrScan(...)`

---

## How to Use

1. Add the module dependency.
2. Include the PIN navigation graphs in the host navigation setup.
3. Provide required navigation IDs from `nav_ids.xml`.
4. Provide a host-level `PinHelper` implementation for alternative authentication integration.
5. Route protected app or wallet flows into PIN setup, input, or reset screens as needed.

---

## Internal Structure

```text
pin/src/main/java/ua/govstack/digitalwallet/app/pin/
├── di           # PIN DI wiring
├── helper       # Host integration contract for alternative auth and QR navigation
├── model        # PIN-related flow models
├── repository   # PIN state persistence/repository layer
├── ui           # PIN create, confirm, input, and reset screens
└── util         # PIN constants and alert-dialog helpers
```

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `pin` were modified or extended in order to support:

- wallet-specific protected flows that require PIN confirmation;
- alternative authentication integration needed by the current project;
- QR-navigation and related host-driven authentication transitions;
- current app navigation and authentication requirements.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Guidelines

- keep generic PIN and alternative-auth integration contracts here if they are reusable across flows;
- do not move wallet-specific business logic into the PIN module unless it is directly part of authentication behavior;
- prefer host integration through `PinHelper` rather than hardcoding app-specific routing inside the module;
- keep Diia-origin shared authentication behavior understandable and scoped when adding project-specific adaptations.

---

## Related Modules

- `:libs:ui_base` - shared UI foundation used by PIN screens
- `:libs:diia_storage` - persistence layer used by PIN flows
- `:wallet:*` - wallet modules that consume PIN-protected user flows
- `:apps:opensource` - application composition root providing host integration

---

## Notes and Limitations

- this module defines contracts for alternative authentication but does not own the host-side implementation of that behavior;
- project-specific adaptations should stay scoped to authentication needs and not turn the module into a general routing layer.
