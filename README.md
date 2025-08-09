# ComicViewer

[![Lint, Test, and Build](https://github.com/SorrowBlue/ComicViewer/actions/workflows/lint-test-build.yml/badge.svg?event=push)](https://github.com/SorrowBlue/ComicViewer/actions/workflows/lint-test-build.yml)
[![Deploy Dokka and static content to Pages](https://github.com/SorrowBlue/ComicViewer/actions/workflows/gh-pages.yml/badge.svg)](https://github.com/SorrowBlue/ComicViewer/actions/workflows/gh-pages.yml)

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

    KotlinMultiplatformAndroidDynamicFeatureConventionPlugin --> AndroidLintConventionPlugin
    KotlinMultiplatformAndroidDynamicFeatureConventionPlugin --> DetektConventionPlugin

    KotlinMultiplatformLibraryConventionPlugin --> AndroidLintConventionPlugin
    KotlinMultiplatformLibraryConventionPlugin --> DetektConventionPlugin

    KotlinMultiplatformComposeConventionPlugin --> DetektConventionPlugin

    KotlinMultiplatformKoinConventionPlugin

    KotlinMultiplatformFeatureConventionPlugin --> KotlinMultiplatformLibraryConventionPlugin
    KotlinMultiplatformFeatureConventionPlugin --> KotlinMultiplatformComposeConventionPlugin
    KotlinMultiplatformFeatureConventionPlugin --> KotlinMultiplatformKoinConventionPlugin
```

## Module configuration

| Module  |                |              | Overview               |
|---------|----------------|--------------|------------------------|
| app     |                |              | Application            |
| data    | coil           |              | サムネイル処理の実装             |
| data    | database       |              | データベースの実装              |
| data    | reader         | document     | ファイルリーダーのドキュメント形式実装    |
| data    | reader         | zip          | ファイルリーダーのアーカイブ形式実装     |
| data    | storage        | client       | ファイルクライアント             |
| data    | storage        | device       | ファイルクライアントのローカルストレージ実装 |
| data    | storage        | smb          | ファイルクライアントのSMBサーバー実装   |
| di      |                |              |                        |
| domain  | model          |              | ドメインモデル                |
| domain  | reader         |              | ページリーダー                |
| domain  | service        |              | ドメインサービス               |
| domain  | usecase        |              | ドメインサービス               |
| feature | authentication |              | 認証画面                   |
| feature | book           |              | ビューワー画面                |
| feature | bookshelf      |              | 本棚画面                   |
| feature | bookshelf      | edit         | 本棚編集画面                 |
| feature | bookshelf      | selection    | 登録可能本棚画面               |
| feature | favorite       |              | お気に入り画面                |
| feature | favorite       | add          | お気に入り追加画面              |
| feature | favorite       | common       | お気に入り共通機能              |
| feature | favorite       | create       | お気に入り作成画面              |
| feature | favorite       | edit         | お気に入り編集画面              |
| feature | file           |              | ファイル共通機能               |
| feature | folder         |              | フォルダ画面                 |
| feature | history        |              | 履歴機能                   |
| feature | library        |              | ライブラリ機能                |
| feature | library        | box          | Boxライブラリ機能             |
| feature | library        | dropbox      | Dropboxライブラリ機能         |
| feature | library        | googledrive  | GoogleDriveライブラリ機能     |
| feature | library        | onedrive     | OneDriveライブラリ機能        |
| feature | readlater      |              | 後で読む画面                 |
| feature | search         |              | 検索画面                   |
| feature | settings       |              | 設定画面                   |
| feature | settings       | common       | 設定共通機能                 |
| feature | settings       | display      | 画面設定画面                 |
| feature | settings       | folder       | フォルダ設定画面               |
| feature | settings       | info         | アプリ情報画面                |
| feature | settings       | security     | セキュリティ設定画面             |
| feature | settings       | viewer       | ビューワー設定画面              |
| feature | tutorial       |              | チュートリアル画面              |
| feature | framework      | common       | フレームワーク共通              |
| feature | framework      | designsystem | デザインシステム               |
| feature | framework      | notificaiton | 通知機能                   |
| feature | framework      | ui           | UI共通機能                 |

## Module dependencies

```mermaid
graph LR
    :app --> :usecase
    :app --> :authentication
    :app --> :book
    :app --> :bookshelf
    :app --> :favorite
    :app --> :favorite:add
    :app --> :readlater
    :app --> :search
    :app --> settings
    :app --> :settings:security
    :app --> :tutorial
    :app --> library
    subgraph feature
        direction LR
        :authentication
        :authentication
        :book
        :bookshelf --> :bookshelf:edit
        :bookshelf --> :bookshelf:selection
        :bookshelf --> :folder
        :favorite --> :file
        :favorite --> :folder
        :favorite --> :favorite:edit
        :favorite --> :favorite:common
        :favorite:add --> :favorite:common
        :favorite:edit --> :favorite:common
        :folder --> :file
        :history --> :file

        subgraph library
            direction RL
            :library:box --> :library
            :library:dropbox --> :library
            :library:googledrive --> :library
            :library:onedrive --> :library
        end
        :readlater --> :file
        :readlater --> :folder
        :search --> :file
        :search --> :folder
        subgraph settings
            direction LR
            :settings --> :settings:common
            :settings --> :settings:display
            :settings --> :settings:folder
            :settings --> :settings:info
            :settings --> :settings:security
            :settings --> :settings:viewer
            :settings:display --> :settings:common
            :settings:folder --> :settings:common
            :settings:info --> :settings:common
            :settings:security --> :settings:common
            :settings:viewer --> :settings:common
        end
    end
    subgraph domain
        direction LR
        :usecase --> :model
        :service --> :model
        :service --> :usecase
        :service --> :reader
    end
    subgraph data
        direction LR
        :coil --> :service
        :coil --> :reader
        base --> :service
        source --> :service
        :reader:document --> :storage:client
        :reader:zip --> :storage:client
        :storage:client --> :service
        :storage:client --> :model
        :storage:client --> :reader
        :storage:device --> :storage:client
        :storage:smb --> :storage:client
    end
```

## Screen transition diagram

![Screen Transition](./docs/screen_transition.svg)
