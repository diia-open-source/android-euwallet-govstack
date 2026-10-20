# Wallet Transaction

Gradle path: `:wallet:wallet_transaction`  
Type: wallet feature module

---

## Description

`wallet_transaction` implements wallet transaction history presentation in Diia GovStack Wallet. It provides transaction list and transaction detail screens, transaction-focused interactors, and transformation logic used to render wallet activity history for the end user.

---

## Responsibilities

- display wallet transaction history;
- display wallet transaction details;
- retrieve transaction log data from wallet-backed persistence;
- transform transaction records into UI-facing models.

---

## Out of Scope

- transaction log persistence belongs to `:wallet:wallet_core`;
- wallet issuance belongs to `:wallet:wallet_issuing`;
- remote and proximity presentation belongs to `:wallet:wallet_sharing`;
- wallet document browsing belongs to `:wallet:wallet_gallery`.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.wallet.walletTransaction)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:wallet:wallet_core` | Transaction log storage, wallet runtime access, and foundational wallet data |

---

## Public Contract

The host application should include the transaction navigation graph:

```xml
<include app:graph="@navigation/nav_transactions" />
```

The graph is provided in:

```text
src/main/res/navigation/nav_transactions.xml
```

---

## How to Use

1. Add the module dependency.
2. Include the transaction navigation graph in the host navigation setup.
3. Ensure wallet core is configured and transaction log persistence is available.
4. Route transaction-history entry points into the screens provided by this module.

---

## Internal Structure

```text
wallet_transaction/src/main/java/ua/govstack/digitalwallet/app/wallet_transaction/
├── data         # Transaction repositories and transformers
├── di           # Transaction DI wiring
├── domain       # Interactors, domain model, and transaction detail transformation
├── ui           # Transaction list and detail screens
└── util         # Transaction-related constants
```

---

## Extension Points

- UI-facing transaction rendering can be extended through list/detail mappers and transformers;
- host applications can control where transaction history is entered from within the app;
- transaction categories and representation can evolve without moving persistence concerns out of `:wallet:wallet_core`.

---

## Guidelines

- keep transaction creation and persistence in `:wallet:wallet_core`, not in this presentation module;
- keep list/detail transformation here when it is specific to transaction-history UI;
- avoid coupling transaction screens to issuance or sharing implementation details beyond the persisted log surface.

---

## Related Modules

- `:wallet:wallet_core` - transaction log persistence and foundational wallet services
- `:wallet:wallet_gallery` - possible user entry points into transaction history

---

## Notes and Limitations

- this module focuses on transaction history presentation, not on transaction log creation;
- correctness depends on transaction events being persisted upstream by wallet core services.
