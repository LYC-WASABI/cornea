# Stage 576t2 recursive segment 0-5% alpha sweep diagnostic

## Status

The `0 -> 5%` in-segment recursive relaxed-feedback method remains numerically
stable while `alpha_pfb576t2` is increased from `0.05` to `0.15`. The load
closure trend improves monotonically, but the total load is still above the
`0.03 N` target after 6 inner iterations.

Artifacts:

```text
576t2_stage576_recursive_segment005_alpha_sweep_setup.mph
576t2_stage576_recursive_segment005_alpha_sweep_checkpoint.mph
576t2_stage576_recursive_segment005_alpha_sweep_results.mph
build_stage576t_recursive_segment_005_alpha_sweep.java
```

## Fixed settings

```text
segment: 0 -> 5%
beta_relax576t2 = 0.15
max inner iterations = 6
physical exterior TFF pressure boundaries
wc_open_anchor573 = off
```

## Final results by alpha

```text
alpha = 0.05:
Fcontact = 0.0247043 N
Ffilm    = 0.0475413 N
Ffeedback= 0.00143821 N
Ftotal   = 0.0722456 N
MaxP     = 41.03 kPa
MinTheta = 0.9999469
MinGap   = -59.49 um

alpha = 0.10:
Fcontact = 0.0241060 N
Ffilm    = 0.0377147 N
Ffeedback= 0.00254966 N
Ftotal   = 0.0618207 N
MaxP     = 31.95 kPa
MinTheta = 0.9999468
MinGap   = -57.51 um

alpha = 0.15:
Fcontact = 0.0237392 N
Ffilm    = 0.0293005 N
Ffeedback= 0.00326997 N
Ftotal   = 0.0530397 N
MaxP     = 30.24 kPa
MinTheta = 0.9999466
MinGap   = -56.36 um
```

## Interpretation

- Increasing recursive feedback strength is the correct direction.
- The method does not blow up when alpha is increased to `0.15`.
- `Ffilm` drops substantially as alpha rises:

  ```text
  0.0475 N -> 0.0377 N -> 0.0293 N
  ```

- `Ftotal` also drops monotonically:

  ```text
  0.0722 N -> 0.0618 N -> 0.0530 N
  ```

- However, even the best tested case is still above the `0.03 N` target.
- Pressure and cavitation remain numerically well behaved in this range.

## Recommended next action

Do not extend to `5 -> 10%` yet.

Next main-line test:

```text
keep alpha_pfb in the stronger range
split 0 -> 5% into 0 -> 2.5% and 2.5% -> 5%
start with alpha = 0.15
optionally test alpha = 0.20 if 2.5% remains stable
```

Reason:

```text
the method is stable
stronger feedback is helping
remaining defect is insufficient local gap release early in the segment
smaller segment length is now more justified than changing the whole method again
```
