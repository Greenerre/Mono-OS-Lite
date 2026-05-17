# Mono OS Lite

Mono OS Lite is a privacy-first AI operating layer demo for Android. It turns a chat or mock voice command into a visible multi-agent execution pipeline: intent classification, semantic compression, memory retrieval, graph-relational indexing, visual context tags, risk gates, approval handling, mock orchestration, cloud LLM escalation simulation, and audit logging.

## Grand Prize positioning

The demo is designed for the Grand Prize track: it shows an end-to-end AI OS control layer, not a single chatbot. The strongest signal is the visible pipeline. Judges can see how a raw user intent becomes structured, risk-aware, multi-agent execution without using real sensitive data, Firebase, or a backend.

## Setup

Prerequisites:

- Android Studio
- JDK 17 or the Android Studio bundled JBR
- Android SDK 36
- Android emulator or connected Android device, API 24+

This Windows shell currently needs `JAVA_HOME` set to Android Studio's JBR:

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
```

## Run instructions

Open the repo in Android Studio and run the `app` configuration, or use:

```powershell
.\gradlew.bat installDebug
& "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe" shell am start -n com.monoos.lite/.MainActivity
```

## Build APK

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

APK output:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## GitHub workflow

This repo is prepared for `Greenerre/Mono-OS-Lite`.

- Use `main` as the protected, demo-ready branch.
- Use short-lived branches such as `codex/mono-os-lite-demo`, `feature/<topic>`, or `fix/<topic>`.
- Use pull requests with Android CI required before merging.
- See `GIT_WORKFLOW.md` for exact Git commands, branch protection settings, release tags, and PR rules.

## Demo flow

1. Enter a command or press `Mock voice`.
2. Select any preset, including the high-risk financial preset.
3. Watch the pipeline classify intent, compress semantics, retrieve memory, attach graph and visual indexes, route agents, set risk, and write the audit log.
4. Inspect the Instruction layer simulation to see the policy packet Mono OS Lite uses before agent execution.
5. For client email and payment flows, use the Approval dashboard.
6. For `Move $500 to my savings account`, confirm that the initial state is `L3` and authentication is required before mocked execution can complete.
7. Inspect the Goal coverage dashboard to confirm all required demo objectives are represented.

## Exact commands to test

- `Summarise my calendar tomorrow and prepare my top 3 priorities`
- `Draft a reply to the latest client email and wait for approval`
- `Research this market and create a short action plan`
- `Move $500 to my savings account`
- `Build a product roadmap for my startup idea`

## Known limitations

- All integrations are mock-only by design.
- Cloud LLM escalation is simulated in-app; no backend or real API key is used.
- No real calendar, email, banking, screen, or microphone data is accessed.
- Local launch was verified on an Android 35 AOSP ATD emulator named `MonoOSLite_API35`.
- The ATD image returned black framebuffer screenshots in this environment even while the app was resumed and interactive. Use a physical Android device or a full Google APIs emulator image for judge-facing visuals.
