# Next Tasks

## Current priority

The current checked forward diagnostic endpoint is now:

```text
576w5n: 87.5% -> 100% lower-DN extension                  DIAGNOSTIC PASS
```

Use `576w5n_stage576_100_from_0875_lower_dn_scan_checked.mph` as the latest
checked forward endpoint. The accepted `100%` branch is `DN6`, with
`F_total_support = 0.0322300492460 N` for RESET and `0.0321501581771 N` for
REUSE.

Next main task:

```text
Build the complete backward stroke: 100% -> 0%
```

Start from the checked `w5n` forward endpoint, preserve the same clean physics
frame (`CORE_0100`, `CAP_6P5KPA`, `alpha_pfb=0.15`, `h_TFF=h_calc573`, local
`sel_film_swept571`) and do not add friction, roughness, or parameter tuning.
Check velocity reversal, pressure response, shear sign, theta, film height, and
load continuity at the reversal point before calling it a full dynamic cycle.

## Stage 577 postprocessing reference

Preserve the Stage 577 result chain with explicit PASS/FAIL status:

```text
577a: conserved 3 um local TFF check                         PASS
577b: conserved-film depletion / low-film activation          PASS
577c: mixed-lubrication boundary-friction postprocess         PASS
577d: postprocess calibration / sensitivity                   FAIL
577e: weakly coupled depleted TFF                             FAIL
577f: load-sharing boundary pressure                          PASS
577g: contact-pressure probe / asperity-pressure proxy        PASS
577h: asperity calibration small scan                         FAIL
577h2: refined asperity calibration                           PASS
577i: fixed-parameter paper-output postprocess                PASS
```

Use `577i_stage577_fixed_asperity_paper_outputs_results.mph` as the newest checked paper-output postprocessing result. Use `577h2_stage577_asperity_calibration_refined_results.mph` as the parameter-calibration source. Use `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph` only as the structural/TFF baseline, not as the final physical film-thickness model.

Latest physical-coupling extension:

```text
576w3d: 5% -> 7.5% -> 10% load-closure extension          MARGINAL
576w3e: 3um film-height gap-window candidate               FAIL
576w3f: 3um micro 5% -> 6.25% decoupled-mask candidate      FAIL
576w3g: TFF-only pressure-source isolation                  DIAGNOSTIC PASS
576w3h: TFF pressure-support formulation diagnostic         DIAGNOSTIC PASS
576w3i: pressure-limited feedback microtest                 DIAGNOSTIC PASS
576w3j: fixed pressure-cap refinement microtest             DIAGNOSTIC PASS
576w3k: pressure-cap short extension to 7.5%                DIAGNOSTIC FAIL
576w3l: pressure-cap 7.5% failure localization              DIAGNOSTIC FAIL
576w3m: TFF state-transition diagnosis                      DIAGNOSTIC PASS
576w3n: coupled handoff replay diagnosis                    DIAGNOSTIC PASS
576w3o: solid/geometry handoff probe                        DIAGNOSTIC PASS
576w3p: geometry handoff readout correction microtest        DIAGNOSTIC PASS
576w3q: solve-level geometry freeze handoff correction       DIAGNOSTIC PASS
576w3r: geometry-freeze short coupled extension              DIAGNOSTIC PASS
576w3s: geometry-freeze 10% extension                        DIAGNOSTIC MARGINAL
576w3t: 10% load-deficit read-only diagnosis                 DIAGNOSTIC PASS
576w3u: support-area decomposition diagnosis                 DIAGNOSTIC PASS
576w3v: core-window readout audit                            DIAGNOSTIC PASS
576w3w: motion/core-expression audit                         DIAGNOSTIC PASS
576w3x: contact-force deficit audit                          DIAGNOSTIC PASS
576w3y: bounded normal-position compensation diagnostic       DIAGNOSTIC PASS
576w3z: coupled normal-position handoff                       DIAGNOSTIC PASS/MARGINAL
576w4a: fixed 2.25 um normal-position refinement              DIAGNOSTIC PASS
576w4b: fixed 2.25 um compensated handoff confirmation         DIAGNOSTIC PASS
576w4c: fixed 2.25 um compensated forward extension to 25%     DIAGNOSTIC FAIL
576w4c_readback: 10% -> 15% failure readback diagnosis         DIAGNOSTIC PASS
576w4d: forward contact/support localization                   DIAGNOSTIC PASS
576w4e: forward normal/core-overlap microtest                  DIAGNOSTIC PASS
576w4f: explicit combined normal/core replay                   DIAGNOSTIC PASS
576w4g: solved combined normal/core microtest                  DIAGNOSTIC MARGINAL
576w4h: combined-branch deficit diagnosis                      DIAGNOSTIC PASS
576w4i: bounded normal-contact recovery at 15%                 DIAGNOSTIC PASS
576w4j: selected normal/core extension toward 25%              DIAGNOSTIC FAIL
576w4k: 20% bounded normal recovery scan                       DIAGNOSTIC FAIL
576w4l: split 20% contact-path diagnostic                       DIAGNOSTIC FAIL
576w4m: contact-collapse onset scan                             DIAGNOSTIC FAIL
576w4n: 18% dn compensation window scan                         DIAGNOSTIC FAIL
576w4o: direct 18% reset scan                                   DIAGNOSTIC FAIL
576w4p: 18% contact/gap geometry audit                           DIAGNOSTIC FAIL
576w4q: contact-spike area/load audit                            DIAGNOSTIC PASS
576w4r: robust contact forward extension toward 25%              DIAGNOSTIC FAIL
576w4s: Fn_contact robust forward extension to 25%               DIAGNOSTIC FAIL
576w4t: 25% solid recovery scan                                  DIAGNOSTIC PASS
576w4u: 25% compensation schedule confirmation                    DIAGNOSTIC PASS
576w4v: 50% forward extension with DN16                           DIAGNOSTIC PASS
576w4w: 75% forward extension attempt                             DIAGNOSTIC FAIL
576w4x: 62.5% normal recovery scan                                DIAGNOSTIC FAIL/PARTIAL
576w4y: 62.5% REUSE normal recovery continuation                   DIAGNOSTIC PARTIAL
576w4z: 62.5% schedule confirmation                                DIAGNOSTIC PASS
576w5a: 75% direct extension from 62.5%                            DIAGNOSTIC FAIL
576w5b: 75% split extension from 62.5%                             DIAGNOSTIC FAIL
576w5c: 75% pressure-reset DN scan                                  DIAGNOSTIC PARTIAL
576w5d: 75% pressure-reset high-DN scan                             DIAGNOSTIC PASS
576w5e: 100% forward extension from 75%                             DIAGNOSTIC FAIL
576w5f: 87.5% pressure-reset DN scan                                DIAGNOSTIC PARTIAL
576w5g: 87.5% pressure-reset fine-DN scan                           DIAGNOSTIC PARTIAL
576w5h: 87.5% fixed-pressure solid recovery                         DIAGNOSTIC FAIL
576w5i: 87.5% split forward DN scan                                 DIAGNOSTIC FAIL
576w5j: 81.25% pressure-reset high-DN scan                          DIAGNOSTIC PASS
576w5k: 87.5% from 81.25% DN scan                                   DIAGNOSTIC MARGINAL
576w5l: 87.5% from 81.25% low-DN scan                               DIAGNOSTIC PASS
576w5m: 100% from 87.5% low-DN scan                                 DIAGNOSTIC PARTIAL
576w5n: 100% from 87.5% lower-DN scan                               DIAGNOSTIC PASS
```

