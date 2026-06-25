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
