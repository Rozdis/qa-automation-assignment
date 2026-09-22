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

# Exclude the one known-issue test that documents a live DemoQA bug (see Challenges & Solutions) — CI runs it and lets it fail
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
  - **Locally, in IntelliJ**: open `index.html` from the project tree and use "Open in Browser".
  - **Downloaded CI artifact**: extract the zip, then run `allure open <extracted-folder>` ([Allure CLI](https://github.com/allure-framework/allure2/releases)) and open the printed URL.
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
  ui/steps/           PracticeFormSteps — higher-level actions composed from page objects

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

- Every test creates and cleans up its own data (`@AfterEach`) — no ordering dependencies.
- Base URLs/credentials come from `application.properties` via Spring, not hardcoded.
- GraphQL queries are built with a small object DSL (`GraphQlQuery`/`GraphQlField`/`GraphQlFragment`), not raw strings — except the malformed-query negative test, where a literal is the point.
- Negative tests check real state, not just status codes (e.g. invalid-token update/delete tests re-fetch the booking to confirm nothing changed).
- Error-message assertions check the message names the actual problem, not just that it's non-blank.
- UI relies on Playwright's auto-waiting; only the post-submit success modal needs an explicit `waitUntilDisplayed()`.
- Booking creation and GraphQL pagination are parameterized to cover multiple cases per test.
- Test classes run in parallel — safe because API are stateless (base URL via constructor, no cached tokens or shared mutable state).

## Challenges & Solutions

- DemoQA's "required" validation is native HTML5 (`checkValidity()`), not a CSS class as it first appears.
- A fixed ad banner overlapped the Submit button — removed via JS before submitting.
- Found a real DemoQA bug: the success modal's Close button throws inside their React bundle and never closes it. Kept as a deliberately failing, `known-issue`-tagged test rather than working around it.
- Allure's REST-Assured filter was only registered in one base class, so GraphQL calls could be missing from the report — centralized into one shared helper.
- Parallel execution exposed a real race: the auth token was cached in a `static` field shared across test classes. Removed the cache and gave `BookingApiClient` a constructor-injected base URL, matching `GraphQlClient`.
- The `known-issue` test always fails — that's intentional, so CI stays honest about the live DemoQA bug rather than hiding it; exclude it locally with `-DexcludedGroups=known-issue` if needed.
- CI launched Chromium headed (`setHeadless(false)`), which crashes on GitHub Actions' runners (no X server) and fails `BaseUiTest`'s shared `@BeforeAll` before any UI test method runs — this looked like tag exclusion wasn't working, since the whole class errors out as one unit regardless of which methods remain. Fixed by binding headless mode to `ui.headless` (`UiProperties`, default `true`), read from the Spring context via an `ApplicationContext` parameter on the static `@BeforeAll` method.

## What I Would Add With More Time

- Web Tables coverage (Option B) for broader UI breadth
- Retry/rerun logic to absorb inherent flakiness of public third-party demo sites
- Broader GraphQL schema coverage (currently limited to the `Product`/`Category` slice of the Ecommerce schema)
- Method-level (not just class-level) parallelism, which would need per-test isolation work beyond what `bookingId`/`@AfterEach` currently provide within a single class
- Externalize test data (booking payloads, GraphQL product fixtures, form registration data) into data files (e.g. JSON/CSV loaded via `@MethodSource`) instead of hardcoding it inline in Java test classes, so data can be edited without touching test code
