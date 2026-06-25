# Cornea-Lid COMSOL Reproduction

This repository tracks the reproducible source artifacts for the cornea-lid mixed lubrication and contact modeling work in this workspace.

## Project entry points

For human or model-assisted reading, start with:

- `CONTEXT.md`
- `LATEST_MODEL.md`
- `AGENTS.md`
- `docs/project_overview.md`
- `docs/model_state.md`
- `docs/next_tasks.md`

## Included

- COMSOL Java build and validation scripts (`*.java`)
- Stage notes and diagnostic summaries (`*.md`)
- Structured result files needed for lightweight review (`*.csv`, `*.json`, `*.mjs`)
- Presentation and document artifacts that are small enough to version directly

## Excluded

Large COMSOL model binaries and transient run artifacts are intentionally ignored by Git:

- `*.mph`
- `*.class`
- `*.log`
- `*.status`

This keeps the Git history lightweight and avoids GitHub file-size limits for multi-GB model outputs.
