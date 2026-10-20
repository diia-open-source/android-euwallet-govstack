# Open Source App

Gradle path: `:apps:opensource`  
Type: application module

---

## Description

`opensource` is the main Android application module of Diia GovStack Wallet. It serves as the application entry point and composition root, combining shared libraries, reusable feature modules, and wallet-specific modules into a runnable app.

This module is responsible for app-level dependency wiring, main navigation setup, deeplink processing, activity/fragment entry points, and environment-specific application configuration required to build and run the wallet.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide the main Android application entry points through `App.kt` and `MainActivity`;
- act as the composition root for feature and wallet modules;
- wire app-level Hilt modules, helpers, delegates, and host implementations;
- configure app-level navigation, deeplink handling, and document flow entry points;
- provide build-time application configuration such as versioning and packaging setup.

---

## Out of Scope

- shared reusable UI primitives belong to `:libs:ui_base`;
- wallet runtime configuration belongs to `:wallet:wallet_core`;
- wallet-specific issuing, sharing, gallery, and transaction logic belong to wallet modules;
- reusable authentication and feature flows belong to their dedicated feature modules.

---

## How to Build

Build the app:

```bash
./gradlew :apps:opensource:assembleGplayDebug
```

Install the app on a connected device or emulator:

```bash
./gradlew :apps:opensource:installGplayDebug
```

---

## Required Dependencies

This module composes the main project modules, including:

- shared libraries such as `:libs:core`, `:libs:ui_base`, `:libs:diia_storage`, `:libs:biometric`, and `:libs:scanner_qr`;
- feature modules such as `:features:home`, `:features:menu`, `:features:login`, `:features:pin`, and `:features:context_menu`;
- wallet modules such as `:wallet:wallet_core`, `:wallet:wallet_issuing`, `:wallet:wallet_sharing`, `:wallet:wallet_gallery`, `:wallet:wallet_transaction`, `:wallet:wallet_doc_pid`, and `:wallet:wallet_doc_mdl`.

---

## Public Contract

This is the application module, so it is not primarily consumed as a library by other modules. Instead, it acts as the host that provides:

- the main navigation graph;
- host implementations for helper interfaces required by feature and wallet modules;
- app-level deeplink processing;
- activity and fragment entry points for the Android application runtime.

The main navigation graph is provided in:

```text
src/main/res/navigation/nav_main.xml
```

---

## Configuration

The module contains app-level configuration inputs such as:

- `version.properties` for version code and version name;
- `google-services.json` for Google services integration when applicable;
- `proguard-rules.pro` and multidex configuration for packaging;
- debug signing configuration through `keys/debug.properties`, if present in the local environment.

The module also exports app-level `BuildConfig` values such as `PLATFORM_TYPE` and token-related configuration.

Configuration of the wallet runtime itself is described in the [`wallet_core` module README](../wallet_core/README.md).

---

## How to Use

1. Open the repository in Android Studio.
2. Build or run `:apps:opensource`.
3. Use this module as the host application for feature and wallet flows.
4. Add or update app-level helper implementations here when a feature or wallet module requires host integration.

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `opensource` were modified or extended in order to support:

- wallet-specific application composition;
- app-level deeplink flows used by wallet scenarios;
- host implementations required by wallet and feature modules;
- current navigation, configuration, and integration requirements of the project.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Internal Structure

```text
opensource/src/main/java/ua/govstack/digitalwallet/app/opensource/
├── data                  # App-level network and cleanup infrastructure
├── deeplinkprocessor     # Deeplink action processing
├── di                    # Hilt wiring, bindings, helpers, and app-level modules
├── helper                # Host implementations for feature and wallet contracts
├── ui                    # App-level activities, screens, and dialogs
└── util                  # App-specific helpers, delegations, and extensions
```

---

## Guidelines

- keep this module focused on app assembly and host-specific integration;
- prefer placing reusable logic in library, feature, or wallet modules before adding it here;
- when a module requires a host implementation, provide it here rather than pushing app-specific wiring down into reusable modules;
- keep deeplink handling and top-level routing centralized in the application layer.

---

## Related Modules

- `:libs:*` - shared foundations used by the app
- `:features:*` - reusable user flows hosted by the app
- `:wallet:*` - wallet-specific modules assembled into the application

---

## Notes and Limitations

- this module is the main host application, so changes here can affect routing and composition across the whole project;
- build and runtime behavior depends on local environment files such as version and signing configuration;
- app-specific integrations should stay here unless they are clearly reusable across modules.
