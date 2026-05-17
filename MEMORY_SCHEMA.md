# Memory Schema

Mono OS Lite uses a local mock memory model for the demo.

## Memory context

Each run retrieves three human-readable memory snippets:

- User preference
- Relevant app context
- Prior rule or project goal

Examples:

- `Mock calendar: design review 9 AM, investor sync 1 PM, build block 3 PM`
- `Prior user rule: never move money without authentication`
- `Startup idea memory: privacy-first AI layer for mobile workflows`

## Graph-relational row

The graph index is represented as rows:

```text
node | relation | evidence
```

Example:

```text
UserIntent | classified_as | Financial action
Savings transfer request | risk_level | L3
VisualContextIndex | tags | launcher:chat, app:banking-mock, ui:auth-required
```

## Visual context index

Visual tags represent mock app and screen context:

- `launcher:chat`
- `dashboard:pipeline`
- `app:calendar-mock`
- `app:mail-mock`
- `app:banking-mock`
- `canvas:roadmap`
- `ui:auth-required`
- `risk:L3`

## Privacy rule

No memory is persisted to a backend. No real app data is read. The schema is demonstrative and local.
