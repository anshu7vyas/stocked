# Stocked! — Agent Working Notes

App: grocery expiry tracker, being migrated 2018 Java → 2026 stack as a documented
"modernize legacy Android with AI" project that ends in a Play Store release and a
Medium blog series.

## Resume protocol (read this first)

1. **State of truth:** `MIGRATION_LOG.md` (repo root) — per-session log: what's done,
   metrics, pain points, surfaced bugs. Update it at the end of every work session.
2. **Plan:** `~/.claude/plans/look-at-the-codebase-recursive-castle.md` — approved
   phased plan (7 phases). Memory dir also has decision summaries.
3. **Check git log** on branch `stocked-revamp` — conventional commits tell the story.
4. Current position: see "Phase status" below; keep it updated.

## Phase status (update when a phase completes)

- [x] Phase 0 — baseline, metrics harness, keystore post-mortem (old keystore LOST →
      new Play listing; new upload keystore in `keystore/` git-ignored, **user must back up**)
- [x] Phase 1 — Gradle 9.5.1 / AGP 9.2.1 / Kotlin DSL / version catalog / AndroidX /
      package rename to `io.github.anshu7vyas.stocked` / minSdk 26, targetSdk 36
- [x] Phase 2 — 100% Kotlin, Room v1 (`stocked.db`, epochDay dates), Hilt, ViewModels +
      StateFlow, WorkManager expiry worker, unit tests (Robolectric DAO + Turbine VM)
- [x] Phase 3 — Compose like-for-like (BOM 2026.05.01, M3, single MainActivity, SplashScreen API,
      TabRow+HorizontalPager, LazyColumn) + Navigation 3 (1.1.2, explicit entryDecorators incl.
      ViewModelStore — NOT default!), all XML/fragments/adapters deleted, 19 tests
- [ ] Phase 4 — Stitch redesign via Playwright MCP → Material 3 Expressive + edge-to-edge + adaptive
- [ ] Phase 5 — In-app AI: Gemini Nano suggestions w/ lookup fallback + AppFunctions (skill: appfunctions)
- [ ] Phase 6 — R8, fastlane rewrite, Play internal → production rollout

## Branch & PR model (user-directed: incremental review)

- `stocked-revamp` = integration branch where work happens.
- Each phase gets a snapshot branch + **stacked PR**: `phase-1-resurrection` (PR #11, base master),
  `phase-2-kotlin-architecture` (PR #12, base phase-1). Continue the pattern:
  after a phase's commits land on stocked-revamp, `git branch phase-N-<name> <sha>`,
  push, `gh pr create --base phase-(N-1)-... --head phase-N-...`.
- GitHub Actions enabled but **0 runs ever fired** as of 2026-06-10 — user should check
  the repo's Actions tab (may need first-run approval/billing).

## Working conventions (user-directed)

- **Per-phase gate:** adversarial review with `voltagent-lang:kotlin-specialist` +
  `voltagent-qa-sec:architect-reviewer` agents → verify findings before fixing (they
  have been wrong; verify against emulator baseline) → `/simplify` (Kotlin phases) →
  `/smart-commit` (conventional commits) → update `MIGRATION_LOG.md`.
- **Verification:** `./gradlew testDebugUnitTest`, then emulator (Pixel_9_Pro, API 36):
  `android run --apks app/build/outputs/apk/debug/app-debug.apk --device emulator-5554`.
  Drive UI via `android layout` (JSON tree with element centers) + `adb input tap` —
  do NOT guess coordinates from screenshots. Baseline gallery: `docs/baseline/`.
- **Behavior preservation:** legacy semantics are load-bearing — expiry date itself =
  "1 day left" (expires day AFTER); notification set = items expiring today/tomorrow;
  date picker min = tomorrow. Tests in `ProductTest` encode this; don't "fix" them.
- **Blog voice:** previous posts at `Documents/previous_blog_posts/` (untracked —
  do not commit). Blog drafts → `docs/blog/`. Series: 3 parts (P1=Ph0-1, P2=Ph2-3, P3=Ph4-6).
- Legacy build needs JDK 8: `JAVA_HOME=~/.sdkman/candidates/java/8.0.492-zulu` (only for history checkouts).
- Playwright MCP installed (plugin) — for driving stitch.withgoogle.com in Phase 4
  (needs the user's Google-logged-in profile; coordinate with user when starting).

## Architecture (current)

Single module, package-by-feature under `io.github.anshu7vyas.stocked`:
`data/` (Product entity w/ nullable expiryEpochDay, ProductDao Flow queries,
StockedDatabase v1, ProductRepository) · `di/DataModule` · `work/ExpiryCheckWorker`
(daily, Hilt) · `ui/{home,shopping,timeline,additem}` (ViewModel + StateFlow each;
XML+ListView views until Phase 3) · `StockedApp` (Hilt, channel, WorkManager schedule).
Shopping-list items are Products with `onShoppingList=true` and null expiry.
