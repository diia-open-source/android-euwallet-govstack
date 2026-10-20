# Diia Storage

Gradle path: `:libs:diia_storage`  
Type: shared storage library module

---

## Description

`diia_storage` provides the local persistence layer used by Diia GovStack Wallet for authorization state, secure key-value data, PIN-related storage, and device-level identifiers needed by the app runtime.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide secure and non-secure preference-backed storage abstractions;
- persist authorization-related state used by the application;
- store and validate PIN-related data through the shared storage layer;
- manage wallet app identifiers such as mobile UUID and service-user UUID;
- expose repository and storage primitives reused by feature and wallet modules.

---

## Out of Scope

- wallet runtime configuration belongs to `:wallet:wallet_core`;
- feature-specific business logic belongs to feature and wallet modules;
- UI, navigation, and screen orchestration are outside this module.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.libs.diiaStorage)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:libs:core` | Shared models such as token and user type contracts used by storage repositories |
| `androidx.datastore:datastore-preferences` | Preference-backed local storage implementation |
| `androidx.security:security-crypto` | Encrypted storage support for secured persistence |
| `moshi` and `gson` converter support | Serialization and persistence helpers used by the storage layer |

---

## Public Contract

This module does not expose navigation graphs or host-owned destination IDs.

Its main integration surface is code-level:

- `DiiaStorage` - shared storage abstraction used by app features;
- `SecureDiiaStorage` - encrypted storage implementation for runtime use;
- `AuthorizationRepository` - authorization-state repository contract;
- Hilt bindings from `di/PreferenceStorage.kt` and related DI files.

---

## How to Use

1. Add the module dependency.
2. Inject storage or repository abstractions instead of accessing preferences directly.
3. Use `AuthorizationRepository` for authorization-state operations.
4. Use `DiiaStorage` or related storage abstractions for shared persisted values that belong to the common app storage layer.

---

## Internal Structure

```text
diia_storage/src/main/java/ua/govstack/digitalwallet/app/diia_storage/
├── di           # Hilt modules for storage-related dependencies
├── model        # Key-value and preference key contracts
├── store        # Data sources, repositories, and preference-backed implementations
├── DiiaStorage.kt
├── SecureDiiaStorage.kt
└── CommonPreferenceKeys.kt
```

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `diia_storage` were modified or extended in order to support:

- the authorization and startup requirements of the wallet application;
- wallet-specific persistence needs built on top of the shared Diia storage foundation;
- project-level secure local state handling required by the current app runtime.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Guidelines

- keep this module focused on shared persistence contracts and implementations;
- prefer repository or storage abstractions over direct preference access in feature modules;
- avoid moving feature-specific business logic into the common storage layer;
- keep security-sensitive persistence changes explicit and reviewable.

---

## Related Modules

- `:libs:core` - shared models and platform contracts used by storage repositories
- `:features:login` - uses storage-backed authorization state
- `:features:pin` - uses PIN-related persistence contracts
- `:wallet:wallet_core` - consumes storage-backed state as part of wallet runtime behavior

---

## Notes and Limitations

- this module is infrastructure-focused and should remain UI-free;
- changes here can affect multiple feature and wallet modules because storage contracts are widely reused across the application.
