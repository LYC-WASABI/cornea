# Stage 576a load-closure reboot diagnostic

Base: `575d_stage575_dynamic_active_gap_regularized_checked.mph`

Status: **not accepted as checked**.

## What Was Run

1. Nominal midpoint closure:
   - `576a_stage576_midpoint_feedback_closure_setup.mph`
   - `576a_stage576_midpoint_feedback_closure_results.mph`
   - Rejected because `MeanCore = 0`, so `F_film = 0`.

2. Active-time probe from Stage 575d:
   - `probe_stage576a_active_time.java`
   - Selected high-load active time:
     - `t_active576a = 0.0690828858385121 s`
     - `MeanCore = 0.876131460654`
     - dynamic-reference `F_film = 0.0140363883180 N`
   - Also identified max-core time:
     - `t = 0.0735299847725936 s`
     - `MeanCore = 0.990206529065`

3. High-load active-time one-way pressure-feedback scans:
   - `576a_stage576_active_time_feedback_closure_setup.mph`
   - `576a_stage576_active_time_feedback_closure_results.mph`
   - `576a_stage576_active_time_feedback_closure_refined_results.mph`
   - `576a_stage576_active_time_feedback_closure_branch2_results.mph`
   - `576a_stage576_active_time_feedback_closure_branch3_results.mph`
   - `576a_stage576_active_time_feedback_closure_branch4_results.mph`
   - `576a_stage576_active_time_feedback_closure_branch5_results.mph`

4. Max-core representative time scan:
   - `576a_stage576_load_closed_maxcore_setup.mph`
   - `576a_stage576_load_closed_maxcore_results.mph`
   - Rejected because stationary TFF pressure fully vented and `F_film = 0`.

## Best High-Load Active-Time Result

| branch | alpha_pfb576a | F_contact (N) | F_film (N) | F_total (N) | MeanCore | min(theta) |
|---|---:|---:|---:|---:|---:|---:|
| branch3 | 0.183 | 0.0269671608189 | 0.00891708899042 | 0.0358842498093 | 0.876215820738 | 0.999780086962 |

Target was:

```text
F_contact + F_film = 0.03 +/- 0.005 N
```

The best accepted-physics state is therefore still high by:

```text
0.0358842498093 - 0.035 = 0.0008842498093 N
```

It was not saved as checked.

## Why This Did Not Pass

- The active film region and pressure field are finite and stable.
- The one-way pressure feedback changes the contact and film load, but the response is not monotonic near `alpha_pfb576a = 0.18`.
- Further tiny alpha scans alternate between lower-load and higher-load states instead of smoothly converging to `0.03 N`.
- The max-core time gives good mask coverage, but stationary pressure becomes zero, so it does not validate shared film/contact load.

## Decision

Do not enter Stage 577 with these results as a checked load-closure base.

The best usable intermediate state is:

```text
576a_stage576_active_time_feedback_closure_branch3_results.mph
alpha_pfb576a = 0.183
solid = sol461
tff = sol480
F_total = 0.0358842498093 N
```

Use it only as a diagnostic or warm start, not as a checked closure.

## Recommended Next Correction

Stop alpha-only one-way pressure feedback scans at this stage. The next attempt should add an outer scalar load controller:

```text
q_release576a or d_release576a
```

that directly adjusts lid indentation/release while solving:

```text
F_contact + F_film - 0.03[N] = 0
```

Then keep `alpha_pfb576a` fixed at a stable low value, for example:

```text
alpha_pfb576a = 0.16 to 0.183
```

This separates the two roles:

- `alpha_pfb576a`: pressure-to-structure feedback strength.
- `q_release576a`: total normal-load closure.

That is more robust than trying to force total load closure using pressure-feedback alpha alone.
