# Next Tasks

## Current priority

Preserve the Stage 577 result chain with explicit PASS/FAIL status:

```text
577a: conserved 3 um local TFF check                         PASS
577b: conserved-film depletion / low-film activation          PASS
577c: mixed-lubrication boundary-friction postprocess         PASS
577d: postprocess calibration / sensitivity                   FAIL
577e: weakly coupled depleted TFF                             FAIL
577f: load-sharing boundary pressure                          PASS
577g: contact-pressure probe / asperity-pressure proxy        PASS
577h: asperity calibration small scan                         FAIL
577h2: refined asperity calibration                           PASS
```

Use `577h2_stage577_asperity_calibration_refined_results.mph` as the newest checked postprocessing calibration result. Use `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph` only as the structural/TFF baseline, not as the final physical film-thickness model.

## Current checked calibration

Preferred Stage 577h2 candidate:

```text
dh_deplete = 2.5 um
h_crit = 1.0 um
K_asp_eff = 7.5 kPa
mu_boundary = 0.20
mu_total_max = 0.0884728
A_close/A_film_mean = 0.0739728
Fn_asp/Fn_ref_max = 1.00473
PARAM_SCORE = 0.3802765
PASS_LEVEL = STRONG_PASS
```

This candidate keeps the useful friction scale from 577h while reducing the asperity normal-load proxy from about `4.02*Fn_ref` to about `1.00*Fn_ref`.

## Recommended next modeling action

Recommended default:

```text
Stage 577i: fixed-parameter paper-output postprocess
base model: 577h2_stage577_asperity_calibration_refined_results.mph
base script: build_stage577h2_asperity_calibration_refined.java
goal: generate time curves, scalar summaries, and spatial plots using the selected 577h2 parameter set
```

Fixed parameters:

```text
dh_deplete = 2.5 um
h_crit = 1.0 um
K_asp_eff = 7.5 kPa
mu_boundary = 0.20
h_min = 0.05 um
eps_h = 0.1 um
Fn_ref = 0.03 N
```

Required time outputs:

```text
mu_TFF_alt(t)
mu_total(t)
Ft_fluid(t)
Ft_asp(t)
Ft_total(t)
A_close(t)
Fn_asp(t)
theta_min(t)
pfilm_max(t)
h_eff_min(t)
```

Required spatial plots:

```text
h_eff
theta
pfilm
w_close
p_asp
tau_tff_signed577a
```

Acceptance:

```text
all outputs finite
Ft_asp and Ft_total reverse sign with reciprocating motion
mu_total remains in the calibrated target range
A_close and Fn_asp remain consistent with 577h2 best candidate
paper-output files clearly state this is postprocessing calibration, not fully coupled mixed lubrication
```

## Fallback

If Stage 577i exposes an inconsistency in the selected fixed parameter set, do not return to blind scanning. Instead create:

```text
Stage 577h3: load-constrained asperity proxy
```

The intended idea is:

```text
shape_asp = w_close*max((h_crit - h_eff)/h_scale, 0)
Fn_boundary_available = max(Fn_ref - Fn_fluid_pos, 0)
p_asp_limited = shape_asp*min(Fn_asp_raw, alpha*Fn_boundary_available)/intop_sweep(shape_asp)
```

This combines the 577f load-sharing idea with the 577g/577h2 asperity spatial shape.

## Do not do yet

- Do not switch to free-surface modeling.
- Do not use direct `solid.Tn` as the accepted boundary pressure from the current TFF-only dataset.
- Do not call `577d`, `577e`, or first-scan `577h` checked.
- Do not present `577f`, `577g`, or `577h2` as fully coupled mixed lubrication. They are postprocessing diagnostics/calibrations.

## Suggested reading before acting

1. `LATEST_MODEL.md`
2. `577h2_stage577_asperity_calibration_refined_diagnostic.md`
3. `577h2_stage577_asperity_calibration_refined_best_params.md`
4. `577h2_stage577_asperity_calibration_refined_summary.csv`
5. `build_stage577h2_asperity_calibration_refined.java`
6. `577h_stage577_asperity_calibration_diagnostic.md`
