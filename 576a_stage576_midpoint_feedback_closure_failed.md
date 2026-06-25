# Stage 576a midpoint feedback closure diagnostic

Base: `575d_stage575_dynamic_active_gap_regularized_checked.mph`

Attempted output:

- `576a_stage576_midpoint_feedback_closure_setup.mph`
- `576a_stage576_midpoint_feedback_closure_results.mph`

Status: **not accepted as checked**.

Reason:

- The nominal midpoint setting used `tau572 = T_pre572 + 0.5*T_slide572`.
- At that frozen time the local patch selection had `MeanCore = 0`.
- Therefore `p_load573 = M_core573*Bfilm573*(tff.p-p_amb573)` integrated to `F_film = 0`.
- The apparent total load was near the target only because it reduced to the frozen contact load, not because contact plus film load closed physically.

Representative values from the run:

| alpha_pfb576a | F_contact (N) | F_film (N) | F_total (N) | MeanCore |
|---:|---:|---:|---:|---:|
| 0 | 0.0285716 | 0 | 0.0285716 | 0 |
| 0.05 | 0.0285883 | 0 | 0.0285883 | 0 |
| 0.08-0.16 | ~0.0285883 | 0 | ~0.0285883 | 0 |

Decision:

Do not save `576a_stage576_midpoint_feedback_closure_checked.mph` from this run. Rerun Stage 576a at an active time selected from the Stage 575d dynamic solution, preferably where the local patch has high `M_core573` coverage and nonzero film load.
