# Stage 577c mixed lubrication / boundary friction postprocess

## Input

- Base model: `577b_stage577_conserved_depletion_rupture_check_results.mph`
- Output model: `577c_stage577_mixed_lubrication_boundary_friction_results.mph`
- Script: `build_stage577c_mixed_lubrication_boundary_friction.java`
- Solution: `sol274`
- Dataset: `dset577c`
- TFF region: `sel_film_swept571`

## Definition

This stage is a postprocessing mixed-lubrication diagnostic. It does not feed boundary friction back into the structure or TFF solve.

```text
Fn_ref577c = 0.03 N
Ft_fluid_signed577c = intop_sweep(tau_tff_signed577a)
A_close577c = intop_sweep(w_close577c)
p_boundary577c = w_close577c*Fn_ref577c/max(A_close577c, 1e-12 m^2)
v_sign577c = tanh(vtheta_signed/1e-6 m/s)
Ft_boundary_signed577c = intop_sweep(mu_boundary577c*p_boundary577c*v_sign577c)
Ft_total_signed577c = Ft_fluid_signed577c + Ft_boundary_signed577c
mu_total577c = abs(Ft_total_signed577c)/Fn_ref577c
```

The low-film activation uses the 577b `dh_deplete = 2.8 um` checked branch.

## Results

```text
TIME_RANGE=[0.0100000000000,0.137059969545] COUNT=41
THETA_MIN_RANGE=[0.100145674579,0.999997361935]
H_PROXY_RANGE=[0.000200000000000,0.00910400000000]
P_MAX_RANGE=[1.89748329375e-10,137336.440741]
A_CLOSE_RANGE=[1.93616419977e-12,8.36458790333e-06]
FT_FLUID_SIGNED_RANGE=[-6.23313681838e-05,6.23313681838e-05]
MU_TFF_ALT_RANGE=[0.00000000000,0.00207771227279]
MU_BOUNDARY=0.020 FT_BOUNDARY_RANGE=[-0.000266448707808,0.000266448707808] FT_TOTAL_RANGE=[-0.000302856587913,0.000302856587913] MU_TOTAL_RANGE=[0.00000000000,0.0100952195971]
MU_BOUNDARY=0.050 FT_BOUNDARY_RANGE=[-0.000666121769521,0.000666121769521] FT_TOTAL_RANGE=[-0.000702529649625,0.000702529649625] MU_TOTAL_RANGE=[0.00000000000,0.0234176549875]
MU_BOUNDARY=0.100 FT_BOUNDARY_RANGE=[-0.00133224353904,0.00133224353904] FT_TOTAL_RANGE=[-0.00136865141915,0.00136865141915] MU_TOTAL_RANGE=[0.00000000000,0.0456217139716]
MU_BOUNDARY=0.200 FT_BOUNDARY_RANGE=[-0.00266448707808,0.00266448707808] FT_TOTAL_RANGE=[-0.00270089495819,0.00270089495819] MU_TOTAL_RANGE=[0.00000000000,0.0900298319396]
CHECK_FINITE=true
CHECK_LOCAL_TFF=true
CHECK_H_CONSERVATION=true
CHECK_BOUNDARY_FINITE=true
CHECK_BOUNDARY_SIGN_REVERSAL=true
CHECK_MU_TOTAL_GT_MU_TFF=true
CHECK_MU_TOTAL_MONOTONIC=true
CHECK_MU_TOTAL_TARGET_BAND=true
CHECK_THETA_P_H_FINITE=true
CHECKED_STATUS=PASS
```

Note: `H_PROXY_RANGE` uses COMSOL's surface min/max display convention. The 3 um conservation check is inherited from 577b via the area-average film thickness.

## Conclusion

`577c` is checked as `PASS` for the first mixed-lubrication / boundary-friction postprocess:

- Boundary friction reverses sign with the reciprocating motion.
- `mu_total577c` is larger than pure-fluid `mu_TFF_alt`.
- `mu_total577c` increases monotonically with `mu_boundary577c`.
- The resulting maximum `mu_total577c` spans about `0.010-0.090` for `mu_boundary = 0.02-0.20`.
- This is still a diagnostic postprocess, not a fully coupled solid-contact-TFF mixed-lubrication solve.
