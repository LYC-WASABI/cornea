# Stage 576u recursive split 0-2.5%-5% diagnostic

## Status

Splitting the early scratch into two shorter recursive-feedback segments is a
useful improvement. The method remains stable and the first half-segment
becomes much closer to the target load scale.

Artifacts:

```text
576u_stage576_recursive_split005_setup.mph
576u_stage576_recursive_split005_checkpoint.mph
576u_stage576_recursive_split005_results.mph
build_stage576u_recursive_split_segment_005.java
```

## Settings

```text
alpha_pfb576u = 0.15
beta_relax576u = 0.15
segments:
0 -> 2.5%
2.5% -> 5%
max inner iterations per segment = 6
physical exterior TFF pressure boundaries
wc_open_anchor573 = off
```

## Segment results

```text
segment 1: 0 -> 2.5%
final iter = 3
Fcontact = 0.0272306 N
Ffilm    = 0.0198804 N
Ffeedback= 0.00117545 N
Ftotal   = 0.0471110 N
MaxP     = 60.54 kPa
MinTheta = 0.9999709
MinGap   = -57.03 um

segment 2: 2.5% -> 5%
final iter = 5
Fcontact = 0.0235696 N
Ffilm    = 0.0290166 N
Ffeedback= 0.00371678 N
Ftotal   = 0.0525862 N
MaxP     = 30.71 kPa
MinTheta = 0.9999467
MinGap   = -56.64 um
```

## Interpretation

- Segment refinement helps more than keeping `0 -> 5%` as one block.
- The first `0 -> 2.5%` segment is significantly improved and is now only
  about `0.017 N` above the target.
- The second `2.5% -> 5%` segment still remains too high, but it ends slightly
  lower than the previous unsplit `0 -> 5%` alpha-0.15 result.
- Pressure and cavitation remain well behaved; the remaining issue is not
  numerical instability but insufficient load release in the second half of
  the early stroke.

## Recommended next action

Stay on the split-segment path.

Next test:

```text
keep the 0 -> 2.5% -> 5% segmentation
increase alpha_pfb to 0.20
keep beta_relax at 0.15 first
```

Reason:

```text
the split segments are stable
alpha strengthening already showed monotonic improvement
the first half-segment is now close enough that stronger feedback is the next
cleanest lever
```
