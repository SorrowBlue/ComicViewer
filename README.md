# ComicViewer

[![Lint, Test, and Build](https://github.com/SorrowBlue/ComicViewer/actions/workflows/lint-test-build.yml/badge.svg)](https://github.com/SorrowBlue/ComicViewer/actions/workflows/lint-test-build.yml)
[![Release](https://github.com/SorrowBlue/ComicViewer/actions/workflows/release.yml/badge.svg)](https://github.com/SorrowBlue/ComicViewer/actions/workflows/release.yml)
[![Deploy Dokka](https://github.com/SorrowBlue/ComicViewer/actions/workflows/gh-pages.yml/badge.svg)](https://github.com/SorrowBlue/ComicViewer/actions/workflows/gh-pages.yml)

## Developer Guide

**For comprehensive development guidelines, please refer to [AGENTS.md](AGENTS.md)**

AGENTS.md contains detailed information about:

- Project setup and environment configuration
- Build and test commands with execution time estimates
- Code style guidelines and best practices
- Architecture and module structure
- Pull Request policies and release process

## Release Automation

ComicViewer uses automated GitHub Actions workflows for releases.
See [docs/release-automation.md](docs/release-automation.md) for details on the automated release
process that builds and deploys both Android and JVM versions.

## Coding rules

Follow Android's [Kotlin style guide](https://developer.android.com/kotlin/style-guide).
Also, use [trailing comma](https://kotlinlang.org/docs/coding-conventions.html#trailing-commas).

Use [detekt](https://github.com/detekt/detekt) as a static code analysis tool.

## Plugin configuration

<link
  href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"
  rel="stylesheet"
/>

```mermaid
---
title: Plugin configuration
---
graph LR
    DetektConventionPlugin
    DokkaConventionPlugin

    KotlinMultiplatformApplicationConventionPlugin --> AndroidLintConventionPlugin
    KotlinMultiplatformApplicationConventionPlugin --> DetektConventionPlugin

    MultiplatformLibraryConventionPlugin --> AndroidLintConventionPlugin
    MultiplatformLibraryConventionPlugin --> DetektConventionPlugin

    MultiplatformComposeConventionPlugin --> DetektConventionPlugin

    DiConventionPlugin

    KotlinMultiplatformFeatureConventionPlugin --> MultiplatformLibraryConventionPlugin
    KotlinMultiplatformFeatureConventionPlugin --> MultiplatformComposeConventionPlugin
    KotlinMultiplatformFeatureConventionPlugin --> DiConventionPlugin
```

## Module configuration

| Module  |                |              | Overview               |
|---------|----------------|--------------|------------------------|
| app     | androidApp     |              | Android Application |
| app     | androidBenchmark|             | Android Benchmark Module |
| app     | jvmApp         |              | JVM (Desktop) Application |
| app     | ios            |              | iOS Application |
| app     | share          |              | Platform shared entry point |
| app     | sync           |              | Background data synchronization and scan implementations |
| domain  | model          |              | Domain models and entities (Core) |
| domain  | repository     |              | Repository interfaces and ports (Data access abstractions) |
| domain  | service        |              | Domain services and pure business logic |
| domain  | usecase        |              | Concrete use cases and application workflow orchestration |
| data    | coil           |              | Thumbnail and image loading implementations |
| data    | database       |              | Room database implementations |
| data    | datastore      |              | Datastore for settings and status persistence |
| data    | reader         | document     | PDF/Document file reader implementations |
| data    | reader         | zip          | ZIP/Archive file reader implementations |
| data    | storage        |              | File storage client abstractions |
| data    | storage        | device       | Local device storage implementations |
| data    | storage        | smb          | SMB network storage implementations |
| feature | authentication |              | Login/Authentication screen |
| feature | authentication | nav          | Authentication navigation |
| feature | book           |              | Comic viewer screen |
| feature | book           | nav          | Comic viewer navigation |
| feature | bookshelf      |              | Bookshelf/Library screen |
| feature | bookshelf      | edit         | Bookshelf editing screen |
| feature | bookshelf      | info         | Bookshelf information screen |
| feature | bookshelf      | nav          | Bookshelf navigation |
| feature | collection      |              | Collection screen |
| feature | collection      | add          | Add collection screen |
| feature | collection      | editor       | Edit collection screen |
| feature | collection      | nav          | Collection navigation |
| feature | folder         |              | Folder navigation screen |
| feature | folder         | nav          | Folder navigation |
| feature | history        |              | Reading history screen |
| feature | readlater      |              | Read later screen |
| feature | search         |              | Search and discovery screen |
| feature | search         | nav          | Search navigation |
| feature | settings       |              | Settings top screen |
| feature | settings       | common       | Common settings definitions |
| feature | settings       | extension    | Extension settings screen |
| feature | settings       | display      | Display/UI settings screen |
| feature | settings       | folder       | Folder settings screen |
| feature | settings       | info         | Application information screen |
| feature | settings       | nav          | Settings navigation |
| feature | settings       | security     | Security settings screen |
| feature | settings       | viewer       | Viewer settings screen |
| feature | tutorial       |              | User onboarding screen |
| feature | tutorial       | nav          | Tutorial navigation |
| framework| common        |              | Common utilities |
| framework| background    |              | Background task processing implementations |
| framework| designsystem  |              | Design system components |
| framework| notification  |              | Notification processing implementations |
| framework| permission    |              | Permission management implementations |
| framework| startup       |              | Application startup initialization |
| framework| test          |              | Testing utilities |
| framework| ui            |              | Shared UI components |
| framework| ui            | file         | Shared file UI components and detail pane |
| framework| navkey-processor|            | Navigation key processor |

## Architecture Overview (Onion Architecture Mapping)

ComicViewer adheres to the principles of **Onion Architecture**, placing the Domain Model at its core with all dependencies pointing inward:

- **Layer 1: Domain Model (Core)**: Pure Kotlin entities, value objects, and domain errors (`:domain:model`).
- **Layer 2: Domain Services & Repository Ports**: Pure domain logic services (`:domain:service`) and Gateway/Port interfaces (`:domain:repository`) abstracting data access, storage, and background tasks.
- **Layer 3: Application Services / Use Cases**: Concrete use cases orchestrating domain logic, domain services, and repository abstractions (`:domain:usecase`).
- **Layer 4: Outer Ring (Infrastructure, Presentation & Composition Root)**:
  - **Presentation (UI)**: UI screens and viewmodels (`:feature:*`), shared design system (`:framework:designsystem`), and UI components (`:framework:ui`).
  - **Infrastructure**: Database (`:data:database`), storage/network (`:data:storage:*`), image loading (`:data:coil`), and platform services (`:framework:background`, `:framework:notification`, `:framework:permission`, `:framework:startup`).
  - **Composition Root & Platform Shell**: Application entry points, background synchronization, and Metro DI wiring (`:app:share`, `:app:sync`, `:app:androidApp`, `:app:jvmApp`, `:app:ios`).

## Module dependencies

```mermaid
graph TD
    subgraph app [app - Composition Root & Platform Shell]
        :app:androidApp --> :app:share
        :app:jvmApp --> :app:share
        :app:ios --> :app:share
        :app:share --> :app:sync
        :app:sync --> :domain:repository
        :app:sync --> :domain:usecase
        :app:sync --> :framework:background
        :app:sync --> :framework:notification
    end

    subgraph feature [feature - Presentation]
        :feature:bookshelf:info --> :feature:bookshelf:common
        :feature:bookshelf:info --> :feature:bookshelf:edit
        :feature:bookshelf:edit --> :feature:bookshelf:common
        :feature:bookshelf --> :feature:bookshelf:common
        :feature:bookshelf --> :feature:bookshelf:info
        :feature:collection:add --> :feature:collection:common
        :feature:collection:editor --> :feature:collection:common
        :feature:collection --> :feature:collection:common
        :feature:collection --> :feature:collection:editor
        :feature:settings:common --> :feature:settings:nav
        :feature:settings:display --> :feature:settings:common
        :feature:settings:folder --> :feature:settings:common
        :feature:settings:info --> :feature:settings:common
        :feature:settings:info --> :feature:tutorial:nav
        :feature:settings:security --> :feature:settings:common
        :feature:settings:security --> :feature:authentication:nav
        :feature:settings:viewer --> :feature:settings:common
        :feature:settings:extension --> :feature:settings:common
        :feature:tutorial --> :feature:tutorial:nav
    end

    subgraph domain [domain - Core & Application]
        :domain:usecase --> :domain:repository
        :domain:usecase --> :domain:service
        :domain:usecase --> :domain:model
        :domain:repository --> :domain:model
        :domain:service --> :domain:model
    end

    subgraph data [data - Infrastructure]
        :data:coil --> :domain:repository
        :data:database --> :domain:repository
        :data:database --> :domain:service
        :data:datastore --> :domain:repository
        :data:reader:document --> :data:storage
        :data:reader:zip --> :data:storage
        :data:storage:device --> :data:storage
        :data:storage:smb --> :data:storage
        :data:storage --> :domain:repository
        :data:storage --> :domain:service
    end

    subgraph framework [framework - UI & Platform Infrastructure]
        :framework:ui --> :framework:designsystem
        :framework:ui --> :framework:common
        :framework:ui:file --> :framework:ui
        :framework:ui:file --> :domain:usecase
        :framework:permission --> :framework:ui
        :framework:permission --> :framework:designsystem
        :framework:notification --> :framework:startup
    end

    :app:share --> feature
    :app:share --> data
    :app:share --> domain
    :app:share --> framework

    feature --> :domain:usecase
    feature --> :framework:designsystem
    feature --> :framework:ui
    feature --> :framework:ui:file

    data --> domain
```

## Screen transition diagram

![Screen Transition](./docs/screen_transition.svg)
