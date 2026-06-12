# Blog Series Plan — "Resurrecting a 2018 Android App with AI"

Working spine for the Medium series. Source of truth for facts/metrics: `MIGRATION_LOG.md`
(repo root). This file owns structure, narrative, and asset mapping.

**Status (2026-06-11):** 4-part structure agreed with the user. Open questions (user to
decide): publishing cadence (trail the work vs. hold for Phase 6), how candid the
keystore-loss story gets, audience altitude (Android-dev-deep throughout vs. accessible
Parts 1/3), final titles.

## Voice (from Documents/previous_blog_posts — read both before drafting)

- Title pattern: practical promise + bold one-line subtitle
  (e.g. *"in, out, reified: A Practical Guide to Kotlin Generics" — "Building a simple
  event bus to understand variance, once and for all"*).
- Mental model **before** code; short bold-led sections; em-dashes; inline doc links.
- First person, curious, zero hype ("These questions led me to dig deeper…").
- Honest about what didn't work — pain points are content, not embarrassment.
- Code blocks minimal and runnable; end with a link to the full source (here: the repo + PRs).

## Depth discipline (user requirement: no nitty-gritty that only helps code-readers)

The unit of content is the **decision, not the change**. Each part = 5–7 numbered
decisions with their *why* — never a chronological tour of the diff.

Every technical detail must pass at least one of:
1. **Transfer test** — the reader can act on it in *their* codebase (e.g. the WorkManager
   initializer trap bites anyone with Hilt workers).
2. **Narrative test** — the story breaks without it (e.g. the keystore loss).
Plus: 3. **Link test** — if a PR/commit link serves the curious reader equally well,
link it instead of prose. Two fails → it becomes a link.

Per section: mental model → one ≤15-line code block *only when the code is the
argument* → PR/commit link as the door to full depth. The repo is the appendix.

**Enforcement:** before the user sees a draft, run an editorial review pass with exactly
this rubric; cut or link-ify anything that fails.

## Chain mechanics (every post links previous + next)

- Identical nav block at **top and bottom** of every post:
  `◀ Part N−1: <title> · Part N+1: <title> ▶`
- Parts 2–4 open with a 2–3 line "Previously in this series" recap.
- Part 1 carries the full series TOC.
- Drafts use placeholder links; publish in order and back-fill each "next" link as the
  following part goes live (Medium posts are editable after publishing).

## Series shape: 4 parts

### Part 1 — "The 8-Year-Old App: Resurrecting a 2018 Android Build in 2026" (Phases 0–1)

Thesis: the scariest part of modernizing isn't the code — it's the toolchain and identity.

1. Hook: `Could not determine java version from '24.0.2'` — Gradle 4.6 can't even parse a modern JDK string. (Asset: CI bootstrap PR #13's deliberately red check.)
2. The patient on the table: 1,870 lines of 2018 Java, minSdk 16, raw SQLite on the main thread, `versionCode 1` — never updated. Baseline gallery (`docs/baseline/`).
3. Cryogenics, not death: with JDK 8 (sdkman) the 2018 build runs in 28 s; jcenter still serves read-only in 2026.
4. **The keystore lesson** (emotional core): release keystore lost → the Play identity dies with the laptop. Why Play App Signing exists. New listing as `io.github.anshu7vyas.stocked`. (Candor level: user to confirm.)
5. The resurrection: Gradle 9.5.1 / AGP 9.2.1 / Kotlin DSL / version catalog / AndroidX — by direct AI rewrite, no Upgrade Assistant, no Jetifier (12+6 deterministic mappings).
6. Bugs the migration *surfaced* (not caused): Application class never registered for 8 years; notifications silently dead since Android 8; lenient-date-parse time bomb — **plus the Parcel write/read mismatch that shipped in 2018 and was finally caught by a bot reviewing the resurrection PR** (#11).
7. AI-reviewing-AI: the gate caught the API 26–32 notification kill; then the human-verified emulator baseline caught the *reviewer's* wrong fix. Layered verification beats any single reviewer.
8. Gradle-wrapper-jar checksum gotcha (update the jar, not just the URL).
9. Metrics close: Phase 0+1 ≈ 35 min wall-clock. PR #11.

### Part 2 — "1,870 Lines of Java → 0: Kotlin, Room, and Compose Without Losing the Plot" (Phases 2–3)

Thesis: convert architecture before UI; preserve behavior on purpose; let the layers pay each other off.

1. Order-of-operations mental model: build system → data/architecture → UI. Why converting UI twice is the classic mistake.
2. Room with epochDay: how one column type kills an entire bug class (cross-year lexicographic sort, unpadded-date parsing). The DAO test that proves the old bug.
3. Behavior preservation as discipline: "expiry date = 1 day left" is load-bearing; tests encode quirks so refactors can't silently "fix" them.
4. The WorkManager trap (Part 2's centerpiece): the startup initializer ignores `Configuration.Provider`; every @AssistedInject worker FAILS silently forever. Reviewer proved it by disassembling bytecode; fixed with manifest `tools:node="remove"`; verified live (`Worker result SUCCESS` + real notification).
5. The viewModelScope+finish() data-loss race → @ApplicationScope writes (this pays off twice later).
6. Compose like-for-like: deleting 11 layouts/4 activities/3 fragments/4 adapters; ViewModels reused *unchanged* — the architecture-first payoff visible in one diff.
7. Nav3 gotcha: `NavDisplay` doesn't install the ViewModelStore decorator by default — "entry-scoped" VMs were silently Activity-scoped. And the beautiful interaction: entry-scoped Add VMs die on back-pop, writes survive only because of #5. *The gates compound.*
8. Metrics: net −255 lines while gaining Room+Hilt+VMs+worker+tests; 19 tests. PRs #12, #14.

### Part 3 — "My Designer Is a Browser Tab: An AI Redesign, Reviewed by AI" (Phase 4 + review/merge)

Thesis: design decisions need a "why" trail just like code — and AI reviewers need adversarial verification just like AI authors.

1. Stitch via Playwright MCP: the agent drives the design tool; total human input = one 2FA tap. Steering AI scope creep away (barcode scanner) mid-conversation. (Assets: `Documents/Redesign_with_stitch.mov` screen recording, Stitch PNGs vs implemented screenshots side-by-side.)
2. `DESIGN.md` as the contract: M3 tokens → Compose theme 1:1; Material You hijacking the brand green on first run (and why brand-first was the right default).
3. UX archaeology: replacing 2018's invisible long-press with kebabs + a bottom-sheet add flow; "data-honest" design cuts (no month stats without a status-date column — the schema tells you what UX you've earned).
4. The save-path consolidation: review gate deleted a whole ViewModel. Altitude reviews ARE simplification.
5. The review story: 13 bot comments across the stack, verify-before-fix discipline — 9 fixed, 4 declined with rationale. The bot found a real 2018 bug AND was confidently wrong twice (phantom compilation issue; unfounded R8 claim). Refutations on the record.
6. The checkbox case study: bot wants M3 `Checkbox` (breaks the design), status quo `RadioButton` (wrong TalkBack semantics) — the right answer is *neither*: `IconToggleButton` with circle icons. Design fidelity AND accessibility.
7. Stacked-PR mechanics sidebar: fixes on lower branches create modify/delete conflicts upward; ripple-merge before merging bottom-up; merge commits (not squash) to keep the stack's shared history. Progression gallery as proof of behavior preservation (`docs/progression/`).
8. PR #15 + the merged stack.

### Part 4 — "Gemini in the Pantry: On-Device AI and a Play Store Release" (Phases 5–6, TO COME)

Thesis: progressive enhancement is the design pattern for AI features — and shipping is a feature.

1. SuggestionService architecture: Gemini Nano preferred, ~100-item lookup table as the always-works fallback. The emulator can't run Nano — the fallback architecture IS the story.
2. Wiring suggestions into the add sheet: debounced, apply-chip UX; the 30-item quality corpus.
3. AppFunctions: exposing the pantry to system agents (`addPantryItem`, `queryExpiringItems`).
4. R8, fastlane rewrite, the new-listing release flow (incl. the Pages-URL-doesn't-redirect gotcha for the privacy policy).
5. Final metrics rollup: phases 0–4 ≈ 4¼ h wall-clock vs ~8-day human estimate — and the honest breakdown of what the human actually did (decisions, 2FA, review).

## Asset inventory

| Asset | Where | Use |
|---|---|---|
| Baseline "before" gallery (9 shots) | `docs/baseline/` | Part 1 §2 |
| Per-phase progression gallery (same Milk item) | `docs/progression/` | Parts 2–3 |
| Final app screenshots (4 screens, seeded data) | `docs/screenshots/` | Parts 3–4, README |
| User screenshots of the live sessions | `Documents/Screenshot 2026-06-10 at 8.59.43 PM.png`, `…12.03.29 AM.png`, `…12.30.53 AM.png`, `…12.50.58 AM.png` (untracked) | Workflow shots, any part |
| Stitch screen recording (64 MB) | `Documents/Redesign_with_stitch.mov` (untracked) | Part 3 §1 — trim to GIF/clips |
| Stitch design package (4 PNGs + HTML + DESIGN.md) | `docs/design/stitch-export/` | Part 3 §1–2 |
| Implemented redesign screenshot | `docs/design/implemented_pantry.png` | Part 3 side-by-side |
| Red CI on 2018 master | PR #13 checks | Part 1 hook |
| Per-phase PRs (review trail) | #11, #12, #14, #15 (+ #16 docs) | All parts, "full source" links |
| All metrics, pain points, gate findings | `MIGRATION_LOG.md` | Everywhere |

## Drafting protocol

Draft in `docs/blog/part-N-draft.md`. Per part: skim the two voice samples first, pull facts
ONLY from `MIGRATION_LOG.md`/PRs (no invented numbers), keep code blocks ≤15 lines, end with
repo + PR links, include the chain nav block top + bottom. Then run the **depth-discipline
editorial pass** (rubric above) before showing the user. Parts 1–3 are draftable now
(their phases are frozen and merged); Part 4 waits for Phases 5–6.