Use `576w3d_stage576_recursive_split010_film_height_release_extended_checked.mph`
as the newest non-FAIL physical Solid + local TFF extension diagnostic, but do
not call it a clean PASS. It stayed finite and local through 10%, but the final
load is above the preferred pass window. Use `576w3e...results.mph` and
`576w3f...results.mph` only as failure diagnostics: they restored 3 um-scale
film height, but failed load closure. Use `576w3g...results.mph` only as a
diagnostic pressure-source isolation result. Use `576w3h...results.mph` only
as a diagnostic pressure-support formulation result; it classifies the current
limiting mechanism as pressure-magnitude over-support. Use `576w3i...results.mph`
only as a short pressure-limited feedback diagnostic; both branches are
`MARGINAL`, not final baselines. Use `576w3j...results.mph` only as a fixed
pressure-cap refinement diagnostic; `CAP_6P5KPA` and `CAP_7P0KPA` passed the
`5% -> 6.25%` microtest, and `CAP_6P5KPA` is the selected candidate for the
next short extension. Use `576w3k...results.mph` only as a failed short
extension diagnostic: `CAP_6P5KPA` still passes at `6.25%`, but fails at
`7.5%` due to raw pressure/theta collapse and under-supported capped load.
Use `576w3l...results.mph` and `576w3l...checked.mph` only as a failed
failure-localization diagnostic: independent microtargets from the stable
`6.25%` checkpoint show `6.50%`, `6.75%`, and `7.25%` as `PASS`, `7.00%` as
`MARGINAL` because of an intermittent raw-pressure/theta warning, and `7.50%`
as `FAIL` because capped support under-carries the normal load.
Use `576w3m...results.mph` and `576w3m...checked.mph` only as a diagnostic
state-transition classification: frozen TFF-only replay is stable at `7.00%`,
`7.25%`, and `7.50%`, but the coupled final states reproduce the `7.00%`
theta/raw-pressure warning and `7.50%` load-support failure. Its diagnosis is
`COUPLED_HANDOFF_TRANSITION_SUSPECT`.
Use `576w3n...results.mph` and `576w3n...checked.mph` only as a coupled
handoff replay diagnostic: cross replay shows `SOLID_GEOMETRY_HANDOFF_DOMINANT`.
Baseline pressure with coupled solid states reproduces failure, while coupled
pressure history with baseline solid state remains stable.
Use `576w3o...results.mph` and `576w3o...checked.mph` only as a solid/geometry
handoff probe: failing states `sol306` and `sol330` are classified as
`GAP_MASK_GEOMETRY_SHIFT_DOMINANT`.
Use `576w3p...results.mph` and `576w3p...checked.mph` only as a readout-only
geometry/Bpress correction diagnostic: freezing, smoothing, or bounding
`Bpress` after the failing solid state exists does not recover load support.
Use `576w3q...results.mph` and `576w3q...checked.mph` only as a solve-level
geometry handoff correction diagnostic: baseline/frozen TFF geometry source
passes `7.00%` and `7.50%` both with solid reset and solid reuse, while current
failing geometry still fails.
Use `576w3r...results.mph` and `576w3r...checked.mph` only as a short
geometry-freeze coupled extension diagnostic: both solid reset and solid reuse
pass over `6.25% -> 7.00% -> 7.50%`.
Use `576w3s...results.mph` and `576w3s...checked.mph` only as a geometry-freeze
10% extension diagnostic: both solid reset and solid reuse are finite and keep
`AvgH ~= 3.34 um`, `MinTheta ~= 0.9999`, and local TFF selection, but verified
`F_total_support ~= 0.0280-0.0281 N`, below the preferred load window.
Use `576w3t...summary.csv` and `576w3t...delta.csv` as the read-only
load-deficit diagnosis: the 10% deficit occurs with stable film state,
contact-force loss is dominant, and capped film support also decreases because
effective `Bpress` support area shrinks.
Use `576w3u...summary.csv` and `576w3u...delta.csv` as the support-area
decomposition: the changing component is `M_core573` / core support coverage,
not `Bhigh`, `B_low`, valid-gap, or gap readouts.
Use `576w3v...summary.csv` and `576w3v...branch_summary.csv` as the core-window
audit: post-hoc `FROZEN_075`, `LAG_0875`, and `CURRENT_100` readouts are
identical, so simple lagged-core readout does not recover the 10% load.
Use `576w3w...summary.csv` and `576w3w...branch_summary.csv` as the
motion/core-expression audit: the saved expressions are coherent, `PLUS` is the
active sign convention, and explicit `READOUT_075/PLUS` restores about
`0.753 mN` support but remains `MARGINAL`.
Use `576w3x...combinations.csv` as the contact-force deficit audit: restoring
support only or contact only remains `MARGINAL`; restoring both reaches `PASS`.
Use `576w3y...summary.csv` and `576w3y...verify_summary.csv` as the bounded
normal-position compensation diagnostic: `dn_comp = 2 um` and `4 um` recover
the `0.030-0.033 N` load window in both reset and reuse branches while keeping
the film state stable. It is still diagnostic-only and not a final coupled
baseline.
Use `576w3z...summary.csv` and `576w3z...verify_summary.csv` as the first
coupled normal-position handoff test: fixed `dn_comp = 2 um` is directionally
correct, but checked reset is only `MARGINAL`.
Use `576w4a...summary.csv` and `576w4a...verify_summary.csv` as the current
fixed compensation refinement: fixed `dn_comp = 2.25 um` gives checked
reset/reuse `PASS` at `10%`, with stable film state.
Use `576w4b...summary.csv` and `576w4b...verify_summary.csv` as the current
full compensated `7.5% -> 10%` handoff confirmation: fixed
`dn_comp = 2.25 um` gives checked reset/reuse `PASS` with stable film state and
local TFF selection.
Use `576w4c...summary.csv` and `576w4c...verify_summary.csv` as the current
failed bounded continuation attempt beyond `10%`: both reset and reuse are
`MARGINAL` at `12.5%` and `FAIL` at `15%`, while `AvgH`, theta, raw pressure,
and local TFF selection remain stable. Treat it as evidence for
contact/support/load drift, not as an accepted `25%` baseline.
Use `576w4c_stage576_readback_diagnosis_states.csv` and
`576w4c_stage576_readback_diagnosis_deltas.csv` as the read-only decomposition:
from `10%` to `15%`, total load drops by about `6.33 mN`, with about `45%`
from contact-force loss and about `55%` from capped film-support loss.
Use `576w4d...summary.csv`, `576w4d...delta.csv`, and `576w4d...combos.csv`
as the current localization: the `12.5% -> 15%` mechanism is
`NORMAL_UNLOADING_PLUS_CORE_SUPPORT_SHRINK`; contact-only or support-only
restoration remains `FAIL`, while restoring both to the `10%` configuration is
`PASS`.
Use `576w4e...variants.csv` and `576w4e...requirements.csv` as the current
normal/core-overlap microtest: normal-only and core-only restoration are
insufficient, while contact near the `12.5%` state plus core/Bpress overlap
near the accepted `10%` state reaches the load window.
Use `576w4f...summary.csv` as the explicit replay on the actual `15%` pressure
field: contact `12.5%` plus `CORE_0100` reaches about `0.03034 N`, while
contact `12.5%` plus `CORE_0125` is only `MARGINAL`.
Use `576w4g...verify_summary.csv` as the newest solved test of the combined
normal/core correction: baseline, normal-only, and `CORE_0100`-only branches
remain `FAIL`, while normal + `CORE_0100` reaches about `0.02808 N` in both
reset and reuse. It is `MARGINAL`, not an accepted forward-extension baseline.
Use `576w4h...summary.csv` as the newest explanation of the w4g marginal
deficit: explicit `CORE_0100` support is preserved exactly, and the remaining
`~2.257 mN` deficit is contact-side recovery relative to the `12.5%` target.
Use `576w4i...verify_summary.csv` as the selected `15%` recovery result:
`dn_extra = 4 um` is the smallest tested branch that gives reset/reuse `PASS`.
Use `576w4j...summary.csv` as the newest forward-extension attempt: fixed
`dn_extra = 4 um + CORE_0100` fails at `20%`; `25%` was not attempted.
Use `576w4k...summary.csv` as the bounded normal recovery scan at `20%`:
increasing `dn_extra` from `4` to `8 um` raises load but leaves the active
contact area collapsed and `MaxTn` near `1.6 MPa`.
Use `576w4l...summary.csv` as the split handoff diagnostic: `DN8` passes at
`17.5%` but still fails at `20%`.
Use `576w4m...summary.csv` as the onset localization: `DN8` passes at `17.5%`
and fails by contact-stress spike at `18.0%`.
Use `576w4n...summary.csv` as the `18%` normal-compensation scan: all tested
`dn_extra = 4-8 um` branches fail at `18.0%` because `MaxTn` is about
`2.22-2.27 MPa` and `MinGap` is about `-0.36 to -0.37 mm`.
Use `576w4o...summary.csv` as the direct reset check: direct `15% -> 18%`
reset reproduces the same contact/gap jump, so the problem is tied to the
`18%` position/contact mapping rather than `17.5%` solid-state inheritance.
Use `576w4p...summary.csv` and `576w4q...summary.csv` as the contact/gap and
stress-spike audit: the `18%` pointwise `MaxTn` spike has zero measured
high-stress area and zero high-stress load fraction.
Use `576w4s...summary.csv` as the first robust forward extension that reaches
`25%`: fixed `DN10` fails there because capped film support collapses to about
`0.000617 N`.
Use `576w4t...summary.csv` and `576w4t...verify_summary.csv` as the latest
`25%` diagnostic PASS candidate: the `DN10` path through `22.5%` plus `DN16`
solid recovery at `25%` gives `F_total_support ~= 0.0308078 N`.
Use `576w4u...summary.csv` and `576w4u...verify_summary.csv` as the latest
controlled `25%` schedule confirmation: reset and reuse both pass with
`DN10` through `22.5%`, `DN16` at `25%`, and
`F_total_support ~= 0.0308078 N`.
The saved checked model is
`576w4u_stage576_025_compensation_schedule_confirmation_checked.mph`.
Use `576w4v...summary.csv` and `576w4v...verify_summary.csv` as the latest
clean checked forward endpoint: `25% -> 37.5% -> 50%` passes in both reset and
reuse with fixed `DN16`, local TFF selection, `AvgH = 3 um`, and
`F_total_support ~= 0.03009-0.03028 N`.
The saved checked model is
`576w4v_stage576_050_forward_extension_diagnostic_checked.mph`.
Use `576w4w...summary.csv` as the failed attempt beyond 50%: fixed `DN16`
fails already at `62.5%` in RESET and is only marginal in REUSE; the REUSE path
then fails at `75%`.
Use `576w4x...summary.csv` as the current 62.5% normal-recovery diagnostic:
RESET reaches `PASS` at `DN20`, but REUSE remains marginal through `DN18` and
the `DN20` solid solve does not converge. No `w4x` checked `.mph` was saved.
Use `576w4y...summary.csv` as the REUSE continuation diagnostic: REUSE reaches
`PASS` at `DN20`, but RESET becomes marginal under the same continuation.
Use `576w4z...summary.csv` and `576w4z...verify_summary.csv` as the latest
checked forward endpoint: RESET direct `DN20` and REUSE continuation `DN20`
both pass at `62.5%`. The saved checked model is
`576w4z_stage576_0625_schedule_confirmation_checked.mph`.
Use `576w5a...summary.csv` and `576w5b...summary.csv` as failed 75% attempts:
RESET remains marginal at `DN20`, and REUSE TFF does not converge from the
current pressure history even with a split segment.
The forward path is still diagnostic-only and not yet a physical
mixed-lubrication or full-cycle baseline.

