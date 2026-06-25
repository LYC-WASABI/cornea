# Stage 575a: Dynamic Fixed-Patch JFO Cavitation

## Purpose

Run a complete dynamic scrape interval with fixed structure, true gap, rupture, and venting JFO.

## Base

```text
574o_stage574_fixed_structure_true_gap_from_003N_checked.mph
```

Initial solution:

```text
sol119
```

This is the true-gap, zero-velocity, ambient-pressure state from Stage 574o.

## Settings

```text
solid = off
ge_force_total111 = off
tff = on
lambda_h574 = 1
lambda_v574 = 1
M_core573 = 1
M_drain573 = 1
M_open573 = 0
tau572 = t
```

Time range:

```text
range(T_pre572,T_slide572/200,T_pre572+T_slide572)
```

## Results

```text
T_slide572 = 0.0635299847725936
time range = 0.0100000000000 .. 0.0735299847726
time steps = 201
omega mean range = -30.2076216691 .. 0 rad/s
F_film range = 1.26541139585e-07 .. 0.0217279742752 N
max pressure range = 7.49536218239 .. 2847004.56411 Pa
min theta range = 0.662018658023 .. 0.999999995292
mean theta range = 0.992379866256 .. 0.999999999870
mean Bfilm573 range = 0.372839530206 .. 0.372839530206
mean h_calc573 range = 1.28144547549e-05 .. 1.28144547549e-05
```

## Status

```text
PASS
```

The complete fixed-patch dynamic JFO scrape interval converged. This is a diagnostic dynamic result, not a load-closed or two-way coupled result.

## Outputs

```text
575a_stage575_dynamic_fixed_patch_jfo_setup.mph
575a_stage575_dynamic_fixed_patch_jfo_results.mph
575a_stage575_dynamic_fixed_patch_jfo_checked.mph
575a_stage575_dynamic_fixed_patch_jfo_checked.md
```
