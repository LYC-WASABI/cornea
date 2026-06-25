# Stage 576a: Load-Controller Closure Checked

Base:

```text
575d_stage575_dynamic_active_gap_regularized_checked.mph
```

Purpose:

Restart load closure after Stage 575d by separating the two controls:

```text
alpha_pfb576a = pressure-feedback strength
q_scale574 = scalar indentation/release load controller
```

## Setup

Active closure time:

```text
t_active576a = 0.0690828858385121[s]
```

This time was selected from the Stage 575d dynamic solution because the local patch is actively covered and has nonzero film load.

Fixed settings:

```text
alpha_pfb576a = 0.183
lambda_h574 = 1
lambda_v574 = 1 after velocity continuation
h_active_max573 = 50[um]
dh_active573 = 5[um]
M_core573 = M_lid572
M_drain573 = M_lid_x572*M_drain_a573
M_open573 = max(1-M_drain573,0)
```

The prescribed lid displacement remains:

```text
U0 = {
  0,
 -q_scale574*q_fixed574*1[mm]*Y/sqrt(Y^2+Z^2),
 -q_scale574*q_fixed574*1[mm]*Z/sqrt(Y^2+Z^2)
}
```

## Output Files

```text
build_stage576a_load_controller_closure.java
576a_stage576_load_controller_closure_setup.mph
576a_stage576_load_controller_closure_results.mph
576a_stage576_load_controller_closure_checked.mph
576a_stage576_load_controller_closure_checked.md
```

## Scan Summary

| q_scale574 | F_contact (N) | F_film (N) | F_total (N) | max pressure (Pa) | MeanCore | min(theta) | status |
|---:|---:|---:|---:|---:|---:|---:|---|
| -7.5 | 0.0285716205813 | 0.0129381433821 | 0.0415097639634 | 649187.458097 | 0.876131460654 | 0.999772203105 | high load |
| -8.5 | 0.0251953634402 | 0.00986726709157 | 0.0350626305317 | 666811.749318 | 0.875950964347 | 0.999785105255 | slightly high |
| -9.5 | 0.0237224795341 | 0.0167968405713 | 0.0405193201055 | 674551.817734 | 0.875577780456 | 0.999794963758 | high load |
| -10.0 | 0.0222318631842 | 0.00986145651181 | 0.0320933196960 | 696912.848945 | 0.875790299638 | 0.999818386280 | PASS |
| -11.0 | failed | failed | failed | failed | failed | failed | TFF NaN/Inf |

## Selected State

```text
q_scale574 = -10.0
alpha_pfb576a = 0.183
F_contact = 0.0222318631842 N
F_film = 0.00986145651181 N
F_total = 0.0320933196960 N
target = 0.03 N
error = +0.00209331969605 N
```

Associated solution tags in the checked model:

```text
solid = sol201
tff = sol220
```

## Acceptance

Status: **PASS**

Criteria:

```text
F_contact + F_film = 0.03 +/- 0.005 N
F_film > 0
MeanCore > 0.1
min(theta) >= 0
pressure finite
gap finite
```

Notes:

- `q=-11.0` failed during the velocity continuation with NaN/Inf in `pfilm`, so it marks the over-release stability boundary.
- The checked model is a one-way load-closure restart, not full two-way coupled FSI.
- This is suitable as the next Stage 576 base. It is not yet Stage 577.
