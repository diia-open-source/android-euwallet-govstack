# Wallet Core

Gradle path: `:wallet:wallet_core`  
Type: wallet foundation module

---

## Description

`wallet_core` is the foundation module of Diia GovStack Wallet. It owns the core wallet contracts, configuration assembly, document infrastructure, issuance and deferred issuance support, revocation handling, transaction logging, wallet-attestation integration, and security helpers used by wallet feature modules.

This module is also the main place where the wallet runtime is configured for a specific deployment environment.

`wallet_core` is built on top of the EUDI wallet library. In the current project setup, the wallet stack is integrated with a locally adapted version of the EUDI library that includes project-specific changes needed for the current implementation. This should be treated as a temporary local integration layer rather than a direct use of the original upstream package.

---

## Responsibilities

- assemble the wallet runtime configuration used by the EUDI wallet SDK;
- provide wallet-level dependency wiring and singleton entry points;
- manage wallet document models, document stores, and document helpers;
- support credential issuance and deferred credential issuance flows;
- support credential revocation tracking and related background work;
- persist wallet transaction log and revocation data;
- provide wallet attestation and trust-related integration helpers;
- expose shared wallet UI helpers, constants, and resource IDs used across wallet flows.

---

## Boundaries

- `:wallet:wallet_core` is intended to be consumed by wallet feature modules such as issuing, sharing, gallery, and transaction history.
- Wallet-specific runtime and trust-facing integration should stay behind `wallet_core` rather than being duplicated in wallet feature modules.
- Shared Android/platform concerns that are not wallet-specific should stay in `:libs:*` modules.
- PID- and mDL-specific document behavior belongs in `:wallet:wallet_doc_pid` and `:wallet:wallet_doc_mdl`, not in generic shared libraries.

---

## Targets / Layers

`wallet_core` is organized around several internal responsibility areas:

- `wallet` - wallet runtime configuration, EUDI wallet wiring, and DI entry points;
- `documents` - shared wallet document models, stores, and helpers;
- `issuance` - issuance orchestration support and deferred issuance infrastructure;
- `revocation` - revocation tracking models and background work;
- `security` - attestation, certificates, trust helpers, and security-facing integration;
- `database` - Room-backed persistence for wallet-specific data such as transaction and revocation state;
- `ui` and `utils` - shared wallet-facing UI helpers and supporting utilities.

