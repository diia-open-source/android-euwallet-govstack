# Wallet Issuing

Gradle path: `:wallet:wallet_issuing`  
Type: wallet feature module

---

## Description

`wallet_issuing` implements wallet-side credential issuance flows in Diia GovStack Wallet. It covers issuer offer resolution, credential offer presentation, issuance orchestration, deferred credential issuance handling, and the PIN-confirmed steps required before completing sensitive issuing actions.

---

## Responsibilities

- resolve issuer and credential offers;
- present available credentials to the user before issuance;
- orchestrate issuance flows through wallet core services;
- handle deferred credential issuance results;
- connect issuance flow with PIN-confirmed actions;
- map issuance results into UI-facing states.

---

## Out of Scope

- wallet runtime configuration belongs to `:wallet:wallet_core`;
- wallet document browsing and details belong to `:wallet:wallet_gallery`;
- remote and proximity presentation belong to `:wallet:wallet_sharing`;
- transaction history representation belongs to `:wallet:wallet_transaction`.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.wallet.walletIssuing)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:wallet:wallet_core` | Wallet contracts, document stores, issuance infrastructure, and deferred issuing support |
| `:features:pin` | PIN confirmation used before protected issuance actions |
| `:libs:diia_storage` | Shared storage integration used by issuance flows |

---

## Public Contract

The host application should include the issuance navigation graphs:

```xml
<include app:graph="@navigation/nav_add_document" />
<include app:graph="@navigation/nav_document_offer" />
```

These graphs are provided in:

```text
src/main/res/navigation/nav_add_document.xml
src/main/res/navigation/nav_document_offer.xml
```

---

## How to Use

1. Add the module dependency.
2. Include the issuance navigation graphs in the host navigation setup.
3. Ensure `:wallet:wallet_core` and `:features:pin` are part of the app composition.
4. Route wallet add-document or issuer-entry flows into the issuance screens.

---

## Internal Structure

```text
wallet_issuing/src/main/java/ua/govstack/digitalwallet/app/wallet_issuing/
├── data         # Repository, deferred result receiver/handler, result transformers
├── di           # Hilt wiring for issuance dependencies
├── domain       # Issuance interactors, models, and result types
├── ui           # Add document, document offer, and pre-auth PIN screens
└── util         # Issuance constants and flow helpers
```

---

## Extension Points

- issuance flow behavior can be extended through wallet-core configuration and issuer catalog changes;
- host applications can control how users enter issuance flows through navigation and surrounding app UX;
- UI mapping and result-handling logic can be extended through module-local mappers and interactors.

---

## Guidelines

- keep issuer and trust configuration in `:wallet:wallet_core` rather than inside issuing screens;
- keep PIN-protected continuation in collaboration with `:features:pin` instead of duplicating auth logic here;
- treat deferred issuance handling as part of the issuing surface, but rely on wallet-core runtime services for shared background behavior.

---

## Related Modules

- `:wallet:wallet_core` - foundational wallet configuration and issuance infrastructure
- `:wallet:wallet_gallery` - user-facing document gallery after issuance
- `:features:pin` - PIN flow used for protected issuance steps

---

## Notes and Limitations

- issuer behavior depends on the issuer catalog and trust configuration assembled in `:wallet:wallet_core`;
- deferred issuance support depends on wallet-core background and result-dispatch infrastructure;
- this module assumes wallet runtime and trust settings are already configured by the application.
