# リリース自動化

このドキュメントでは、GitHub Actionsを使用したComicViewerの自動リリースプロセスについて説明します。

## 概要

リリース自動化ワークフロー（`/.github/workflows/release.yml`）は、ComicViewerのAndroidおよびDesktopバージョンの両方をリリースするためのエンドツーエンドの自動化を提供します。

## ワークフロー

### リリースワークフロー
メインのリリースワークフロー（`/.github/workflows/release.yml`）は、以下の2つのシナリオで実行されます：

1. **自動**: GitHubでリリースが公開されたとき
2. **手動**: GitHub Actionsタブの「Run workflow」ボタンを使用

### バージョンバンプワークフロー
バージョンバンプワークフロー（`/.github/workflows/version-bump-release.yml`）は、バージョンインクリメントプロセスを自動化します：

1. **手動**: GitHub Actionsタブの「Run workflow」ボタンを使用
2. **機能**:
   - `gradle/libs.versions.toml`内の`versionCode`を自動的にインクリメント
   - フィーチャーブランチを作成し、`:rocket: bump versionCode`メッセージでコミット
   - バージョンバンプのためのmainブランチへのPull Requestを作成

### ドラフトリリース公開ワークフロー
ドラフトリリース公開ワークフロー（`/.github/workflows/publish-draft-release.yml`）は、ドラフトリリースを自動的に公開します：

1. **自動**: バージョンバンプ変更がmainブランチにマージされたときにトリガー
2. **機能**:
   - バージョンバンプPRがマージされたことを検出
   - 最新のドラフトリリースを見つけて適切に公開
   - "beta"タグ検出に基づいてリリースタイプ（PreRelease vs Release）を決定

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

## 手動リリースプロセス

### オプション1: 従来のリリースプロセス

1. GitHubで新しいリリースを作成：
   - リポジトリの「Releases」に移動
   - 「Create a new release」をクリック
   - タグを選択または作成（例：`v1.0.0`）
   - リリースノートを追加
   - 「Publish release」をクリック

2. ワークフローが自動的に開始され：
   - すべての品質チェックを実行
   - リリース成果物をビルド
   - Play Consoleにアップロード
   - GitHub Releaseに成果物をアップロード
   - Discord通知を送信

### オプション2: 自動バージョンバンプとリリース

1. 「Version Bump」ワークフローを使用：
   - リポジトリの「Actions」タブに移動
   - 「Version Bump」ワークフローを選択
   - 「Run workflow」をクリック
   - テスト用に「dry run」モードを有効にすることも可能

2. ワークフローが自動的に：
   - `gradle/libs.versions.toml`内の`versionCode`をインクリメント
   - フィーチャーブランチを作成し、`:rocket: bump versionCode`でコミット
   - バージョンバンプのためのmainブランチへのPull Requestを作成

3. バージョンバンプPRをレビューしてマージ：
   - バージョン変更のために生成されたPull Requestをレビュー
   - PRを承認してmainブランチにマージ

4. 自動ドラフトリリース公開：
   - バージョンバンプPRがマージされると、「Publish Draft Release」ワークフローが自動的にトリガー
   - バージョン変更を検出し、最新のドラフトリリースを見つける
   - PreRelease（「beta」を含む）かReleaseかを決定
   - ドラフトリリースを適切に公開

**注意**: プロセスはより良い制御のために2段階に分割されています：
1. **Version Bump**: 手動でレビューとマージが必要なバージョン変更のPRを作成
2. **Release Publishing**: バージョン変更がmainにマージされたときに自動的にドラフトリリースを公開

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