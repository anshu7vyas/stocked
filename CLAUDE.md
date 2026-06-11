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
- [x] Phase 4 — Stitch redesign shipped: bottom nav + badge, pantry dashboard (urgent carousel,
      expiry buckets), add bottom sheet (quick expiry chips), shopping move-to-pantry flow,
      timeline stats; design assets + DESIGN.md tokens in docs/design/. Deferred: adaptive
      (tablet rail), dynamic-color setting, month grouping (needs status-date column)
- [ ] Phase 5 — In-app AI: Gemini Nano suggestions w/ lookup fallback + AppFunctions (skill: appfunctions)
- [ ] Phase 6 — R8, fastlane rewrite, Play internal → production rollout

## Next session: Phase 5 pickup (concrete steps)

1. `git checkout stocked-revamp && git pull` — PRs #11→#15 are **all merged** (2026-06-11,
   review comments addressed; see MIGRATION_LOG Session 3); master == phase-4 redesign,
   stocked-revamp = master + docs. Boot emulator: `android emulator start Pixel_9_Pro`.
2. Build `ai/` package: `SuggestionService` interface (suggest(name) → category + shelf-life days),
   `LocalLookupSuggestionService` (~100-item bundled table — ALWAYS works) +
   `GeminiNanoSuggestionService` (ML Kit GenAI / AICore, runtime capability check; expect
   UNAVAILABLE on emulator — the fallback architecture is the story). Hilt-bind with Nano
   preferred, lookup fallback.
3. Wire into `AddItemSheet`: debounced suggestion row under the name field
   ("Looks like 🥛 Dairy — usually keeps ~7 days" + Apply chip) — the slot exists in the
   Stitch design (`docs/design/stitch-export/.../add_item_modal/`). Suggestion fills
   category + expiry quick-pick. Tests: fake service; 30-item quality corpus → MIGRATION_LOG.
4. Run the **appfunctions skill** → expose `addPantryItem`, `addToShoppingList`,
   `queryExpiringItems` from ProductRepository; verify on API 36 emulator.
5. Gate as usual (kotlin-specialist + architect agents → verify → /simplify decision →
   smart-commit) → branch `phase-5-ai` → PR base `master` (stack is merged) → update
   MIGRATION_LOG + this checklist.
6. Phase 6 checklist additions discovered along the way: R8 keep rules for @Serializable
   Nav3 routes; bundled font fallback (offline-first launch); dynamic-color user setting.

**Still owed by user (blocks Phase 6 release only):** Play Console new-app entry for
`io.github.anshu7vyas.stocked`, service-account JSON for fastlane, keystore backup
(`keystore/` — git-ignored, NOT backed up anywhere yet!).

## Blog

Structure + asset map: `docs/blog/BLOG_PLAN.md`. Voice samples: `Documents/previous_blog_posts/`
(untracked). User assets (untracked, do not commit): `Documents/Redesign_with_stitch.mov`
+ four workflow screenshots in `Documents/`. Part 1 is draft-ready now.

## Branch & PR model (user-directed: incremental review)

- `stocked-revamp` = integration branch where work happens.
- Phases 1–4: snapshot branches + stacked PRs #11→#12→#14→#15 — **all merged to master
  2026-06-11** (bottom-up, merge commits, manual `gh pr edit --base master` retarget per merge;
  snapshot branches kept for the blog). Review-comment triage in MIGRATION_LOG Session 3.
- Phase 5+: branch `phase-N-<name>` off stocked-revamp, PR base `master` (stack is gone).
- Stacked-PR lesson: fix commits on a lower branch touch files upper branches delete —
  ripple-merge upward resolving modify/delete to the deletion, BEFORE merging bottom-up.
- GitHub Actions ("Android CI") fires on pushes and is green as of 2026-06-11.
- Per-phase progression screenshots: `docs/progression/` (same Milk item across phases).

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
