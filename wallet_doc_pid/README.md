# Wallet Doc PID

Gradle path: `:wallet:wallet_doc_pid`  
Type: wallet document module

---

## Description

`wallet_doc_pid` contains Personal Identification Document (PID)-specific models and UI-facing helpers used by Diia GovStack Wallet. It provides PID document structures, full-information mapping, and PID-specific context-menu action support used by gallery and wallet document flows.

---

## Responsibilities

- define PID-specific wallet document models;
- provide PID-specific full-information mapping;
- provide PID-specific document action helpers used by wallet flows.

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
implementation(projects.wallet.walletDocPid)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:wallet:wallet_core` | Shared wallet document concepts and wallet-level integration contracts |

---

## Public Contract

This module contributes PID-specific document representation to wallet flows. It does not expose its own navigation graph and is intended to be consumed by wallet feature modules that need PID-aware rendering or actions.

---

## How to Use

1. Add the module dependency.
2. Use the PID-specific models and mappers from wallet gallery or related wallet flows.
3. Keep PID-only display and action logic in this module rather than moving it into shared wallet code.

---

## Internal Structure

```text
wallet_doc_pid/src/main/java/ua/govstack/digitalwallet/app/wallet_doc_pid/
├── models       # PID-specific document models
└── util         # PID full-info mapping and PID context-menu action helpers
```

---

## Guidelines

- keep PID-specific mapping and action helpers inside this module rather than promoting them to shared wallet code too early;
- move code out of this module only when it becomes genuinely document-type-agnostic;
- let wallet orchestration stay in `:wallet:wallet_core`, `:wallet:wallet_gallery`, or other wallet feature modules.

---

## Related Modules

- `:wallet:wallet_core` - wallet foundation and shared document contracts
- `:wallet:wallet_gallery` - consumes PID-specific display helpers

---

## Notes and Limitations

- this module is intentionally small and type-specific;
- if PID logic becomes generic across document types, it should be promoted carefully rather than by default.
