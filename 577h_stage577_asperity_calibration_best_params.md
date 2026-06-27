# Stage 577h asperity calibration best parameters

```text
SCAN_COUNT=24
PASS_COUNT=0
CHECK_FINITE=true
CHECK_SIGN_REVERSAL_ANY=true
CHECK_MU_MONOTONIC_WITH_MU_BOUNDARY=true
CHECK_MU_MONOTONIC_WITH_K_ASP=true
CHECKED_STATUS=FAIL
```

## Recommended candidates

- No passing candidate in the first small scan.

## Closest diagnostic case

The closest COF-range case was:

```text
dh_deplete = 2.5 um
h_crit = 1.0 um
K_asp_eff = 30 kPa
mu_boundary = 0.05
mu_total_max = 0.0884727697316
A_close/A_film_mean = 0.0739728232305
Fn_asp/Fn_ref_max = 4.01892989209
FAIL_REASON = LOAD_FAIL
```

It reaches the target friction range and has a reasonable low-film activation area, but the asperity normal load proxy is too large relative to `Fn_ref`.
