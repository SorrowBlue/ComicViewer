---
description: コミットメッセージ生成（英語）
---

# Commit Message Generator (English)

**Trigger:** `/commit`

**Description:**
Generate a clean Conventional Commits message from staged changes **in English**.

> **Note:** This workflow reads git state but does NOT modify any files.
> All commit rules are defined in `.agent/rules/git-commit-rules.md`.

---

## 1. Analyze Staged Changes
- **Goal:** Understand what has changed.
- **Action:**
  - Run `git diff --cached` to inspect staged changes.
  - **IF no staged changes exist:** Reply with "No staged changes detected. Please stage files first." and STOP.
  - Identify the logical units of change (files modified, additions, deletions).

## 2. Classify Change Type
- **Goal:** Determine the appropriate Conventional Commits type.
- **Action:**
  - Select the most accurate type: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `chore`, `build`, `ci`, `revert`.
  - **IF changes span multiple types:** Propose splitting into separate commits before generating.

## 3. Generate Commit Message
- **Goal:** Produce a ready-to-use commit message.
- **Action:**
  - Follow all rules in `.agent/rules/git-commit-rules.md`.
  - **Language:** English.
  - **Description:** Imperative mood, present tense, capitalize first letter, max 50 chars, no trailing period.
  - **Format:** Output ONLY a single line (the subject line). Do NOT generate a body or footer.

---

**Output Rules:**
- Output ONLY the commit message text (ready to copy).
- NO meta-commentary, NO introductions, NO explanations, NO thought process.
- Example:
  ```
  feat(auth): add OAuth2 login endpoint
  ```