## Current checked calibration

Preferred Stage 577h2 candidate:

```text
dh_deplete = 2.5 um
h_crit = 1.0 um
K_asp_eff = 7.5 kPa
mu_boundary = 0.20
mu_total_max = 0.0884728
A_close/A_film_mean = 0.0739728
Fn_asp/Fn_ref_max = 1.00473
PARAM_SCORE = 0.3802765
PASS_LEVEL = STRONG_PASS
```

This candidate keeps the useful friction scale from 577h while reducing the asperity normal-load proxy from about `4.02*Fn_ref` to about `1.00*Fn_ref`.

## Current checked paper output

Stage 577i fixed the selected 577h2 parameter set and generated a checked time-series output:

```text
TIME_COUNT = 41
MU_TOTAL_MAX = 0.0884728
MU_TOTAL_MEAN = 0.0117808
A_CLOSE_OVER_AFILM_MEAN = 0.0739728
FN_ASP_OVER_FNREF_MAX = 1.00473
CHECKED_STATUS = PASS
```

Primary output files:

```text
build_stage577i_fixed_asperity_paper_outputs.java
577i_stage577_fixed_asperity_paper_outputs_diagnostic.md
577i_stage577_fixed_asperity_time_series.csv
577i_stage577_fixed_asperity_paper_outputs_results.mph
```

## Recommended next modeling action

Recommended default:

