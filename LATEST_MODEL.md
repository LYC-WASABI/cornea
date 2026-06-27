# Latest Model Guide

This file identifies the latest local model state in this workspace.

Do not assume that "latest by timestamp" and "latest validated result" are the same thing.

## Short Answer

- Latest local model attempt by timestamp:
  - `577h2_stage577_asperity_calibration_refined_results.mph`
- Latest local diagnostic note for that attempt:
  - `577h2_stage577_asperity_calibration_refined_diagnostic.md`
- Latest local builder script for that attempt:
  - `build_stage577h2_asperity_calibration_refined.java`

- Latest local TFF-only conserved-film milestone:
  - `577a_stage577_conserved_3um_local_tff_check_results.mph`
- Latest local depletion / low-film activation milestone:
  - `577b_stage577_conserved_depletion_rupture_check_results.mph`
- Latest local mixed-lubrication postprocess milestone:
  - `577c_stage577_mixed_lubrication_boundary_friction_results.mph`
- Latest local load-sharing boundary-pressure milestone:
  - `577f_stage577_load_sharing_boundary_pressure_results.mph`
- Latest local asperity-pressure proxy milestone:
  - `577g_stage577_contact_or_asperity_boundary_model_results.mph`
- Latest local asperity-calibration attempt:
  - `577h2_stage577_asperity_calibration_refined_results.mph`
  - status: `PASS`, refined small scan found 53 strong-pass parameter sets

- Latest early-stroke checked and independently verified milestone:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`
- Latest early-stroke checked milestone note:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md`
- Latest early-stroke checked milestone verifier:
  - `verify_stage576w3c_checked.java`

- Latest full-path verified checked milestone:
  - `576n12_stage576_full_dynamic_recursive_checked.mph`

## Recommended Interpretation

Use the following distinction when asking another model to guide work:

- Use `577h2...` when you want the newest checked roughness/asperity
  calibration result.
- Use `577h...` only when you want the earlier failed small-scan calibration
  diagnostic; do not treat it as checked.
- Use `577f...` when you want the newest checked load-sharing boundary-pressure
  diagnostic.
- Use `576w3c...checked` when you want the newest explicitly verified
  early-stroke split structural/TFF load-balance result.
- Use `576n12...checked` when you want the older full-path verified reference.

Do not interpret `577f` or `577g` as fully coupled solid-contact-TFF solves.
They are postprocessing diagnostics based on the passed 577a/577b local-film
checks. `577g` passes through the asperity proxy path; the direct `solid.Tn`
path is reported as not accepted for the current dataset.

## Latest Experimental Main-Line Attempt

### Model

```text
577h2_stage577_asperity_calibration_refined_results.mph
```

### Companion files

```text
577a_stage577_conserved_3um_local_tff_check_diagnostic.md
577b_stage577_conserved_depletion_rupture_check_diagnostic.md
577c_stage577_mixed_lubrication_boundary_friction_diagnostic.md
577d_stage577_postprocess_calibration_sensitivity_diagnostic.md
577e_stage577_weakly_coupled_depleted_tff_diagnostic.md
577f_stage577_load_sharing_boundary_pressure_diagnostic.md
577g_stage577_contact_or_asperity_boundary_model_diagnostic.md
577h_stage577_asperity_calibration_diagnostic.md
577h2_stage577_asperity_calibration_refined_diagnostic.md
build_stage577a_conserved_3um_local_tff_check.java
build_stage577b_conserved_depletion_rupture_check.java
build_stage577c_mixed_lubrication_boundary_friction.java
build_stage577d_postprocess_calibration_sensitivity.java
build_stage577e_weakly_coupled_depleted_tff.java
build_stage577f_load_sharing_boundary_pressure.java
build_stage577g_contact_or_asperity_boundary_model.java
build_stage577h_asperity_calibration.java
build_stage577h2_asperity_calibration_refined.java
577a_stage577_conserved_3um_local_tff_check_results.mph
577b_stage577_conserved_depletion_rupture_check_results.mph
577c_stage577_mixed_lubrication_boundary_friction_results.mph
577f_stage577_load_sharing_boundary_pressure_results.mph
577g_stage577_contact_or_asperity_boundary_model_results.mph
577h_stage577_asperity_calibration_results.mph
577h2_stage577_asperity_calibration_refined_results.mph
```

### What it is

This is the newest Stage 577 checked diagnostic branch. It keeps `576w3c` as
the input baseline, but replaces the nonphysical final-film interpretation of
`h_calc576w3c = h_calc573 + drel576w3c` with a staged local-film diagnostic:

```text
577a: local TFF with conserved h_TFF = 3 um
577b: postprocessed conserved-film depletion / low-film activation
577c: postprocessed mixed-lubrication boundary-friction diagnostic
577d: calibration/sensitivity scan, useful but FAIL on monotonic-with-dh
577e: weakly coupled depleted TFF attempt, FAIL due impractical solve time
577f: load-sharing boundary-pressure diagnostic
577g: asperity-pressure proxy diagnostic
577h: first small asperity-calibration scan, useful but FAIL
577h2: refined asperity-calibration scan, PASS
```

### Current conclusion

Stage 577g is the preferred current postprocessing diagnostic for asperity
boundary-friction output, while `576w3c` remains the preferred checked
early-stroke structural / TFF load-balance baseline.

According to the 577 diagnostics:

```text
577a CHECKED_STATUS=PASS
577b CHECKED_STATUS=PASS
577c CHECKED_STATUS=PASS
577d CHECKED_STATUS=FAIL
577e CHECKED_STATUS=FAIL
577f CHECKED_STATUS=PASS
577g CHECKED_STATUS=PASS
577h CHECKED_STATUS=FAIL
577h2 CHECKED_STATUS=PASS

577a h_avg = 3.000e-6 m exactly over the local film region
577b h_avg range = 2.936e-6 to 3.098e-6 m
577c mu_TFF_alt max = 0.0020777
577c mu_total max =
  0.010095 for mu_boundary=0.02
  0.023418 for mu_boundary=0.05
  0.045622 for mu_boundary=0.10
  0.090030 for mu_boundary=0.20
577f load-sharing mu_total max = about 0.0887 to 0.0904
577g asperity-proxy mu_total max = 0.116789
577h first small scan PASS_COUNT = 0/24
577h2 refined scan STRONG_PASS_COUNT = 53/180
577h2 selected candidate:
  dh_deplete = 2.5 um
  h_crit = 1.0 um
  K_asp_eff = 7.5 kPa
  mu_boundary = 0.20
  mu_total_max = 0.0884728
  A_close/A_film_mean = 0.0739728
  Fn_asp/Fn_ref_max = 1.00473
```

Important limitation:

- `577b`, `577c`, `577f`, and `577g` are postprocessing diagnostics.
- They do not feed depleted film height or boundary friction back into the TFF
  PDE or the solid/contact mechanics.
- `577d` failed the monotonic-with-`dh_deplete` criterion.
- `577e` failed because direct depleted film height in `ffp1.hw1` was too slow.
- `577h` failed because no first-scan parameter set satisfied area/load/COF
  filters simultaneously.
- `577h2` passed by lowering `K_asp_eff` and increasing `mu_boundary`, reducing
  the best candidate `Fn_asp/Fn_ref_max` to about `1.00`.
- The next physical upgrade should only be attempted after preserving these
  checked files as references.

## Latest Early-Stroke Structural/TFF Baseline

### Model

```text
576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph
```

### Companion files

```text
576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md
build_stage576w3c_recursive_split005_film_height_release_extended.java
verify_stage576w3c_checked.java
```

### Current conclusion

According to `576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md`
and `verify_stage576w3c_checked.java`:

- both early split segments passed,
- explicit indentation release plus explicit film-height release closed the
  load to the `0.03 N` target window,
- the checked model was independently read back from saved solutions
  `sol271`, `sol272`, and `sol273`.

Verified final values:

```text
F_contact       = 0.0196747609743 N
F_film          = 0.0132800398588 N
F_total         = 0.0329548008331 N
load error      = 0.00295480083306 N
field residual  = 0.000726197089437 N
min(theta)      = 0.999997361935
VERIFY_STATUS   = PASS
```

Earlier negative branch information remains useful:

```text
576u2: alpha=0.20 made the second segment worse.
576u3: beta=0.10 did not improve the second segment.
576v: finer 1.25% segments failed at the second segment.
576w/w2/w3: solid-only or insufficient film-height release did not close load.
```

The next main-line test should extend the verified `576w3c` mechanism rather
than retune `alpha`, `beta`, or segment size immediately.

## Latest Explicitly Verified Checked Result

### Model

```text
576n12_stage576_full_dynamic_recursive_checked.mph
```

### Companion files

```text
576n12_stage576_full_dynamic_recursive_checked.md
verify_stage576n12_checked.java
build_stage576m_recursive_field_relaxation.java
```

### What it is

This is the older Stage 576 result in the repository that is both:

- marked as checked in the note, and
- paired with a dedicated verification script.

### Reported acceptance state

From `576n12_stage576_full_dynamic_recursive_checked.md`, the full dynamic path
reached fraction `1.0000` with:

```text
alpha_pfb576m = 0.20
beta_relax576m = 0.10
```

Reported final checks include:

```text
F_contact       = 0.0253440421858 N
F_film          = 2.91827459255e-08 N
F_total         = 0.0253440713686 N
field residual  = 9.16350621805e-06 N
min(theta)      = 0.999999999712
all values finite = true
```

and the note marks the acceptance checks as PASS. It remains the latest
verified full-path reference, while `576w3c` is the latest verified early-stroke
split milestone.

## Which Model To Use For Guidance

If you want help deciding the next research or debugging step, tell the model:

- the latest Stage 577 postprocessing diagnostic is `577c...`,
- the latest verified early-stroke split milestone is `576w3c...checked`, and
- the latest verified full-path baseline is `576n12...checked`.

That gives the model both:

- the newest unsuccessful or inconclusive branch state, and
- the newest trusted validated reference point.

## Recommended Prompt For Another ChatGPT/Codex Session

```text
Do not stop at repository metadata.

Use LATEST_MODEL.md as the entry point:
https://github.com/LYC-WASABI/cornea/blob/main/LATEST_MODEL.md

Then read these files:
1. CONTEXT.md
2. 576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md
3. build_stage576w3c_recursive_split005_film_height_release_extended.java
4. verify_stage576w3c_checked.java
5. 576n12_stage576_full_dynamic_recursive_checked.md
6. verify_stage576n12_checked.java

Interpret 576w3c as the latest verified early-stroke split milestone and
576n12_checked as the older full-path verified milestone.

After reading them, tell me:
1. what the current latest attempt is,
2. why 576w3c is now preferred over 576u/u2/u3/v/w/w2/w3,
3. what the last full-path validated reference state is,
4. what the next most defensible modeling step should be.
```
