# Wallet Sharing

Gradle path: `:wallet:wallet_sharing`  
Type: wallet feature module

---

## Description

`wallet_sharing` implements credential presentation and sharing flows in Diia GovStack Wallet. It covers remote presentation, proximity presentation, QR-based session setup, request transformation, consent flow handling, and sharing-session orchestration built on top of wallet core services.

---

## Responsibilities

- handle remote credential presentation flows;
- handle proximity credential presentation flows;
- support QR-based session and request entry points;
- manage sharing session state and presentation lifecycle;
- transform verifier requests into UI-facing consent and sharing states;
- coordinate PIN-protected continuation when required by the flow.

---

## Out of Scope

- wallet runtime configuration and trust-material setup belong to `:wallet:wallet_core`;
- document browsing and document action presentation belong to `:wallet:wallet_gallery`;
- credential issuance belongs to `:wallet:wallet_issuing`;
- transaction history UI belongs to `:wallet:wallet_transaction`.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.wallet.walletSharing)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:wallet:wallet_core` | Wallet contracts, document access, security integration, and runtime configuration |
| `:features:pin` | PIN continuation for protected sharing and presentation steps |

---

## Public Contract

The host application should include the sharing navigation graph:

```xml
<include app:graph="@navigation/nav_sharing" />
```

The graph is provided in:

```text
src/main/res/navigation/nav_sharing.xml
```

---

## How to Use

1. Add the module dependency.
2. Include the sharing navigation graph in the host navigation setup.
3. Ensure wallet runtime is configured through `:wallet:wallet_core`.
4. Route verifier requests, QR entry points, or in-app sharing actions into the sharing flow.

---

## Internal Structure

```text
wallet_sharing/src/main/java/ua/govstack/digitalwallet/app/wallet_sharing/
├── data         # Presentation repositories and sharing-session managers
├── di           # Hilt and feature wiring
├── domain       # Interactors, models, serializers, observers, and transformers
├── ui           # Sharing screen, bottom sheet, and sharing helpers
└── util         # Sharing constants
```

---

## Extension Points

- request transformation and consent presentation can be extended through module transformers;
- host applications can control entry points into sharing through QR, deeplink, or in-app navigation;
- feature-specific session behavior can be extended through the module DI surface and feature manager wiring.

---

## Guidelines

- keep trust-sensitive wallet runtime and attestation behavior in `:wallet:wallet_core`;
- keep document browsing and document action entry points in `:wallet:wallet_gallery`;
- treat remote and proximity presentation as sharing concerns here, even when both rely on shared wallet-core infrastructure.

---

## Related Modules

- `:wallet:wallet_core` - wallet runtime, trust, document, and attestation foundation
- `:wallet:wallet_gallery` - document actions and user entry points into sharing
- `:features:pin` - protected continuation flow

---

## Notes and Limitations

- trust-sensitive behavior depends on wallet-core trust configuration and wallet runtime setup;
- sharing behavior relies on session managers and presentation repositories defined inside this module;
- proximity and remote presentation have different runtime paths, but both depend on wallet-core document and security infrastructure.
