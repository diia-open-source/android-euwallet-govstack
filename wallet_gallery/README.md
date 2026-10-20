# Wallet Gallery

Gradle path: `:wallet:wallet_gallery`  
Type: wallet feature module

---

## Description

`wallet_gallery` implements the main document browsing experience of Diia GovStack Wallet. It is responsible for wallet document list presentation, lobby and gallery screens, document actions, full information views, document ordering, and type-specific document transformation for PID, mDL, and deferred document representations.

---

## Responsibilities

- display the wallet document gallery and lobby flows;
- provide document preview and detailed representations;
- expose document actions and action navigation handlers;
- support document ordering and stack reordering flows;
- transform PID, mDL, and deferred documents into UI-facing gallery models;
- provide navigation helpers for wallet document screens.

---

## Out of Scope

- wallet runtime configuration belongs to `:wallet:wallet_core`;
- wallet issuance orchestration belongs to `:wallet:wallet_issuing`;
- remote and proximity presentation logic belongs to `:wallet:wallet_sharing`;
- transaction history belongs to `:wallet:wallet_transaction`.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.wallet.walletGallery)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:wallet:wallet_core` | Wallet document stores, shared document models, and wallet foundation services |
| `:wallet:wallet_sharing` | Document actions and presentation entry points connect to sharing flows |
| `:wallet:wallet_doc_pid` | PID-specific document mapping and display support |
| `:wallet:wallet_doc_mdl` | mDL-specific document mapping and display support |
| `:libs:diia_storage` | Shared storage integration used by gallery-related data flows |

---

## Public Contract

The host application should include the gallery-related navigation graphs:

```xml
<include app:graph="@navigation/nav_doc_actions" />
<include app:graph="@navigation/nav_doc_full_info" />
<include app:graph="@navigation/nav_stack_order" />
```

These graphs are provided in:

```text
src/main/res/navigation/nav_doc_actions.xml
src/main/res/navigation/nav_doc_full_info.xml
src/main/res/navigation/nav_stack_order.xml
```

---

## How to Use

1. Add the module dependency.
2. Include the gallery navigation graphs in the host navigation setup.
3. Ensure wallet core and type-specific wallet document modules are part of the app composition.
4. Route the main wallet document experience into gallery and document detail screens provided by this module.

---

## Internal Structure

```text
wallet_gallery/src/main/java/ua/govstack/digitalwallet/app/wallet_gallery/
├── data         # Gallery repository access
├── di           # Gallery Hilt wiring and bindings
├── domain       # Interactors, providers, transformers, and gallery models
├── ui           # Gallery, lobby, order, actions, details, and navigation helpers
```

---

## Extension Points

- document-type display behavior can be extended through PID and mDL-specific mappers/providers;
- gallery ordering can be extended through order providers and order screens;
- document action handling can be extended through action mappers and navigation handlers.

---

## Guidelines

- keep wallet-wide runtime and trust logic in `:wallet:wallet_core` rather than pushing it into gallery screens;
- keep document-type-specific rendering in `:wallet:wallet_doc_pid` and `:wallet:wallet_doc_mdl`;
- keep action entry points here, but delegate presentation-session execution to `:wallet:wallet_sharing`.

---

## Related Modules

- `:wallet:wallet_core` - wallet document and foundation services
- `:wallet:wallet_sharing` - sharing entry points from document actions
- `:wallet:wallet_doc_pid` - PID-specific gallery representation
- `:wallet:wallet_doc_mdl` - mDL-specific gallery representation

---

## Notes and Limitations

- gallery behavior depends on the wallet document model and status information provided by `:wallet:wallet_core`;
- document detail rendering is type-specific and intentionally delegated to wallet document modules where needed;
- deferred document representation is handled here, but deferred issuance orchestration belongs to `:wallet:wallet_issuing`.
