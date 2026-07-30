---
trigger: always_on
---

# Language Strategies (言語戦略)

- **内部推論 (Internal Reasoning):** 精度を保つため英語が許可/推奨されます。ただし、ツール呼び出し時のパラメータ（上記 `TaskName` 等）は、出力直前に必ず日本語へ翻訳してください。
- **コード (Code):** 標準的な英語を使用してください（コード、変数名）。
- **コミットメッセージ:** [.agent/rules/git-commit-rules.md](file:///.agent/rules/git-commit-rules.md) のルールに従ってください。
- **ユーザー向け出力 (User-Facing):** **日本語** でなければなりません。
  - **チャット (Chat):** 常に日本語を使用してください。
  - **成果物 (Artifacts):** `task.md`, `implementation_plan.md`, `agents_adjustment_proposal.md`, `walkthrough.md` などのファイル内容は必ず **日本語** で記述してください。
  - **タスクメタデータ (Task Metadata):** 
    - `TaskName`: **MUST** be written in **Japanese** (日本語). English is STRICTLY PROHIBITED.
      - GOOD: "ログイン機能の実装"
      - BAD: "Implement Login Feature"
    - `TaskSummary`: **MUST** be written in **Japanese** (日本語).
    - `TaskStatus`: Can be in English or Japanese.