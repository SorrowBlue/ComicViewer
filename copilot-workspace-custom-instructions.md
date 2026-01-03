# Copilot Workspace Custom Instructions

このファイルはGitHub Copilotに、ComicViewerリポジトリ内でのコーディングや提案時に従ってほしいルール・前提・ガイドラインを伝えるためのものです。

> **📘 重要**: より詳細で包括的な開発ガイドラインについては、[AGENTS.md](AGENTS.md)を参照してください。AGENTS.mdには、環境構築、ビルドコマンド、テスト手順、トラブルシューティングなどの詳細情報が含まれています。

---

## 1. プロジェクト概要

ComicViewerは、Android、iOS、Desktopをサポートするマルチプラットフォームコミックビューアアプリです。

### 技術スタック
- **Kotlin Multiplatform**: メインプログラミング言語
- **Jetpack Compose**: UI フレームワーク（Android、Desktop、iOS）
- **モジュラーアーキテクチャ**: feature/domain/data レイヤー構成
- **Metro**: 依存性注入フレームワーク
- **Room**: データベース（Android）

## 2. コーディング規約・スタイル

### 基本ルール
- Android の [Kotlin style guide](https://developer.android.com/kotlin/style-guide) に従ってください
- **トレーリングカンマ**を必ず使用してください（配列、関数パラメータなど）
- 関数名はキャメルケースで記述してください
- クラス名はパスカルケースで記述してください

### Compose固有のルール
- `@Composable` 関数は名前付きパラメータを使用してください
- `@Composable` 関数では、デフォルト引数と共にトレーリングラムダを活用してください
- Material3 コンポーネントを優先使用してください

### コメント・ドキュメント
- 公開関数にはKDocコメントを追加してください
- 複雑なロジックには日本語でのコメントを付けてください
- TODOコメントには担当者を明記してください

### ファイル構成
- パッケージ構造は `com.sorrowblue.comicviewer.*` を維持してください
- ファイル末尾に改行を入れてください
- imports は `*` と `^` の順序で整理してください（detekt設定準拠）

## 3. アーキテクチャとパターン

### モジュール構成
- **feature**: UI機能モジュール（画面ごと）
- **domain**: ビジネスロジック層（usecase、model）
- **data**: データアクセス層（repository、API、DB）
- **framework**: 共通フレームワーク（UI、designsystem）

### 依存関係のルール
- 上位レイヤーは下位レイヤーに依存可能
- 同レベルレイヤー間での依存は最小限に
- feature モジュール間の直接依存は避けてください

### Navigation
- Compose Navigation を使用してください
- 画面遷移は Destinations ライブラリーを活用してください

## 4. 品質・セキュリティ

### 必須チェック項目
- 入力値のバリデーションを必ず実装してください
- null安全性を考慮したコードを書いてください
- 外部ライブラリは最新の安定版を使用してください
- API通信はエラーハンドリングを含めてください

### テスト
- 新機能にはユニットテストを追加してください
- UI テストが必要な場合は Compose テストフレームワークを使用してください
- テストファイルは対応するソースファイルと同じパッケージ構造にしてください

## 5. プラットフォーム固有の考慮事項

### Android
- Lifecycle を考慮したコード実装をしてください
- Material3 Adaptive コンポーネントを活用してください
- Android リソースは適切なqualifierを使用してください

### iOS
- Platform-specific実装は expect/actual パターンを使用してください
- iOS Human Interface Guidelines を考慮してください

### Desktop
- デスクトップ特有のUI パターン（メニューバー、キーボードショートカット）を考慮してください
- ウィンドウ管理機能を適切に実装してください

## 6. ローカライゼーション

- 文字列リソースは多言語対応を前提として実装してください
- 日本語をデフォルト言語として、英語も対応してください
- string resources は `Res.string.*` パターンを使用してください

## 7. パフォーマンス

- Compose の再コンポーズを最小限に抑えるコードを書いてください
- 大量データの処理には LazyColumn/LazyRow を使用してください
- 画像読み込みには Coil ライブラリーを使用してください
- メモリリークを避けるため、適切なライフサイクル管理をしてください

## 8. Git・CI/CD

### コミットメッセージ
- 英語で記述してください
- Conventional Commits 形式を推奨します
- タイプ: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

### ブランチ命名規則
- 命名規則: `[type]/[issue-number]-[Issueタイトルを簡略化した内容]`
- typeは以下の種別で運用してください:
  - `feature/`: 「新しい機能の提案や実装」[Proposals and implementation of new features]
  - `enhancement/`: 「既存機能の改善」[Improvements to existing functions]
  - `refactor/`: 「コードの内部的な改善（機能変更なし）」[Internal improvements to the code (no feature changes)]
  - `fix/`: 「バグ、予期しない動作」[Bugs, unexpected behavior]
  - `doc/`: 「ドキュメントの作成、修正、追加」[Create, edit, add documents]
  - `dependencies/`: 「依存関係更新」[Dependency Updates]
  - `chore/`: 「ビルド、CI/CD、依存関係更新など」[Build, CI/CD, dependency updates, etc.]

### ラベル管理
- Issueにつけるラベルは `.github/labels.yml` に定義されているものから適切なものを選択してください
- PRのラベルはReleaseDrafterによって自動的に付与されるため、Copilotは手動でつける必要はありません

### Issue・PR言語ガイドライン
- **Issueタイトル**: 英語で記述してください
- **Issue説明・コメント**: 日本語で記述してください
- **PRタイトル**: 英語で記述してください
- **PR説明・コメント**: 日本語で記述してください
- **会話・コミュニケーション**: 日本語で行ってください

### 品質チェック
- コミット前に detekt による静的解析を実行してください
- Android Lint チェックも必須です
- プルリクエスト作成時に自動テストが実行されます

## 9. 参考資料

- [Android Developers](https://developer.android.com/) - Android開発の公式ガイドライン
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Compose開発ガイド
- [Kotlin Multiplatform](https://kotlinlang.org/lp/multiplatform/) - KMP公式ドキュメント
- [Material Design 3](https://m3.material.io/) - Material3デザインシステム
- プロジェクト固有の詳細は `docs/` フォルダーを参照してください

---

これらのガイドラインに従って、一貫性のあるコードベースの維持にご協力ください。
