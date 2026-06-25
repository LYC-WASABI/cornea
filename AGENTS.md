# AGENTS.md

## Project Identity

This repository is a COMSOL-based cornea-lid mixed lubrication project.
The goal is to track reproducible source artifacts, diagnostic notes, and
postprocessing scripts for tear-film flow, contact mechanics, and staged model
development.

## Important Constraint

Do not treat `.mph` files as directly inspectable source code.
If model understanding is required, prefer the repository text artifacts:

- `README.md`
- `CONTEXT.md`
- `LATEST_MODEL.md`
- `docs/*.md`
- selected `*.java` builder, probe, and verification scripts

## Reading Order

Read these files first, in order:

1. `README.md`
2. `CONTEXT.md`
3. `LATEST_MODEL.md`
4. `docs/project_overview.md`
5. `docs/model_state.md`
6. `docs/next_tasks.md`

Then read the specific stage note and Java scripts referenced there.

## Modeling Conventions

- Stage numbers matter. Later `576*` files are more recent than Stage 200.
- Distinguish between:
  - latest experimental attempt
  - latest explicitly verified checked milestone
- Do not invent simulation results.
- Mark assumptions clearly when a conclusion is inferred from notes rather than
  from direct postprocessing.

## Script Roles

Treat Java files by function:

- `build_*`: model construction or staged continuation
- `probe_*`: read-only inspection or postprocessing
- `verify_*` / `validate_*`: acceptance checks
- `diagnose_*`: targeted failure investigation

## Output Style

Use Chinese unless the user asks otherwise.
Prefer precise COMSOL and mechanics terminology.
