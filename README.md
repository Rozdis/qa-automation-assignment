# QA Automation Test Suite

API and UI test automation for:
- **Restful Booker** (`https://restful-booker.herokuapp.com`) — auth + booking CRUD
- **Hygraph GraphQL** (Ecommerce schema) — positive and negative query scenarios
- **DemoQA** (`https://demoqa.com/automation-practice-form`) — student registration form, via Playwright + Page Object Model

## Prerequisites
- Java 17+
- Maven 3.6+
- Chromium (installed automatically by Playwright — see setup below, no manual browser install needed)

## First-Time Setup

Playwright needs its browser binaries downloaded once per machine:

```
mvn test-compile exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium" -Dexec.classpathScope=test
```

## How to Run

```
# Run all tests (Booking API + GraphQL + UI)
mvn clean test

# Run only API tests (Restful Booker + GraphQL)
mvn test -Dgroups="api"

# Run only UI tests
mvn test -Dgroups="ui"

# Exclude the one known-issue test that documents a live DemoQA bug (see Challenges & Solutions)
mvn test -DexcludedGroups="known-issue"
```

Test classes run in parallel with each other (methods within a class stay sequential) — see `src/test/resources/junit-platform.properties`.

## Test Reports

- **Surefire**: `target/surefire-reports` (generated on every `mvn test`)
- **Allure**:
  ```
  mvn allure:report
  ```
  then open `target/site/allure-maven-plugin/index.html`. Includes REST Assured request/response details for every API/GraphQL call and embedded screenshots for failed UI tests.
- **UI failure screenshots**: also saved individually to `target/screenshots/`.

Note: with parallel class execution enabled, Surefire's plain-text per-class `.txt` summaries can misattribute *which* class a passing test count belongs to (a known Surefire/JUnit5-parallel interaction) — the *overall* pass/fail total is still correct, and the Allure report (which writes one independent result file per test) is unaffected either way, so it's the reliable source for a per-test/per-class breakdown.

## Project Structure

```
src/main/java/com/flamingo/qa/
  config/           Spring @ConfigurationProperties (booker.*, graphql.*, ui.* from application.properties)
  api/client/        BookingApiClient, GraphQlClient — REST Assured wrappers, no JUnit dependency
  api/model/         Request/response POJOs (auth, booking, graphql)
  api/graphql/        Small object-based GraphQL query/fragment/variable builder
  ui/model/           Gender, Hobby, StudentRegistration — UI domain data
  ui/pages/           PracticeFormPage, SubmissionModal — Page Object Model

src/test/java/com/flamingo/qa/
  base/               Shared Spring context bootstrap, idempotent Allure filter registration
  api/base/           BaseApiTest, BaseGraphQlTest — JUnit lifecycle, auth, cleanup
  api/tests/          BookingCrudTest, BookingNegativeTest, GraphQlPositiveTest, GraphQlNegativeTest
  ui/base/            BaseUiTest (browser/page lifecycle), ScreenshotOnFailureExtension
  ui/tests/           PracticeFormTest

src/test/resources/junit-platform.properties   Parallel execution config (classes concurrent, methods sequential)
.github/workflows/ci.yml                       GitHub Actions: install Playwright, run suite, publish Allure/Surefire/screenshots as artifacts
```

Reusable framework code (clients, models, page objects, config) lives under `src/main` since it has no JUnit dependency; only actual test classes and JUnit-coupled lifecycle code live under `src/test`.

## Test Strategy

