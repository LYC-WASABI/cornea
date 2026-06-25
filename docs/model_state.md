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

## Latest experimental attempt

- model:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`
- note:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md`
- builder:
  - `build_stage576w3c_recursive_split005_film_height_release_extended.java`

Current interpretation:

- this is the newest Stage 576 checked result
- it passed both early split segments: `0 -> 2.5%` and `2.5% -> 5%`
- it introduced explicit imposed-indentation release plus film-height release:
  `h_calc576w3c = h_calc573 + drel576w3c`
- segment 1 closed to `F_total = 0.0328395 N`
- segment 2 closed to `F_total = 0.0329548 N`
- both are within the acceptance window around the `0.03 N` target

## 2026-06-25-22.42

### Changed

- Created Stage 576w3c film-height release extended test.
- Kept the 576u split route:
  `segments = 0 -> 2.5% -> 5%`.
- Kept `alpha = 0.15` and `beta = 0.15`.
- Added explicit imposed-indentation release and explicit TFF film-height release:
  `h_calc576w3c = h_calc573 + drel576w3c`.
- Increased inner iterations to 32.
- Used `gamma = 0.12`, `Keff = 5000 N/m`,
  `drel_step_max = 0.5 um`, and `drel_max = 40 um`.

### Observed

- Segment 1, `0 -> 2.5%`, passed:
  `F_contact = 0.0221469 N`,
  `F_film = 0.0106927 N`,
  `F_total = 0.0328395 N`,
  `drel = 4.4197 um`.
- Segment 2, `2.5% -> 5%`, passed:
  `F_contact = 0.0196748 N`,
  `F_film = 0.0132800 N`,
  `F_total = 0.0329548 N`,
  `drel = 4.9723 um`.
- `CHECKED_STATUS=PASS`.

### Interpretation

- Stage 576w3c is the first Stage 576 branch that closed both early split segments to the `0.03 N` target range using explicit load release.
- The earlier 576w and 576w2 branches showed that releasing only the solid indentation was insufficient because the film load did not decrease.
- The successful change was adding the release displacement directly into the TFF film height:
  `h_calc576w3c = h_calc573 + drel576w3c`.

### Next Step

- Do not continue tuning `alpha`, `beta`, `segment size`, or release gain immediately.
- Treat 576w3c as the current checked milestone.
- Create and run `verify_stage576w3c_checked.java` to independently verify the checked `.mph` result.

### Files

- `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`
- `576w3c_stage576_recursive_split005_film_height_release_extended_results.mph`
- `576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md`
- `build_stage576w3c_recursive_split005_film_height_release_extended.java`

## 2026-06-25

### Changed

- Created Stage 576v fine-segment recursive test.
- Kept `alpha_pfb576v = 0.15`.
- Kept `beta_relax576v = 0.15`.
- Changed segment endpoints from `0 -> 2.5% -> 5%` to
  `0 -> 1.25% -> 2.5% -> 3.75% -> 5%`.

### Observed

- Segment 1, `0 -> 1.25%`, converged with `F_contact = 0.0280043 N`,
  `F_film = 0.0100258 N`, and `F_total = 0.0380302 N`.
- Segment 2, `1.25% -> 2.5%`, ended with `F_contact = 0.0262712 N`,
  `F_film = 0.0358211 N`, and `F_total = 0.0620923 N`.
- Segment 2 failed acceptance before the run advanced to `3.75%` or `5%`.
- Pressure/cavitation remained finite: `MaxP = 56.99 kPa`,
  `MinTheta = 0.9999710`, and `MinGap = -57.22 um` at the failed segment.

### Interpretation

- Finer `1.25%` segmentation improves the very first segment but worsens the
  next pressure-history handoff.
- The remaining issue is not gross numerical instability, mask direction, or
  exterior pressure anchoring.
- `alpha`, `beta`, and segment size alone have not closed
  `F_contact + F_film` to the `0.03 N` target.

### Next Step

- Return to the `576u` two-segment baseline.
- Test explicit load-control/release on the imposed indentation instead of
  continuing to tune `alpha`, `beta`, or segment size alone.

### Files

- `build_stage576v_recursive_fine_segment_005.java`
- `576v_stage576_recursive_fine005_diagnostic.md`
- `576v_stage576_recursive_fine005_results.mph`

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
