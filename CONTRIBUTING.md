# Contributing to WaterMarkIt

You can help through code, examples, documentation, reproducible bug reports, and test media. A useful first contribution can be small: one clear example or one regression test makes the library easier to use.

[Choose a task](#choose-a-first-contribution) · [Build and test](#build-and-test) · [Package map](#find-your-way-around) · [PR checklist](#submit-a-pull-request)

## Choose a first contribution

Start with [good first issues](https://github.com/watermark-lab/WaterMarkIt/issues?q=is%3Aissue%20is%3Aopen%20label%3A%22good%20first%20issue%22) or [help wanted](https://github.com/watermark-lab/WaterMarkIt/issues?q=is%3Aissue%20is%3Aopen%20label%3A%22help%20wanted%22). Read the discussion before starting so you can see the scope and whether someone is already working on it.

If no labeled issue fits, use one of these starting points to propose a small contribution and agree its scope in an issue.

| Idea | Where to start | What a finished contribution demonstrates |
| --- | --- | --- |
| Add a PNG transparency regression test | `src/test/java/com/markit/image/`; `DefaultImageWatermarker` | A small generated transparent PNG accepts a watermark, remains readable, retains its dimensions, and preserves transparency outside the watermark. |
| Test PDF text preservation | `src/test/java/com/markit/pdf/`; `WatermarkingMethod.OVERLAY` | A generated PDF retains its original extractable text and page count after adding an overlay; the test does not depend on an external PDF. |
| Document FFmpeg setup on one OS | The [video instructions](README.md#video-watermarks) and video test | A reproducible setup recipe lists the tested OS and FFmpeg version, verifies both executables, and successfully processes a small video. |

Keep a first task focused on one behavior. For a new feature, open an [issue](https://github.com/watermark-lab/WaterMarkIt/issues) with the use case, proposed behavior, and a sketch of the API. Discuss changes to public APIs, dependencies, or processing defaults before investing in a large implementation.

To report a bug, include a minimal example, expected and actual behavior, library/JDK/OS versions, and the smallest shareable input that reproduces it. Include FFmpeg details for audio and video. Use synthetic or redistributable fixtures without private data.

## Build and test

### Prerequisites

- A JDK: use Java 11 or 17 to match the [CI configuration](.github/workflows/mvn.yml).
- Maven. Import `pom.xml` in your IDE; Maven configures both Java and Kotlin compilation.
- FFmpeg, including `ffprobe`, on `PATH` for the complete media test suite. See the [video setup notes](README.md#video-watermarks).

Verify the tools in the same shell used to build:

```shell
java -version
mvn -version
ffmpeg -version
ffprobe -version
```

Make sure Maven reports the intended JDK. Some audio tests skip when FFmpeg is unavailable; the video test requires FFmpeg and ffprobe. A run with skipped media tests is not a full media verification.

### Get the source

Fork [watermark-lab/WaterMarkIt](https://github.com/watermark-lab/WaterMarkIt), then clone your fork and create a branch:

```shell
git clone https://github.com/YOUR_USERNAME/WaterMarkIt.git
cd WaterMarkIt
git checkout -b docs/your-change
```

Use a branch name that describes your change; for example, `fix/png-transparency` or `test/pdf-overlay-text`.

### Run the checks

Run tests during development:

```shell
mvn test
```

Run one relevant class while iterating, for example:

```shell
mvn "-Dtest=FormatContentStepTest" test
```

Before submitting a code change, run the same build command as CI:

```shell
mvn --errors --batch-mode clean install
```

This runs tests, packages the library, and installs it into your local Maven repository using the version declared in `pom.xml`. It does not publish a release. Use the installed version in a consuming project when trying source-tree features.

Test reports are in `target/surefire-reports/`. A complete build also generates the JaCoCo report in `target/site/jacoco/`.

For documentation-only changes, check Markdown rendering, links, and the relevant code examples. State what you verified in the PR; distinguish compilation, test execution, and visual inspection.

## Find your way around

Production Java and Kotlin sources both live under `src/main/java/com/markit/`; Kotlin tests live under `src/test/java/com/markit/`.

| Path under `src/main/` | Responsibility |
| --- | --- |
| `java/com/markit/api/` | Public entry point, format-specific fluent builders, and watermark configuration |
| `java/com/markit/pdf/` | PDF orchestration, rasterization, overlays, and font handling |
| `java/com/markit/image/` | Image conversion, positioning, and watermark painting |
| `java/com/markit/video/ffmpeg/` | Video probing, filter graphs, and FFmpeg execution |
| `java/com/markit/audio/` | Audible watermarks, audio filter graphs, and text-to-speech |
| `java/com/markit/servicelocator/` | Discovery and priority selection of SPI implementations |
| `resources/META-INF/services/` | `ServiceLoader` provider registrations |

Start with a nearby test and follow the call from `WatermarkService` into the format-specific builder. The [advanced usage guide](docs/advanced-usage.md) explains the public extension mechanism.

## Code and test conventions

- Keep changes focused. Use explicit dependencies, small methods, and clear responsibility boundaries; prefer composition over deep inheritance.
- Use Java for public service interfaces and core processing. Kotlin is used for tests and selected value types, enums, and exceptions; follow the conventions of the area you are changing.
- Keep Java callers in mind when changing Kotlin types exposed by the API. Document public methods and any changes to defaults, formats, resource ownership, or exceptions.
- Preserve the existing encoding and line endings of every file. Follow nearby indentation and naming; avoid unrelated formatting or import changes.
- Add behavior-focused JUnit 5 tests for fixes and new processing behavior. Prefer small generated inputs and assertions about the result over checks that only assert non-empty bytes.
- Cover resource cleanup and failure behavior when changing external process execution or temporary files.
- When changing an SPI implementation, check its registration under `META-INF/services` and the relevant service-loading tests.
- Update examples and documentation when changing public behavior. Keep the declared Java baseline in mind.

## Reproduce the README preview

The [preview generator](examples/GenerateImagePreview.java) creates an original sample PNG and processes it with WaterMarkIt. It needs no external input files or FFmpeg. Run these commands from the repository root after compiling the library:

```shell
mvn compile dependency:build-classpath -DincludeScope=runtime -Dmdep.outputFile=target/runtime-classpath.txt
```

PowerShell:

```powershell
$dependencies = Get-Content -Raw target/runtime-classpath.txt
java --class-path "target/classes;$($dependencies.Trim())" examples/GenerateImagePreview.java
```

Linux or macOS:

```sh
dependencies="$(cat target/runtime-classpath.txt)"
java --class-path "target/classes:$dependencies" examples/GenerateImagePreview.java
```

The output is written to `target/readme-preview/image-before.png` and `image-after.png`. Inspect both before replacing the checked-in files under `docs/assets/`. The generator also accepts an output directory as its final argument.

## Submit a pull request

Open a PR from your branch to `master`. Describe the concrete problem or use case, the resulting behavior, and the checks you ran. Link the related issue if one exists.

Before submitting:

- Check that the diff contains only the intended changes.
- Preserve source encoding, line endings, and non-ASCII text.
- Include regression tests for changed behavior, or explain why the change does not require them.
- Update any affected examples, API documentation, and resource-ownership notes.
- Report the commands and environment used for verification, including skipped tests or checks you could not run.
- For a visual change, attach or link a small before/after example.

Review focuses on behavior, compatibility, design, and test evidence. Keep design questions and follow-up discussion in the issue or PR so future contributors can understand the decision.

Thank you for helping improve WaterMarkIt. Questions about setup or contribution scope are welcome in [GitHub Issues](https://github.com/watermark-lab/WaterMarkIt/issues).
