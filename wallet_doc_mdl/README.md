# Wallet Doc mDL

Gradle path: `:wallet:wallet_doc_mdl`  
Type: wallet document module

---

## Description

`wallet_doc_mdl` contains mobile Driving Licence (mDL)-specific models and UI-facing helpers used by Diia GovStack Wallet. It provides mDL document structures, full-information mapping, and mDL-specific context-menu action support used by gallery and wallet document flows.

---

## Responsibilities

- define mDL-specific wallet document models;
- provide mDL-specific full-information mapping;
- provide mDL-specific document action helpers used by wallet flows.

---

## Out of Scope

- wallet runtime configuration belongs to `:wallet:wallet_core`;
- generic wallet document orchestration belongs to `:wallet:wallet_gallery`;
- credential issuance belongs to `:wallet:wallet_issuing`;
- credential presentation belongs to `:wallet:wallet_sharing`.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.wallet.walletDocMdl)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:wallet:wallet_core` | Shared wallet document concepts and wallet-level integration contracts |

---

## Public Contract

This module contributes mDL-specific document representation to wallet flows. It does not expose its own navigation graph and is intended to be consumed by wallet feature modules that need mDL-aware rendering or actions.

---

## How to Use

1. Add the module dependency.
2. Use the mDL-specific models and mappers from wallet gallery or related wallet flows.
3. Keep mDL-only display and action logic in this module rather than moving it into shared wallet code.

---

## Internal Structure

```text
wallet_doc_mdl/src/main/java/ua/govstack/digitalwallet/app/wallet_doc_mdl/
├── models       # mDL-specific document models
└── util         # mDL full-info mapping and mDL context-menu action helpers
```

---

## Guidelines

- keep mDL-specific mapping and action helpers inside this module rather than promoting them to shared wallet code too early;
- move code out of this module only when it becomes genuinely document-type-agnostic;
- let wallet orchestration stay in `:wallet:wallet_core`, `:wallet:wallet_gallery`, or other wallet feature modules.

---

## Related Modules

- `:wallet:wallet_core` - wallet foundation and shared document contracts
- `:wallet:wallet_gallery` - consumes mDL-specific display helpers

---

## Notes and Limitations

- this module is intentionally small and type-specific;
- if mDL logic becomes generic across document types, it should be promoted carefully rather than by default.
