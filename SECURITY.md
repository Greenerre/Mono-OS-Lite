# Security Policy

## Supported version

This demo currently supports the latest `main` branch only.

## Reporting

For private security reports, use GitHub private vulnerability reporting if enabled on `Greenerre/Mono-OS-Lite`. If it is not enabled, open a minimal issue that does not disclose exploit details and ask the maintainer to move the discussion private.

## Demo data policy

- No real calendar, email, banking, microphone, or screen data should be collected.
- No API keys should be committed.
- No Firebase or backend integration is currently approved.
- Cloud LLM escalation is simulated locally in the app.

## Secret handling

Use local environment variables or untracked local files for secrets. Never commit:

- `local.properties`
- API keys
- keystores
- signing passwords
- generated APKs intended for private distribution
