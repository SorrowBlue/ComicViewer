---
trigger: always_on
globs: ["**/*"]
---

# Git Commit Rules (コミットメッセージ規約)

**Activation:** This rule is **ALWAYS ON** for all git-related operations.

## Scope

- **対象**: staged 変更（`git diff --cached`）のみ。unstaged・未追跡ファイルは無視。
- **適用範囲**: Git サイドバーの Generate ボタン、`/commit` ワークフロー、その他すべての git commit 関連操作。
- staged 変更がない場合、コミットメッセージの生成を行わず、ステージングを促すメッセージのみ返すこと。

## Format: Conventional Commits

すべてのコミットメッセージは **Conventional Commits** 形式の1行目（件名のみ）を厳守する。本文（Body）やフッター（Footer）などの複数行は出力しないこと。

```
<type>[optional scope]: <description>
```

### Type（常に英語）

`feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`, `build`, `ci`, `revert`

### Description（件名）

- 50文字以内
- 末尾にピリオド（`.`）を付けない
- 言語は常に**英語**とする。

## Guardrails（禁止事項）

- ❌ 絵文字の使用
- ❌ スラング・口語表現
- ❌ 曖昧な表現: `update`, `fix`, `change`, `modify`, `更新`, `修正`, `変更`, `対応`, `wip`
- ❌ メタ解説・導入文・締めの言葉（コミットメッセージ本文のみを出力）

## Principles（原則）

- **1コミット1変更**: 論理的に独立した変更は分割する。複数の type にまたがる場合、分割を提案すること。
- **破壊的変更**: `!` を type の後に付与する（例: `feat!:`, `fix!:`）。footer に `BREAKING CHANGE:` も記述可。
- **出力形式**: コミットメッセージ本文のみを直接出力する。思考プロセスや解説は一切含めない。
