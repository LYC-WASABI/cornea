# Model State

## Stable baseline

- Baseline reference explanation:
  - `385_stage200_model_explanation.md`
- Baseline deformation postprocessing probe:
  - `probe_stage200_cornea_surface_deformation.java`

This Stage 200 branch is the trusted reference for:

- target load interpretation around `0.03 N`
- JFO-based thin-film treatment
- measured deformation and load-sharing outputs from the reference model

## Latest experimental attempt

- model:
  - `576u2_stage576_recursive_split005_alpha020_results.mph`
- note:
  - `576u2_stage576_recursive_split005_alpha020_diagnostic.md`
- builder:
  - `build_stage576u2_recursive_split_segment_005_alpha020.java`

Current interpretation:

- this is the newest local branch by timestamp
- it is numerically stable
- but `alpha = 0.20` worsened the second split segment compared with the
  `alpha = 0.15` split case

## Latest validated checked milestone

- model:
  - `576n12_stage576_full_dynamic_recursive_checked.mph`
- note:
  - `576n12_stage576_full_dynamic_recursive_checked.md`
- verifier:
  - `verify_stage576n12_checked.java`

Current interpretation:

- this is the newest clearly checked and verified Stage 576 result
- it reached fraction `1.0000`
- it passed the acceptance checks recorded in the note

## Working rule

When discussing "the latest model", always clarify whether the meaning is:

1. latest experimental attempt by timestamp, or
2. latest explicitly verified checked result

## Update Log Template

Use the following template for each new modeling session. Add the newest entry
at the top of the log.

```md
## YYYY-MM-DD

### Changed

- what geometry, physics, solver, variable, boundary, or script was changed
- what file names were updated

### Observed

- what happened after the change
- include concrete quantities when available
- examples: `F_contact`, `F_film`, `F_total`, `theta`, `max pressure`, `min gap`

### Interpretation

- what the change appears to mean physically or numerically
- whether the result is better, worse, inconclusive, or only partially useful

### Next Step

- the single most defensible next action
- if needed, list a fallback action after that

### Files

- `note.md`
- `build_or_probe.java`
- `result_or_checkpoint.mph`
```

## Recommended Logging Rules

- Record only one coherent modeling step per dated block.
- Prefer measured quantities over narrative impressions.
- If a result is not validated, say so explicitly.
- If a branch becomes a dead end, mark that clearly so later sessions do not
  mistake it for the preferred path.
- When possible, name both the note file and the matching Java script.
