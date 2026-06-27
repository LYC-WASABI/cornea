# Stage 577h asperity calibration small scan

## Input

- Base model: `577g_stage577_contact_or_asperity_boundary_model_results.mph`
- Output model: `577h_stage577_asperity_calibration_results.mph`
- Script: `build_stage577h_asperity_calibration.java`
- Solution: `sol274`
- TFF region: `sel_film_swept571`

## Scan

This stage is a postprocessing-only small scan. It does not modify `ffp1.hw1` and does not re-solve the TFF PDE.

```text
dh_deplete = 2.0, 2.5 um
h_crit = 0.5, 1.0 um
K_asp_eff = 30, 100 kPa
mu_boundary = 0.05, 0.10, 0.15
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
SCAN_COUNT=24
PASS_COUNT=0
CHECK_FINITE=true
CHECK_SIGN_REVERSAL_ANY=true
CHECK_MU_MONOTONIC_WITH_MU_BOUNDARY=true
CHECK_MU_MONOTONIC_WITH_K_ASP=true
CHECKED_STATUS=FAIL
```

## Interpretation

`577h` first small scan is a useful failed diagnostic.

What passed:

- All 24 parameter combinations remained finite.
- The asperity friction path can reverse sign.
- `mu_total` is monotonic with `mu_boundary`.
- `mu_total` is monotonic with `K_asp_eff`.

What failed:

- No parameter combination satisfied all filters simultaneously.
- `h_crit = 0.5 um` did not activate asperity pressure in this parameter window.
- `h_crit = 1.0 um` with `dh_deplete = 2.5 um` activated a reasonable area, but `Fn_asp/Fn_ref` was too large.
- The closest useful case reached `mu_total_max = 0.08847`, but had `Fn_asp/Fn_ref_max = 4.02`, so it failed the load-ratio criterion.

## Status

Do not treat 577h as checked. The next adjustment should reduce the asperity load while retaining friction-scale response, for example by scanning lower `K_asp_eff` values or using a load-normalized asperity pressure cap.
