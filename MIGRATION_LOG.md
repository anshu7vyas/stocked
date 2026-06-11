# Stocked! Modernization Log

> Raw data for the blog series: every work session, what AI did vs. what the human did,
> metrics, and pain points. Times are wall-clock, honest, unpolished.

## Baseline (the "before" picture)

| Metric | Value | Source |
|---|---|---|
| App | Stocked! — grocery expiry tracker, live on Play since 2018, `versionCode 1` (never updated) | Play listing / `app/build.gradle` |
| Language | 100% Java — **1,870 LOC** (18 files) | `wc -l` over `app/src/main/java` |
| Resource XML | 603 LOC (11 layouts + values) | `wc -l` over `app/src/main/res` |
| Build system | Gradle 4.6 · AGP 3.2.1 · jcenter | `gradle-wrapper.properties`, `build.gradle` |
| Dependencies | Support Library 28 (pre-AndroidX), JodaTime 2.9 | `app/build.gradle` |
| SDKs | minSdk 16 · target/compileSdk 28 | `app/build.gradle` |
| Architecture | None — Fragments call SQLite directly on the main thread | code read |
| Data | Raw SQLiteOpenHelper, `Fooded.db` v5, dates as `MM/dd/yyyy` TEXT (lexicographic sort bug), `onUpgrade` = `DROP TABLE` | `Model/ProductTable.java:58` |
| Tests | 1 defunct file (`ApplicationTestCase`), 0 real tests | `app/src/androidTest` |
| APK size (debug) | **2.9 MB** (2,961,803 bytes), build time 28 s cold | legacy `assembleDebug` |
| Host toolchain (2026) | JDK 24 (Corretto), macOS | `java -version` |

## Sessions

### Session 1 — 2026-06-10 (Phase 0: baseline & safety net)

- **Start:** 20:52 PDT
- **Plan:** explored codebase, made phased plan (7 phases → 3 blog parts), key decisions:
  rename to `io.github.anshu7vyas.stocked` (old keystore likely lost → new Play listing),
  minSdk 26, target/compileSdk 36, Room fresh v1 schema with epochDay dates, single module.
- **AI/human split:** Claude Code (Fable 5) explored, planned, executes; human decided
  strategy, package rename, AI scope, review process (adversarial agent reviews + /simplify + /smart-commit).
- **Pain points:** _(running list)_
  - Gradle 4.6 dies on JDK 24 *and* JDK 17 with `Could not determine java version` — it can't even parse the version string of any modern JDK.
  - `brew install --cask corretto@8` needs sudo (pkg installer) → blocked in non-interactive shell; sdkman saved the day (`sdk install java 8.0.492-zulu`, user-dir, no sudo).
  - `local.properties` was missing (gitignored, machine-specific) — `SDK location not found`.
  - **Plot twist:** with JDK 8 + sdk.dir set, the 2018 build *succeeds in 28 s*. AGP 3.2 auto-downloaded platform-28/build-tools 28.0.3. jcenter.bintray.com still serves read-only in 2026. The legacy toolchain isn't dead — it's cryogenically frozen; you just need a 2018 JDK to thaw it.

#### Phase 0 checklist
- [x] MIGRATION_LOG.md created, clock started
- [x] Legacy build attempt documented — fails on JDK 17/24, builds in 28 s on JDK 8 (2.9 MB debug APK)
- [x] Baseline screenshots (`docs/baseline/` — 9 captures: home empty/with item, add-item + date picker, long-press action dialog, shopping empty/with item, timeline w/ STOCKED badge). Driven via `android` CLI + adb on Pixel 9 Pro emulator (API 36). Legacy 2018 APK runs fine on Android 16. Note: item actions are **long-press only** — zero affordance, classic 2018 UX; redesign fixes this.
- [x] Keystore post-mortem: searched `~/code`, `~/Documents`, `~/Desktop`, `~/Downloads`, `~/.android` — **old release keystore not on this machine**. Only the AOSP debug keystore + unrelated work keystores. Conclusion: new Play listing under `io.github.anshu7vyas.stocked` confirmed. Lesson for the blog: 2018-era apps predate default Play App Signing enrollment — one lost laptop = one dead app identity.
- [x] New upload keystore generated: `keystore/stocked-upload.jks` (RSA 4096, 30-yr validity, alias `stocked-upload`), creds in `keystore/keystore.properties` — both git-ignored. **TODO(user): back up keystore + properties to a password manager; enroll new Play listing in Play App Signing at creation.**
- [ ] Playwright MCP → Stitch smoke test (deferred to Phase 4 prep)
- [ ] Play Console: create new app entry + service-account JSON (user)

