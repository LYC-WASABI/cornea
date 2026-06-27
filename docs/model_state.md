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
  - `577h2_stage577_asperity_calibration_refined_results.mph`
- note:
  - `577h2_stage577_asperity_calibration_refined_diagnostic.md`
- builder:
  - `build_stage577h2_asperity_calibration_refined.java`

Current interpretation:

- this is the newest Stage 577 checked postprocessing diagnostic
- it preserves `576w3c` as the input baseline but does not overwrite it
- `577a` passed the local TFF check with conserved `3 um` film thickness
- `577b` passed low-film / rupture activation as a postprocessing diagnostic
- `577c` passed mixed-lubrication / boundary-friction postprocessing
- `577d` is a useful failed sensitivity diagnostic because `mu_total` did not increase monotonically with `dh_deplete`
- `577e` failed because direct depleted film height in `ffp1.hw1` was too slow
- `577f` passed load-sharing boundary-pressure postprocessing
- `577g` passed the asperity-pressure proxy path; direct `solid.Tn` was reported but not accepted
- `577h` first small asperity-calibration scan failed; it is the latest attempt but not a checked milestone
- `577h2` refined asperity-calibration scan passed and is the latest checked postprocessing calibration result
- it outputs effective `mu_total` diagnostics, but it is not yet a fully coupled solid-contact-TFF result

Latest trusted structural/TFF load-balance baseline:

- model:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`
- note:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md`
- verifier:
  - `verify_stage576w3c_checked.java`

This remains the newest checked early-stroke split load-balance result.

## 2026-06-26

### Changed

- Created Stage 577a conserved `3 um` local TFF check from `576w3c`.
- Created Stage 577b low-film / rupture activation diagnostic from 577a.
- Created Stage 577c mixed-lubrication / boundary-friction postprocess from 577b.
- Kept all changes in new files; `576w3c` was not overwritten.

### Observed

- `577a` passed with `h_avg = 3.000e-6 m`, finite pressure/theta, and signed shear reversal.
- `577b` passed with `h_avg = 2.936e-6 to 3.098e-6 m` for the strongest `dh_deplete = 2.8 um` branch.
- `577b` depletion scan showed increasing low-film activation from `dh_deplete = 0.5` to `2.8 um`.
- `577c` passed with boundary friction sign reversal and monotonic `mu_total`.
- `577c` maximum `mu_total` values:
  `0.010095`, `0.023418`, `0.045622`, and `0.090030`
  for `mu_boundary = 0.02`, `0.05`, `0.10`, and `0.20`.

### Interpretation

- Pure-fluid `mu_TFF_alt` remains small, with maximum `0.0020777`, as expected.
- The Stage 577 mixed-lubrication postprocess raises the effective coefficient into the desired diagnostic range without retuning TFF pressure.
- The current limitation is that `577b` and `577c` are postprocessing checks; depleted film height and boundary friction are not yet coupled back into the governing solve.

### Next Step

- Preserve 577a/577b/577c as checked diagnostics.
- Next decide whether to calibrate `mu_boundary577c` against a target experimental COF, or to implement a weakly coupled version where low-film activation affects the TFF solve.

### Files

- `build_stage577a_conserved_3um_local_tff_check.java`
- `build_stage577b_conserved_depletion_rupture_check.java`
- `build_stage577c_mixed_lubrication_boundary_friction.java`
- `577a_stage577_conserved_3um_local_tff_check_diagnostic.md`
- `577b_stage577_conserved_depletion_rupture_check_diagnostic.md`
- `577c_stage577_mixed_lubrication_boundary_friction_diagnostic.md`
- `577a_stage577_conserved_3um_local_tff_check_results.mph`
- `577b_stage577_conserved_depletion_rupture_check_results.mph`
- `577c_stage577_mixed_lubrication_boundary_friction_results.mph`

## 2026-06-27

### Changed

- Created Stage 577d postprocess calibration and sensitivity scan.
- Created Stage 577e weakly coupled depleted TFF attempt.
- Created Stage 577f load-sharing boundary-pressure postprocess.
- Created Stage 577g contact-pressure / asperity-pressure boundary model.

### Observed

- `577d` found target-COF candidates for `dh_deplete >= 2.0 um`, but failed the monotonic-with-`dh_deplete` criterion.
- `577e` was manually terminated twice because the depleted-height TFF solve became impractically slow in the early transient.
- `577f` passed with `Fn_boundary = max(Fn_ref - Fn_fluid_pos, 0)` and `mu_total` about `0.089-0.090` at `mu_boundary = 0.20`.
- `577g` found that `solid.Tn` is available but not useful for dynamic friction in the current TFF dataset; the asperity proxy passed with `mu_total max = 0.116789`.

### Interpretation

- `577d` confirms that `p_boundary = Fn_ref/A_close` can flatten or slightly reverse the `dh_deplete` trend because increasing low-film area lowers pressure.
- `577e` shows that directly inserting spatially depleted film height into `ffp1.hw1` is not yet practical without continuation, smoothing, or shorter micro-window testing.
- `577f` is the preferred load-sharing postprocess branch.
- `577g` is the preferred roughness/asperity proxy branch; do not use direct `solid.Tn` from the current dataset as final boundary pressure.

### Next Step