- **Independence over ordering**: every test creates and cleans up its own data (`@AfterEach`), so any test can run alone or in any order — nothing depends on execution sequence.
- **Config over hardcoding**: base URLs and credentials are bound via Spring `@ConfigurationProperties` from `application.properties`, not hardcoded in test code, so environments can be swapped without touching test logic.
- **Structured query building over string literals**: GraphQL queries are composed from a small object DSL (`GraphQlQuery`/`GraphQlField`/`GraphQlFragment`) instead of hand-written strings, except where a test's entire point is invalid syntax (the malformed-query negative test), where a literal is unavoidable by definition.
- **Negative tests verify real effect, not just status codes**: e.g. the invalid-token update/delete tests re-fetch the booking afterward to confirm it was genuinely untouched, not just that the response code looked right.
- **Assertions are specific**: error-message assertions check the message actually names the right problem (e.g. `contains("nonExistentField")`) rather than merely `isNotBlank()`, so a test can't pass against an unrelated failure.
- **UI waits**: Playwright's built-in auto-waiting handles most interactions; the one place that needs an explicit wait — the success modal appearing after submit, since the click itself returns before the async UI update completes — has its own `waitUntilDisplayed()` on the page object.
- **Data-driven where it adds real coverage**: booking creation runs against three varied payloads via `@ParameterizedTest` + `@MethodSource`, and the GraphQL pagination test runs against multiple `first` values, so each exercises more than a single hardcoded case without duplicating test logic.
- **Parallel-safe by construction**: test classes run concurrently (`junit-platform.properties`), which only works because API clients hold no mutable shared state (each takes its base URL as a constructor argument) and tests never cache anything — like an auth token — in state shared across sibling classes.

## Challenges & Solutions

- **Required-field validation on the DemoQA form isn't a CSS class.** It's native HTML5 constraint validation (`required` attribute), not a Bootstrap `is-invalid` class as commonly assumed. Fixed by checking `element.checkValidity()` via JS evaluation instead.
- **Subjects autocomplete raced with native form submission.** Pressing Enter before the dropdown suggestion was confirmed sometimes fell through as a plain Enter keypress, which native-submitted the form prematurely. Fixed by clicking the actual suggestion option (Playwright auto-waits for it) instead of relying on keyboard timing.
- **DemoQA's fixed ad banner overlaps the Submit button** at the viewport size used, intercepting the click. Removed it via JS (`element.remove()`) immediately before submitting.
- **Found a genuine bug in DemoQA itself**: clicking the success modal's Close button throws `TypeError: Lr.findDOMNode is not a function` inside their React bundle and never closes the modal — reproduced identically via both a real Playwright click and a raw native DOM click. Documented as a deliberately failing test (`shouldCloseSubmissionModal`) rather than silently working around it, since it isn't a defect in this test suite.
- **Allure attachments could go missing depending on class execution order.** Only one base test class was registering the Allure/REST-Assured filter, so GraphQL requests risked being excluded from the report (or double-attached) depending on which test classes ran first. Centralized registration into a single idempotent, guarded helper shared by both API base classes.
- **PowerShell JSON parsing broke down during manual GraphQL schema introspection** (`ConvertFrom-Json` failed on deeply nested introspection responses) — worked around by using `curl.exe` with an explicit no-BOM UTF-8 request file instead of `Invoke-RestMethod`, purely as a one-off exploration tool (not part of the shipped suite).
- **Parallel execution exposed a real thread-safety bug before it ever ran in CI**: `BaseApiTest` originally cached the Restful Booker auth token in a `static` field. Since it's declared in the shared abstract base class, sibling subclasses (`BookingCrudTest`, `BookingNegativeTest`) don't get separate copies — they share the one static slot. Under class-level parallelism that's a genuine race. Fixed by removing the cache entirely: `authenticate()` is now called fresh wherever a token is needed. As part of the same fix, `BookingApiClient` now takes its base URL as a constructor argument (mirroring `GraphQlClient`) instead of relying on the global mutable `RestAssured.baseURI`, removing the last piece of shared mutable state from the API layer.
- **The one `known-issue`-tagged test would otherwise always redden CI.** Since it deliberately documents a live, reproducible DemoQA bug (see above) rather than a defect in this suite, CI excludes just that tag (`-DexcludedGroups=known-issue`) so the pipeline reports a meaningful green/red signal instead of a permanent, uninformative failure — while `mvn clean test` locally still runs and shows it by default, keeping the bug visible during normal development.

## What I Would Add With More Time

- Web Tables coverage (Option B) for broader UI breadth
- Retry/rerun logic to absorb inherent flakiness of public third-party demo sites
- Broader GraphQL schema coverage (currently limited to the `Product`/`Category` slice of the Ecommerce schema)
- Method-level (not just class-level) parallelism, which would need per-test isolation work beyond what `bookingId`/`@AfterEach` currently provide within a single class
- Externalize test data (booking payloads, GraphQL product fixtures, form registration data) into data files (e.g. JSON/CSV loaded via `@MethodSource`) instead of hardcoding it inline in Java test classes, so data can be edited without touching test code
