# Stage 577d postprocess calibration / sensitivity

## Input

- Base model: `577b_stage577_conserved_depletion_rupture_check_results.mph`
- Output model: `577d_stage577_postprocess_calibration_sensitivity_results.mph`
- Script: `build_stage577d_postprocess_calibration_sensitivity.java`
- Solution: `sol274`
- TFF region: `sel_film_swept571`

## Definition

This stage scans postprocessed low-film activation and boundary friction. It does not re-solve TFF.

```text
h_raw = max(0.05 um, 3 um - dh_deplete*M_core573)
w_close = 0.5*(1+tanh((1 um - h_raw)/0.2 um))
p_boundary = w_close*Fn_ref/max(A_close, 1e-12 m^2)
Ft_boundary = intop_sweep(mu_boundary*p_boundary*v_sign)
mu_total = abs(Ft_fluid + Ft_boundary)/Fn_ref
```

Scan:

```text
dh_deplete = 1.0, 1.5, 2.0, 2.5, 2.8 um
mu_boundary = 0.02, 0.05, 0.10, 0.15, 0.20
Fn_ref = 0.03 N
```

## Key results

```text
A_FILM=8.75712633587e-05
MU_FLUID_RANGE=[0.00000000000,0.00207771227279]

dh=1.0: A_close/A_film max=4.191e-06, mu_total max=0.09015 at mu_boundary=0.20, candidate=false
dh=1.5: A_close/A_film max=6.130e-04, mu_total max=0.09048 at mu_boundary=0.20, candidate=false
dh=2.0: A_close/A_film max=4.578e-02, mu_total max=0.09038 at mu_boundary=0.20, candidate=true
dh=2.5: A_close/A_film max=9.405e-02, mu_total max=0.08923 at mu_boundary=0.20, candidate=true
dh=2.8: A_close/A_film max=9.747e-02, mu_total max=0.08877 at mu_boundary=0.20, candidate=true

CHECK_FINITE=true
CHECK_LOCAL_TFF=true
CHECK_AREA_NONTRIVIAL_ANY=true
CHECK_BOUNDARY_SIGN_REVERSAL=true
CHECK_MU_MONOTONIC_WITH_MU_BOUNDARY=true
CHECK_MU_MONOTONIC_WITH_DH=false
CHECK_HAS_TARGET_CANDIDATE=true
CHECKED_STATUS=FAIL
```

## Interpretation

`577d` is a useful failed diagnostic, not a checked milestone.

The scan found plausible target-COF candidates for `dh_deplete >= 2.0 um`, and `mu_total` is monotonic with `mu_boundary`. However, `mu_total` is not monotonic with `dh_deplete`. The likely reason is the current pressure proxy:

```text
p_boundary = w_close*Fn_ref/A_close
```

As `dh_deplete` increases, `A_close` increases. The normalization spreads the same `Fn_ref` over a larger low-film area, so the peak friction contribution can remain flat or decrease slightly. This is exactly the limitation that Stage 577f is intended to address with load sharing.

## Status

`577d` is marked `FAIL` because it does not satisfy the original monotonic-with-`dh_deplete` acceptance condition. Do not treat this branch as checked.
