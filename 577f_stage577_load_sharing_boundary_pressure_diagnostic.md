# Stage 577f load-sharing boundary pressure

## Input

- Base model: `577b_stage577_conserved_depletion_rupture_check_results.mph`
- Output model: `577f_stage577_load_sharing_boundary_pressure_results.mph`
- Script: `build_stage577f_load_sharing_boundary_pressure.java`
- Solution: `sol274`
- TFF region: `sel_film_swept571`

## Definition

This stage replaces the 577c/577d full-load boundary pressure proxy with a load-sharing proxy:

```text
Fn_fluid_pos = intop_sweep(max(tff.p - p_amb573, 0))
Fn_boundary = max(Fn_ref - Fn_fluid_pos, 0)
A_close = intop_sweep(w_close)
p_boundary = w_close*Fn_boundary/max(A_close, 1e-12 m^2)
Ft_boundary = intop_sweep(mu_boundary*p_boundary*v_sign)
mu_total = abs(Ft_fluid + Ft_boundary)/Fn_ref
```

Scan:

```text
dh_deplete = 2.0, 2.5, 2.8 um
mu_boundary = 0.02, 0.05, 0.10, 0.15, 0.20
Fn_ref = 0.03 N
```

## Results

```text
TIME_RANGE=[0.0100000000000,0.137059969545] COUNT=41
A_FILM=8.75712633587e-05
FN_FLUID_POS_RANGE=[1.28261448294e-15,0.119707027299]
FN_BOUNDARY_RANGE=[0.00000000000,0.0300000000000]
MU_FLUID_RANGE=[0.00000000000,0.00207771227279]

dh=2.0, A_close/A_film max=0.045776, mu_total max=0.090359 at mu_boundary=0.20
dh=2.5, A_close/A_film max=0.094054, mu_total max=0.089209 at mu_boundary=0.20
dh=2.8, A_close/A_film max=0.097466, mu_total max=0.088747 at mu_boundary=0.20

CHECK_FINITE=true
CHECK_LOCAL_TFF=true
CHECK_FN_BOUNDARY_NONNEGATIVE=true
CHECK_BOUNDARY_SIGN_REVERSAL=true
CHECK_MU_MONOTONIC_WITH_MU_BOUNDARY=true
CHECK_MU_TOTAL_GT_MU_FLUID_ANY=true
CHECK_HAS_TARGET_CANDIDATE=true
CHECKED_STATUS=PASS
```

## Interpretation

`577f` is checked as `PASS` for a load-sharing boundary-pressure postprocess.

The fluid positive pressure can exceed `Fn_ref` at some time points, so `Fn_boundary` correctly clips to zero there. At low fluid load time points, the remaining load is assigned to the low-film area. The resulting `mu_total` remains in the same useful range as 577d, but with a clearer physical interpretation than assigning all `Fn_ref` to `A_close`.
