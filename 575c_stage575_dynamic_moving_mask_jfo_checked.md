# Stage 575c: Dynamic Moving-Mask JFO

## Purpose

Restore the moving scrape mask on the dynamic fixed-structure JFO model.

## Base

```text
575a_stage575_dynamic_fixed_patch_jfo_checked.mph
```

## Settings

```text
M_core573 = M_lid572
M_drain573 = M_lid_x572*M_drain_a573
M_open573 = max(1-M_drain573,0)
tau572 = t
solid = off
tff = on
lambda_h574 = 1
lambda_v574 = 1
```

## Results

```text
time range = 0.0100000000000 .. 0.0735299847726
time steps = 201
F_film range = 0 .. 0.0154349750496 N
max pressure range = 29.5880803009 .. 3966673.44506 Pa
min theta range = 0.0896985387283 .. 0.999999995720
mean M_core573 range = 0 .. 0.990206529065
mean M_drain573 range = 0 .. 0.995379357239
mean Bfilm573 range = 0.372839530206 .. 0.372839530206
```

## Status

```text
PASS
```

The moving mask becomes active on the local patch during the scrape interval and the full dynamic JFO solve converges.

## Outputs

```text
575c_stage575_dynamic_moving_mask_jfo_setup.mph
575c_stage575_dynamic_moving_mask_jfo_results.mph
575c_stage575_dynamic_moving_mask_jfo_checked.mph
575c_stage575_dynamic_moving_mask_jfo_checked.md
```