```text
Stage 576w5c_075_pressure_reset_dn_scan:
base diagnostics:
  576w3g_stage576_tff_only_microtarget_diagnostic.md
  576w3g_stage576_tff_only_microtarget_summary.csv
  576w3h_stage576_tff_pressure_support_diagnostic.md
  576w3h_stage576_tff_pressure_support_summary.csv
  576w3i_stage576_pressure_limited_feedback_diagnostic.md
  576w3i_stage576_pressure_limited_feedback_iterations.csv
  576w3j_stage576_pressure_cap_refinement_diagnostic.md
  576w3j_stage576_pressure_cap_refinement_summary.csv
  576w3k_stage576_pressure_cap_short_extension_diagnostic.md
  576w3k_stage576_pressure_cap_short_extension_summary.csv
  576w3l_stage576_pressure_cap_075_failure_diagnosis.md
  576w3l_stage576_pressure_cap_075_failure_diagnosis_summary.csv
  576w3l_stage576_pressure_cap_075_failure_diagnosis_iterations.csv
  576w3m_stage576_tff_state_transition_diagnosis.md
  576w3m_stage576_tff_state_transition_diagnosis_summary.csv
  576w3n_stage576_coupled_handoff_replay_diagnosis.md
  576w3n_stage576_coupled_handoff_replay_diagnosis_summary.csv
  576w3o_stage576_solid_geometry_handoff_probe.md
  576w3o_stage576_solid_geometry_handoff_probe_summary.csv
  576w3p_stage576_geometry_handoff_correction_microtest.md
  576w3p_stage576_geometry_handoff_correction_microtest_summary.csv
  576w3q_stage576_solve_level_geometry_handoff_correction.md
  576w3q_stage576_solve_level_geometry_handoff_correction_summary.csv
  576w3r_stage576_geometry_freeze_short_coupled_extension.md
  576w3r_stage576_geometry_freeze_short_coupled_extension_summary.csv
  576w3s_stage576_geometry_freeze_010_extension.md
  576w3s_stage576_geometry_freeze_010_extension_summary.csv
  576w3t_stage576_010_load_deficit_diagnosis.md
  576w3t_stage576_010_load_deficit_diagnosis_summary.csv
  576w3t_stage576_010_load_deficit_diagnosis_delta.csv
  576w3u_stage576_support_area_diagnosis.md
  576w3u_stage576_support_area_diagnosis_summary.csv
  576w3u_stage576_support_area_diagnosis_delta.csv
  576w3v_stage576_core_window_audit.md
  576w3v_stage576_core_window_audit_summary.csv
  576w3v_stage576_core_window_audit_branch_summary.csv
  576w3w_stage576_motion_core_expression_audit.md
  576w3w_stage576_motion_core_expression_audit_summary.csv
  576w3w_stage576_motion_core_expression_audit_branch_summary.csv
  576w3w_stage576_motion_core_expression_audit_expressions.csv
  576w3x_stage576_contact_force_deficit_audit.md
  576w3x_stage576_contact_force_deficit_audit_summary.csv
  576w3x_stage576_contact_force_deficit_audit_delta.csv
  576w3x_stage576_contact_force_deficit_audit_combinations.csv
  576w3y_stage576_normal_position_compensation_diagnostic.md
  576w3y_stage576_normal_position_compensation_diagnostic_summary.csv
  576w3y_stage576_normal_position_compensation_verify_summary.csv
  576w3z_stage576_coupled_normal_position_handoff.md
  576w3z_stage576_coupled_normal_position_handoff_summary.csv
  576w3z_stage576_coupled_normal_position_handoff_verify_summary.csv
  576w4a_stage576_normal_position_compensation_refinement.md
  576w4a_stage576_normal_position_compensation_refinement_summary.csv
  576w4a_stage576_normal_position_compensation_refinement_verify_summary.csv
  576w4b_stage576_compensated_handoff_confirmation.md
  576w4b_stage576_compensated_handoff_confirmation_summary.csv
  576w4b_stage576_compensated_handoff_confirmation_verify_summary.csv
  576w4c_stage576_compensated_forward_extension_025.md
  576w4c_stage576_compensated_forward_extension_025_summary.csv
  576w4c_stage576_compensated_forward_extension_025_verify_summary.csv
  576w4c_stage576_readback_diagnosis.md
  576w4c_stage576_readback_diagnosis_states.csv
  576w4c_stage576_readback_diagnosis_deltas.csv
  576w4d_stage576_forward_contact_support_localization.md
  576w4d_stage576_forward_contact_support_localization_summary.csv
  576w4d_stage576_forward_contact_support_localization_delta.csv
  576w4d_stage576_forward_contact_support_localization_combos.csv
  576w4e_stage576_forward_normal_core_overlap_microtest.md
  576w4e_stage576_forward_normal_core_overlap_microtest_base.csv
  576w4e_stage576_forward_normal_core_overlap_microtest_variants.csv
  576w4e_stage576_forward_normal_core_overlap_microtest_requirements.csv
  576w4f_stage576_forward_combined_normal_core_replay.md
  576w4f_stage576_forward_combined_normal_core_replay_contacts.csv
  576w4f_stage576_forward_combined_normal_core_replay_core.csv
  576w4f_stage576_forward_combined_normal_core_replay_summary.csv
  576w4g_stage576_forward_combined_normal_core_solve_microtest.md
  576w4g_stage576_forward_combined_normal_core_solve_microtest_verify_summary.csv
  576w4h_stage576_forward_combined_deficit_diagnosis.md
  576w4h_stage576_forward_combined_deficit_diagnosis_summary.csv
  576w4i_stage576_bounded_normal_contact_recovery_microtest.md
  576w4i_stage576_bounded_normal_contact_recovery_microtest_verify_summary.csv
  576w4i_stage576_bounded_normal_contact_recovery_best_branch.md
  576w4p_stage576_0180_contact_gap_geometry_audit.md
  576w4p_stage576_0180_contact_gap_geometry_audit_summary.csv
  576w4q_stage576_contact_spike_area_audit.md
  576w4q_stage576_contact_spike_area_audit_summary.csv
  576w4s_stage576_fncontact_robust_forward_extension_025.md
  576w4s_stage576_fncontact_robust_forward_extension_025_summary.csv
  576w4t_stage576_025_solid_recovery_scan.md
  576w4t_stage576_025_solid_recovery_scan_summary.csv
  576w4t_stage576_025_solid_recovery_verify_summary.csv
  576w4u_stage576_025_compensation_schedule_confirmation.md
  576w4u_stage576_025_compensation_schedule_confirmation_summary.csv
  576w4u_stage576_025_compensation_schedule_confirmation_verify_summary.csv
  576w4v_stage576_050_forward_extension_diagnostic.md
  576w4v_stage576_050_forward_extension_diagnostic_summary.csv
  576w4v_stage576_050_forward_extension_diagnostic_verify_summary.csv
  576w4w_stage576_075_forward_extension_diagnostic.md
  576w4w_stage576_075_forward_extension_diagnostic_summary.csv
  576w4w_stage576_075_forward_extension_diagnostic_verify_summary.csv
  576w4x_stage576_0625_normal_recovery_scan.md
  576w4x_stage576_0625_normal_recovery_scan_summary.csv
  576w4x_stage576_0625_normal_recovery_scan_verify_summary.csv
  576w4y_stage576_0625_reuse_normal_recovery_continuation.md
  576w4y_stage576_0625_reuse_normal_recovery_continuation_summary.csv
  576w4z_stage576_0625_schedule_confirmation.md
  576w4z_stage576_0625_schedule_confirmation_summary.csv
  576w4z_stage576_0625_schedule_confirmation_verify_summary.csv
  576w5a_stage576_075_forward_extension_from_0625.md
  576w5a_stage576_075_forward_extension_from_0625_summary.csv
  576w5b_stage576_075_split_extension_from_0625.md
  576w5b_stage576_075_split_extension_from_0625_summary.csv
goal: recover a reset/reuse-consistent `75%` checkpoint before attempting
      `100%`. Start from the `w4z` checked 62.5% endpoint, reset all TFF
      pressure histories from `sol493`, scan `DN20/DN22`, keep `CORE_0100`,
      `CAP_6P5KPA`, `alpha_pfb=0.15`, and `h_TFF=h_calc573`, and do not add
      friction or retune controller parameters.
do not:
  call w4u a full-cycle or final mixed-lubrication baseline
  call w4v a full-cycle or final mixed-lubrication baseline
  call w4z a full-cycle or final mixed-lubrication baseline
  continue to 100% before 75% reset/reuse is confirmed
  increase CAP_6P5KPA
  tune alpha/beta/gamma/Keff
  add asperity or boundary friction
  put drel back into real TFF film height
```

Current 576w3l read-back:

```text
VERIFY_STATUS = FAIL
OVERALL_DIAGNOSIS =
  FAILURE_BETWEEN_0725_AND_0750_WITH_INTERMITTENT_THETA_PRESSURE_WARNING
6.50% status = PASS, F_total_support = 0.0316369305909 N
6.75% status = PASS, F_total_support = 0.0319811986933 N
7.00% status = MARGINAL, F_total_support = 0.0314431827624 N
  MaxP_raw = 16.7 MPa
  MinTheta = 0.0634422225228
  LowThetaAreaRatio02 = 0.000862191600078
7.25% status = PASS, F_total_support = 0.0325208397726 N
7.50% status = FAIL, F_total_support = 0.0259962587863 N
AvgH remains 3.27-3.33 um
DrelSaturationRatio = 0.124308574393
TFF selection equals sel_film_swept571
```

Recommended next solve:

```text
576w3n_stage576_coupled_handoff_replay_diagnosis
targets:
  7.00%, 7.25%, 7.50%
purpose:
  isolate the coupled handoff mechanism now that frozen TFF-only replay is
  stable at all three targets
minimum variants:
  relaxation from zero field versus inherited relaxed field
  solid solve from inherited contact state versus reset/reinitialized state
  TFF from inherited coupled pressure versus sol280 pressure baseline
  fixed drel versus inherited/update drel geometry handoff
do not:
  continue to 10%
  tune alpha/beta/gamma/Keff
  increase cap_pressure just to recover load
  add asperity or boundary friction
```