- Do not continue with direct full-cycle 577e as written.
- Next either tune the 577g asperity proxy parameters, or create a short-window/continuation 577e2 weak-coupling attempt.

### Files

- `build_stage577d_postprocess_calibration_sensitivity.java`
- `build_stage577e_weakly_coupled_depleted_tff.java`
- `build_stage577f_load_sharing_boundary_pressure.java`
- `build_stage577g_contact_or_asperity_boundary_model.java`
- `577d_stage577_postprocess_calibration_sensitivity_diagnostic.md`
- `577e_stage577_weakly_coupled_depleted_tff_diagnostic.md`
- `577f_stage577_load_sharing_boundary_pressure_diagnostic.md`
- `577g_stage577_contact_or_asperity_boundary_model_diagnostic.md`
- `577d_stage577_postprocess_calibration_sensitivity_results.mph`
- `577f_stage577_load_sharing_boundary_pressure_results.mph`
- `577g_stage577_contact_or_asperity_boundary_model_results.mph`

## 2026-06-27-577h

### Changed

- Created Stage 577h first small asperity-calibration scan.
- Used `K_asp_eff` in kPa and scanned 24 postprocessing parameter combinations.
- Did not re-solve TFF and did not use direct `solid.Tn`.

### Observed

- All 24 combinations were finite.
- `mu_total` increased monotonically with `mu_boundary`.
- `mu_total` increased monotonically with `K_asp_eff`.
- No combination passed all filters.
- `h_crit = 0.5 um` did not activate asperity pressure in the tested window.
- The closest useful case was `dh=2.5 um`, `h_crit=1.0 um`, `K_asp_eff=30 kPa`, `mu_boundary=0.05`, with `mu_total_max=0.08847`, but `Fn_asp/Fn_ref_max=4.02`.

### Interpretation

- The first 577h window brackets the problem: low `h_crit` is underactive, while `h_crit=1.0 um` plus current `K_asp_eff` overproduces asperity normal load.
- The next scan should lower `K_asp_eff` and add intermediate `h_crit` values.

### Next Step

- Create `577h2` adjusted small scan:
  `dh_deplete = 2.3, 2.5, 2.8 um`,
  `h_crit = 0.7, 0.8, 0.9, 1.0 um`,
  `K_asp_eff = 5, 10, 20, 30 kPa`,
  `mu_boundary = 0.05, 0.10, 0.15`.

### Files

- `build_stage577h_asperity_calibration.java`
- `577h_stage577_asperity_calibration_diagnostic.md`
- `577h_stage577_asperity_calibration_summary.csv`
- `577h_stage577_asperity_calibration_best_params.md`
- `577h_stage577_asperity_calibration_results.mph`

## 2026-06-27-577h2

### Changed

- Created Stage 577h2 refined asperity-calibration scan.
- Reduced `K_asp_eff` and increased `mu_boundary` to preserve friction scale while reducing asperity normal-load proxy.
- Added `PARAM_SCORE` and `PASS_LEVEL` to the calibration CSV.

### Observed

- `SCAN_COUNT = 180`.
- `STRONG_PASS_COUNT = 53`.
- `CANDIDATE_PASS_COUNT = 15`.
- All values were finite.
- `mu_total` remained monotonic with `mu_boundary`.
- `mu_total` remained monotonic with `K_asp_eff`.
- Best candidate:
  `dh_deplete = 2.5 um`,
  `h_crit = 1.0 um`,
  `K_asp_eff = 7.5 kPa`,
  `mu_boundary = 0.20`,
  `mu_total_max = 0.0884728`,
  `A_close/A_film_mean = 0.0739728`,
  `Fn_asp/Fn_ref_max = 1.00473`.

### Interpretation

- Stage 577h2 fixed the main Stage 577h failure mode.
- The preferred candidate keeps the target friction scale while reducing the asperity normal-load proxy from about `4.02*Fn_ref` to about `1.00*Fn_ref`.
- This is still a postprocessing calibration, not a fully coupled mixed-lubrication solve.

### Next Step

- Create Stage 577i using the selected fixed parameter set.
- Output paper-facing time curves and spatial plots:
  `mu_TFF_alt(t)`, `mu_total(t)`, `Ft_fluid(t)`, `Ft_asp(t)`, `A_close(t)`, `Fn_asp(t)`, `theta_min(t)`, `pfilm_max(t)`, `h_eff_min(t)`, plus spatial fields for `h_eff`, `theta`, `pfilm`, `w_close`, and `p_asp`.

### Files

- `build_stage577h2_asperity_calibration_refined.java`
- `577h2_stage577_asperity_calibration_refined_diagnostic.md`
- `577h2_stage577_asperity_calibration_refined_summary.csv`
- `577h2_stage577_asperity_calibration_refined_best_params.md`
- `577h2_stage577_asperity_calibration_refined_results.mph`

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

## Latest full-path validated checked milestone

- model:
  - `576n12_stage576_full_dynamic_recursive_checked.mph`
- note:
  - `576n12_stage576_full_dynamic_recursive_checked.md`
- verifier:
  - `verify_stage576n12_checked.java`

Current interpretation:

- this is the older clearly checked and verified full-path Stage 576 result
- it reached fraction `1.0000`
- it passed the acceptance checks recorded in the note
- it remains the full-path reference, while `576w3c` is the newest verified
  early-stroke split milestone

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