### Session 1 (cont.) — Phase 1: build system resurrection

- **Phase 0 + 1 wall-clock so far:** ~35 min (20:52 → 21:25 PDT), fully AI-driven.
- **Done:** Gradle 4.6 → **9.5.1**, AGP 3.2.1 → **9.2.1** (Kotlin DSL + version catalog, straight rewrite — no Upgrade Assistant, no Jetifier), package renamed to `io.github.anshu7vyas.stocked` (sources moved, manifest `package` → Gradle `namespace`), Support Lib → AndroidX/Material via deterministic sed map (12 Java + 6 XML class mappings), minSdk 16→26, target/compileSdk 28→36, JodaTime deleted → `java.time`, `Notification.Builder` → NotificationCompat + channel + POST_NOTIFICATIONS runtime request, GitHub Actions CI, signed release AAB (4.99 MB) via new upload keystore.
- **Bugs the migration *surfaced* (blog gold):**
  1. `StockedApp` was never registered in the manifest — `JodaTimeAndroid.init()` was dead code for 8 years; the app only worked because Joda's core bundles default tz data.
  2. Date picker writes **unpadded** dates (`6/18/2026`, `AddItemActivity.java:75`); legacy `SimpleDateFormat("MM/dd/yyyy")` parsed them only via lenient mode. Strict `java.time` rejected them → items instantly "Expired". Fixed with `M/d/yyyy`; epochDay in Phase 2 kills the whole bug class.
  3. Legacy `getLeftDays` had a time-of-day-dependent off-by-one (Joda day-diff truncation compensated with `+1`); now calendar-day exact.
  4. `windowOptOutEdgeToEdgeEnforcement` is **ignored at targetSdk 36** — plan assumed it would carry us to Phase 4; instead `fitsSystemWindows` stopgap on activity roots.
