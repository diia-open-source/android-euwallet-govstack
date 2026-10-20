# Core

Gradle path: `:libs:core`  
Type: shared foundation library module

---

## Description

`core` provides the shared application foundation used across Diia GovStack Wallet. It contains common models, repository contracts, navigation helpers, deeplink handling, network primitives, worker utilities, state helpers, and reusable infrastructure needed by feature, wallet, and host modules.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide shared domain and UI-support models reused across the application;
- define common repository and service contracts used by upper-layer modules;
- provide shared navigation, event, delegation, and state-management helpers;
- host base network, work scheduling, deeplink, file, date, and system-service utilities;
- expose DI modules and infrastructure building blocks consumed throughout the app.

---

## Out of Scope

- wallet runtime configuration belongs to `:wallet:wallet_core`;
- persistence implementation belongs to `:libs:diia_storage`;
- feature-specific UI and business logic belong to feature and wallet modules.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.libs.core)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| AndroidX lifecycle, navigation, appcompat, and WorkManager | Shared Android platform infrastructure used across app layers |
| Hilt and Hilt Work | Dependency injection and worker integration |
| Retrofit, Ktor, Moshi | Shared networking and serialization primitives |
| ML Kit, ZXing, compressor, ExifInterface | Shared scanning and media-processing support |
| Lottie and Compose support libraries | Shared UI-support and rendering helpers used by upper modules |

---

## Public Contract

This module does not expose navigation graphs or host-owned destination IDs.

Its integration surface is code-level and shared across the app:

- common models from `models/*`;
- repository contracts from `data/repository/*`;
- navigation and deeplink helpers from `util/navigation/*` and `util/deeplink/*`;
- delegation and state helpers from `util/delegation/*`, `util/state/*`, and `util/event/*`;
- DI modules from `di/*`.

---

## How to Use

1. Add the module dependency.
2. Reuse existing contracts, models, and helpers before introducing new shared primitives elsewhere.
3. Keep module-specific business logic outside `core`, and only move code here when it is broadly reusable across app layers.
4. Depend on `core` from feature, wallet, or infrastructure modules that need shared app contracts.

---

## Internal Structure

```text
core/src/main/java/ua/govstack/digitalwallet/app/core/
├── controller    # Shared coordinators such as deeplink processing
├── data          # Repository contracts and common data-layer abstractions
├── di            # Shared DI modules and worker bindings
├── models        # Common models, UI-support payloads, and cross-module contracts
├── network       # Base network APIs, connectivity, and transport helpers
├── push          # Push-related base actions
└── util          # Navigation, state, event, system, file, date, and helper utilities
```

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `core` were modified or extended in order to support:

- wallet-oriented app flows and shared models used by GovStack wallet scenarios;
- project-specific navigation, deeplink, and event-handling behavior;
- shared infrastructure needs required by the current Android wallet implementation.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Guidelines

- keep `core` focused on broadly reusable application-level contracts and helpers;
- do not move feature-specific or wallet-specific logic here unless it is truly cross-cutting;
- prefer stable, narrow abstractions because changes in `core` ripple into many other modules;
- keep additions reviewable and well-justified, especially in shared models and utility APIs.

---

## Related Modules

- `:libs:diia_storage` - shared persistence implementation built on top of common app contracts
- `:libs:ui_base` - shared UI foundation that works alongside core infrastructure
- `:wallet:wallet_core` - wallet runtime foundation consuming shared contracts from `core`
- `:apps:opensource` - host application wiring core services into the full app

---

## Notes and Limitations

- `core` is intentionally broad, so unclear ownership should be treated carefully before adding new code here;
- changes in this module can affect many features at once, so shared-contract changes should stay conservative.