Current 576w3m read-back:

```text
VERIFY_STATUS = PASS
OVERALL_DIAGNOSIS = COUPLED_HANDOFF_TRANSITION_SUSPECT

FROZEN_TFF_ONLY:
  7.00% STABLE_LOAD_WINDOW, F_total_support = 0.0319050341945 N
  7.25% STABLE_LOAD_WINDOW, F_total_support = 0.0319066370296 N
  7.50% STABLE_LOAD_WINDOW, F_total_support = 0.0318979909491 N
  MaxP_raw = 73.8-78.8 kPa
  MinTheta > 0.99993

W3L_COUPLED_FINAL:
  7.00% THETA_PRESSURE_WARNING
    F_total_support = 0.0314431827624 N
    MaxP_raw = 16.7 MPa
    MinTheta = 0.0634422225228
  7.25% STABLE_LOAD_WINDOW
    F_total_support = 0.0325208397726 N
  7.50% LOAD_SUPPORT_FAIL
    F_total_support = 0.0259962587863 N

TFF selection equals sel_film_swept571
```

Current 576w3n read-back:

```text
VERIFY_STATUS = PASS
OVERALL_DIAGNOSIS = SOLID_GEOMETRY_HANDOFF_DOMINANT

7.00%:
  baseline pressure sol280 + coupled solid sol306:
    LOAD_SUPPORT_FAIL
    F_total_support = 0.0275151306489 N
    MinTheta = 0.213681426078
  coupled pressure sol304 + baseline solid sol282:
    STABLE_LOAD_WINDOW
    F_total_support = 0.0319050339490 N
    MinTheta = 0.999941451515

7.50%:
  baseline pressure sol280 + coupled solid sol330:
    LOAD_SUPPORT_FAIL
    F_total_support = 0.0253228646089 N
  coupled pressure sol328 + baseline solid sol282:
    STABLE_LOAD_WINDOW
    F_total_support = 0.0318979910947 N

TFF selection equals sel_film_swept571
```

Completed 576w3p readout-correction test:

```text
VERIFY_STATUS = PASS
OVERALL_DIAGNOSIS = READOUT_BPRESS_CORRECTION_FAILS

7.00%:
  CURRENT F_total_support = 0.0275151306489 N, MinTheta = 0.213681426078
  FROZEN_BASE F_total_support = 0.0255550347501 N
  SMOOTH_50 F_total_support = 0.0265350826995 N
  BOUNDED_10PCT F_total_support = 0.0255873128434 N

7.50%:
  CURRENT F_total_support = 0.0253228646089 N, MinTheta = 0.999922397890
  FROZEN_BASE F_total_support = 0.0242007691806 N
  SMOOTH_50 F_total_support = 0.0247618168948 N
  BOUNDED_10PCT F_total_support = 0.0241464785922 N
```

Completed 576w3q solve-level geometry handoff correction:

```text
VERIFY_STATUS = PASS
OVERALL_DIAGNOSIS =
  SOLVE_LEVEL_GEOMETRY_FREEZE_ACCEPTED_CONTACT_REUSE_OK

current failing geometry:
  CURR_GEOM_700_READ F_total_support = 0.0275151306616 N, FAIL
  CURR_GEOM_700_SOLID_RESET F_total_support = 0.0279152511889 N, FAIL
  CURR_GEOM_750_READ F_total_support = 0.0253228646089 N, FAIL
  CURR_GEOM_750_SOLID_RESET F_total_support = 0.0255055921720 N, FAIL

baseline/frozen geometry:
  BASE_GEOM_700_READ F_total_support = 0.0319050341945 N, PASS
  BASE_GEOM_700_SOLID_RESET F_total_support = 0.0308579973370 N, PASS
  BASE_GEOM_700_SOLID_REUSE F_total_support = 0.0308280893527 N, PASS
  BASE_GEOM_750_READ F_total_support = 0.0318979909491 N, PASS
  BASE_GEOM_750_SOLID_RESET F_total_support = 0.0309150827390 N, PASS
  BASE_GEOM_750_SOLID_REUSE F_total_support = 0.0308837268578 N, PASS
```

Completed 576w3r geometry-freeze short coupled extension:

```text
VERIFY_STATUS = PASS
OVERALL_DIAGNOSIS = GEOMETRY_FREEZE_SHORT_EXTENSION_PASS_BOTH

BASE_GEOM_FREEZE_SOLID_RESET:
  7.00% F_total_support = 0.0304135223232 N, PASS
  7.50% F_total_support = 0.0302889718282 N, PASS

BASE_GEOM_FREEZE_SOLID_REUSE:
  7.00% F_total_support = 0.0304135223232 N, PASS
  7.50% F_total_support = 0.0303035411173 N, PASS
```

Recommended next solve:

```text
576w3s_stage576_geometry_freeze_010_extension
targets:
  7.50% -> 10.00%
purpose:
  test whether the accepted geometry-freeze coupled handoff remains stable at
  10.00% before any full-cycle or friction expansion
candidate variants:
  BASE_GEOM_FREEZE_SOLID_RESET
  BASE_GEOM_FREEZE_SOLID_REUSE
do not:
  continue beyond 10%
  tune alpha/beta/gamma/Keff
  increase cap_pressure just to recover load
  put drel back into real TFF film height
  add asperity or boundary friction
```

Current 576w3o read-back:

```text
VERIFY_STATUS = PASS
OVERALL_DIAGNOSIS = GAP_MASK_GEOMETRY_SHIFT_DOMINANT

7.00%:
  baseline sol282:
    F_total_support = 0.0319050339490 N
    MinGap = -61.1962052459 um
    Bpress/A_swept = 0.0249934656940
  failing sol306:
    F_total_support = 0.0275151306489 N
    MinGap = -91.0026728393 um
    Bpress/A_swept = 0.0306078772540
    MinTheta = 0.213681426078

7.50%:
  baseline sol282:
    F_total_support = 0.0318979910947 N
    MinGap = -61.1962052459 um
    Bpress/A_swept = 0.0249676348216
  failing sol330:
    F_total_support = 0.0253228646089 N
    MinGap = -103.505959932 um
    Bpress/A_swept = 0.0280890428946
```

Current 576w3e read-back:

```text
VERIFY_STATUS = FAIL
failed segment = 3, 5% -> 7.5%
F_contact = 0.00568746688190 N
F_film = 0.0748136883950 N
F_total = 0.0805011552769 N
AvgH = 3.86495287430e-06 m
MinTheta = 0.736878212489
LowThetaAreaRatio02 = 0
DrelSaturationRatio = 0.361808574393
TFF selection equals sel_film_swept571
```

Current 576w3d read-back:

```text
VERIFY_STATUS = MARGINAL
F_contact = 0.0107429622941 N
F_film = 0.0250608173034 N
F_total = 0.0358037795975 N
DrelSaturationRatio = 0.277447704960
MinTheta = 0.999999599159
LowThetaAreaRatio02 = 0
TFF selection equals sel_film_swept571
```

Load-offset probe:

```text
LOAD_OFFSET_PROBE_STATUS = PASS
F_film_load/F_film_swept = 0.239039697675
A_wet_load/A_swept = 0.0517822557137
A_wet_load/A_core = 0.572869486842
MaxLoadPressure = 11319.0757691 Pa
MeanLoadPressure = 286.176267673 Pa
```

Mask-review probe:

```text
MASK_REVIEW_PROBE_STATUS = PASS
segment 3 pressure/relaxed/solid = sol337 / sol338 / sol339
segment 4 pressure/relaxed/solid = sol379 / sol380 / sol381
F_contact change = -0.0045674421867 N
F_film_load_masked change = +0.0049415912453 N
F_total change = +0.0003741490585 N
A_wet_load/A_swept: 0.0454293294855 -> 0.0517822557137
A_wet_load/A_core: 0.478048413663 -> 0.572869486842
max load pressure: 15505.3329694 Pa -> 11319.0757691 Pa
mean wet-mask load pressure: 5057.23725186 Pa -> 5526.53150638 Pa
figure export: PASS; p_load and M_core573*Bfilm573 remain local, but the
10% wet-load mask is more connected along the central load band
```

Current interpretation:

```text
The 7.5% -> 10% marginal load offset is not a pressure-spike, theta-collapse,
selection-drift, or drel-saturation failure.

The issue is film-load over-support: drel reduces contact load, but the
M_core573*Bfilm573 wet-load area expands and the mean load pressure inside
that mask increases enough for F_film_load_masked to rise slightly more than
F_contact falls.
```

576w3e failure diagnosis:

```text
W3E_FAILURE_DIAGNOSIS_STATUS = PASS

The inherited 5% state is acceptable when evaluated with the 576w3e 3um/mask:

inherited_5pct:
  F_contact = 0.0196747609743 N
  F_film_w3e_mask = 0.00906008018197 N
  F_total_w3e_mask = 0.0287348411563 N

The first 7.5% TFF recomputation is already overloaded:

iter01_first_075:
  F_contact = 0.0179308146922 N
  F_film_w3e_mask = 0.0383611032966 N
  F_total_w3e_mask = 0.0562919179888 N

The inherited 5% pressure field is therefore not the primary failure cause.
The supported mechanism is immediate 7.5% 3um TFF pressure over-production,
with the wet-load mask admitting too much of the recomputed pressure while
drel reduces solid contact. Coupled use of Bfilm576w3e in both wall velocity
and load feedback is the next mechanism to isolate.
```

Recommended next solve:

