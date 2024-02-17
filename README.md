# ComicViewer

[![Qodana](https://github.com/SorrowBlue/ComicViewer/actions/workflows/qodana.yml/badge.svg?branch=main)](https://github.com/SorrowBlue/ComicViewer/actions/workflows/qodana.yml)

## Coding rules

Follow Android's [Kotlin style guide](https://developer.android.com/kotlin/style-guide).
Also, use [trailing comma](https://kotlinlang.org/docs/coding-conventions.html#trailing-commas).

Use [detekt](https://github.com/detekt/detekt) as a static code analysis tool.

## Plugin configuration

```mermaid
classDiagram
    class DaggerHilt
    class Koin
    class Detekt

    class AndroidApplication
    class AndroidLibrary
    class AndroidDynamicFeature

    class AndroidApplicationCompose
    class AndroidLibraryCompose
    class AndroidDynamicFeatureCompose

    class AndroidApp
    class AndroidFeature
    class AndroidFeatureDynamicFeature

    AndroidApplication ..> Detekt
    AndroidLibrary ..> Detekt
    AndroidDynamicFeature ..> Detekt

    AndroidApp ..> AndroidApplication
    AndroidApp ..> AndroidApplicationCompose
    AndroidApp ..> DaggerHilt
    AndroidApp ..> Koin

    AndroidFeature ..> AndroidLibrary
    AndroidFeature ..> AndroidLibraryCompose
    AndroidFeature ..> DaggerHilt

    AndroidFeatureDynamicFeature ..> AndroidDynamicFeature
    AndroidFeatureDynamicFeature ..> AndroidDynamicFeatureCompose
    AndroidFeatureDynamicFeature ..> DaggerHilt
    AndroidFeatureDynamicFeature ..> Koin

```

## Module configuration

    :app  # Application
    :data:database         # Room Database
    :data:datastore        # DataStore
    :data:infrastructure   # Infrastructure
    :data:reader           # ファイル読み込みのインターフェース
    :data:reader:document  # pdf, epub, cbz, cbr .etc の実装
    :data:reader:zip       # zip, rar, 7z .etc の実装
    :data:service          # WorkManagerの実装
    :data:storage          # 様々なプロトコルの抽象クラス
    :data:storage:device   # Androidのストレージの実装クラス
    :data:storage:smb      # SMBの実装クラス
    :di                    # Daggerの依存関係解決用のモジュール
                             appモジュールからdataへ直接的に依存させないため
    :domain:model          # ドメインモデル
    :domain:service        # ドメインサービス
    :domain:usecase        # ユースケース
    :feature:authentication  # 認証機能
    :feature:book          # 読書機能
    :feature:bookshelf     # 本棚機能
    :feature:bookshelf:edit  # 本棚編集機能
    :feature:bookshelf:selection  # 登録可能本棚リスト機能
    :feature:favorite      # お気に入り機能
    :feature:favorite:add  # お気に入り追加機能
    :feature:favorite:common  # お気に入り共通機能
    :feature:favorite:create  # お気に入り作成機能
    :feature:favorite:edit  # お気に入り編集機能
    :feature:file          # ファイルリスト
    :feature:folder        # フォルダ画面
    :feature:history       # 閲覧履歴機能
    :feature:library       # ライブラリ機能
    :feature:library:box   # Boxライブラリ機能
    :feature:library:dropbox  # Dropboxライブラリ機能
    :feature:library:googledrive  # GoogleDriveライブラリ機能
    :feature:library:onedrive  # OneDriveライブラリ機能
    :feature:readlater     # 後で読む機能
    :feature:search        # 検索機能
    :feature:settings      # 設定機能
    :feature:settings:common  # 設定共通機能
    :feature:settings:display  # 表示設定機能
    :feature:settings:folder  # フォルダ設定機能
    :feature:settings:info  # 情報設定機能
    :feature:settings:security  # セキュリティ設定機能
    :feature:settings:viewer  # ビューア設定機能
    :feature:tutorial      # チュートリアル機能
    :feature:framework:common  # 共通機能
    :feature:framework:designsystem  # デザインシステム
    :feature:framework:notificaiton  # 通知機能
    :feature:framework:ui  # UI機能

## Module dependencies

```mermaid
graph LR
    subgraph feature
        direction LR
        :bookshelf --> :bookshelf:edit
        :bookshelf --> :bookshelf:selection
        :bookshelf --> :folder
        :favorite --> :file
        :favorite --> :folder
        :favorite --> :favorite:common
        :favorite --> :favorite:edit
        :favorite:add --> :favorite:common
        :search --> :file
        :search --> :folder
        :readlater --> :file
        :readlater --> :folder
        :folder --> :file
        :history --> :file
        :library --> :history
        :library:googledrive --> :library
        :library:onedrive --> :library
        :library:dropbox --> :library
        :library:box --> :library
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
        :settings:security --> :authentication
        :settings --> :tutorial
    end
    subgraph app
        direction LR
        :app --> :bookshelf
        :app --> :favorite
        :app --> :library
        :app --> :readlater
        :app --> :search
        :app --> :authentication
        :app --> :tutorial
    end
    feature --> domain
    subgraph domain
        direction LR
        :service --> :usecase
        :usecase --> :model
    end
    subgraph feature
        direction LR
        :tutorial
        :library:googledrive --> :app
        :library:onedrive --> :app
        :library:dropbox --> :app
        :library:box --> :app
    end
    subgraph data
        direction LR
        :infrastructure --> :usecase
        :infrastructure --> :model
        :service --> :infrastructure
        :coil --> :infrastructure
        :coil --> :reader
        :document --> :reader
        :document --> :app
        :zip --> :reader
        :device --> :infrastructure
        :device --> :reader
        :storage --> :device
        :smb --> :device
        :datastore --> :infrastructure
        :database --> :infrastructure
        :paging --> :infrastructure
        :paging --> :database
    end
```

## Screen transition diagram

![Screen Transition](./docs/screen_transition.svg)
