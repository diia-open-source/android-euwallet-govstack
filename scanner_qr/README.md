# Scanner QR

Gradle path: `:libs:scanner_qr`  
Type: shared scanner feature module

---

## Description

`scanner_qr` provides QR scanning functionality used by Diia GovStack Wallet. It contains the camera-driven scanner screen, barcode processing integration, scanner result models, and supporting DI/helpers used to detect and process QR-based entry points inside the application.

---

## Responsibilities

- provide a QR scanner screen and ViewModel;
- integrate camera frame processing with barcode recognition;
- detect supported QR codes and route them into app deeplink handling;
- manage scanner permission, error state, and flash-related UI state;
- expose reusable scanning infrastructure for app flows that depend on QR input.

---

## Out of Scope

- deeplink business handling belongs to higher-level app or feature flows;
- wallet-specific issuance, sharing, or gallery logic belongs to wallet modules;
- shared visual UI building blocks belong to `:libs:ui_base`.

---

## How to Install

Add the module dependency:

```kotlin
implementation(projects.libs.scannerQr)
```

---

## Required Dependencies

| Dependency | Why it is needed |
|---|---|
| ML Kit Barcode Scanning | QR and barcode recognition from camera frames |
| CameraView | Camera preview and frame delivery for scanner flows |
| `:libs:ui_base` | Shared UI components and infrastructure used by the scanner screen |
| App-level deeplink handling | Successful scan results are forwarded into higher-level routing |

---

## Public Contract

The host application should include the scanner navigation graph:

```xml
<include app:graph="@navigation/nav_qr_scanner" />
```

The graph is provided in:

```text
src/main/res/navigation/nav_qr_scanner.xml
```

This module expects camera permission handling through the Android runtime permission flow and routes successful QR scans into app deeplink handling.

---

## How to Use

1. Add the module dependency.
2. Include the scanner navigation graph in the host navigation setup.
3. Route QR-based user entry points into `QrScannerF`.
4. Ensure app-level deeplink handling is configured, since successful scans are emitted as deeplink actions.

---

## Internal Structure

```text
scanner_qr/src/main/java/ua/govstack/digitalwallet/app/scanner_qr/
├── di           # Scanner DI wiring and annotations
├── model        # Scanner result models
├── ui           # Scanner fragment, screen, and ViewModel
├── util         # Scanner processing and image helpers
└── BarcodeScanner.kt   # Scanner integration entry point
```

---

## Extension Points

- supported scan interpretation can evolve through scanner result handling and QR marker logic;
- host modules can decide where scanner entry points appear in the user journey;
- scanner UI can be adjusted through the screen and ViewModel layer without changing barcode processing internals.

---

## Related Modules

- `:apps:opensource` - application composition root that consumes scanner results
- `:libs:ui_base` - shared UI components used by the scanner screen
- wallet and feature modules that use QR-based entry points

---

## Guidelines

- keep this module focused on scanning and scan-result extraction, not on business interpretation of scanned data;
- keep deeplink or verifier-specific branching in higher-level app or wallet flows;
- prefer evolving scan-result contracts and helpers here instead of duplicating scanner setup across feature modules.

---

## Notes and Limitations

- this module scans QR codes and forwards recognized values into higher-level app handling;
- recognized QR values are validated against configured QR markers before being emitted as deeplinks;
- scanner behavior depends on camera permission and on app-level deeplink routing being available.
