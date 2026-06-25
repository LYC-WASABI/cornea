# Stage 574q: One-Way Pressure Feedback Diagnostic

## Purpose

Introduce a one-way structural correction from the fixed-structure true-gap JFO pressure:

```text
source pressure = withsol('sol138', p_load573)
structural load = -alpha_pfb574q * source pressure * n
target = Fn_contact570 + intop_film(p_load573) ~= 0.03 N
```

This is still not monolithic coupling. It is a one-way pressure-feedback/load-release diagnostic.

## Base Model

```text
574o_stage574_fixed_structure_true_gap_from_003N_checked.mph
```

## Output Files

```text
build_stage574q_one_way_pressure_feedback.java
574q_stage574_one_way_pressure_feedback_setup.mph
574q_stage574_one_way_pressure_feedback_results.mph
574q_stage574_one_way_pressure_feedback_failed.md
```

No checked model was saved.

## Implementation

A new solid boundary load was created:

```text
load_pfilm574q
selection = sel_local_cornea_patch574
FperArea =
  -p_feedback574q*nx
  -p_feedback574q*ny
  -p_feedback574q*nz
```

with

```text
p_feedback574q = alpha_pfb574q * withsol(previous_tff_solution, p_load573)
```

Each accepted feedback level used:

```text
1. solid-only solve with pressure feedback
2. true-gap/rupture/vent TFF velocity continuation to lambda_v574 = 1
```

## Stable Results

| alpha_pfb574q | F_contact [N] | feedback load applied [N] | F_film after TFF [N] | F_total [N] | positive pressure integral [N] | max pressure [Pa] | mean Bfilm | mean theta | min theta | min gap |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| 0.05 | 0.0282970489552 | 0.00108268208283 | 0.0236518008101 | 0.0519488497653 | 0.233672848843 | 2838776.06663 | 0.381450801908 | 0.992365345705 | 0.661430610513 | -6.37771145581e-05 |
| 0.10 | 0.0270475557073 | 0.00236518008101 | 0.0144726457994 | 0.0415202015067 | 0.218484777790 | 3622402.32264 | 0.422810911528 | 0.992320666338 | 0.662096556649 | -6.38121550247e-05 |

## Failed Points

### alpha_pfb574q = 0.15

Failed during TFF velocity continuation:

```text
lambda_v574 = 0.25
comp1.pfilm produced NaN/Inf in 12 degrees of freedom.
```

### alpha_pfb574q = 0.12

A narrow follow-up run also failed:

```text
lambda_v574 = 0.25
MUMPS reported NaN or Inf in the linear system.
```

## Interpretation

The feedback direction is partly useful:

```text
alpha 0.05 -> 0.10:
F_contact decreases from 0.02830 N to 0.02705 N
F_film decreases from 0.02365 N to 0.01447 N
F_total decreases from 0.05195 N to 0.04152 N
```

However, the target was not reached:

```text
best stable F_total = 0.04152 N
target F_total = 0.03 N
error = +0.01152 N
```

Increasing feedback beyond about `alpha = 0.10` destabilizes the true-gap TFF solve before full speed.

## Status

```text
FAIL
```

Reason:

```text
One-way pressure feedback reduced total load but did not reach 0.03 N.
The next feedback levels became numerically unstable before lambda_v574 = 1.
```

## Recommended Next Step

The limiting issue is now local TFF stability after pressure-induced structural shape changes, not simple contact preload.

Recommended Stage 574r:

```text
add active-gap upper clipping / high-gap deactivation:
  h_active_max573 = 30[um] or 50[um]
  B_high573 = 0.5*(1-tanh((g_pair_safe573-h_active_max573)/dh_active573))
  Bfilm573 = g_pair_valid573*B_low573*B_high573
  g_pair_physical573 = min(g_pair_safe573,h_active_max573)

then rerun one-way feedback from alpha = 0.10 upward
```

This is the same regularization previously identified by the 574j gap/pressure-location diagnostic. It should be introduced before stronger pressure feedback or monolithic coupling.
