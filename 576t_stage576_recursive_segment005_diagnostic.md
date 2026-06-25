# Stage 576t recursive segment 0-5% diagnostic

## Status

The in-segment recursive relaxed-feedback workflow runs and converges
numerically for the `0 -> 5%` segment, but it does not yet achieve physical
load closure.

Artifacts:

```text
576t_stage576_recursive_segment005_setup.mph
576t_stage576_recursive_segment005_checkpoint.mph
576t_stage576_recursive_segment005_results.mph
build_stage576t_recursive_segment_005.java
```

## Settings

```text
Base: 576p2r_stage576_moving_structure_sparse_jfo_results.mph
segment: 0 -> 5%
alpha_pfb576t = 0.02
beta_relax576t = 0.15
max inner iterations = 6
physical exterior TFF pressure boundaries
wc_open_anchor573 = off
```

## Iteration history

```text
iter 1:
Fcontact = 0.0256664 N
Ffilm    = 0.0401065 N
Ffeedback= 0.000120329 N
Ftotal   = 0.0657729 N
MaxP     = 106.4 kPa
MinTheta = 0.999901
MinGap   = -62.83 um

iter 2:
Fcontact = 0.0253468 N
Ffilm    = 0.0535464 N
Ffeedback= 0.000251421 N
Ftotal   = 0.0788931 N
MaxP     = 70.48 kPa
MinTheta = 0.999947
MinGap   = -62.52 um

iter 3:
Fcontact = 0.0252867 N
Ffilm    = 0.0532114 N
Ffeedback= 0.000361907 N
Ftotal   = 0.0784981 N
MaxP     = 68.72 kPa
MinTheta = 0.999947
MinGap   = -62.30 um
```

## Interpretation

- The recursive relaxed-field mechanism is working.
- The iteration stabilizes quickly instead of blowing up.
- But the relaxed feedback magnitude is still much too small relative to the
  film load.
- Therefore this stage is a numerical-method pass, not a physical-load pass.

## Next action

Keep the same Stage 576t structure and continue by increasing only the
effective relaxed feedback strength, not by going back to direct one-shot
feedback.

Recommended next sweep:

```text
alpha_pfb576t = 0.05, 0.10, 0.15
beta_relax576t fixed at 0.15 first
still only on the 0 -> 5% segment
```

Acceptance for the next round:

```text
Ftotal trends toward 0.03 N
Ffilm decreases rather than growing across inner iterations
MaxP stays finite
MinTheta stays >= 0
MinGap does not show new penetration growth
```
