# AGENTS.md

このドキュメントは、ComicViewerリポジトリの概要と開発・運用に関するガイドラインを提供します。

## 目次

- [プロジェクト概要](#プロジェクト概要)
- [技術スタック](#技術スタック)
- [環境構築](#環境構築)
- [ビルドおよびテストコマンド](#ビルドおよびテストコマンド)
- [コードスタイルガイドライン](#コードスタイルガイドライン)
- [テスト手順](#テスト手順)
- [セキュリティに関する考慮事項](#セキュリティに関する考慮事項)
- [Pull Requestポリシー](#pull-requestポリシー)
- [アーキテクチャ](#アーキテクチャ)
- [リリースプロセス](#リリースプロセス)
- [トラブルシューティング](#トラブルシューティング)

---

## プロジェクト概要

ComicViewerは、Android、iOS、JVM(Desktop)をサポートするマルチプラットフォームコミックビューアアプリケーションです。Kotlin Multiplatformで開発され、Jetpack Composeを使用したモダンなUIを提供します。

### 主な機能
- マルチプラットフォーム対応（Android、iOS、JVM）
- モジュラーアーキテクチャによる高い保守性
- ローカルストレージおよびネットワークストレージ（SMB）のサポート
- 複数のファイル形式に対応（アーカイブ、ドキュメント）

---

## 技術スタック

- **Kotlin Multiplatform**: メインプログラミング言語
- **Jetpack Compose**: UI フレームワーク（Android、JVM、iOS）
- **Kotlin**: 2.3.0
- **Gradle**: 9.2.1
- **Java**: 21（必須）
- **Android SDK**: compileSdk 36、minSdk 30
- **モジュラーアーキテクチャ**: feature/domain/data レイヤー構成
- **Metro**: 依存性注入フレームワーク
- **Room**: データベース（Android）
- **Coil**: 画像読み込みライブラリ

---

## 環境構築

### 前提条件

1. **Java 21のインストール（必須）**
   - Java 17では動作しません
   - `JAVA_HOME`環境変数をJava 21のインストール先に設定
   - 確認コマンド: `java -version` でOpenJDK 21+が表示されること

2. **Android SDK**
   - API 36（compileSdk）
   - 最小API 30（minSdk）
   - Android SDK Build-Tools

3. **ネットワークアクセス**
   - Google Maven repository（dl.google.com）へのアクセスが必要
   - Maven Central（repo1.maven.org）
   - Gradle Plugin Portal（plugins.gradle.org）

### 初期セットアップ

```bash
# Java 21が有効か確認
java -version

# リポジトリをクローン
git clone https://github.com/SorrowBlue/ComicViewer.git
cd ComicViewer

# Gradleラッパーの権限を付与
chmod +x gradlew
```

---

## ビルドおよびテストコマンド

### 基本ビルドコマンド

```bash
# クリーンビルド（45-60分）
./gradlew clean build

# アプリのビルド（30-40分）
./gradlew app:android:build app:jvm:build

# 全モジュールのチェック（25-35分）
./gradlew check
```

### プラットフォーム別ビルド

```bash
# Android Debug（25-35分）
./gradlew app:android:assembleDebug

# Android Release（30-40分）
./gradlew app:android:assembleRelease

# JVM（20-30分）
./gradlew app:jvm:packageDistributionForCurrentOS
```

### テストコマンド

```bash
# 全テストの実行（20-30分）
./gradlew allTests

# Android Unitテストのみ（10-15分）
./gradlew testDebugUnitTest

# JVM テスト（5-10分）
./gradlew jvmTest

# 特定のモジュールのテスト
./gradlew :domain:model:test
./gradlew :data:database:test
```

### 品質チェックコマンド

```bash
# Detekt（静的コード解析）全プラットフォーム（15-20分）
./gradlew reportMerge

# build-logic の Detekt（3-5分）
./gradlew :build-logic:detektAll

# コードフォーマット
./gradlew detektFormat

# Android Lint - Debug（8-12分）
./gradlew app:android:lintDebug

# Android Lint - すべてのビルドバリアント
./gradlew app:android:lintDebug
./gradlew app:android:lintInternal
./gradlew app:android:lintPrerelease
./gradlew app:android:lintRelease

# Version Catalog チェック（2-3分）
./gradlew versionCatalogLint
```

### 重要な注意事項

- **ビルドを絶対にキャンセルしないでください**: フルビルドには30-45分かかります。タイムアウトは60分以上に設定してください。
- **ネットワークアクセス**: Google Mavenリポジトリへのアクセスが必要です。制限された環境では失敗します。

---

## コードスタイルガイドライン

### 基本ルール

1. **Kotlin Style Guide**
   - Androidの[Kotlin style guide](https://developer.android.com/kotlin/style-guide)に従う
   - **トレーリングカンマ**を必ず使用する（配列、関数パラメータ、クラス宣言など）
   - 関数名: キャメルケース（camelCase）
   - クラス名: パスカルケース（PascalCase）

2. **パッケージ構造**
   - すべてのパッケージは`com.sorrowblue.comicviewer.*`プレフィックスを使用
   - ファイル末尾に改行を含める
   - importsは`*`グループ、次に`^`グループの順序で整理（detekt強制）

### Compose固有のルール

```kotlin
// ✅ 正しい例
@Composable
fun ScreenContent(
    title: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Material3 コンポーネントを優先
    Button(
        onClick = onAction,
        modifier = modifier,
    ) {
        Text(text = title)
    }
}

// ❌ 避けるべき例（トレーリングカンマがない）
@Composable
fun ScreenContent(
    title: String,
    onAction: () -> Unit
) {
    // ...
}
```

- `@Composable`関数は名前付きパラメータを使用
- デフォルト引数とトレーリングラムダを活用
- Material3コンポーネントを優先使用
- `@Preview`アノテーションでUIコンポーネントのプレビューを提供
- Compose命名規則に従う: `ScreenContent`, `ScreenState`など
- Android向けにMaterial3 Adaptiveコンポーネントを使用
- 適切な状態管理で再コンポーズを最小化

### ドキュメント要件

```kotlin
/**
 * 公開APIにはKDocコメントを追加
 *
 * @param userId ユーザーID
 * @return ユーザー情報
 */
fun fetchUser(userId: String): User {
    // 複雑なロジックには日本語でのコメントを付ける
    // データベースからユーザー情報を取得する
    return database.getUser(userId)
}

// TODO(username): 実装予定の機能について担当者を明記
```

- **公開API**: すべての公開関数とクラスにKDocコメントを追加
- **複雑なロジック**: ビジネスロジックには日本語でのコメント
- **TODO**: 担当者情報を含める
- **アーキテクチャの決定**: マルチプラットフォームのexpect/actual実装を文書化

---

## テスト手順

### テスト標準

1. **新機能には必ずユニットテストを含める**
2. **UIテストにはCompose Testing Frameworkを使用**
3. **テストファイルはソースファイルと同じパッケージ構造に配置**
4. **テストダブルを使用して外部依存関係をモック**
5. **マルチプラットフォームコードはすべてのターゲットプラットフォームでテスト**

### テストファイル構造

```
src/
  main/kotlin/com/sorrowblue/comicviewer/feature/
    UserRepository.kt
  test/kotlin/com/sorrowblue/comicviewer/feature/
    UserRepositoryTest.kt
```

### 検証シナリオ

#### Android検証
1. `./gradlew app:android:assembleDebug app:android:testDebugUnitTest`
2. `./gradlew app:android:lintDebug`
3. UI変更がある場合は手動でコアユーザーフローをテスト

#### JVM検証
1. `./gradlew app:jvm:packageDistributionForCurrentOS`
2. デスクトップ固有機能のテスト（ウィンドウ管理、ファイルシステムアクセス）
3. マルチプラットフォームコードが全ターゲットで動作することを確認

#### クロスプラットフォーム検証
1. `./gradlew reportMerge`
2. `./gradlew :domain:model:test :data:database:test`
3. expect/actual実装が正しく動作することを確認

---

## セキュリティに関する考慮事項

### 必須チェック項目

1. **入力値の検証**
   - すべてのユーザー入力に対してバリデーションを実装
   - SQLインジェクション、XSS攻撃などを防ぐ

2. **Null安全性**
   - Kotlinのnull安全性機能を活用
   - null可能性を考慮したコード設計

3. **外部ライブラリ**
   - 最新の安定版を使用
   - 既知の脆弱性がないか定期的に確認

4. **API通信**
   - 適切なエラーハンドリングを含める
   - 機密情報を含むデータの暗号化

5. **ライフサイクル管理**
   - Android向けに適切なライフサイクル管理を実装
   - メモリリークを避ける

6. **シークレット管理**
   - ソースコードにシークレットをコミットしない
   - 環境変数やGitHub Secretsを使用

### GitHub Actionsで使用されるシークレット

- `ANDROID_STORE_FILE_BASE64`: Android keystoreファイル
- `ANDROID_STORE_PASSWORD`: keystoreパスワード
- `ANDROID_KEY_ALIAS`: キーエイリアス
- `ANDROID_KEY_PASSWORD`: 署名キーパスワード
- `GOOGLE_WORKLOAD_IDENTITY_PROVIDER`: Google Cloudワークロードアイデンティティ
- `GOOGLE_SERVICE_ACCOUNT`: Play Consoleアクセス用サービスアカウント
- `DISCORD_WEBHOOK`: Discord通知用webhook URL

---

## Pull Requestポリシー

### ブランチ命名規則

命名規則: `[type]/[issue-number]-[Issueタイトルを簡略化した内容]`

**Type一覧:**
- `feature/`: 新しい機能の提案や実装
- `enhancement/`: 既存機能の改善
- `refactor/`: コードの内部的な改善（機能変更なし）
- `fix/`: バグ、予期しない動作
- `doc/`: ドキュメントの作成、修正、追加
- `dependencies/`: 依存関係更新
- `chore/`: ビルド、CI/CD、依存関係更新など

**例:**
```
feature/123-add-bookmark-feature
fix/456-crash-on-startup
doc/789-update-readme
```

### コミットメッセージ標準

- **英語で記述**
- **Conventional Commits形式を推奨**

**フォーマット:**
```
<type>: <subject>

<body>

<footer>
```

**Type一覧:**
- `feat`: 新機能
- `fix`: バグ修正
- `docs`: ドキュメント
- `style`: コードスタイル（機能に影響しない）
- `refactor`: リファクタリング
- `test`: テストの追加・修正
- `chore`: ビルドプロセスやツールの変更

**例:**
```
feat: Add bookmark feature to comic viewer

Implement bookmark functionality that allows users to save
their reading progress.

Closes #123
```

### Issue・PR言語ガイドライン

- **Issueタイトル**: 英語で記述
- **Issue説明・コメント**: 日本語で記述
- **PRタイトル**: 英語で記述
- **PR説明・コメント**: 日本語で記述
- **会話・コミュニケーション**: 日本語で行う

### PR作成前チェックリスト

コミット前に以下を必ず実行してください:

```bash
# 1. コードフォーマット
./gradlew detektFormat

# 2. 静的コード解析
./gradlew reportMerge

# 3. Lint チェック
./gradlew app:android:lintDebug

# 4. テスト実行
./gradlew allTests
```

### PR説明の書き方

1. **変更内容の要約**を明確に記載
2. **関連するIssue番号**を含める（`Fixed #123`）
3. **テスト方法**を記載
4. **スクリーンショット**（UI変更がある場合）
5. **破壊的変更**がある場合は明記

**テンプレート例:**
```markdown
## 変更内容
ブックマーク機能を実装しました。

## 関連Issue
Fixed #123

## テスト方法
1. アプリを起動
2. コミックを開く
3. ブックマークボタンをタップ
4. ブックマークが保存されることを確認

## スクリーンショット
（該当する場合）

## チェックリスト
- [x] detekt実行済み
- [x] lint実行済み
- [x] テスト実行済み
- [x] ドキュメント更新済み
```

### ラベル管理

- **Issue**: `.github/labels.yml`に定義されているラベルから適切なものを選択
- **PR**: Release Drafterによって自動的に付与されるため、手動でつける必要はありません

### 品質ゲート

すべてのPRは以下をパスする必要があります:

1. **Lint**: Android Lint チェック
2. **Detekt**: 静的コード解析
3. **Test**: ユニットテスト
4. **Build**: ビルド成功

これらは`.github/workflows/lint-test-build.yml`で自動実行されます。

---

## アーキテクチャ

### モジュール構成

ComicViewerは以下のレイヤー構造を採用しています:

```
├── app/                  # メインアプリケーション（Android/Desktop/iOS エントリポイント）
│   ├── android/          # Android アプリケーション
│   ├── desktop/          # Desktop アプリケーション
│   ├── ios/              # iOS アプリケーション
│   └── share/            # 共有コード
├── feature/              # UI機能モジュール（画面とナビゲーション）
│   ├── authentication/   # ログイン・認証画面
│   ├── book/             # コミックビューワーと管理
│   ├── bookshelf/        # ライブラリとコレクションビュー
│   ├── favorite/         # お気に入り管理
│   ├── file/             # ファイルブラウザと管理
│   ├── folder/           # フォルダナビゲーション
│   ├── library/          # 外部ライブラリ統合
│   ├── readlater/        # 後で読む機能
│   ├── search/           # 検索と発見
│   ├── settings/         # アプリケーション設定
│   └── tutorial/         # ユーザーオンボーディング
├── domain/               # ビジネスロジック層
│   ├── model/            # データモデルとエンティティ
│   ├── service/          # ビジネスサービス
│   └── usecase/          # ユースケース実装
├── data/                 # データアクセス層
│   ├── coil/             # 画像読み込み設定
│   ├── database/         # Roomデータベースセットアップ
│   ├── reader/           # ファイル読み込み実装
│   └── storage/          # ストレージクライアント実装
└── framework/            # 共有フレームワークコンポーネント
    ├── common/           # 共通ユーティリティ
    ├── designsystem/     # デザインシステムコンポーネント
    ├── notification/     # 通知処理
    └── ui/               # 共通UIコンポーネント
```

### 依存関係ルール

- **上位レイヤーは下位レイヤーに依存可能**
- **同レベルレイヤー間の依存は最小限に**
- **featureモジュール間の直接依存は避ける**
- **Compose Navigationで画面遷移を実装**
- **Destinationsライブラリを活用**

詳細なモジュール依存関係図は[README.md](./README.md)を参照してください。

### 一般的な開発タスク

#### 新機能の追加

1. `feature/`ディレクトリに機能モジュールを作成
2. `domain/model/`でドメインモデルを定義
3. `domain/usecase/`でユースケースを実装
4. `data/`にデータレイヤーコンポーネントを追加
5. 変更後に必ず`./gradlew detektAll`を実行

#### 既存機能の修正

1. README.mdのモジュール依存関係図で依存関係を確認
2. 同じパッケージ構造で対応するテストファイルを更新
3. 影響を受けるモジュールのlintとtestコマンドを実行
4. AndroidとJVMの両方の互換性を確認

#### データベース変更

1. Roomデータベースファイルは`data/database/`にあります
2. スキーマ変更には必ずマイグレーションスクリプトを作成
3. `./gradlew :data:database:test`でマイグレーションをテスト
4. データベース設定でバージョンを更新

### プラットフォーム固有の考慮事項

#### Android
- すべての実装でAndroid Lifecycleを考慮
- Material3 Adaptiveコンポーネントを使用
- 適切な修飾子を使用したAndroidリソース命名規則に従う
- ActivityとFragmentのライフサイクル管理を確実に実装

#### iOS
- プラットフォーム固有の実装にはexpect/actualパターンを使用
- iOS Human Interface Guidelinesを考慮
- すべてのexpect宣言に対応するactual実装があることを確認

#### JVM
- デスクトップ固有のUIパターン（メニューバー、キーボードショートカット）を考慮
- 適切なウィンドウ管理機能を実装
- ファイルシステムアクセスなどのデスクトップ固有機能をテスト

---

## リリースプロセス

### 自動リリースフロー

ComicViewerは完全自動化されたリリースプロセスを採用しています。

#### 1. Release Drafter

`.github/workflows/release-drafter.yml`が以下を自動実行:

- mainブランチへのPush時にドラフトリリースを作成・更新
- マージされたPull Requestからリリースノートを自動生成
- Pull Requestに適切なラベルを自動付与

#### 2. リリースワークフロー

`.github/workflows/release.yml`が以下を実行:

- リリースが公開されたときに自動実行
- 品質チェック（Detekt、Lint、Test）
- AndroidリリースとJVMリリースの並行ビルド
- Google Play Console（Internal App Sharing）へのAABアップロード
- GitHub Releaseへの成果物アップロード
- Discord通知の送信

### バージョン管理

#### 自動バージョン計算

- **versionName**: Gitタグから取得（例: `v0.1.0-beta.1`, `v0.1.0`）
- **versionCode**: versionNameから自動計算

#### versionCodeの計算式

- **正式リリース**: `(major × 10000 + minor × 100 + patch) × 100 + 99`
- **Betaリリース**: `(major × 10000 + minor × 100 + patch) × 100 + beta番号`

**例:**
- `v0.1.0-beta.1` → versionCode: `10001`
- `v0.1.0-beta.2` → versionCode: `10002`
- `v0.1.0` → versionCode: `10099`

### リリース手順

1. **開発とPull Request作成**
2. **適切なGitタグを作成**（例: `v0.1.0-beta.1`）
3. **ドラフトリリースを確認・編集**
4. **GitHubでリリースをPublish**
5. **自動ビルドと配布が実行される**

詳細は[docs/release-automation.md](./docs/release-automation.md)を参照してください。

---

## トラブルシューティング

### Detekt失敗

```bash
# コードフォーマットの自動修正
./gradlew detektFormat
```

**一般的な問題:**
- **トレーリングカンマの欠落**: コレクションと関数の最後の要素/パラメータの後にカンマを追加
- **import順序**: IDEツールまたはdetekt自動修正でimportを並べ替え
- **複雑な関数**: 複雑性のしきい値を超える関数を分割

### Android Lint失敗

**一般的な問題:**
- **APIレベルの問題**: 最小SDK（30）とターゲットSDK（36）の互換性を確認
- **リソース命名**: drawable、string、layoutのAndroid命名規則に従う
- **ハードコードされた文字列**: 文字列を適切なvaluesフォルダのリソースに抽出
- **ライフサイクルの問題**: ActivityとFragmentで適切なライフサイクル管理を確保

### マルチプラットフォームの問題

**一般的な問題:**
- **Expect/Actual不一致**: すべてのexpect宣言に対応するactual実装があることを確認
- **プラットフォーム固有API**: プラットフォーム差異に適切なexpect/actualパターンを使用
- **依存関係の競合**: 互換性のあるマルチプラットフォームライブラリバージョンのバージョンカタログを確認
- **ビルドバリアントの問題**: AndroidとJVMの両方のターゲットで変更をテスト

### モジュール依存関係の問題

**一般的な問題:**
- **循環依存**: README.mdのモジュール依存関係図を参照
- **API変更**: 公開APIを変更する際はすべての依存モジュールを更新
- **バージョン競合**: 一貫した依存関係バージョンを維持するためにバージョンカタログを使用
- **依存関係の欠落**: 影響を受けるすべてのプラットフォームソースセットに必要な依存関係を追加

### パフォーマンスの問題

**一般的な問題:**
- **ビルド時間**: Gradleデーモン、設定キャッシュ、並列ビルドを使用（デフォルトで有効）
- **メモリエラー**: OOMでビルドが失敗する場合、gradle.propertiesでヒープサイズを増やす
- **キャッシュの問題**: 奇妙な動作が発生した場合は`./gradlew clean`でビルドキャッシュをクリア
- **ネットワークタイムアウト**: リポジトリアクセスのための企業プロキシ設定を確認

### ビルド環境の問題

#### ネットワーク要件

- Google Mavenリポジトリ（dl.google.com）へのアクセスが必要
- 制限された環境ではビルドが「Plugin not found」エラーで失敗します
- 企業環境ではプロキシ設定が必要な場合があります

#### 制限された環境での代替検証

ネットワークアクセスが制限されている場合:

- コードベースの既存パターンに対して手動でコード変更をレビュー
- ローカルで利用可能な静的解析ツールを使用
- 既存ファイルに合わせた一貫したコードスタイルに焦点を当てる
- 可能な限り分離してロジック変更をテスト
- README.mdに示されているモジュール依存関係ルールに対して検証

#### 一般的なビルド失敗

- **ネットワーク接続**: 最も一般的な問題 - Google/Mavenリポジトリへのアクセスを確認
- **Javaバージョンの不一致**: Java 17や他のバージョンではなくJava 21が有効であることを確認
- **Android SDKの欠落**: API 36とビルドツールを含むAndroid SDKをインストール
- **メモリの問題**: ビルドには十分なメモリが必要（gradle.propertiesで4GB+推奨）
- **Gradleキャッシュの破損**: ビルドが予期しない動作をする場合は`./gradlew clean`でクリア

---

## 参考資料

### 公式ドキュメント
- [Android Developers](https://developer.android.com/) - Android開発の公式ガイドライン
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Compose開発ガイド
- [Kotlin Multiplatform](https://kotlinlang.org/lp/multiplatform/) - KMP公式ドキュメント
- [Material Design 3](https://m3.material.io/) - Material3デザインシステム

### プロジェクト固有ドキュメント
- [README.md](./README.md) - プロジェクト概要とモジュール依存関係図
- [docs/release-automation.md](./docs/release-automation.md) - リリースプロセス詳細
- [docs/screen_transition.svg](./docs/screen_transition.svg) - 画面遷移図
- [copilot-workspace-custom-instructions.md](./copilot-workspace-custom-instructions.md) - 追加のコーディングガイドライン
- [.github/copilot-instructions.md](./.github/copilot-instructions.md) - GitHub Copilot用の詳細なガイドライン

### 設定ファイル
- `gradle/libs.versions.toml` - 一元化された依存関係バージョン
- `gradle.properties` - Gradleとビルド設定
- `settings.gradle.kts` - マルチモジュールプロジェクト設定

---
