# Biometric

Gradle path: `:libs:biometric`  
Type: shared feature module

---

## Description

`biometric` provides biometric setup and availability handling used by Diia GovStack Wallet. It exposes the setup screen shown after PIN creation, the runtime biometric capability abstraction, and the repository layer used to persist biometric-auth enablement state.

> [!NOTE]
> This module originates from the Diia codebase. In the current project, selected files in this module have been adapted and extended to support the functional requirements of the wallet application.

---

## Responsibilities

- provide the biometric setup screen and completion flow;
- expose biometric availability and type detection through the shared `Biometric` contract;
- persist biometric-auth enablement state through `BiometricRepository`;
- bridge biometric setup completion back into the calling navigation flow.

---

## Out of Scope

- PIN creation and validation belong to `:features:pin`;
- wallet runtime configuration belongs to `:wallet:wallet_core`;
- broader host navigation orchestration belongs to the application module.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.libs.biometric)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| `:libs:diia_storage` | Persistence for biometric enablement state |
| `:libs:core` | Shared navigation result, action, and utility contracts used by the setup flow |
| `:libs:ui_base` | Shared UI infrastructure and compose-based screen support |
| `androidx.biometric` | Android biometric capability and prompt integration |

---

## Public Contract

The host application should include the biometric navigation graph:

```xml
<include app:graph="@navigation/nav_biometric" />
```

The graph is provided in:

```text
src/main/res/navigation/nav_biometric.xml
```

The setup destination expects:

```text
resultDestinationId: Int
resultKey: String
pin: String
```

The root navigation graph should also expose the global biometric setup action expected by the module:

```xml
<action
    android:id="@+id/action_global_to_setupBiometric"
    app:destination="@id/nav_biometric">
    <argument
        android:name="resultDestinationId"
        app:argType="integer" />
    <argument
        android:name="resultKey"
        app:argType="string" />
    <argument
        android:name="pin"
        app:argType="string" />
</action>
```

The module also exposes code-level contracts:

- `Biometric`
- `BiometricRepository`

---

## How to Use

1. Add the module dependency.
2. Include `nav_biometric.xml` in the host navigation setup.
3. Add the global `action_global_to_setupBiometric` action to the root graph.
4. Navigate into biometric setup after PIN creation, providing `resultDestinationId`, `resultKey`, and `pin`.
5. Consume the navigation result in the calling flow after setup completes.

---

## Internal Structure

```text
biometric/src/main/java/ua/govstack/digitalwallet/app/biometric/
├── di           # Hilt bindings for biometric contracts
├── store        # Repository contract and persistence-backed implementation
├── ui           # Setup fragment, prompt helpers, ViewModel, and Compose screen
├── AndroidBiometric.kt
└── Biometric.kt
```

---

## Project-Specific Adaptation Note

This repository does not consume this module as a pristine untouched copy.

For the current wallet project, selected files in `biometric` were modified or extended in order to support:

- the wallet authentication onboarding flow after PIN creation;
- project-specific biometric enablement behavior and setup completion flow;
- integration with the current wallet app navigation and authorization requirements.

Because of that, this module should be treated as a Diia-based module adapted for the current project, not as a pure upstream snapshot.

---

## Guidelines

- keep this module focused on biometric setup and shared biometric capability handling;
- keep PIN-specific logic inside `:features:pin` rather than duplicating it here;
- keep host-specific routing outside the module except for the documented navigation contract;
- treat biometric-state changes carefully because they affect authorization UX.

---

## Related Modules

- `:features:pin` - PIN creation flow that typically routes into biometric setup
- `:libs:diia_storage` - persistence layer used for biometric enablement state
- `:libs:ui_base` - shared UI infrastructure used by the setup screen
- `:apps:opensource` - host application composing root navigation actions

---

## Notes and Limitations

- this module covers biometric setup and capability handling, not the entire authentication flow;
- root navigation contracts must remain aligned with the documented global biometric setup action.