```text
Stage 576w3f result:
  CHECKED_STATUS = FAIL
  VERIFY_STATUS = FAIL
  tested 5% -> 6.25%
  best F_total = 0.0502179738930 N
  final F_total = 0.0839746946386 N
  final AvgH = 3.94192970705e-06 m
  final MinTheta = 0.999976153096
  TFF selection equals sel_film_swept571

Stage 576w3g result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  DIAGNOSIS_CLASS = PRESSURE_SOURCE_OVER_SUPPORT
  frozen solid/release, TFF-only targets = 5.5%, 6.0%, 6.25%
  5.5% F_total_frozen = 0.0470957506334 N
  6.0% F_total_frozen = 0.0494792630215 N
  6.25% F_total_frozen = 0.0506865935341 N
  AvgH = 3.41160 to 3.41185 um
  MinTheta = 0.999913687 to 0.999925927
  edge/center pressure ratio = 1.29 to 1.34
  TFF selection equals sel_film_swept571

Stage 576w3h result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  DIAGNOSIS_CLASS = PRESSURE_MAGNITUDE_OVER_SUPPORT
  GAP_HTFF raw F_total_frozen = 0.0470957506334 to 0.0506865935341 N
  GAP_HTFF MASK_H12D4 F_total_frozen = 0.0411622338956 to 0.0439588760751 N
  GAP_HTFF CAP_7P5KPA F_total_frozen = 0.0326466833365 to 0.0327105881254 N
  GAP_HTFF LOAD_LIMITED F_total_frozen = about 0.030000000000 N
  FIXED_3UM raw F_total_frozen = 0.0551962568507 to 0.0592931738057 N
  TFF selection equals sel_film_swept571

Stage 576w3i result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  CAP_BRANCH_STATUS = MARGINAL
  LOAD_LIMITED_BRANCH_STATUS = MARGINAL
  OVERALL_DIAGNOSIS = BOTH_ACCEPTED_COMPARE_STABILITY
  CAP_7P5KPA F_total_support = 0.0334552214753 N
  LOAD_LIMITED F_total_support = 0.0292133418040 N
  AvgH = 3.35 to 3.37 um
  MinTheta = about 0.99995
  TFF selection equals sel_film_swept571

Stage 576w3j result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = FIXED_CAP_PASS_SELECT_BEST_BRANCH
  VERIFY_CAP_MONOTONIC = true
  CAP_6P5KPA F_total_support = 0.0315863923117 N, PASS
  CAP_7P0KPA F_total_support = 0.0325100989913 N, PASS
  CAP_7P5KPA F_total_support = 0.0334552214752 N, MARGINAL
  LOAD_LIMITED_REF F_total_support = 0.0292133418041 N, MARGINAL
  AvgH = 3.35 to 3.37 um
  MinTheta = about 0.99995
  TFF selection equals sel_film_swept571

Stage 576w3k result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = FAIL
  OVERALL_DIAGNOSIS = CAP_6P5_FAILS_AT_075
  segment 3 / 6.25% F_total_support = 0.0315863923117 N, PASS
  segment 4 / 7.5% F_total_support = 0.0244334008270 N, FAIL
  segment 4 raw swept film load = 9.35661253392 N
  segment 4 MaxP_raw = 58.4 MPa
  segment 4 MinTheta = 0.000681273035266
  segment 4 AvgH = 3.28377925627 um
  segment 4 DrelSaturationRatio = 0.124308574393
  TFF selection equals sel_film_swept571

Stage 576w3l result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = FAIL
  OVERALL_DIAGNOSIS =
    FAILURE_BETWEEN_0725_AND_0750_WITH_INTERMITTENT_THETA_PRESSURE_WARNING
  6.50% = PASS
  6.75% = PASS
  7.00% = MARGINAL due to raw-pressure/theta warning
  7.25% = PASS
  7.50% = FAIL due to capped load under-support

Stage 576w3m result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = COUPLED_HANDOFF_TRANSITION_SUSPECT
  frozen TFF-only replay is stable at 7.00%, 7.25%, and 7.50%
  coupled 7.00% shows theta/raw-pressure warning
  coupled 7.50% fails by capped load under-support

Stage 576w3n result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = SOLID_GEOMETRY_HANDOFF_DOMINANT
  baseline pressure + coupled solid state reproduces failure
  coupled pressure history + baseline solid state stays stable

Stage 576w3o result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = GAP_MASK_GEOMETRY_SHIFT_DOMINANT
  failing sol306/sol330 shift MinGap more negative and increase Bpress/A_swept
  capped film support drops below the load window

Stage 576w3p result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = READOUT_BPRESS_CORRECTION_FAILS
  all CURRENT/FROZEN_BASE/SMOOTH_50/BOUNDED_10PCT readout variants fail at
  7.00% and 7.50%

Stage 576w3q result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = SOLVE_LEVEL_GEOMETRY_FREEZE_ACCEPTED_CONTACT_REUSE_OK
  baseline/frozen TFF geometry source passes 7.00% and 7.50%, both with
  solid reset and solid reuse
  current failing geometry still fails even with solid reset

Stage 576w3r result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = GEOMETRY_FREEZE_SHORT_EXTENSION_PASS_BOTH
  both solid reset and solid reuse pass over 6.25% -> 7.00% -> 7.50%

Stage 576w3s result:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = GEOMETRY_FREEZE_010_EXTENSION_MARGINAL_BOTH
  both solid reset and solid reuse remain stable at 10.00%
  verified F_total_support is only about 0.0280-0.0281 N

Stage 576w3t result:
  W3T_PROBE_STATUS = PASS
  OVERALL_DIAGNOSIS = 010_LOAD_DEFICIT_WITH_STABLE_FILM_STATE
  reset delta_F_contact = -0.00152016964997 N
  reset delta_F_support = -0.000724577545398 N
  reuse delta_F_contact = -0.00143625129390 N
  reuse delta_F_support = -0.000724577545398 N
  Bpress/A_swept decreases from 0.0249676348216 to 0.0228749423002

Stage 576w3u result:
  W3U_PROBE_STATUS = PASS
  OVERALL_DIAGNOSIS = SUPPORT_AREA_SHRINK_WITH_CONTACT_LOSS
  Bpress/A_swept decreases from 0.0249676348216 to 0.0228749423002
  core/A_swept decreases from 0.0950677282709 to 0.0905577860756
  Bhigh/A_swept is unchanged at 0.981603420071
  extra support area needed at cap is only about 2.86-3.01e-7 m^2

Stage 576w3v result:
  W3V_PROBE_STATUS = PASS
  OVERALL_DIAGNOSIS = CORE_WINDOW_LAG_READOUT_TESTED
  FROZEN_075, LAG_0875, and CURRENT_100 readouts are identical
  frozen075_minus_current_support = 0
  both branches remain MARGINAL

Stage 576w3w result:
  W3W_PROBE_STATUS = PASS
  OVERALL_DIAGNOSIS = MOTION_EXPRESSIONS_AND_EXPLICIT_CORE_READOUT_TESTED
  tau572 = t_position576p2
  theta_lid_spatial572 = theta_lid_physical572 + lid_mask_aoffset572
  active sign = PLUS
  READOUT_075/PLUS improves total load by 0.000753272063358 N
  both branches remain MARGINAL

Stage 576w3x result:
  W3X_PROBE_STATUS = PASS
  OVERALL_DIAGNOSIS = CONTACT_FORCE_DROP_PLUS_SUPPORT_AREA_DEFICIT
  reset delta_Fn_contact = -0.00152016964997 N
  reuse delta_Fn_contact = -0.00143625129390 N
  restoring support only remains MARGINAL
  restoring contact only remains MARGINAL
  restoring both gives PASS around 0.03032 N

Stage 576w3y result:
  W3Y_BUILD_STATUS = PASS
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = NORMAL_POSITION_COMPENSATION_RECOVERS_010_LOAD_BOTH
  dn_comp = 2 um and 4 um recover the 0.030-0.033 N load window
  AvgH remains 3.33875957936e-06 m
  MinTheta remains 0.999905690467

Stage 576w4c result:
  W4C_BUILD_STATUS = PASS
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = COMPENSATED_FORWARD_EXTENSION_025_FAILS
  12.5% reset/reuse are MARGINAL at F_total_support ~= 0.02896 N
  15.0% reset/reuse fail at F_total_support ~= 0.02390 N
  AvgH remains 3.27-3.32 um, MinTheta remains > 0.9998, and TFF selection is local
  failure mode = contact/support/load drift, not film-height or theta collapse

Stage 576w4c_readback result:
  PROBE_STATUS = PASS
  OVERALL_DIAGNOSIS = CONTACT_AND_SUPPORT_AREA_DRIFT_BEYOND_010
  branch diagnoses = CONTACT_AND_SUPPORT_AREA_DRIFT for reset and reuse
  10% -> 15% total load drop ~= 6.33 mN
  contact-force loss contributes about 45%
  capped film-support loss contributes about 55%
  AvgH remains 3.27-3.34 um, MinTheta remains > 0.9998, and TFF selection is local

Stage 576w4d result:
  PROBE_STATUS = PASS
  OVERALL_DIAGNOSIS = NORMAL_UNLOADING_PLUS_CORE_SUPPORT_SHRINK
  12.5% -> 15% contact loss ~= 2.815 mN
  12.5% -> 15% support loss ~= 2.245 mN
  active contact area, MaxTn, BpressOverSwept, and McoreOverSwept all drop
  Bhigh, B_low, and valid-gap admission stay unchanged
  contact-only or support-only restoration remains FAIL

Stage 576w4e result:
  PROBE_STATUS = PASS
  OVERALL_DIAGNOSIS = COMBINED_NORMAL_AND_CORE_OVERLAP_REQUIRED
  normal-only restoration remains FAIL
  core/Bpress-only restoration remains FAIL or MARGINAL
  contact restored to 12.5% plus core/Bpress overlap restored to 10% gives PASS
  exact 10% support plus 12.5% contact also gives PASS

Stage 576w4f result:
  PROBE_STATUS = PASS
  OVERALL_DIAGNOSIS = EXPLICIT_CORE_REPLAY_PLUS_NORMAL_REPLAY_TESTED
  actual 15% pressure + contact 12.5% + CORE_0100 gives PASS
  actual 15% pressure + contact 12.5% + CORE_0125 is only MARGINAL
  actual 15% pressure + contact 15% + CORE_0100 remains FAIL

Stage 576w4g_forward_combined_normal_core_solve_microtest result:
1. Tested only 12.5% -> 15%.
2. Baseline, normal-only, and CORE_0100-only branches remain FAIL.
3. Combined normal + CORE_0100 reaches MARGINAL at about 0.02808 N in both
   reset and reuse.
4. Film state remains stable: AvgH about 3.27 um, MinTheta about 0.99988,
   LowThetaAreaRatio02 = 0, MaxP_raw about 0.175 MPa, and TFF selection local.
5. Next action is a w4h deficit diagnosis, not 20%/25% extension, cap increase,
   gain tuning, or friction-model expansion.

Stage 576w4h_forward_combined_deficit_diagnosis result:
1. The w4f replay target is CONTACT_0125 + CORE_0100.
2. The w4g solved combined branch preserves CORE_0100 support exactly at
   about 0.01219329 N.
3. The remaining ~2.257 mN deficit comes from solved contact force.
4. The next branch is w4i: bounded normal/contact recovery around the combined
   branch, still only over 12.5% -> 15%.

Stage 576w4i_bounded_normal_contact_recovery_microtest result:
1. dn_extra = 4 um is the smallest passing normal/contact recovery branch at
   15%.
2. Reset/reuse totals are about 0.030414741 N with stable film state.
3. This branch was selected for forward extension.

Stage 576w4j_selected_normal_core_forward_extension_025 result:
1. Selected DN4 + CORE_0100 fails at 20%.
2. F_total_support is only about 0.026274896 N.
3. Film state remains stable, but active contact area collapses to about
   0.0617 and MaxTn rises to about 1.59 MPa.
4. 25% was not attempted.
5. Next action is w4k: diagnose/recover the 20% contact-localization failure.
```

Preserved paper-output branch:

```text
577i-closeout: 577i is frozen as a postprocessing mixed-lubrication proxy
closeout note: 577i_stage577_fixed_asperity_closeout.md
figure note: 577i_stage577_fixed_asperity_figures_diagnostic.md
```

577i figure/caption constraint:

```text
selected curves are visually interpretable
spatial plots show local rather than full-surface asperity activation
figure captions state that 577i is postprocessing calibration, not fully coupled mixed lubrication
```

## Fallback

If Stage 576w3d review or Stage 577i closeout exposes an inconsistency in the selected fixed parameter set, do not return to blind scanning. Instead create:

```text
Stage 577h3: load-constrained asperity proxy
```

The intended idea is:

```text
shape_asp = w_close*max((h_crit - h_eff)/h_scale, 0)
Fn_boundary_available = max(Fn_ref - Fn_fluid_pos, 0)
p_asp_limited = shape_asp*min(Fn_asp_raw, alpha*Fn_boundary_available)/(intop_sweep(shape_asp)+eps_asp_area)
eps_asp_area = 1e-12[m^2]
```

This combines the 577f load-sharing idea with the 577g/577h2 asperity spatial shape.

## Do not do yet

