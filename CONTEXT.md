# Repository Reading Guide

This file is the recommended entry point for ChatGPT/Codex-style tools that need to read this repository.

Do not stop at repository metadata. Read the files below in order.

## Goal

This repository contains reproducible source artifacts for cornea-lid mixed lubrication, tear-film flow, contact mechanics, and staged COMSOL model development.

The main modeling themes are:

- Thin-Film Flow with JFO/Elrod-Adams cavitation
- Solid Mechanics contact between lid wiper and cornea
- Load sharing between film pressure and solid contact
- Stage-by-stage progression from a static baseline to dynamic recursive coupling

## Read Order

Read these files first, in this order:

1. `README.md`
   - Repository scope, tracked artifacts, and excluded large binaries.
2. `385_stage200_model_explanation.md`
   - Stage 200 baseline explanation for the calibrated static or quasi-static reference model.
3. `probe_stage200_cornea_surface_deformation.java`
   - Read-only postprocessing probe for actual corneal deformation, film thickness, and load-sharing outputs from the Stage 200 reference model.
4. `576n12_stage576_full_dynamic_recursive_checked.md`
   - Current dynamic recursive coupling milestone and acceptance summary.
5. `verify_stage576n12_checked.java`
   - Read-back verification script for the final Stage 576n12 checked result.

## Optional Next File

If you need to understand how the Stage 576n12 state was generated, then read:

6. `build_stage576m_recursive_field_relaxation.java`
   - Builder script for the recursive relaxed-field coupling path that leads into the checked Stage 576n12 result set.

## What Each File Answers

- `README.md`
  - What is version-controlled here?
  - Why are `.mph` files not present in GitHub?

- `385_stage200_model_explanation.md`
  - What is the baseline physical model?
  - How is the `0.03 N` target load interpreted?
  - What are the main parameters and limitations of the baseline setup?

- `probe_stage200_cornea_surface_deformation.java`
  - What are the actual measured Stage 200 outputs from the COMSOL model?
  - Is the script only probing results, or does it modify the model?

- `576n12_stage576_full_dynamic_recursive_checked.md`
  - What is the latest dynamic milestone?
  - What final force balance, pressure, theta, and gap checks passed?

- `verify_stage576n12_checked.java`
  - How are the Stage 576n12 final checks computed from saved solutions?
  - Which exact quantities are used for validation?

- `build_stage576m_recursive_field_relaxation.java`
  - How is the recursive pressure-field relaxation workflow assembled?
  - What intermediate setup, checkpoint, and result files are involved?

## Important Repository Facts

- Large COMSOL binaries such as `*.mph` are intentionally excluded from Git.
- Many Java files in this repository are COMSOL builder, probe, audit, or verification scripts.
- The most reliable way to understand the project is to read the curated files above first, not to sample random scripts.
- Stage numbers matter. Lower stages are earlier development steps; later `576*` stages represent the more advanced dynamic coupling work.

## Recommended Instruction For Another Model

Use this wording when asking another ChatGPT/Codex session to read the repository:

```text
Do not stop at repository metadata.
Read CONTEXT.md first, then read the files listed in its Read Order section in order.
After reading them, summarize:
1. the repository purpose,
2. the Stage 200 baseline,
3. the current Stage 576 dynamic status,
4. which scripts are probes versus builders versus verifiers.
```
