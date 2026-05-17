# API Escalation

Mono OS Lite includes a Cloud LLM Escalation Layer, but this demo does not call a real API.

## Current behavior

The app displays simulated escalation decisions:

- Product roadmap and market research use simulated cloud synthesis.
- Client email uses simulated redacted drafting and waits for approval.
- Financial actions do not send a cloud payload.
- Critical actions are blocked locally.

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
