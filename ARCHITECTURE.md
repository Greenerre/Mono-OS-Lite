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
2. `Semantic Compressor` converts the command into compact task atoms.
3. `Instruction Layer` turns the compressed task into visible directives for privacy, memory, risk, approval, routing, and audit.
4. `Memory Indexer` retrieves mock context relevant to the intent.
5. `Graph-Relational Memory Layer` emits node/relation/evidence rows.
6. `Visual Context Index` attaches synthetic UI and app tags.
7. `Risk Gate Engine` assigns L0-L4 risk.
8. `Agent Router` assigns one or more agents.
9. `Approval Manager` gates L2/L3 flows.
10. `Cloud LLM Escalation Layer` describes simulated escalation.
11. `Mock App Orchestrator` creates workflow statuses.
12. `Audit Logger` records every step.

## UI composition

`MainActivity.kt` renders a single scrollable AI control surface:

- AI launcher interface
- Chat command box
- Mock audio input button
- Context permissions dashboard
- Instruction layer simulation
- Workflow management dashboard
- Visual agent task review dashboard
- Memory vault dashboard
- Approval dashboard
- Goal coverage dashboard
- Audit log panel

## Data policy

The app never reads real device content. All context is generated from local deterministic demo presets and the current command string.
