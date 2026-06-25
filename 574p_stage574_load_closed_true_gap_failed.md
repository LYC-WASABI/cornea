# Stage 574p: Fixed-Structure True-Gap Load-Closure Scan

## Purpose

Attempt to close the total normal support under fixed-structure true-gap JFO:

```text
F_total = Fn_contact570 + intop_film(p_load573) ~= 0.03 N
```

Each q step was intended to run:

```text
1. solid-only contact solve at q_scale574
2. fixed-structure true-gap/rupture/vent TFF solve on that new structural state
```

## Base Model

```text
574o_stage574_fixed_structure_true_gap_from_003N_checked.mph
```

## Output Files

```text
build_stage574p_load_closed_true_gap_scan.java
574p_stage574_load_closed_true_gap_setup.mph
574p_stage574_load_closed_true_gap_results.mph
574p_stage574_load_closed_true_gap_failed.md
```

No checked model was saved.

## Successful Diagnostic Points

| q_scale574 | F_contact [N] | F_film [N] | F_total [N] | positive pressure integral [N] | max pressure [Pa] | mean Bfilm | mean theta | min theta | min gap |
|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|
| -7.5 | 0.0285882899058 | 0.0216547010250 | 0.0502429909309 | 0.238794523723 | 2838712.60351 | 0.372839514368 | 0.992365481591 | 0.661437006775 | -6.37672801832e-05 |
| -8.5 | 0.0265986147438 | 0.0281781541782 | 0.0547767689220 | 0.235875160802 | 2829399.67194 | 0.392590484092 | 0.992380560052 | 0.662031890635 | -6.32807021291e-05 |

## Failed Diagnostic Points

### q_scale574 = -9.5

The solid contact solve completed, but TFF failed during velocity continuation at:

```text
lambda_v574 = 0.25
```

Failure:

```text
Undefined value found in comp1.pfilm residual vector.
149 degrees of freedom produced NaN/Inf.
```

### q_scale574 = -6.5

The solid contact solve completed, but TFF failed during velocity continuation at:

```text
lambda_v574 = 0.25
```

Failure:

```text
Maximum Newton iterations reached.
Relative error was above tolerance.
Returned solution was not converged.
```

## Interpretation

The fixed-structure q-scan did not bracket the target 0.03 N load.

At `q=-7.5`, the recalibrated structural contact is near the desired 0.03 N by itself, but the true-gap JFO adds about 0.0217 N:

```text
F_total ~= 0.0502 N
```

Releasing further to `q=-8.5` reduces contact force, but increases active film support:

```text
F_contact: 0.0286 -> 0.0266 N
F_film:    0.0217 -> 0.0282 N
F_total:   0.0502 -> 0.0548 N
```

So the total load moves away from the target instead of toward it.

The neighboring q directions also become numerically unstable before reaching full velocity. This means fixed-structure indentation adjustment alone is not a reliable closure method for this setup.

## Status

```text
FAIL
```

Reason:

```text
No q value reached F_contact + F_film ~= 0.03 N.
Stable full-speed true-gap points remained above target.
Adjacent q values failed during TFF velocity continuation.
```

## Recommended Next Step

Do not keep manually tuning fixed q in this frozen-structure model.

The next useful step is a one-way load-balance iteration:

```text
1. solve solid contact
2. solve true-gap JFO
3. apply p_load573 as a one-way pressure correction or equivalent release load
4. resolve structure
5. repeat until F_contact + F_film approaches 0.03 N
```

This is the first point where pressure-induced structural release should be introduced. The current fixed-structure result is useful as a pressure-field diagnostic, but not as a final load-balanced state.
