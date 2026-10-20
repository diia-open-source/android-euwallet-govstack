# Local EUDI Wallet Core Copy

This directory contains a temporary in-repository local copy of `eudi-lib-android-wallet-core`.

## Current Base Version

The current local copy is based on:

- `eudi-lib-android-wallet-core`
- `VERSION_NAME=0.28.1`

The version is defined in [gradle.properties](/Users/alexliapin/StudioProjects/wallet/third_party/eudi-lib-android-wallet-core/gradle.properties:1).

## Why This Local Copy Exists

The wallet currently depends on EUDI wallet-core behavior that is not yet consumed as a plain upstream dependency in this project.

This local copy exists so the Android wallet can:

- keep the required EUDI behavior directly inside the repository;
- integrate and test project-specific changes in a stable way;
- avoid depending on a separately checked-out modified library during active development.

This should be treated as a temporary local version of the library, not as the long-term source of truth.

## Project-Specific Additions Currently Tracked Here

The currently documented local scope includes:

- issuer metadata policy handling;
- transaction logging improvements.

In practice, this local version is the one the wallet integrates with for the current issuance, deferred issuance, presentation, attestation, and transaction logging behavior expected by the project.

## Integration Rule

Keep this local copy as close as possible to the original upstream library.

When changes are needed:

- prefer upstream-compatible improvements;
- avoid wallet-app-specific rewrites inside the library when the same result can be achieved in app modules;
- document every project-specific reason for keeping a local change here.

## Planned Direction

The intended end state is to stop using this local copy and switch back to the regular upstream EUDI library dependency once the required changes are available in a suitable upstream release.

In other words, after the upstream library is updated with the functionality this project needs, the wallet should:

1. replace the local in-repo copy with the normal upstream dependency;
2. remove any temporary project-only wiring that exists only to support the local copy;
3. keep `third_party/eudi-lib-android-wallet-core` only if there is still a documented, unavoidable gap with upstream.

