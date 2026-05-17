# Git Workflow

## Repository

Target remote:

```text
https://github.com/Greenerre/Mono-OS-Lite.git
```

## Branch strategy

- `main` is protected and always demo-ready.
- Feature branches use `codex/<short-topic>` or `feature/<short-topic>`.
- Fix branches use `fix/<short-topic>`.
- Documentation branches use `docs/<short-topic>`.
- Release tags use `vMAJOR.MINOR.PATCH`, for example `v0.1.0`.

## Commit style

Use Conventional Commits:

```text
feat: add approval dashboard
fix: correct financial risk gate
docs: update judge demo script
test: cover L3 financial workflow
chore: update repo templates
```

## Local commands

Install Git for Windows first, then from the project root:

```powershell
git init
git branch -M main
git remote add origin https://github.com/Greenerre/Mono-OS-Lite.git
git status
git add .
git commit -m "feat: build Mono OS Lite Android demo"
git push -u origin main
```

If the GitHub repository already has commits:

```powershell
git init
git remote add origin https://github.com/Greenerre/Mono-OS-Lite.git
git fetch origin
git checkout -b codex/mono-os-lite-demo
git add .
git commit -m "feat: build Mono OS Lite Android demo"
git push -u origin codex/mono-os-lite-demo
```

Then open a pull request into `main`.

## Pull request rules

Every PR should:

- Explain the user-facing change.
- Link any issue or judging requirement.
- Include screenshots or emulator/device notes for UI changes.
- Pass Android CI.
- Avoid committing local files, APKs, build folders, SDK paths, or generated emulator artifacts.

## Main branch protection

Recommended GitHub settings:

- Require pull request before merging.
- Require at least one approving review.
- Require status check: `Android CI`.
- Require branches to be up to date before merge.
- Block force pushes.
- Block deletion of `main`.
- Use squash merge for feature PRs.

## Release process

1. Verify `README.md`, `DEMO_SCRIPT.md`, and `CHANGELOG.md`.
2. Run:

   ```powershell
   .\gradlew.bat testDebugUnitTest assembleDebug
   ```

3. Create a tag:

   ```powershell
   git tag v0.1.0
   git push origin v0.1.0
   ```

4. Upload the debug APK to a GitHub Release only when appropriate for demo distribution.
