# Stage 576v recursive fine 0-1.25%-2.5%-3.75%-5% diagnostic

## Status

Stage 576v is not checked. The finer `1.25%` segment strategy was tested with
the previously best feedback parameters:

```text
alpha_pfb576v = 0.15
beta_relax576v = 0.15
segments = 0 -> 1.25% -> 2.5% -> 3.75% -> 5%
```

The first segment improved toward the target load, but the second segment
became worse and failed the convergence/acceptance criteria. The run stopped
after segment 2, so it did not continue to `3.75%` or `5%`.

Artifacts:

```text
build_stage576v_recursive_fine_segment_005.java
build_stage576v_recursive_fine_segment_005.log
576v_stage576_recursive_fine005_setup.mph
576v_stage576_recursive_fine005_checkpoint.mph
576v_stage576_recursive_fine005_results.mph
```

## Results

Segment 1, `0 -> 1.25%`, converged:

```text
iter 3:
Fcontact = 0.0280043 N
Ffilm    = 0.0100258 N
Ffeedback= 0.000587631 N
Ftotal   = 0.0380302 N
MaxP     = 31.08 kPa
MinTheta = 0.9999728
MinGap   = -62.43 um
```

Segment 2, `1.25% -> 2.5%`, did not pass:

```text
iter 6:
Fcontact = 0.0262712 N
Ffilm    = 0.0358211 N
Ffeedback= 0.00306340 N
Ftotal   = 0.0620923 N
MaxP     = 56.99 kPa
MinTheta = 0.9999710
MinGap   = -57.22 um
```

## Comparison

Previous best split result:

```text
576u, alpha=0.15, beta=0.15
0 -> 2.5% final Ftotal = 0.0471110 N
2.5% -> 5% final Ftotal = 0.0525862 N
```

New fine-segment result:

```text
576v, alpha=0.15, beta=0.15
0 -> 1.25% final Ftotal = 0.0380302 N
1.25% -> 2.5% final Ftotal = 0.0620923 N
```

The first half-step is better, but the second half-step overshoots badly. This
means simple segment refinement is not enough and may amplify pressure-history
inheritance between very short segments.

## Interpretation

- The very first segment is now close to the `0.03 N` load scale.
- The following segment accumulates too much film load despite stable pressure
  and cavitation fields.
- `MaxP`, `MinTheta`, and `MinGap` remain finite, so this is not a gross
  numerical blow-up.
- The remaining problem is pressure-history inheritance and load closure, not
  mask direction or exterior pressure anchoring.

## Next Step

Do not continue the 1.25% segmentation as-is.

Recommended next branch:

```text
return to the 576u two-segment baseline
alpha = 0.15
beta = 0.15
introduce explicit load-control/release on the imposed indentation
```

The reason is that `alpha`, `beta`, and segment size have now all been tested
around the early stroke, and none of those alone brings
`F_contact + F_film` close to `0.03 N`.
