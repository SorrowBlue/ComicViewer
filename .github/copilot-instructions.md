# GitHub Copilot Instructions for ComicViewer

このファイルは、GitHub Copilotがこのリポジトリで作業する際に自動的に参照するプロジェクト固有の指示です。

## 重要: まず AGENTS.md を確認してください

**すべてのタスクを開始する前に、必ず [AGENTS.md](/AGENTS.md) を確認してください。**

AGENTS.mdには以下の重要な情報が含まれています：

- ✅ プロジェクト概要と技術スタック
- ✅ 環境構築手順
- ✅ ビルドおよびテストコマンド（実行時間の目安付き）
- ✅ コードスタイルガイドライン
- ✅ テスト手順
- ✅ セキュリティに関する考慮事項
- ✅ Pull Requestポリシー
- ✅ アーキテクチャとモジュール構成
- ✅ リリースプロセス
- ✅ トラブルシューティング

## 基本的なワークフロー

1. **AGENTS.mdを読む** - タスクを開始する前に必ず確認
2. **コードスタイルに従う** - AGENTS.mdのコードスタイルガイドラインセクションを参照
3. **適切なテストを実行** - AGENTS.mdのビルドおよびテストコマンドセクションを参照
4. **品質チェックを実行** - detekt、lint、テストを実行
5. **PRポリシーに従う** - AGENTS.mdのPull Requestポリシーセクションを参照

## クイックリファレンス

### コードスタイル
- トレーリングカンマを必ず使用
- Material3コンポーネントを優先
- パッケージは `com.sorrowblue.comicviewer.*` プレフィックスを使用
- 詳細: [AGENTS.md - コードスタイルガイドライン](/AGENTS.md#コードスタイルガイドライン)

### ビルドコマンド
```bash
# Detekt（静的コード解析）
./gradlew detektAndroidAll detektDesktopAll detektIosAll

# Android Lint
./gradlew app:android:lintDebug

# テスト実行
./gradlew allTests
```

詳細: [AGENTS.md - ビルドおよびテストコマンド](/AGENTS.md#ビルドおよびテストコマンド)

### アーキテクチャ
- **feature**: UI機能モジュール
- **domain**: ビジネスロジック層
- **data**: データアクセス層
- **framework**: 共有フレームワークコンポーネント

詳細: [AGENTS.md - アーキテクチャ](/AGENTS.md#アーキテクチャ)

## その他のドキュメント

- [copilot-workspace-custom-instructions.md](/copilot-workspace-custom-instructions.md) - GitHub Copilot Workspace用の詳細な指示
- [README.md](/README.md) - プロジェクト概要とモジュール依存関係図
- [docs/release-automation.md](/docs/release-automation.md) - リリースプロセス詳細

---

**注意**: このファイルは、GitHub Copilotがプロジェクト固有の指示を取得するための標準的な場所です。このファイルを変更する際は、AGENTS.mdの内容と一貫性を保ってください。
