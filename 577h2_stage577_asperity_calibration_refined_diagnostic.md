# Stage 577h2 refined asperity calibration

## Input

- Base model: `577g_stage577_contact_or_asperity_boundary_model_results.mph`
- Output model: `577h2_stage577_asperity_calibration_refined_results.mph`
- Script: `build_stage577h2_asperity_calibration_refined.java`
- Solution: `sol274`
- TFF region: `sel_film_swept571`

## Scan

This stage is a postprocessing-only refined asperity-proxy calibration. It does not modify `ffp1.hw1`, does not re-solve TFF, and does not accept direct `solid.Tn`.

```text
dh_deplete = 2.3, 2.5, 2.8 um
h_crit = 0.7, 0.8, 0.9, 1.0 um
K_asp_eff = 5, 7.5, 10, 12.5, 15 kPa
mu_boundary = 0.10, 0.15, 0.20
eps_h = 0.1 um
Fn_ref = 0.03 N
```

Formula:

```text
h_eff = max(0.05 um, 3 um - dh_deplete*M_core573)
w_close = M_core573*flc2hs(h_crit - h_eff, 0.1 um)
p_asp = w_close*K_asp_eff*max((h_crit - h_eff)/1 um, 0)
Ft_asp_signed = mu_boundary*intop_sweep(p_asp*v_sign)
mu_total = abs(Ft_fluid_signed + Ft_asp_signed)/Fn_ref
```

## Results

```text
TIME_RANGE=[0.0100000000000,0.137059969545] COUNT=41
A_FILM=8.75712633587e-05
MU_FLUID_MAX=0.00207771227279
THETA_MIN=0.100145674579
PFILM_MAX=137336.440741
SCAN_COUNT=180
STRONG_PASS_COUNT=53
CANDIDATE_PASS_COUNT=15
CHECK_FINITE=true
CHECK_SIGN_REVERSAL_ANY=true
CHECK_MU_MONOTONIC_WITH_MU_BOUNDARY=true
CHECK_MU_MONOTONIC_WITH_K_ASP=true
CHECKED_STATUS=PASS
```

The CSV grouping is:

```text
STRONG_PASS = 53
FAIL = 127
```

## Best candidates

Top ranked candidate:

```text
dh_deplete = 2.5 um
h_crit = 1.0 um
K_asp_eff = 7.5 kPa
mu_boundary = 0.20
mu_total_max = 0.0884727697316
A_close/A_film_mean = 0.0739728232305
Fn_asp/Fn_ref_max = 1.00473247302
PARAM_SCORE = 0.380276543401
PASS_LEVEL = STRONG_PASS
```

Other top candidates:

```text
dh_deplete = 2.8 um, h_crit = 0.7 um, K_asp_eff = 7.5 kPa, mu_boundary = 0.20
mu_total_max = 0.0883790355293
A_close/A_film_mean = 0.0737991680286
Fn_asp/Fn_ref_max = 1.00334413792

dh_deplete = 2.3 um, h_crit = 1.0 um, K_asp_eff = 12.5 kPa, mu_boundary = 0.20
mu_total_max = 0.0880869662684
A_close/A_film_mean = 0.0734592707904
Fn_asp/Fn_ref_max = 1.00141860368
```

## Interpretation

`577h2` is checked as `PASS` for refined postprocessing calibration. Lowering `K_asp_eff` while increasing `mu_boundary` fixed the main 577h failure mode: the best candidates keep `mu_total` in the target range while reducing `Fn_asp/Fn_ref_max` from about `4.02` to about `1.00`.

The best candidate matches the expected tradeoff:

```text
K_asp_eff = 7.5 kPa
mu_boundary = 0.20
```

This keeps roughly the same friction scale as the failed 577h case with:

```text
K_asp_eff = 30 kPa
mu_boundary = 0.05
```

but it reduces the asperity normal-load proxy to a physically more defensible range.

## Status

`577h2` may be used as the current checked asperity-calibration result. The next recommended stage is `577i`: fix the selected candidate parameters and generate paper-facing time curves and spatial plots.
