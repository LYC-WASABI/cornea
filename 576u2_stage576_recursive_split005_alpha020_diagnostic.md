# Stage 576u2 recursive split 0-2.5%-5% alpha020 diagnostic

## Status

Increasing the split-segment recursive feedback strength from `alpha=0.15` to
`alpha=0.20` does not improve the early load state. The second `2.5% -> 5%`
segment becomes worse than the `alpha=0.15` case.

Artifacts:

```text
576u2_stage576_recursive_split005_alpha020_setup.mph
576u2_stage576_recursive_split005_alpha020_checkpoint.mph
576u2_stage576_recursive_split005_alpha020_results.mph
build_stage576u2_recursive_split_segment_005_alpha020.java
```

## Settings

```text
alpha_pfb576u2 = 0.20
beta_relax576u2 = 0.15
segments:
0 -> 2.5%
2.5% -> 5%
max inner iterations per segment = 6
```

## Results

```text
segment 1: 0 -> 2.5%
final iter = 3
Fcontact = 0.0270102 N
Ffilm    = 0.0217744 N
Ffeedback= 0.00162023 N
Ftotal   = 0.0487846 N
MaxP     = 57.56 kPa
MinTheta = 0.9999709
MinGap   = -57.08 um

segment 2: 2.5% -> 5%
final iter = 5
Fcontact = 0.0226552 N
Ffilm    = 0.0418879 N
Ffeedback= 0.00578149 N
Ftotal   = 0.0645431 N
MaxP     = 29.60 kPa
MinTheta = 0.9999466
MinGap   = -56.09 um
```

## Comparison against alpha = 0.15

```text
alpha = 0.15, segment 2 final Ftotal = 0.0525862 N
alpha = 0.20, segment 2 final Ftotal = 0.0645431 N
```

So the stronger feedback does not continue the monotonic improvement after the
segment split. This indicates that the current issue is no longer "feedback is
too weak" in a simple sense.

## Interpretation

- The split-segment method remains numerically stable.
- But `alpha=0.20` over-corrects the coupled early state and worsens the
  second segment's load closure.
- The best tested setting so far for the split path remains:

  ```text
  alpha = 0.15
  beta = 0.15
  segments = 0 -> 2.5% -> 5%
  ```

## Recommended next action

Do not increase alpha further on this split setup.

Next main-line test:

```text
return to alpha = 0.15
keep the split segments
reduce beta_relax from 0.15 to 0.10
or equivalently add more conservative relaxed-field inheritance in segment 2
```

Reason:

```text
alpha=0.20 worsened the second segment
the method is stable, so the next lever should be gentler relaxed-field update,
not stronger direct pressure injection
```
