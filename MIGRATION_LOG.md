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