- Do not switch to free-surface modeling.
- Do not use direct `solid.Tn` as the accepted boundary pressure from the current TFF-only dataset.
- Do not call `577d`, `577e`, or first-scan `577h` checked.
- Do not present `577f`, `577g`, `577h2`, or `577i` as fully coupled mixed lubrication. They are postprocessing diagnostics/calibrations.
- Do not call `576w3d` a clean PASS; it is a MARGINAL load-closure extension.
- Do not call `576w3e` checked; it is a failed 3 um film-height transition test.
- Do not continue to a full cycle, friction, roughness, parameter scanning, or
  optical-quality postprocessing before confirming the forward extension beyond
  `62.5%`. The next allowed action is now a controlled `62.5%` REUSE
  normal-recovery/continuation diagnostic based on `576w4v`, not
  cap/gain/friction tuning.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`; 576w3g isolated the
  overload to the frozen-solid TFF pressure source, 576w3h classified the
  limiting mechanism as pressure-magnitude over-support, 576w3i showed
  pressure-limited feedback is only marginal in the first coupled microtest,
  576w3j selected a fixed pressure-cap candidate without gain tuning, 576w3k
  showed that the selected fixed cap fails at `7.5%`, 576w3p showed that
  readout-only Bpress correction does not recover support, 576w3q showed that
  solve-level baseline-geometry freeze is the accepted correction path, and
  576w3r showed that this path passes the short coupled extension to `7.50%`.

## Suggested reading before acting

1. `LATEST_MODEL.md`
2. `577i_stage577_fixed_asperity_paper_outputs_diagnostic.md`
3. `577i_stage577_fixed_asperity_time_series.csv`
4. `577h2_stage577_asperity_calibration_refined_diagnostic.md`
5. `577h2_stage577_asperity_calibration_refined_best_params.md`
6. `build_stage577i_fixed_asperity_paper_outputs.java`
7. `577i_stage577_fixed_asperity_closeout.md`
8. `576w3d_stage576_recursive_split010_film_height_release_extended_diagnostic.md`
9. `build_stage576w3d_recursive_split010_film_height_release_extended.java`
10. `verify_stage576w3d_checked.java`
11. `576w3e_stage576_recursive_split010_3um_gap_window_candidate_diagnostic.md`
12. `build_stage576w3e_recursive_split010_3um_gap_window_candidate.java`
13. `verify_stage576w3e_results.java`
14. `576w3g_stage576_tff_only_microtarget_diagnostic.md`
15. `576w3g_stage576_tff_only_microtarget_summary.csv`
16. `576w3h_stage576_tff_pressure_support_diagnostic.md`
17. `576w3h_stage576_tff_pressure_support_summary.csv`
18. `576w3i_stage576_pressure_limited_feedback_diagnostic.md`
19. `576w3i_stage576_pressure_limited_feedback_iterations.csv`
20. `576w3j_stage576_pressure_cap_refinement_diagnostic.md`
21. `576w3j_stage576_pressure_cap_refinement_summary.csv`
22. `576w3j_stage576_pressure_cap_refinement_best_branch.md`
23. `576w3k_stage576_pressure_cap_short_extension_diagnostic.md`
24. `576w3k_stage576_pressure_cap_short_extension_summary.csv`
25. `576w3k_stage576_pressure_cap_short_extension_iterations.csv`
26. `576w3l_stage576_pressure_cap_075_failure_diagnosis.md`
27. `576w3m_stage576_tff_state_transition_diagnosis.md`
28. `576w3n_stage576_coupled_handoff_replay_diagnosis.md`
29. `576w3o_stage576_solid_geometry_handoff_probe.md`
30. `576w3p_stage576_geometry_handoff_correction_microtest.md`
31. `576w3p_stage576_geometry_handoff_correction_microtest_summary.csv`
32. `verify_stage576w3p_results.java`
33. `576w3q_stage576_solve_level_geometry_handoff_correction.md`
34. `576w3q_stage576_solve_level_geometry_handoff_correction_summary.csv`
35. `verify_stage576w3q_results.java`
36. `576w3r_stage576_geometry_freeze_short_coupled_extension.md`
37. `576w3r_stage576_geometry_freeze_short_coupled_extension_summary.csv`
38. `verify_stage576w3r_results.java`
39. `576w3s_stage576_geometry_freeze_010_extension.md`
40. `576w3s_stage576_geometry_freeze_010_extension_summary.csv`
41. `verify_stage576w3s_results.java`
42. `576w3t_stage576_010_load_deficit_diagnosis.md`
43. `576w3t_stage576_010_load_deficit_diagnosis_summary.csv`
44. `576w3t_stage576_010_load_deficit_diagnosis_delta.csv`
45. `probe_stage576w3t_010_load_deficit_diagnosis.java`
46. `576w3u_stage576_support_area_diagnosis.md`
47. `576w3u_stage576_support_area_diagnosis_summary.csv`
48. `576w3u_stage576_support_area_diagnosis_delta.csv`
49. `probe_stage576w3u_support_area_diagnosis.java`
50. `576w3v_stage576_core_window_audit.md`
51. `576w3v_stage576_core_window_audit_summary.csv`
52. `576w3v_stage576_core_window_audit_branch_summary.csv`
53. `probe_stage576w3v_core_window_audit.java`
54. `576w3w_stage576_motion_core_expression_audit.md`
55. `576w3w_stage576_motion_core_expression_audit_summary.csv`
56. `576w3w_stage576_motion_core_expression_audit_branch_summary.csv`
57. `576w3w_stage576_motion_core_expression_audit_expressions.csv`
58. `probe_stage576w3w_motion_core_expression_audit.java`
59. `576w3x_stage576_contact_force_deficit_audit.md`
60. `576w3x_stage576_contact_force_deficit_audit_summary.csv`
61. `576w3x_stage576_contact_force_deficit_audit_delta.csv`
62. `576w3x_stage576_contact_force_deficit_audit_combinations.csv`
63. `probe_stage576w3x_contact_force_deficit_audit.java`
64. `576w3y_stage576_normal_position_compensation_diagnostic.md`
65. `576w3y_stage576_normal_position_compensation_verify_summary.csv`
66. `576w3z_stage576_coupled_normal_position_handoff.md`
67. `576w3z_stage576_coupled_normal_position_handoff_verify_summary.csv`
68. `576w4a_stage576_normal_position_compensation_refinement.md`
69. `576w4a_stage576_normal_position_compensation_refinement_verify_summary.csv`
70. `576w4b_stage576_compensated_handoff_confirmation.md`
71. `576w4b_stage576_compensated_handoff_confirmation_verify_summary.csv`
72. `576w4c_stage576_compensated_forward_extension_025.md`
73. `576w4c_stage576_compensated_forward_extension_025_summary.csv`
74. `576w4c_stage576_compensated_forward_extension_025_verify_summary.csv`
75. `576w4c_stage576_readback_diagnosis.md`
76. `576w4c_stage576_readback_diagnosis_states.csv`
77. `576w4c_stage576_readback_diagnosis_deltas.csv`
78. `576w4d_stage576_forward_contact_support_localization.md`
79. `576w4d_stage576_forward_contact_support_localization_summary.csv`
80. `576w4d_stage576_forward_contact_support_localization_delta.csv`
81. `576w4d_stage576_forward_contact_support_localization_combos.csv`
82. `576w4e_stage576_forward_normal_core_overlap_microtest.md`
83. `576w4e_stage576_forward_normal_core_overlap_microtest_base.csv`
84. `576w4e_stage576_forward_normal_core_overlap_microtest_variants.csv`
85. `576w4e_stage576_forward_normal_core_overlap_microtest_requirements.csv`
86. `576w4f_stage576_forward_combined_normal_core_replay.md`
87. `576w4f_stage576_forward_combined_normal_core_replay_contacts.csv`
88. `576w4f_stage576_forward_combined_normal_core_replay_core.csv`
89. `576w4f_stage576_forward_combined_normal_core_replay_summary.csv`