The direction is intentionally inward toward foundational wallet services: wallet feature modules consume these layers, but `wallet_core` should not depend on wallet feature modules.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.wallet.walletCore)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:libs:diia_storage` | Shared storage abstractions used by wallet persistence and data handling |
| `:features:pin` | PIN-related flows used by wallet issuance and authentication-sensitive operations |
| `:wallet:wallet_doc_pid` | PID-specific document models and helpers |
| `:wallet:wallet_doc_mdl` | mDL-specific document models and helpers |

---

## Public Contract

The module exposes wallet foundation behavior that other wallet modules rely on:

- wallet configuration through `WalletCoreConfig`;
- document stores and transaction log storage;
- issuance and deferred issuance infrastructure;
- revocation work and revocation persistence;
- wallet attestation integration;
- resource IDs declared in `src/main/res/values/nav_ids.xml`.

---

## Public API Highlights

- `WalletCoreConfig` - wallet runtime configuration surface consumed by the rest of the wallet stack
- `WalletCoreConfigImpl` - configuration assembly based on Gradle-provided and asset-based inputs
- `WalletModule` - main DI wiring entry point for wallet runtime objects
- wallet document stores and helpers - shared document access surface used by wallet feature modules
- revocation and deferred issuance infrastructure - shared runtime behavior used by issuance- and status-sensitive flows

---

## Wallet Configuration

Configuration of the wallet runtime is assembled inside `WalletCoreConfigImpl` and injected through `WalletModule`.

The main configuration inputs are:

| Input | Purpose |
|---|---|
| `WALLET_PROVIDER_HOST` | Base URL used by wallet attestation-related flows |
| `WALLET_ISSUERS_CONFIG_ASSET` | Asset path of the packaged issuer catalog |
| `ALLOW_UNTRUSTED_ISSUERS` | Trust toggle for issuer validation |
| `ALLOW_UNTRUSTED_VERIFIERS` | Trust toggle for verifier validation |
| `ENFORCE_ISSUER_ALLOW_LIST` | Restricts issuance to explicitly allowed issuers |
| `WALLET_DEBUG_LOGGING` | Enables or reduces wallet SDK logging |

These values are exported to `BuildConfig` from [build.gradle.kts](/Users/alexliapin/StudioProjects/wallet/wallet_core/build.gradle.kts:1) and consumed by the config layer in:

- [WalletCoreConfig.kt](/Users/alexliapin/StudioProjects/wallet/wallet_core/src/main/java/ua/govstack/digitalwallet/app/wallet_core/wallet/configs/WalletCoreConfig.kt:1)
- [WalletCoreConfigImpl.kt](/Users/alexliapin/StudioProjects/wallet/wallet_core/src/main/java/ua/govstack/digitalwallet/app/wallet_core/wallet/configs/WalletCoreConfigImpl.kt:1)
- [WalletModule.kt](/Users/alexliapin/StudioProjects/wallet/wallet_core/src/main/java/ua/govstack/digitalwallet/app/wallet_core/wallet/di/WalletModule.kt:1)

### Asset-Based Configuration

The wallet expects deployment-specific assets under:

```text
src/main/assets/configs/issuers_config.json
src/main/assets/certificates/*
```

- `issuers_config.json` defines issuer endpoints and issuer ordering for issuance flows.
- `assets/certificates` provides trust material used by reader and issuer-related validation logic.

### Runtime Configuration Surface

At runtime, `wallet_core` assembles:

- `EudiWalletConfig` for the wallet SDK;
- issuer configuration list for issuance flows;
- document category and issuance rules;
- trust-handling policy;
- wallet provider host used by attestation flows.

---

## Module Hierarchy

`wallet_core` sits between shared Android infrastructure and wallet feature modules:

- it depends on shared libraries such as `:libs:diia_storage`;
- it is consumed by `:wallet:wallet_issuing`, `:wallet:wallet_sharing`, `:wallet:wallet_gallery`, and `:wallet:wallet_transaction`;
- it also collaborates with document-type modules such as `:wallet:wallet_doc_pid` and `:wallet:wallet_doc_mdl` for type-specific document behavior.

The application module composes the wallet stack through `:apps:opensource`, but wallet features should treat `wallet_core` as the main wallet-level integration boundary.

---

## Critical Integration Notes

- wallet runtime behavior is environment-sensitive and depends on both Gradle properties and packaged asset files;
- trust-related toggles such as `ALLOW_UNTRUSTED_ISSUERS` and `ALLOW_UNTRUSTED_VERIFIERS` should be treated as security-sensitive configuration;
- changes in this module can affect multiple wallet features at once, especially issuance, sharing, and transaction-history flows.

---

## Internal Structure

```text
wallet_core/src/main/java/ua/govstack/digitalwallet/app/wallet_core/
├── database      # Room database, DAOs, entities, and database DI
├── documents     # Document models, helpers, stores, and document DI
├── issuance      # Issuance manager, deferred issuance work, issuance DI
├── revocation    # Revocation work and revocation models
├── security      # Attestation, certificates, trust helpers, security DI
├── ui            # Shared wallet UI helpers and alert dialog factories
├── utils         # Wallet-specific utilities and extensions
└── wallet        # Wallet configuration and EUDI wallet DI wiring
```

---

## Guidelines

- Keep environment-specific wallet settings in the config layer rather than scattering constants across wallet features.
- Add new wallet-wide runtime contracts here only when they are truly foundational for multiple wallet modules.
- Keep PID- and mDL-specific logic in document-type modules unless it is genuinely generic.
- Treat trust toggles and attestation-related settings as security-sensitive configuration.
- Prefer extending wallet stores, config objects, or DI modules here before duplicating logic in issuing, sharing, or gallery modules.

---

## Related Modules

- `:apps:opensource` - application composition root that consumes the wallet stack
- `:wallet:wallet_issuing` - wallet issuance flow built on top of wallet core
- `:wallet:wallet_sharing` - remote and proximity presentation flow
- `:wallet:wallet_gallery` - wallet document browsing and display
- `:wallet:wallet_transaction` - transaction log presentation layer
- `:wallet:wallet_doc_pid` - PID-specific document support
- `:wallet:wallet_doc_mdl` - mDL-specific document support

---

## Notes and Limitations

- Wallet configuration is environment-sensitive and depends on packaged assets and Gradle properties.
- Trust-related flags can materially change validation behavior and should be reviewed carefully for non-debug deployments.
- This module owns foundational wallet behavior, so changes here can affect multiple wallet feature modules at once.