- **Metrics:** debug APK 2.9 MB → release (unsigned-minify-off) APK 5.4 MB / AAB 4.99 MB — AndroidX+Material weight, R8 off until Phase 6 (honest number). Clean build 33 s; incremental 4 s (config cache on).
- **Verification:** fresh install on API 36 emulator; add-item flow end-to-end (layout-tree-driven automation via `android layout` — more reliable than coordinate taps); notification permission prompt works (first time notifications can fire since Android 8).
- **Adversarial review gate** (architect-reviewer + code-reviewer agents, ~5 min, findings verified before fixing):
  - **Confirmed HIGH:** my `checkSelfPermission(POST_NOTIFICATIONS)` gate silently killed notifications on API 26–32 (permission doesn't exist pre-33 → always DENIED). Fixed with `areNotificationsEnabled()`. *AI reviewing AI caught an AI bug.*
  - **Confirmed:** day-count regressions — first my calendar-day "fix" expired items a day early; then the reviewer's suggested `+1` showed 9 where legacy showed 8. Truth (verified on emulator against baseline): legacy = plain day diff, except expiry day = "1 day left". Lesson: *verify reviewer fixes too* — both directions of the off-by-one shipped past one layer of review.
  - **Confirmed:** parse-failure path destructively marked items expired → now inert sentinel. Dead `windowOptOutEdgeToEdgeEnforcement` attr removed (ignored at targetSdk 36).
  - **Refuted:** "viewpager 1.1.0 may not exist" (build resolves it); deferred: first-launch permission race (notification path moves to WorkManager in Phase 2).
- `/simplify` skipped this phase by design: remaining Java is deleted/converted in Phase 2; polishing doomed code is churn.

### Session 1 (cont.) — Phase 2: Kotlin + Room + architecture

- **Wall-clock Phase 2:** ~45 min (21:30 → 22:15 PDT).
- **Done:** 1,870 lines of Java → **0** (22 Kotlin files); Room v1 `stocked.db` with
  `expiryEpochDay: Long?` (nullable — shopping items have no expiry; date-string bug class eliminated);
  ProductDao Flow queries replace rawQuery string concat; ProductRepository; Hilt everywhere;
  4 ViewModels exposing StateFlow; fragments collect via `repeatOnLifecycle`; main-thread DB access dead;
  `ExpiryCheckWorker` (daily WorkManager) — notifications now fire without opening the app, and the
  in-Home notification hack is gone; unit tests: 14 (Product day-math semantics incl. cross-year
  lexicographic-bug proof, DAO via Robolectric in-memory Room, HomeViewModel via Turbine + FakeProductDao).
- **Deleted legacy horrors:** `new HomeFragment()` inside ProductAdapter just to call getLeftDays;
  FragmentRefreshListener callback web (reactive Flows made it free); destructive `DROP TABLE` onUpgrade.
- **Pain points:**
  - androidx.core 1.19.0 demands compileSdk 37 → pinned 1.17.0 (compileSdk 36). Library-vs-SDK
    treadmill is alive and well in 2026.
  - ViewModel-under-test gotcha: a VM constructed in `@Before` with `init { launch { … } }` raced the
    test's seed data; constructing the VM after seeding (realistic: sweep runs against existing data) fixed it.
  - KSP versioning changed format (now standalone `2.3.x`, not `2.2.x-y`) — easy to query the wrong metadata.
- **Verification:** full suite green; emulator end-to-end: add item ("expire in 8 days" matches baseline),
  shopping add, timeline badges, long-press consume → Home updates instantly via Flow emission (UDF payoff visible).
- Created repo `CLAUDE.md` — session-resume protocol so context loss can't strand the project.
- **Adversarial review gate** (kotlin-specialist + architect-reviewer, parallel; findings verified before fixing):
  - **CRITICAL (kotlin-specialist, proven by disassembling WorkManager 2.11.2 bytecode):** WorkManager's
    androidx.startup initializer self-initializes with the default factory *before* `Application.onCreate`,
    so `Configuration.Provider`/`HiltWorkerFactory` was never consulted → `ExpiryCheckWorker` (@AssistedInject,
    no 2-arg ctor) would FAIL on every run, silently, forever. Unit tests couldn't catch it (FakeProductDao
    bypasses WorkManager). Fixed via manifest `tools:node="remove"` of WorkManagerInitializer; **verified on
    emulator**: forced a one-time run → `Worker result SUCCESS` + real notification on `expiry_alerts` channel.
  - **HIGH (architect-reviewer):** `viewModelScope.launch { insert }` + immediate `finish()` in the two add
    screens = write cancelled with the ViewModel → possible data loss of the exact thing the screen exists to
    save. Fixed with `@ApplicationScope` CoroutineScope (SupervisorJob) for must-survive writes.
  - Accepted smaller: NULLs-first `ORDER BY` on nullable expiry (latent trap → `IS NULL,` prefix), UTC/DST-fragile
    `minDate` millis math (→ local-midnight-tomorrow), missing `@Singleton` on DAO provider, test double-sweep
    (VM now constructed per-test after seeding), cross-reference comments pinning the expiry-boundary invariant.
- **`/simplify` gate** (4 parallel angle agents: reuse/simplification/efficiency/altitude): applied — unused
  legacy strings removed, `LocalDate.now()` hoisted once per emission, worker's duplicate
  `NotificationManagerCompat.from()` collapsed. **Deliberately skipped** findings invested in code Phase 3
  deletes (adapter `submitList` dedup, fragment collect boilerplate, ViewHolder pattern, validation helpers) and
  architecture seams kept on purpose (repository pass-throughs, `HomeUiState` wrapper — both grow in Phases 3–5).
  Noted for Phase 3: consider moving the open-sweep from `HomeViewModel.init` into the repository/app layer.

### Session 1 (cont.) — PR model + CI bootstrap (~23:00–23:40 PDT)

- Stacked per-phase PRs for incremental review: **#11** (Phases 0–1 → master), **#12** (Phase 2 → phase-1).
- **CI never fired** until a workflow existed on the *default branch* — bootstrapped via PR **#13**
  (deliberately red: master is still the 2018 code; that failure is the migration's thesis in one screenshot).
- **Pain point:** Phase 1 updated only `gradle-wrapper.properties`; the 2018-era `gradle-wrapper.jar`
  fails gradle/actions checksum validation. Lesson: always regenerate the wrapper
  (`./gradlew wrapper --gradle-version X`) — the jar and scripts version too, not just the URL.
- After the fix + stack rebase: **PR #11 ✓ green, PR #12 ✓ green, stocked-revamp ✓ green.**
