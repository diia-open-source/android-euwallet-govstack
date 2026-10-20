# Contributing to Diia GovStack Wallet

Thank you for your interest in contributing to Diia GovStack Wallet.

This repository contains a multi-module Android implementation of a GovStack-oriented digital identity wallet built on the foundation of Ukraine's Diia platform. Contributions are welcome across product, architecture, documentation, and code.

## Ways to Contribute

You can contribute in several ways:

- report bugs or unexpected behavior;
- propose product, UX, or technical improvements;
- suggest architecture or module-boundary refinements;
- improve documentation;
- submit code changes.

## Before You Contribute

Please take a few minutes to understand the project structure before making changes.

At a high level:

- `:apps:*` contains application entry points and composition roots;
- `:libs:*` contains shared platform, UI, storage, biometric, and scanning foundations;
- `:features:*` contains reusable user flows;
- `:wallet:*` contains wallet-specific business logic and flows.

When possible:

- keep wallet-specific logic inside wallet modules;
- keep shared platform concerns inside shared libraries;
- avoid broad cross-module refactors unless they are necessary for the change;
- prefer small, reviewable contributions with a clear purpose.

## Reporting Issues

We use GitHub issues to track bugs, ideas, and discussion points.

Report a bug, feature request, or discussion point by [opening a new issue](../../issues/new).

When reporting an issue, please include:

- a short summary of the problem or proposal;
- the affected module or feature, if known;
- steps to reproduce, if this is a bug;
- expected behavior;
- actual behavior;
- screenshots, logs, sample payloads, or additional context when helpful.

For security-sensitive issues or vulnerabilities, please contact us directly at [modt.opensource@thedigital.gov.ua](mailto:modt.opensource@thedigital.gov.ua) instead of opening a public issue.

## Contributing Code

If you plan to contribute code, please align the change with the existing module structure and ownership boundaries.

As a general rule:

- application wiring belongs in `:apps:*`;
- reusable shared foundations belong in `:libs:*`;
- reusable user flows belong in `:features:*`;
- wallet-specific orchestration, credential flows, and wallet domain behavior belong in `:wallet:*`.

Good code contributions usually:

- solve one focused problem at a time;
- keep changes scoped to the relevant module or flow;
- include enough context in the pull request description for reviewers to understand the intent;
- avoid unrelated cleanup unless it is required to complete the change safely.

## Documentation Contributions

Documentation improvements are valuable and encouraged.

Useful documentation contributions include:

- clarifying setup or build steps;
- improving module documentation;
- documenting wallet flows and integration points;
- tightening terminology around standards, protocols, and credential formats.

## Licensing

By contributing, you agree that your contributions will be licensed under the EUPL.

You may obtain a copy of the License at [https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12](https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12).

Questions regarding the project, the License, or reuse should be directed to [modt.opensource@thedigital.gov.ua](mailto:modt.opensource@thedigital.gov.ua).
