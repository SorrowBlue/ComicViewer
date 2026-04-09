# GitHub Copilot Instructions for ComicViewer

このファイルは、Copilot向けの**全体方針（What）**を定義します。
実行手順・コマンド・運用フローなどの**具体的なやり方（How）**は、[AGENTS.md](/AGENTS.md) を参照してください。

## 役割分担（What / How）

- `.github/copilot-instructions.md`: 方針、規約、制約（What）
- `AGENTS.md`: コマンド、テスト手順、PR運用、境界条件（How）
- 同じ内容を両方に重複記載しない
- 複数ファイルはマージして読まれる前提で、矛盾する指示を作らない

## プロジェクト概要（Project Overview）

- ComicViewerは、Android/iOS/JVMを対象としたマルチプラットフォームのコミックビューアである
- 主目的は、複数ストレージと複数形式のコミック閲覧体験を提供すること
- 提案・実装時は、保守性と拡張性を優先する

## テックスタック（Tech Stack）

- Kotlin Multiplatform + Jetpack Compose を前提に実装する
- UIはMaterial3を優先する
- データ永続化・画像・DIなどの採用技術は `AGENTS.md` とVersion Catalogに従う

## コーディングガイドライン（Coding Guidelines）

- Kotlin style guide に従い、トレーリングカンマを必ず使用する
- 公開APIにはKDocを付与する
- セキュリティ要件（入力検証、シークレット非コミット、null安全）を満たす

## プロジェクト構造（Project Structure）

- モジュール構成（feature/domain/data/framework）と依存方向を守る
- 変更は対象モジュールの責務内で完結させ、層の越境依存を避ける
- 詳細なモジュール説明は `README.md` と `AGENTS.md` を参照する

## リソース（Resources）

- 実行手順・コマンド・検証フロー: [AGENTS.md](/AGENTS.md)
- プロジェクト概要と依存関係図: [README.md](/README.md)
- リリース詳細: [docs/release-automation.md](/docs/release-automation.md)
- 依存関係バージョン管理: `gradle/libs.versions.toml`

## 作業開始時の最短フロー

1. [AGENTS.md](/AGENTS.md) を確認する
2. 変更対象モジュールの規約とテスト方針を確認する
3. 変更後に `AGENTS.md` 記載のコマンドで検証する

## 参照ドキュメント

- [AGENTS.md](/AGENTS.md) - 実行手順・コマンド・運用ルール（How）
- [README.md](/README.md) - プロジェクト概要とモジュール依存関係図
- [docs/release-automation.md](/docs/release-automation.md) - リリースプロセス詳細

---

このファイルを更新する際は、`AGENTS.md` との役割分担を崩さないこと。
