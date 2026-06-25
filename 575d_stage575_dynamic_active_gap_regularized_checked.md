# Stage 575d: Dynamic Active-Gap Regularized JFO

## Purpose

Add active-gap upper clipping and high-gap deactivation to the moving-mask dynamic JFO model.

## Base

```text
575c_stage575_dynamic_moving_mask_jfo_checked.mph
```

## Settings

```text
h_active_max573 = 50[um]
dh_active573 = 5[um]

B_low573 =
0.5*(1+tanh((g_pair_safe573-h_break573)/dh_break573))

B_high573 =
0.5*(1-tanh((g_pair_safe573-h_active_max573)/dh_active573))

Bfilm573 =
g_pair_valid573*B_low573*B_high573

g_pair_physical573 =
min(g_pair_safe573,h_active_max573)

h_wet573 =
h_num573+0.5*((g_pair_physical573-h_num573)
 +sqrt((g_pair_physical573-h_num573)^2+eps_h_num573^2))
```

The moving mask remains active:

```text
M_core573 = M_lid572
M_drain573 = M_lid_x572*M_drain_a573
M_open573 = max(1-M_drain573,0)
```

## Results

```text
time range = 0.0100000000000 .. 0.0735299847726
time steps = 201
F_film range = 0 .. 0.0294407941504 N
max pressure range = 29.5880803009 .. 3966673.44506 Pa
min theta range = 0.0901296649334 .. 0.999999995720
mean M_core573 range = 0 .. 0.990206529065
mean M_drain573 range = 0 .. 0.995379357239
mean Bfilm573 range = 0.270460541924 .. 0.270460541924
mean B_high573 range = 0.897621010146 .. 0.897621010146
h_calc573 min range = 5.00000392021e-06 .. 0.00300000000000
h_calc573 max range = 0.00300000000000 .. 0.0407086947758
```

## Status

```text
PASS
```

The active-gap regularized dynamic moving-mask JFO solve converged across the full scrape interval.

## Outputs

```text
575d_stage575_dynamic_active_gap_regularized_setup.mph
575d_stage575_dynamic_active_gap_regularized_results.mph
575d_stage575_dynamic_active_gap_regularized_checked.mph
575d_stage575_dynamic_active_gap_regularized_checked.md
```

## Notes

This remains a fixed-structure diagnostic model. It is not load-closed and does not include two-way fluid-structure coupling.
