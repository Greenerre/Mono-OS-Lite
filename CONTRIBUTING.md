# Contributing

Mono OS Lite is a fast Android demo repo, but changes should still be reviewable and reproducible.

## Development loop

```powershell
$env:JAVA_HOME='C:\Program Files\Android\Android Studio\jbr'
$env:Path="$env:JAVA_HOME\bin;$env:Path"
.\gradlew.bat testDebugUnitTest assembleDebug
```

## Before opening a PR

- Keep the app mock-only unless a product decision explicitly changes that.
- Do not add Firebase or backend services without an architecture update.
- Do not commit generated APKs or local Android SDK paths.
- Add or update unit tests when changing `MonoEngine.kt`.
- Update docs when changing demo flow, risk behavior, or judging script.

## Review focus

Reviewers should check:

- Does the demo still launch?
- Does the financial preset trigger L3 authentication?
- Are sensitive flows still approval-gated?
- Is the UI clear for judges?
- Did the change avoid real sensitive data?
