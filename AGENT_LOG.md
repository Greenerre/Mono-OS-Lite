# Agent Log

## 2026-05-17

### Autonomous decisions

- Preserved the existing Gradle wrapper and Android module to maximize chance of building within the 3-hour goal.
- Replaced the prior FX dashboard app with a fresh Mono OS Lite product surface.
- Kept all integrations mock-only and deterministic so the demo works offline and avoids sensitive data.
- Used a single Compose activity for speed, clarity, and judge-friendly navigation.
- Put the full AI OS pipeline in one pure Kotlin engine so unit tests can verify the core behavior without emulator dependencies.

### Architecture choices

- Package and application id: `com.monoos.lite`.
- Engine file: `MonoEngine.kt`.
- UI file: `MainActivity.kt`.
- Risk model: L0-L4 with L2 approval, L3 authentication, and L4 block.
- Cloud LLM escalation: simulated text only, no backend.
- Memory model: local mock memories plus graph rows and visual tags.

### Files created or changed

- `settings.gradle.kts`
- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/values/styles.xml`
- `app/src/main/java/com/monoos/lite/MainActivity.kt`
- `app/src/main/java/com/monoos/lite/MonoEngine.kt`
- `app/src/test/java/com/monoos/lite/MonoEngineTest.kt`
- `.editorconfig`
- `.github/CODEOWNERS`
- `.github/dependabot.yml`
- `.github/pull_request_template.md`
- `.github/ISSUE_TEMPLATE/bug_report.yml`
- `.github/ISSUE_TEMPLATE/feature_request.yml`
- `.github/workflows/android-ci.yml`
- `CONTRIBUTING.md`
- `GIT_WORKFLOW.md`
- `SECURITY.md`
- `README.md`
- `PRODUCT_THESIS.md`
- `ARCHITECTURE.md`
- `DEMO_SCRIPT.md`
- `AGENT_LOG.md`
- `MEMORY_SCHEMA.md`
- `RISK_MODEL.md`
- `API_ESCALATION.md`
- `CHANGELOG.md`

### Build and test steps

- Initial `.\gradlew.bat testDebugUnitTest assembleDebug --offline` failed because `JAVA_HOME` was not set.
- Found Android Studio JBR at `C:\Program Files\Android\Android Studio\jbr`.
- Running the wrapper initially needed network access to verify/download the Gradle distribution.
- `.\gradlew.bat testDebugUnitTest assembleDebug` succeeded.

### Verification results

- Unit tests passed.
- Debug APK built successfully at `app/build/outputs/apk/debug/app-debug.apk`.
- APK size observed: about 11.6 MB.
- `adb devices` returned no connected device.
- `emulator -list-avds` returned no configured AVD.

### Emulator validation update

- Installed Android 35 AOSP ATD x86_64 system image.
- Created local AVD `MonoOSLite_API35`.
- Booted the emulator and installed `app-debug.apk`.
- Launched `com.monoos.lite/.MainActivity`.
- Verified through Android UI hierarchy that the launcher, presets, pipeline, and financial L3 risk state render logically.
- Known emulator caveat: this ATD image returned black framebuffer screenshots in this environment even while the app was resumed and interactive. A physical Android device or full Google APIs emulator image is recommended for judge-facing visuals.

### GitHub repo management pass

- Added Git workflow documentation for `Greenerre/Mono-OS-Lite`.
- Added Android CI for pull requests and protected branches.
- Added Dependabot config for Gradle and GitHub Actions.
- Added PR template, issue templates, CODEOWNERS, security policy, contribution guide, and editor config.
- Expanded `.gitignore` to exclude generated emulator proofs, build outputs, local SDK paths, and IDE noise.
- Removed local emulator screenshot/XML artifacts from the workspace.
- Git could not be initialized or pushed from this machine because `git` and `gh` are not installed or available on `PATH`.

### Instruction layer simulation pass

- Added explicit instruction packet modeling to `MonoEngine.kt`.
- Added `InstructionRule` and `ObjectiveCoverage` runtime data.
- Added `Instruction layer simulation` dashboard to make OS policy directives visible.
- Added `Goal coverage dashboard` to show whether core demo objectives are represented in the running app.
- Added unit tests for L3 instruction-layer enforcement and objective coverage.
- Updated README, architecture, demo script, and changelog.
- Verified `.\gradlew.bat testDebugUnitTest assembleDebug` succeeds.

### Human-centric UX and demo video pass

- Added conversation turns to the pipeline output.
- Reworked the launcher into a human input layer with user/assistant chat bubbles.
- Added a mock speech-to-text preview strip for the voice prototype path.
- Added objective coverage for the human-centric input layer.
- Added unit test coverage for conversation and mock speech-to-text output.
- Added browser-playable animated SVG demo at `demo/mono-os-lite-demo.svg`.
- Added `demo/VIDEO_DEMO.md` with usage and recording guidance.

### Chat-first simplification pass

- Removed the visible speech-to-text prototype path from the Android UI.
- Rebuilt `MainActivity.kt` around a simple chatbot, conversation/project sidebar, text input, thinking notes, semantic memory, execution, approval, and audit panels.
- Added engine outputs for thinking notes, deterministic memory record ids, simulated local Gemma/Ollama responses, and simulated ChatGPT escalation traces.
- Chose simulation over direct Ollama integration for this pass because emulator/device networking would add risk inside the 30-minute prototype window.
- Updated unit tests for chat input, thinking notes, memory indexing, ChatGPT escalation, and financial no-cloud policy.
