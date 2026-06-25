# Study Settings

This file records the high-level study patterns used in the repository.

## Stage 200 pattern

Stage 200 is used as a static or quasi-static calibrated reference state for:

- load sharing near `0.03 N`
- JFO thin-film response
- read-only deformation and force postprocessing

Primary references:

- `385_stage200_model_explanation.md`
- `probe_stage200_cornea_surface_deformation.java`

## Stage 576 pattern

Stage 576 development focuses on dynamic recursive coupling and split-segment
continuation strategies.

Important references:

- `576n12_stage576_full_dynamic_recursive_checked.md`
- `build_stage576m_recursive_field_relaxation.java`
- `576u_stage576_recursive_split005_diagnostic.md`
- `576u2_stage576_recursive_split005_alpha020_diagnostic.md`

## Current practical rule

When a study setting question appears, answer it using the matching stage note
plus the nearest `build_*` script, not from `.mph` assumptions.
