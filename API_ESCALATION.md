# API Escalation

Mono OS Lite includes a local LLM simulation layer and a Cloud LLM Escalation Layer, but this demo does not call a real API.

## Current behavior

The app displays simulated escalation decisions:

- Every command first receives a simulated local Gemma/Ollama response.
- Product roadmap and market research use simulated ChatGPT synthesis.
- Client email uses simulated redacted ChatGPT drafting and waits for approval.
- Financial actions do not send a ChatGPT payload.
- Critical actions are blocked locally.

## Why Ollama is simulated

The user has Ollama/Gemma available on the workstation, but a reliable Android demo must handle emulator host networking, physical-device LAN access, and cleartext HTTP policy. For this hackathon build, the app simulates the local model response in Kotlin so the APK works offline and judge setup stays predictable.

A later real integration can call:

- Emulator host: `http://10.0.2.2:11434`
- Physical device: `http://<host-lan-ip>:11434`

## Future API boundary

A production version would send only a redacted task packet:

```json
{
  "intent": "Product strategy planning",
  "risk": "L1",
  "compressed_intent": "intent=... | atoms=...",
  "memory_refs": ["mock_ref_1", "mock_ref_2"],
  "forbidden_fields": ["raw_email_body", "bank_credentials", "full_calendar"]
}
```

## Why no backend

The hackathon demo prioritizes clear orchestration over infrastructure. Avoiding a backend makes the APK easy to build, inspect, and present while preserving the privacy-first story.
