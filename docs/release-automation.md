# リリース自動化

このドキュメントでは、GitHub Actionsを使用したComicViewerの自動リリースプロセスについて説明します。

## 概要

リリース自動化は以下の2つのワークフローによって実現されています：
- **Release Drafter**: Pull Requestから自動的にドラフトリリースを作成
- **Release**: リリースが公開されたときにAndroidおよびDesktopバージョンをビルド・配布

バージョン管理は完全に自動化されており、`versionCode`は`versionName`（Gitタグ）から自動計算されます。

## ワークフロー

### Release Drafterワークフロー
Release Drafterワークフロー（`/.github/workflows/release-drafter.yml`）は、以下のタイミングで実行されます：

1. **自動**: mainブランチへのPush時
2. **自動**: Pull Requestの作成・更新時
3. **機能**:
   - マージされたPull Requestから自動的にリリースノートを生成
   - ドラフトリリースを作成・更新
   - Pull Requestに適切なラベルを自動付与

### リリースワークフロー
メインのリリースワークフロー（`/.github/workflows/release.yml`）は、以下のシナリオで実行されます：

1. **自動**: GitHubでリリースが公開されたとき
2. **手動**: GitHub Actionsタブの「Run workflow」ボタンを使用

## バージョン管理

### 自動バージョン計算
バージョン管理は完全に自動化されています：

- **versionName**: Gitタグから取得（例：`v0.1.0-beta.1`、`v0.1.0`）
- **versionCode**: versionNameから自動計算される整数値

### versionCodeの計算式
- **正式リリース**: `(major × 10000 + minor × 100 + patch) × 100 + 99`
- **Beta リリース**: `(major × 10000 + minor × 100 + patch) × 100 + beta番号`

#### 例
- `v0.1.0-beta.1` → versionCode: `10001`
- `v0.1.0-beta.2` → versionCode: `10002`
- `v0.1.0` → versionCode: `10099`

これにより、betaバージョンは番号順に増加し、正式リリースは常に対応するbetaバージョンより高い値になります。

## プロセスフロー

### 1. 品質チェック
リリース成果物がビルドされる前に、以下の品質チェックが通過する必要があります：
- **Detekt**: Kotlinコードの静的コード解析
- **Android Lint**: Android固有のコード品質チェック
- **Version Catalog**: 依存関係バージョンの検証
- **Unit Tests**: すべてのプロジェクトユニットテスト

### 2. Androidリリース
品質チェックが通過した場合：
- 適切なコード署名でリリースAAB（Android App Bundle）をビルド
- Google Play Console（Internal App Sharing）にAABをアップロード
- GitHub ReleaseのためにAABとAPK成果物を保存

### 3. Desktopリリース
Androidリリースと並行して：
- Windows、macOS、Linuxのネイティブディストリビューションをビルド
- Compose Multiplatformを使用してプラットフォーム固有のパッケージを作成
- GitHub Releaseのためにディストリビューション成果物を保存

### 4. アセットアップロードと通知
ビルドが成功した後：
- すべての成果物をGitHub Releaseページにアップロード
- ダウンロードリンク付きの成功通知をDiscordに送信
- 失敗時は、エラー通知をDiscordに送信

## 必要なシークレット

以下のシークレットがGitHubリポジトリ設定で構成されている必要があります：

### Android署名
- `ANDROID_STORE_FILE_BASE64`: Base64エンコードされたAndroidキーストアファイル
- `ANDROID_STORE_PASSWORD`: キーストアファイルのパスワード
- `ANDROID_KEY_ALIAS`: キーストア内のキーエイリアス
- `ANDROID_KEY_PASSWORD`: 署名キーのパスワード

### Google Play Console
- `GOOGLE_WORKLOAD_IDENTITY_PROVIDER`: Google Cloudワークロードアイデンティティプロバイダー
- `GOOGLE_SERVICE_ACCOUNT`: Play Consoleアクセス用のGoogleサービスアカウント

### Discord通知
- `DISCORD_WEBHOOK`: 通知用のDiscord webhook URL

## 環境要件

ワークフローは`android`環境を使用し、以下で構成される必要があります：
- 上記に記載された必要なシークレット
- 必要に応じて適切な保護ルール

## リリースプロセス

### 現在のリリースフロー

1. **開発とPull Request**:
   - 機能開発やバグ修正のPull Requestを作成
   - Release Drafterが自動的にドラフトリリースを更新

2. **リリース準備**:
   - 適切なGitタグを作成（例：`v0.1.0-beta.1`、`v0.1.0`）
   - Release Drafterが作成したドラフトリリースを確認・編集

3. **リリース公開**:
   - GitHubでドラフトリリースを「Publish」
   - リリースワークフローが自動的に開始される

4. **自動ビルドと配布**:
   - 品質チェック（lint、test、static code analysis）の実行
   - AndroidリリースとDesktopリリースの並行ビルド
   - 成果物のGitHub Releaseへのアップロード
   - Discord通知の送信

### 手動リリーストリガー
必要に応じて、GitHub Actionsタブから「Release」ワークフローを手動実行することも可能です。

## 監視

- ワークフローの進行状況についてはActionsタブを確認
- Discordが成功/失敗の通知を受信
- リリース成果物がGitHub Releaseページに表示
- Play ConsoleアップロードURLがDiscord通知で提供

## トラブルシューティング

ワークフローが失敗した場合：
1. Actionsタブでワークフローログを確認
2. 必要なシークレットがすべて構成されていることを確認
3. `android`環境が存在し、シークレットにアクセスできることを確認
4. ログへの直接リンク付きのエラー通知についてDiscordを確認