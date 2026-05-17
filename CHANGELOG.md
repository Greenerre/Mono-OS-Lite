# Changelog

## 0.1.0 - 2026-05-17

- Replaced prior FX dashboard sample with Mono OS Lite Android demo.
- Updated app id and namespace to `com.monoos.lite`.
- Built a Compose AI launcher and dashboard suite.
- Added deterministic intent classification, compression, memory indexing, graph rows, visual tags, risk gates, approvals, agent routing, simulated cloud escalation, workflow statuses, and audit logs.
- Added unit tests for high-risk financial gating, multi-agent roadmap routing, and sensitive email approval.
- Added explicit instruction layer simulation and goal coverage dashboard.
- Added unit tests proving instruction-layer L3 enforcement and objective coverage.
- Added human-centric chat/voice input surface with conversation bubbles and mock speech-to-text preview.
- Added animated SVG video-style demo in `demo/mono-os-lite-demo.svg`.
- Simplified the Android UI into a chat-first experience with project sidebar, thinking notes, semantic memory receipt, local Gemma/Ollama simulation, ChatGPT escalation trace, approval gate, and audit trail.
- Removed the visible speech-to-text prototype path from the app UI.
- Built debug APK successfully.

## Verification

- `.\gradlew.bat testDebugUnitTest assembleDebug` completed successfully.
- APK generated at `app/build/outputs/apk/debug/app-debug.apk`.
- Local app launch remains unverified because no emulator/device is configured on this workstation.
