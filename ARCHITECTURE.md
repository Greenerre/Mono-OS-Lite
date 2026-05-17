# Architecture

## Shape

Mono OS Lite is a single-module Android app:

- `:app`
- Kotlin
- Jetpack Compose
- Material 3
- No backend
- No Firebase
- No real sensitive data

## Runtime pipeline

The core pipeline lives in `app/src/main/java/com/monoos/lite/MonoEngine.kt`.

1. `Intent Classifier` maps raw text to a demo intent.
2. `Thinking Notes` expose observe, classify, compress, index, route, and escalation decisions.
3. `Semantic Compressor` converts the command into compact task atoms.
4. `Local LLM Simulation` returns a Gemma/Ollama-style response without a network dependency.
5. `Instruction Layer` turns the compressed task into directives for privacy, memory, risk, approval, routing, and audit.
6. `Memory Indexer` retrieves mock context and assigns a local memory record id.
7. `Graph-Relational Memory Layer` emits node/relation/evidence rows.
8. `Visual Context Index` attaches synthetic UI and app tags.
9. `Risk Gate Engine` assigns L0-L4 risk.
10. `Agent Router` assigns one or more agents.
11. `Approval Manager` gates L2/L3 flows.
12. `Cloud LLM Escalation Layer` describes simulated ChatGPT escalation for complex tasks.
13. `Mock App Orchestrator` creates workflow statuses.
14. `Audit Logger` records every step.

## UI composition

`MainActivity.kt` renders a single scrollable AI control surface:

- Chat-first launcher
- Conversation/project sidebar
- Text command box
- Thinking notes
- Semantic memory panel
- Graph-relational memory rows
- Visual context tags
- Execution and agent routing panel
- Approval/authentication gate
- Audit log panel

## Demo video artifact

`demo/mono-os-lite-demo.svg` is a browser-playable animated storyboard that simulates the app flow for judging and sharing. It exists because the local ATD emulator was functionally interactive but returned black framebuffer screenshots in this environment.

## Data policy

The app never reads real device content. All context is generated from local deterministic demo presets and the current command string.
