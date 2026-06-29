# Latest Model Guide

This file identifies the latest local model state in this workspace.

Do not assume that "latest by timestamp" and "latest validated result" are the same thing.

## Short Answer

- Latest local model attempt by timestamp:
  - `576w5n_stage576_100_from_0875_lower_dn_scan_summary.csv`
- Latest local diagnostic note for that attempt:
  - `docs/576w5n_stage576_100_from_0875_lower_dn_scan.md`
- Latest local builder script for that attempt:
  - `build_stage576w5n_100_from_0875_lower_dn_scan.java`
- Latest saved checked forward-extension model:
  - `576w5n_stage576_100_from_0875_lower_dn_scan_checked.mph`
- status:
  - Latest checked forward endpoint is `576w5n`: `PASS` as diagnostic-only
    `100%` forward schedule extension,
    `OVERALL_DIAGNOSIS=100_FROM_0875_LOWER_DN_SCAN_HAS_PASS_BOTH`.
  - The accepted `100%` branch uses the `87.5%` `w5l` endpoint and
    `DN6` at `100%`.
  - At `100%`, reset `DN6` gives
    `F_total_support = 0.0322300492460 N`; reuse `DN6` gives
    `F_total_support = 0.0321501581771 N`.
  - Film readout stayed stable in the verifier rows:
    `AvgH = 3 um`, `MinTheta = 1`, and TFF selection remains local to
    `sel_film_swept571`.
  - This completes a clean diagnostic forward path to `100%`; it is not yet a
    backward stroke, friction, roughness, final mixed-lubrication, or imaging
    postprocessing result.
- Latest accepted compensated `10%` baseline:
  - `576w4b_stage576_compensated_handoff_confirmation_checked.mph`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=COUPLED_NORMAL_POSITION_HANDOFF_PASS_BOTH`
  - Fixed `dn_comp = 2.25 um` recovers the `10.00%` load window in both reset
    and reuse branches in the full TFF + solid handoff while keeping
    `CAP_6P5KPA`, `h_TFF = h_calc573`, and local TFF pressure readout.
- Latest read-only load-deficit diagnosis:
  - `576w3t_stage576_010_load_deficit_diagnosis.md`
  - probe: `probe_stage576w3t_010_load_deficit_diagnosis.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=010_LOAD_DEFICIT_WITH_STABLE_FILM_STATE`
- Latest read-only support-area diagnosis:
  - `576w3u_stage576_support_area_diagnosis.md`
  - probe: `probe_stage576w3u_support_area_diagnosis.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=SUPPORT_AREA_SHRINK_WITH_CONTACT_LOSS`
- Latest read-only core-window audit:
  - `576w3v_stage576_core_window_audit.md`
  - probe: `probe_stage576w3v_core_window_audit.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=CORE_WINDOW_LAG_READOUT_TESTED`
- Latest read-only motion/core-expression audit:
  - `576w3w_stage576_motion_core_expression_audit.md`
  - probe: `probe_stage576w3w_motion_core_expression_audit.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=MOTION_EXPRESSIONS_AND_EXPLICIT_CORE_READOUT_TESTED`
- Latest read-only contact-force deficit audit:
  - `576w3x_stage576_contact_force_deficit_audit.md`
  - probe: `probe_stage576w3x_contact_force_deficit_audit.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=CONTACT_FORCE_DROP_PLUS_SUPPORT_AREA_DEFICIT`
- Latest bounded normal-position compensation diagnostic:
  - `576w3y_stage576_normal_position_compensation_diagnostic.md`
  - builder: `build_stage576w3y_normal_position_compensation_diagnostic.java`
  - verifier: `verify_stage576w3y_results.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=NORMAL_POSITION_COMPENSATION_RECOVERS_010_LOAD_BOTH`
- Latest coupled normal-position handoff diagnostic:
  - `576w3z_stage576_coupled_normal_position_handoff.md`
  - builder: `build_stage576w3z_coupled_normal_position_handoff.java`
  - verifier: `verify_stage576w3z_results.java`
  - status: `PASS` as diagnostic, with read-back
    `OVERALL_DIAGNOSIS=COUPLED_NORMAL_POSITION_HANDOFF_MIXED_PASS_MARGINAL`
- Latest fixed normal-position compensation refinement:
  - `576w4a_stage576_normal_position_compensation_refinement.md`
  - builder: `build_stage576w4a_normal_position_compensation_refinement.java`
  - verifier: `verify_stage576w4a_results.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=NORMAL_POSITION_COMPENSATION_2P25UM_PASS_BOTH`
- Latest fixed compensated handoff confirmation:
  - `576w4b_stage576_compensated_handoff_confirmation.md`
  - builder: `build_stage576w4b_compensated_handoff_confirmation.java`
  - verifier: `verify_stage576w4b_results.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=COUPLED_NORMAL_POSITION_HANDOFF_PASS_BOTH`
- Latest fixed compensated forward-extension attempt:
  - `576w4c_stage576_compensated_forward_extension_025.md`
  - builder: `build_stage576w4c_compensated_forward_extension_025.java`
  - verifier: `verify_stage576w4c_results.java`
  - status: `FAIL`,
    `OVERALL_DIAGNOSIS=COMPENSATED_FORWARD_EXTENSION_025_FAILS`
- Latest read-only forward-extension failure diagnosis:
  - `576w4c_stage576_readback_diagnosis.md`
  - probe: `probe_stage576w4c_readback_diagnosis.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=CONTACT_AND_SUPPORT_AREA_DRIFT_BEYOND_010`
- Latest forward contact/support localization:
  - `576w4d_stage576_forward_contact_support_localization.md`
  - probe: `probe_stage576w4d_forward_contact_support_localization.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=NORMAL_UNLOADING_PLUS_CORE_SUPPORT_SHRINK`
- Latest forward normal/core-overlap microtest:
  - `576w4e_stage576_forward_normal_core_overlap_microtest.md`
  - probe: `probe_stage576w4e_forward_normal_core_overlap_microtest.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=COMBINED_NORMAL_AND_CORE_OVERLAP_REQUIRED`
- Latest explicit combined normal/core replay:
  - `576w4f_stage576_forward_combined_normal_core_replay.md`
  - probe: `probe_stage576w4f_forward_combined_normal_core_replay.java`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=EXPLICIT_CORE_REPLAY_PLUS_NORMAL_REPLAY_TESTED`
- Latest solved combined normal/core microtest:
  - `576w4g_stage576_forward_combined_normal_core_solve_microtest.md`
  - builder: `build_stage576w4g_forward_combined_normal_core_solve_microtest.java`
  - verifier: `verify_stage576w4g_results.java`
  - status: `MARGINAL` as diagnostic-only,
    `OVERALL_DIAGNOSIS=COMBINED_NORMAL_CORE_SOLVE_MARGINAL`
  - combined normal + `CORE_0100` improves `15%` load to about `0.02808 N`
    in both reset and reuse, but it remains below the preferred
    `0.030-0.033 N` pass window.
- Latest combined-branch deficit diagnosis:
  - `576w4h_stage576_forward_combined_deficit_diagnosis.md`
  - probe: `probe_stage576w4h_forward_combined_deficit_diagnosis.py`
  - status: `PASS`,
    `OVERALL_DIAGNOSIS=CONTACT_RECOVERY_INSUFFICIENT_SUPPORT_MATCHES_REPLAY`
  - `CORE_0100` support matches the replay result; the missing load is from
    solved contact force and contact area remaining below the `12.5%` target.
- Latest selected normal/core forward extension attempt:
  - `576w4j_stage576_selected_normal_core_forward_extension_025.md`
  - builder: `build_stage576w4j_selected_normal_core_forward_extension_025.java`
  - status: `FAIL`,
    `OVERALL_DIAGNOSIS=SELECTED_NORMAL_CORE_FORWARD_EXTENSION_025_FAILS`
  - fixed `dn_extra = 4 um` and `CORE_0100` fail at `20%`; the film state is
    stable but contact is highly localized with `MaxTn ~= 1.59 MPa`.
- Latest 20% bounded normal recovery scan:
  - `576w4k_stage576_020_failure_localization_recovery.md`
  - builder: `build_stage576w4k_020_failure_localization_recovery.java`
  - status: `FAIL`,
    `OVERALL_DIAGNOSIS=020_FAILURE_RECOVERY_FAILS`
  - `dn_extra = 4-8 um` raises contact force but does not recover contact area;
    `MaxTn` remains about `1.6 MPa`.
- Latest split 20% contact-path diagnostic:
  - `576w4l_stage576_split_020_contact_path_diagnostic.md`
  - builder: `build_stage576w4l_split_020_contact_path_diagnostic.java`
  - status: `FAIL`,
    `OVERALL_DIAGNOSIS=SPLIT_020_CONTACT_PATH_FAILS`
  - `DN8` passes at `17.5%` but fails at `20%`.
- Latest contact-collapse onset scan:
  - `576w4m_stage576_contact_collapse_onset_scan.md`
  - builder: `build_stage576w4m_contact_collapse_onset_scan.java`
  - status: `FAIL`,
    `OVERALL_DIAGNOSIS=CONTACT_COLLAPSE_ONSET_SCAN_FAILS_BEFORE_020`
  - `DN8` passes at `17.5%` and fails at `18.0%` due to a contact stress spike.
- Latest 18% normal-compensation window scan:
  - `576w4n_stage576_0180_dn_window_scan.md`
  - builder: `build_stage576w4n_0180_dn_window_scan.java`
  - status: `FAIL`,
    `OVERALL_DIAGNOSIS=DN_WINDOW_0180_FAILS`
  - all tested `dn_extra = 4-8 um` branches fail at `18.0%`.
- Latest direct 18% reset scan:
  - `576w4o_stage576_direct_0180_reset_scan.md`
  - builder: `build_stage576w4o_direct_0180_reset_scan.java`
  - status: `FAIL`,
    `OVERALL_DIAGNOSIS=DIRECT_0180_RESET_FAILS`
  - direct `15% -> 18%` reset reproduces the same contact/gap jump, so the
    failure is tied to the `18%` position/contact mapping rather than
    `17.5%` solid-state inheritance.
- Latest 18% contact/gap geometry audit:
  - `576w4p_stage576_0180_contact_gap_geometry_audit.md`
  - builder: `build_stage576w4p_0180_contact_gap_geometry_audit.java`
  - status: `FAIL` as geometry audit,
    `OVERALL_DIAGNOSIS=CONTACT_GAP_GEOMETRY_AUDIT_FAILS`
- Latest contact-spike area audit:
  - `576w4q_stage576_contact_spike_area_audit.md`
  - builder: `build_stage576w4q_contact_spike_area_audit.java`
  - status: diagnostic `PASS` in interpretation: high-stress area/load
    contributions are zero despite pointwise `MaxTn` spikes.
- Latest robust contact forward extension:
  - `576w4s_stage576_fncontact_robust_forward_extension_025.md`
  - builder: `build_stage576w4s_fncontact_robust_forward_extension_025.java`
  - status: `FAIL` at `25%`, because capped film support collapses and
    `F_total_support ~= 0.02509 N`.
- Latest 25% solid recovery scan:
  - `576w4t_stage576_025_solid_recovery_scan.md`
  - builder: `build_stage576w4t_025_solid_recovery_scan.java`
  - verifier: `verify_stage576w4t_results.py`
  - status: `PASS` as diagnostic-only,
    `OVERALL_DIAGNOSIS=025_SOLID_RECOVERY_HAS_VERIFIED_PASS_CANDIDATE`
  - best branch: `NORMAL_PLUS_CORE_0100_DN10P0UM_RESET_SOLIDDN16P0UM`,
    `F_total_support = 0.0308078281079 N`.
- Latest 25% compensation schedule confirmation:
  - `576w4u_stage576_025_compensation_schedule_confirmation.md`
  - model: `576w4u_stage576_025_compensation_schedule_confirmation_checked.mph`
  - builder: `build_stage576w4u_025_compensation_schedule_confirmation.java`
  - verifier: `verify_stage576w4u_results.py`
  - status: `PASS` as diagnostic-only,
    `OVERALL_DIAGNOSIS=025_COMPENSATION_SCHEDULE_PASS_BOTH`
  - schedule: `DN10` through `22.5%`, then `DN16` at `25%`
  - reset/reuse endpoint:
    `F_total_support = 0.0308078281079 N`,
    `AvgH ~= 3.000543 um`,
    `MinTheta ~= 0.999983`.

- Latest successful fixed pressure-cap refinement:
  - `576w3j_stage576_pressure_cap_refinement_microtest_results.mph`
  - status: `PASS`, selected candidate `CAP_6P5KPA`

- Latest read-only failure diagnosis before that attempt:
  - `576w3e_stage576_failure_diagnosis.md`

- Latest non-FAIL physical-coupling extension diagnostic:
  - `576w3d_stage576_recursive_split010_film_height_release_extended_checked.mph`
  - status: `MARGINAL`, not clean `PASS`

- Latest checked paper-output postprocessing result:
  - `577i_stage577_fixed_asperity_paper_outputs_results.mph`
- Latest paper-output closeout note:
  - `577i_stage577_fixed_asperity_closeout.md`

- Latest local TFF-only conserved-film milestone:
  - `577a_stage577_conserved_3um_local_tff_check_results.mph`
- Latest local depletion / low-film activation milestone:
  - `577b_stage577_conserved_depletion_rupture_check_results.mph`
- Latest local mixed-lubrication postprocess milestone:
  - `577c_stage577_mixed_lubrication_boundary_friction_results.mph`
- Latest local load-sharing boundary-pressure milestone:
  - `577f_stage577_load_sharing_boundary_pressure_results.mph`
- Latest local asperity-pressure proxy milestone:
  - `577g_stage577_contact_or_asperity_boundary_model_results.mph`
- Latest local asperity-calibration attempt:
  - `577h2_stage577_asperity_calibration_refined_results.mph`
  - status: `PASS`, refined small scan found 53 strong-pass parameter sets
- Latest local fixed-parameter paper-output attempt:
  - `577i_stage577_fixed_asperity_paper_outputs_results.mph`
  - status: `PASS`, reproduced the selected 577h2 calibration metrics

- Latest early-stroke checked and independently verified milestone:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`
- Latest early-stroke checked milestone note:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md`
- Latest early-stroke checked milestone verifier:
  - `verify_stage576w3c_checked.java`

- Latest full-path verified checked milestone:
  - `576n12_stage576_full_dynamic_recursive_checked.mph`

## Recommended Interpretation

Use the following distinction when asking another model to guide work:

- Use `576w3d...` when you want the newest physical Solid + local TFF
  extension diagnostic through 10% travel. It is `MARGINAL`, not a clean pass.
- Use `576w3e...` when you want the newest 3 um physical film-height
  consistency test. It is `FAIL`; it restored the intended film-thickness
  scale but did not close load.
- Use `576w3f...` when you want the newest controlled 3um micro-continuation
  test. It is `FAIL`; it tested `5% -> 6.25%` with TFF wall-velocity gating
  separated from structural load-feedback masking, but still over-produced
  liquid-film load.
- Use `576w3g...` when you want the newest 3um TFF pressure-source isolation
  diagnostic. It is diagnostic-only `PASS`; frozen-solid TFF-only microtargets
  already over-support load, so the current issue is the local TFF pressure
  source rather than solid feedback or drel update.
- Use `576w3h...` when you want the newest TFF pressure-support formulation
  diagnostic. It is diagnostic-only `PASS`; mask-only support tightening did
  not fix the overload, fixed `h=3 um` increased raw pressure support, and
  pressure-cap/load-limited readouts isolated the issue as pressure-magnitude
  over-support.
- Use `576w3i...` when you want the newest pressure-limited short coupled
  microtest. It is diagnostic-only `PASS`; both `CAP_7P5KPA` and
  `LOAD_LIMITED` completed `5% -> 6.25%` as `MARGINAL`, with
  `OVERALL_DIAGNOSIS=BOTH_ACCEPTED_COMPARE_STABILITY`.
- Use `576w3j...` when you want the newest fixed pressure-cap refinement
  microtest. It is diagnostic-only `PASS`; `CAP_6P5KPA` and `CAP_7P0KPA`
  passed the `5% -> 6.25%` window, `CAP_7P5KPA` was `MARGINAL`, and
  `CAP_6P5KPA` is the selected candidate for the next short extension.
- Use `576w3k...` when you want the newest short extension attempt with the
  selected `CAP_6P5KPA`. It is diagnostic-only `FAIL`: the 6.25% segment
  remains `PASS`, but the 7.5% segment fails with raw TFF pressure/theta
  collapse and under-supported capped load.
- Use `576w3l...` when you want the newest pressure-cap 7.5% failure
  localization. It is diagnostic-only `FAIL`: independent microtargets from
  the stable 6.25% checkpoint show 6.50%, 6.75%, and 7.25% as `PASS`, 7.00%
  as `MARGINAL` because of a local raw-pressure/theta warning, and 7.50% as
  `FAIL` because capped film support under-carries the normal load.
- Use `576w3m...` when you want the newest TFF state-transition diagnostic. It
  is diagnostic-only `PASS`: frozen TFF-only replay is stable at 7.00%, 7.25%,
  and 7.50%, while the coupled final states reproduce the 7.00% theta/raw
  pressure warning and 7.50% load-support failure. The current limiting
  mechanism is therefore classified as coupled handoff rather than TFF-only
  PDE instability.
- Use `576w3n...` when you want the newest coupled handoff replay diagnostic.
  It is diagnostic-only `PASS`: solid/geometry state handoff is dominant.
  Replaying baseline pressure with coupled solid states reproduces the failure,
  while replaying coupled pressure with baseline solid state stays stable.
- Use `576w3o...` when you want the newest solid/geometry handoff probe. It is
  diagnostic-only `PASS`: the failing solid states are classified as
  `GAP_MASK_GEOMETRY_SHIFT_DOMINANT` because MinGap shifts more negative and
  `Bpress/A_swept` increases while capped support drops.
- Use `576w3p...` when you want the newest geometry-handoff correction
  diagnostic. It is diagnostic-only `PASS`: all readout-only `Bpress`
  correction variants failed, so the next step should be a solve-level
  geometry/contact handoff correction rather than cap increase, gain tuning, or
  friction-model expansion.
- Use `576w3q...` when you want the newest solve-level geometry handoff
  correction diagnostic. It is diagnostic-only `PASS`: baseline/frozen TFF
  geometry passes at `7.00%` and `7.50%` even after solid replay, while current
  failing geometry still fails. The next step is a short coupled
  geometry-freeze extension, not 10% continuation or cap/gain tuning.
- Use `576w3r...` when you want the newest geometry-freeze short coupled
  extension diagnostic. It is diagnostic-only `PASS`: both solid reset and
  solid reuse pass over `6.25% -> 7.00% -> 7.50%` using the accepted frozen
  TFF geometry source. The next diagnostic step is only a controlled extension
  to `10%`, not full-cycle progression.
- Use `576w3s...` when you want the newest geometry-freeze 10% extension
  diagnostic. It is diagnostic-only `MARGINAL`: both solid reset and solid
  reuse remain finite with `AvgH ~= 3.34 um`, `MinTheta ~= 0.9999`, and local
  TFF selection, but verified `F_total_support` is only about
  `0.0280-0.0281 N`, below the preferred `0.030-0.033 N` load window.
- Use `576w3t...` when you want the newest 10% load-deficit diagnosis. It is
  read-only `PASS`: the 10% deficit occurs with stable film state and is
  classified as `010_LOAD_DEFICIT_WITH_STABLE_FILM_STATE`, with contact force
  loss dominant and capped film support also decreasing from 7.50% to 10.00%.
- Use `576w3u...` when you want the newest support-area decomposition. It is
  read-only `PASS`: the 10% deficit is classified as
  `SUPPORT_AREA_SHRINK_WITH_CONTACT_LOSS`; `Bhigh`, `B_low`, valid-gap, and
  gap readouts are unchanged, while `core/A_swept` and `Bpress/A_swept`
  decrease.
- Use `576w3v...` when you want the newest core-window audit. It is read-only
  `PASS`: lagged or frozen readout positions (`0.075`, `0.0875`, `0.100`) all
  give identical `M_core/Bpress/F_support`, so a simple post-hoc lagged-core
  readout does not recover the 10% load.
- Use `576w3w...` when you want the newest motion/core-expression audit. It is
  read-only `PASS`: saved expressions are coherent, the active convention is
  `theta_lid_spatial = theta_lid_physical + offset`, and an explicit
  `READOUT_075/PLUS` core window restores about `0.753 mN` of film support but
  still leaves the 10% state `MARGINAL`.
- Use `576w3x...` when you want the newest contact-force deficit audit. It is
  read-only `PASS`: contact force and core-window support both contribute to
  the 10% deficit; restoring either one alone remains `MARGINAL`, while
  restoring both reaches `PASS` in the readout combination test.
- Use `576w3y...` when you want the newest bounded normal-position
  compensation diagnostic. It is diagnostic-only `PASS`: `dn_comp = 2 um` and
  `4 um` recover the `0.030-0.033 N` load window in both reset and reuse
  branches, with stable `AvgH ~= 3.34 um`, `MinTheta ~= 0.9999`, and local TFF
  selection. It is not a final coupled baseline; the compensation still needs
  to be inserted into the coupled handoff workflow.
- Use `576w3z...` when you want the first coupled normal-position handoff test.
  It is diagnostic-only `PASS` with mixed checked status: fixed
  `dn_comp = 2 um` keeps the film state stable and makes reuse `PASS`, but reset
  reads back just below the load window as `MARGINAL`.
- Use `576w4a...` when you want the newest fixed normal-position compensation
  refinement. It is diagnostic-only `PASS`: fixed `dn_comp = 2.25 um` gives
  checked reset/reuse totals of about `0.030225 N` and `0.030227 N`, with stable
  `AvgH ~= 3.34 um` and `MinTheta ~= 0.9999`.
- Use `576w4b...` when you want the newest full compensated `7.5% -> 10%`
  handoff confirmation. It is diagnostic-only `PASS`: fixed
  `dn_comp = 2.25 um` gives checked reset/reuse totals of about `0.030225 N`
  and `0.030227 N`, with stable `AvgH ~= 3.34 um`, `MinTheta ~= 0.9999`, and
  local TFF selection.
- Use `576w4c...` when you want the newest bounded continuation attempt beyond
  `10%`. It is diagnostic-only `FAIL`: fixed `dn_comp = 2.25 um` keeps the
  film state stable and local, but `10% -> 12.5%` is only `MARGINAL` and
  `15%` fails with checked `F_total_support ~= 0.02390 N`. Do not use it as
  an accepted extension baseline; use it to diagnose contact/support drift.
- Use `576w4c_readback...` when you want the newest read-only diagnosis of
  that failure. It is `PASS` as a probe and classifies the `10% -> 15%` loss
  as `CONTACT_AND_SUPPORT_AREA_DRIFT_BEYOND_010`: total load drops by about
  `6.33 mN`, with about `45%` from contact-force loss and about `55%` from
  capped support loss.
- Use `576w4d...` when you want the newest localization of the `15%` failure.
  It is read-only `PASS` and classifies the mechanism as
  `NORMAL_UNLOADING_PLUS_CORE_SUPPORT_SHRINK`: active contact area, `MaxTn`,
  `BpressOverSwept`, and `McoreOverSwept` all drop from `12.5%` to `15%`,
  while `Bhigh`, `B_low`, valid-gap admission, theta, and film height remain
  stable.
- Use `576w4e...` when you want the newest read-only normal/core-overlap
  microtest. It is diagnostic `PASS` and classifies the next requirement as
  `COMBINED_NORMAL_AND_CORE_OVERLAP_REQUIRED`: normal-only and core-only
  restoration do not recover the `15%` state, while contact near `12.5%` plus
  core/Bpress overlap near the accepted `10%` state reaches the load window.
- Use `576w4f...` when you want the newest explicit replay on the actual `15%`
  pressure field. It is diagnostic `PASS`: contact `12.5%` plus explicit
  `CORE_0100` gives `F_total ~= 0.03034 N`, while `CORE_0125` is only
  `MARGINAL` and actual `CORE_0150` fails.
- Use `576w4g...` when you want the newest solved test of that combined
  normal/core correction. It is diagnostic `MARGINAL`: baseline, normal-only,
  and `CORE_0100`-only branches fail; normal + `CORE_0100` reaches about
  `0.02808 N` with stable `AvgH`, theta, raw pressure, and local TFF
  selection, but does not reach the preferred load window.
- Use `576w4h...` when you want the newest explanation of why `576w4g` remains
  marginal. It is readback `PASS`: the support term matches the w4f replay, so
  the remaining deficit is contact recovery, not pressure cap, core support,
  theta, or TFF selection.
- Use `576w4i...` when you want the selected `15%` recovery candidate:
  `dn_extra = 4 um`, `CORE_0100`, reset/reuse `PASS`.
- Use `576w4j...` when you want the newest attempt to continue toward `25%`.
  It fails at `20%`; do not treat it as a 25% baseline.
- Use `576w4o...` when you want the newest localization of the forward
  extension blocker. It shows the current limiting event is an `18%`
  contact/gap mapping jump, not a TFF pressure/theta failure and not inherited
  from the `17.5%` solid state.
- Use `577i...` when you want the newest checked fixed-parameter paper-output
  postprocessing result.
- Use `577h2...` when you want the roughness/asperity parameter calibration
  source for 577i.
- Use `577h...` only when you want the earlier failed small-scan calibration
  diagnostic; do not treat it as checked.
- Use `577f...` when you want the newest checked load-sharing boundary-pressure
  diagnostic.
- Use `576w3c...checked` when you want the newest explicitly verified
  clean PASS early-stroke split structural/TFF load-balance result.
- Use `576n12...checked` when you want the older full-path verified reference.

Do not interpret `577f`, `577g`, `577h2`, or `577i` as fully coupled
solid-contact-TFF solves. They are postprocessing diagnostics based on the
passed 577a/577b local-film checks. `577g` passes through the asperity proxy
path; the direct `solid.Tn` path is reported as not accepted for the current
dataset.

## Latest Experimental Main-Line Attempt

### Model

```text
577i_stage577_fixed_asperity_paper_outputs_results.mph
```

### Companion files

```text
577a_stage577_conserved_3um_local_tff_check_diagnostic.md
577b_stage577_conserved_depletion_rupture_check_diagnostic.md
577c_stage577_mixed_lubrication_boundary_friction_diagnostic.md
577d_stage577_postprocess_calibration_sensitivity_diagnostic.md
577e_stage577_weakly_coupled_depleted_tff_diagnostic.md
577f_stage577_load_sharing_boundary_pressure_diagnostic.md
577g_stage577_contact_or_asperity_boundary_model_diagnostic.md
577h_stage577_asperity_calibration_diagnostic.md
577h2_stage577_asperity_calibration_refined_diagnostic.md
577i_stage577_fixed_asperity_paper_outputs_diagnostic.md
577i_stage577_fixed_asperity_time_series.csv
build_stage577a_conserved_3um_local_tff_check.java
build_stage577b_conserved_depletion_rupture_check.java
build_stage577c_mixed_lubrication_boundary_friction.java
build_stage577d_postprocess_calibration_sensitivity.java
build_stage577e_weakly_coupled_depleted_tff.java
build_stage577f_load_sharing_boundary_pressure.java
build_stage577g_contact_or_asperity_boundary_model.java
build_stage577h_asperity_calibration.java
build_stage577h2_asperity_calibration_refined.java
build_stage577i_fixed_asperity_paper_outputs.java
577a_stage577_conserved_3um_local_tff_check_results.mph
577b_stage577_conserved_depletion_rupture_check_results.mph
577c_stage577_mixed_lubrication_boundary_friction_results.mph
577f_stage577_load_sharing_boundary_pressure_results.mph
577g_stage577_contact_or_asperity_boundary_model_results.mph
577h_stage577_asperity_calibration_results.mph
577h2_stage577_asperity_calibration_refined_results.mph
577i_stage577_fixed_asperity_paper_outputs_results.mph
```

### What it is

This is the newest Stage 577 checked diagnostic branch. It keeps `576w3c` as
the input baseline, but replaces the nonphysical final-film interpretation of
`h_calc576w3c = h_calc573 + drel576w3c` with a staged local-film diagnostic:

```text
577a: local TFF with conserved h_TFF = 3 um
577b: postprocessed conserved-film depletion / low-film activation
577c: postprocessed mixed-lubrication boundary-friction diagnostic
577d: calibration/sensitivity scan, useful but FAIL on monotonic-with-dh
577e: weakly coupled depleted TFF attempt, FAIL due impractical solve time
577f: load-sharing boundary-pressure diagnostic
577g: asperity-pressure proxy diagnostic
577h: first small asperity-calibration scan, useful but FAIL
577h2: refined asperity-calibration scan, PASS
577i: fixed-parameter paper-output postprocess, PASS
```

### Current conclusion

Stage 577i is the preferred current paper-output postprocessing result for the
selected asperity-proxy mixed-lubrication parameters, while `576w3c` remains
the preferred clean PASS early-stroke structural / TFF load-balance baseline.
Stage 576w3d extends the physical Solid + local TFF path to 10%, but it is only
`MARGINAL`. Stage 576w3e is newer by timestamp and tests 3 um-scale film height
without adding `drel` to the TFF film height, but it fails in segment 3.

According to the 577 diagnostics:

```text
577a CHECKED_STATUS=PASS
577b CHECKED_STATUS=PASS
577c CHECKED_STATUS=PASS
577d CHECKED_STATUS=FAIL
577e CHECKED_STATUS=FAIL
577f CHECKED_STATUS=PASS
577g CHECKED_STATUS=PASS
577h CHECKED_STATUS=FAIL
577h2 CHECKED_STATUS=PASS

577a h_avg = 3.000e-6 m exactly over the local film region
577b h_avg range = 2.936e-6 to 3.098e-6 m
577c mu_TFF_alt max = 0.0020777
577c mu_total max =
  0.010095 for mu_boundary=0.02
  0.023418 for mu_boundary=0.05
  0.045622 for mu_boundary=0.10
  0.090030 for mu_boundary=0.20
577f load-sharing mu_total max = about 0.0887 to 0.0904
577g asperity-proxy mu_total max = 0.116789
577h first small scan PASS_COUNT = 0/24
577h2 refined scan STRONG_PASS_COUNT = 53/180
577h2 selected candidate:
  dh_deplete = 2.5 um
  h_crit = 1.0 um
  K_asp_eff = 7.5 kPa
  mu_boundary = 0.20
  mu_total_max = 0.0884728
  A_close/A_film_mean = 0.0739728
  Fn_asp/Fn_ref_max = 1.00473
577i fixed-parameter paper outputs:
  TIME_COUNT = 41
  MU_TOTAL_MAX = 0.0884728
  A_CLOSE_OVER_AFILM_MEAN = 0.0739728
  FN_ASP_OVER_FNREF_MAX = 1.00473
  CHECKED_STATUS = PASS

576w3d load-closure extension:
  VERIFY_STATUS = MARGINAL
  F_contact = 0.0107429622941 N
  F_film = 0.0250608173034 N
  F_total = 0.0358037795975 N
  DrelSaturationRatio = 0.277447704960
  MinTheta = 0.999999599159
  LowThetaAreaRatio02 = 0
  TFF selection equals sel_film_swept571

576w3d mask-review:
  MASK_REVIEW_PROBE_STATUS = PASS
  segment 3 pressure/relaxed/solid = sol337 / sol338 / sol339
  segment 4 pressure/relaxed/solid = sol379 / sol380 / sol381
  F_contact change from 7.5% to 10% = -0.0045674421867 N
  F_film_load_masked change from 7.5% to 10% = +0.0049415912453 N
  F_total change from 7.5% to 10% = +0.0003741490585 N
  A_wet_load/A_swept = 0.0454293294855 -> 0.0517822557137
  mean wet-mask load pressure = 5057.23725186 Pa -> 5526.53150638 Pa

576w3d mask-definition review:
  MASK_DEFINITION_REVIEW_STATUS = PASS
  Bfilm573 = g_pair_valid573*B_low573*B_high573
  p_load573 = M_core573*Bfilm573*(tff.p-p_amb573)
  Bfilm is controlled by g_pair_safe573, not directly by tff.p or h_calc576w3d
  Bfilm > 0.5 area / core = 0.480489888885 -> 0.577035660672
  Bfilm > 0.9 area / core = 0.463955153815 -> 0.555662183476

576w3d mask-candidate review:
  MASK_CANDIDATE_REVIEW_STATUS = PASS
  CANDIDATE_ACCEPTANCE = REVIEW
  h_cap=15 um, dh_cap=5 um gives F_total_alt = 0.0317624666 N at 7.5%
    and 0.0275761014 N at 10%
  h_cap=20 um family gives better 10% closure, but leaves 7.5% slightly high
  no scanned candidate gives clean PASS at both 7.5% and 10%

576w3d candidate spatial review:
  MASK_CANDIDATE_SPATIAL_EXPORT_STATUS = PASS
  h_cap=15 um likely over-corrects the 10% load band
  h_cap=20 um, dh_cap=5 um preserves more central swept load while removing
    localized high-gap over-support patches
  preferred next solved candidate, if proceeding:
    576w3e_stage576_recursive_split010_gap_window_candidate

576w3e 3um gap-window candidate:
  CHECKED_STATUS = FAIL
  VERIFY_STATUS = FAIL
  failed in segment 3, 5% -> 7.5%; 10% was not attempted
  FINAL_FCONTACT = 0.00568746688190 N
  FINAL_FFILM = 0.0748136883950 N
  FINAL_FTOTAL = 0.0805011552769 N
  FINAL_AVG_HEIGHT = 3.86495287430e-06 m
  FINAL_MIN_THETA = 0.736878212489
  VERIFY_TFF_SELECTION_EQUALS_SWEPT = true

576w3e failure diagnosis:
  W3E_FAILURE_DIAGNOSIS_STATUS = PASS
  inherited 5% with w3e 3um/mask gives F_total = 0.0287348411563 N
  first 7.5% TFF recomputation gives F_total = 0.0562919179888 N
  final failed state gives F_total = 0.0805011552769 N
  evidence does not support inherited 5% pressure as the primary failure cause
  evidence supports immediate 7.5% 3um TFF pressure over-production plus
    wet-load mask admission and coupled wall-velocity/load-feedback gating

576w3f micro 6.25% decoupled-mask candidate:
  CHECKED_STATUS = FAIL
  VERIFY_STATUS = FAIL
  tested 5% -> 6.25%; 7.5% and 10% were not attempted
  wall velocity used M_drain573*Bfilm573
  structural feedback used max(p_load576w3f,0)
  FINAL_FCONTACT = 0.00653342006671 N
  FINAL_FFILM = 0.0774412745719 N
  FINAL_FTOTAL = 0.0839746946386 N
  FINAL_AVG_HEIGHT = 3.94192970705e-06 m
  FINAL_MIN_THETA = 0.999976153096
  VERIFY_TFF_SELECTION_EQUALS_SWEPT = true

576w3g TFF-only microtarget pressure-source diagnostic:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  DIAGNOSIS_CLASS = PRESSURE_SOURCE_OVER_SUPPORT
  frozen solid/release, no relaxation or solid update
  targets = 0.055, 0.060, 0.0625
  target 0.055: F_total_frozen = 0.0470957506334 N
  target 0.060: F_total_frozen = 0.0494792630215 N
  target 0.0625: F_total_frozen = 0.0506865935341 N
  AvgH = 3.41160 to 3.41185 um
  MinTheta = 0.999913687 to 0.999925927
  TFF selection equals sel_film_swept571

576w3h TFF pressure-support formulation diagnostic:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  DIAGNOSIS_CLASS = PRESSURE_MAGNITUDE_OVER_SUPPORT
  frozen solid/release, no relaxation or solid update
  height modes = GAP_HTFF and FIXED_3UM_HEIGHT
  support variants = RAW_H20D5, MASK_H15D5, MASK_H12D4,
    CAP_5KPA, CAP_7P5KPA, CAP_10KPA, LOAD_LIMITED
  GAP_HTFF raw F_total_frozen = 0.0470957506334 to 0.0506865935341 N
  GAP_HTFF MASK_H12D4 F_total_frozen =
    0.0411622338956 to 0.0439588760751 N
  GAP_HTFF CAP_7P5KPA F_total_frozen =
    0.0326466833365 to 0.0327105881254 N
  GAP_HTFF LOAD_LIMITED F_total_frozen = about 0.030000000000 N
  FIXED_3UM raw F_total_frozen = 0.0551962568507 to 0.0592931738057 N
  FIXED_3UM raw MaxP = 79.3 to 89.4 kPa
  TFF selection equals sel_film_swept571

576w3i pressure-limited feedback microtest:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  CAP_BRANCH_STATUS = MARGINAL
  LOAD_LIMITED_BRANCH_STATUS = MARGINAL
  OVERALL_DIAGNOSIS = BOTH_ACCEPTED_COMPARE_STABILITY
  tested only 5% -> 6.25%
  CAP_7P5KPA:
    F_contact = 0.0191075779330 N
    F_film_support = 0.0143476435423 N
    F_total_support = 0.0334552214753 N
    Residual = 0.00131017716771 N
  LOAD_LIMITED:
    F_contact = 0.0185740056183 N
    F_film_support = 0.0106393361857 N
    F_total_support = 0.0292133418040 N
    Residual = 0.000410391416565 N
  AvgH = 3.35 to 3.37 um
  MinTheta = about 0.99995
  LowThetaAreaRatio02 = 0
  TFF selection equals sel_film_swept571

576w3j fixed pressure-cap refinement microtest:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = PASS
  OVERALL_DIAGNOSIS = FIXED_CAP_PASS_SELECT_BEST_BRANCH
  tested only 5% -> 6.25%
  cap monotonicity check = true
  CAP_6P5KPA:
    status = PASS
    F_contact = 0.0191506708460 N
    F_film_support = 0.0124357214657 N
    F_total_support = 0.0315863923117 N
    Residual = 0.00114312384144 N
  CAP_7P0KPA:
    status = PASS
    F_total_support = 0.0325100989913 N
  CAP_7P5KPA:
    status = MARGINAL
    F_total_support = 0.0334552214752 N
  LOAD_LIMITED_REF:
    status = MARGINAL
    F_total_support = 0.0292133418041 N
  selected next candidate = CAP_6P5KPA
  AvgH = 3.35 to 3.37 um
  MinTheta = about 0.99995
  LowThetaAreaRatio02 = 0
  TFF selection equals sel_film_swept571

576w3k pressure-cap short extension:
  CHECKED_STATUS = DIAGNOSTIC_ONLY
  VERIFY_STATUS = FAIL
  OVERALL_DIAGNOSIS = CAP_6P5_FAILS_AT_075
  selected cap = CAP_6P5KPA
  tested 5% -> 6.25% -> 7.5%
  segment 3 / 6.25%:
    status = PASS
    F_total_support = 0.0315863923117 N
    AvgH = 3.35121338445 um
    MinTheta = 0.999948453776
  segment 4 / 7.5%:
    status = FAIL
    F_contact = 0.0183359898446 N
    F_film_support = 0.00609741098240 N
    F_total_support = 0.0244334008270 N
    F_film_raw_swept = 9.35661253392 N
    MaxP_raw = 58.4 MPa
    MinTheta = 0.000681273035266
    AvgH = 3.28377925627 um
    DrelSaturationRatio = 0.124308574393
    TFF selection equals sel_film_swept571
```

Important limitation:

- `577b`, `577c`, `577f`, and `577g` are postprocessing diagnostics.
- They do not feed depleted film height or boundary friction back into the TFF
  PDE or the solid/contact mechanics.
- `577d` failed the monotonic-with-`dh_deplete` criterion.
- `577e` failed because direct depleted film height in `ffp1.hw1` was too slow.
- `577h` failed because no first-scan parameter set satisfied area/load/COF
  filters simultaneously.
- `577h2` passed by lowering `K_asp_eff` and increasing `mu_boundary`, reducing
  the best candidate `Fn_asp/Fn_ref_max` to about `1.00`.
- `577i` fixed that parameter set and generated checked time-series and result
  plot groups for paper-facing interpretation.
- The next physical upgrade should only be attempted after preserving these
  checked files as references.
- The next physical-coupling action should diagnose the 576w3d marginal load
  offset before extending beyond 10% or retuning controller parameters. The
  first mask-review diagnostic indicates film-load over-support from wet-load
  mask expansion plus pressure-history support, not selection drift, theta
  collapse, drel saturation, or increasing pressure spikes. The follow-up
  mask-definition review shows that the mask expansion is driven by solid
  contact-pair gap redistribution into the active `Bfilm573` window. A
  candidate upper-gap mask can reduce over-support in postprocessing, but it
  did not succeed when solved in 576w3e with 3 um-scale film height. The
  576w3e failure diagnosis shows that the inherited 5% state is acceptable
  under the 3um/w3e mask, but the first 7.5% TFF recomputation immediately
  over-produces liquid-film load. The 576w3f micro-continuation then tested
  `5% -> 6.25%` with `h_TFF=h_calc573`, `drel` as solid release only, and
  separated TFF wall-velocity / structural load-feedback masks. It still
  failed with `F_total=0.0839746946386 N`. The 576w3g TFF-only diagnostic then
  froze solid/release and showed overload already at `5% -> 5.5%`:
  `F_total_frozen=0.0470957506334 N`. Stage 576w3h then showed that fixed
  `h=3 um` worsens raw pressure support and mask-only tightening remains
  overloaded, while `CAP_7P5KPA` and `LOAD_LIMITED` return the frozen totals to
  the target window. Stage 576w3i then showed that both pressure-limited coupled
  branches can complete `5% -> 6.25%` as `MARGINAL`. Stage 576w3j refined the
  fixed pressure-cap branch around `6.5-7.5 kPa`: `CAP_6P5KPA` and
  `CAP_7P0KPA` passed, `CAP_7P5KPA` remained marginal, and `CAP_6P5KPA` was
  selected for short extension. Stage 576w3k then showed that this fixed cap
  still fails at `7.5%`: the cap limits support pressure, but the raw TFF state
  develops a pressure/theta collapse and the capped support falls below the
  load window. The next physical-coupling action should localize the 7.5%
  failure onset, not tune controller gains or add mixed-lubrication friction.

## Latest Early-Stroke Structural/TFF Baseline

### Model

```text
576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph
```

### Companion files

```text
576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md
build_stage576w3c_recursive_split005_film_height_release_extended.java
verify_stage576w3c_checked.java
```

### Current conclusion

According to `576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md`
and `verify_stage576w3c_checked.java`:

- both early split segments passed,
- explicit indentation release plus explicit film-height release closed the
  load to the `0.03 N` target window,
- the checked model was independently read back from saved solutions
  `sol271`, `sol272`, and `sol273`.

Verified final values:

```text
F_contact       = 0.0196747609743 N
F_film          = 0.0132800398588 N
F_total         = 0.0329548008331 N
load error      = 0.00295480083306 N
field residual  = 0.000726197089437 N
min(theta)      = 0.999997361935
VERIFY_STATUS   = PASS
```

Earlier negative branch information remains useful:

```text
576u2: alpha=0.20 made the second segment worse.
576u3: beta=0.10 did not improve the second segment.
576v: finer 1.25% segments failed at the second segment.
576w/w2/w3: solid-only or insufficient film-height release did not close load.
```

The next main-line test should extend the verified `576w3c` mechanism rather
than retune `alpha`, `beta`, or segment size immediately.

## Latest Explicitly Verified Checked Result

### Model

```text
576n12_stage576_full_dynamic_recursive_checked.mph
```

### Companion files

```text
576n12_stage576_full_dynamic_recursive_checked.md
verify_stage576n12_checked.java
build_stage576m_recursive_field_relaxation.java
```

### What it is

This is the older Stage 576 result in the repository that is both:

- marked as checked in the note, and
- paired with a dedicated verification script.

### Reported acceptance state

From `576n12_stage576_full_dynamic_recursive_checked.md`, the full dynamic path
reached fraction `1.0000` with:

```text
alpha_pfb576m = 0.20
beta_relax576m = 0.10
```

Reported final checks include:

```text
F_contact       = 0.0253440421858 N
F_film          = 2.91827459255e-08 N
F_total         = 0.0253440713686 N
field residual  = 9.16350621805e-06 N
min(theta)      = 0.999999999712
all values finite = true
```

and the note marks the acceptance checks as PASS. It remains the latest
verified full-path reference, while `576w3c` is the latest verified early-stroke
split milestone.

## Which Model To Use For Guidance

If you want help deciding the next research or debugging step, tell the model:

- the latest physical-coupling extension diagnostic is `576w3d...checked`
  with `VERIFY_STATUS=MARGINAL`,
- the latest checked paper-output postprocessing result is `577i...`,
- the latest clean PASS early-stroke split milestone is `576w3c...checked`,
- the latest 3um transition failure diagnosis is
  `576w3e_stage576_failure_diagnosis.md`,
- the latest 3um micro-continuation attempt is `576w3f...results` with
  `VERIFY_STATUS=FAIL`,
- the latest 3um pressure-source isolation diagnostic is `576w3g...results`
  with `DIAGNOSIS_CLASS=PRESSURE_SOURCE_OVER_SUPPORT`,
- the latest pressure-support formulation diagnostic is `576w3h...results`
  with `DIAGNOSIS_CLASS=PRESSURE_MAGNITUDE_OVER_SUPPORT`,
- the latest pressure-limited feedback microtest is `576w3i...results`
  with `OVERALL_DIAGNOSIS=BOTH_ACCEPTED_COMPARE_STABILITY`,
- the latest fixed pressure-cap refinement microtest is `576w3j...results`
  with `OVERALL_DIAGNOSIS=FIXED_CAP_PASS_SELECT_BEST_BRANCH`; selected
  next candidate is `CAP_6P5KPA`,
- the latest short extension attempt is `576w3k...results` with
  `OVERALL_DIAGNOSIS=CAP_6P5_FAILS_AT_075`, and
- the latest verified full-path baseline is `576n12...checked`.

That gives the model both:

- the newest marginal physical extension state,
- the newest postprocessing paper-output chain, and
- the newest trusted validated reference point.

## Recommended Prompt For Another ChatGPT/Codex Session

```text
Do not stop at repository metadata.

Use LATEST_MODEL.md as the entry point:
https://github.com/LYC-WASABI/cornea/blob/main/LATEST_MODEL.md

Then read these files:
1. CONTEXT.md
2. 576w3d_stage576_recursive_split010_film_height_release_extended_diagnostic.md
3. build_stage576w3d_recursive_split010_film_height_release_extended.java
4. verify_stage576w3d_checked.java
5. 576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md
6. verify_stage576w3c_checked.java
7. 577i_stage577_fixed_asperity_closeout.md
8. 576w3g_stage576_tff_only_microtarget_diagnostic.md
9. 576w3h_stage576_tff_pressure_support_diagnostic.md
10. 576w3i_stage576_pressure_limited_feedback_diagnostic.md
11. 576w3j_stage576_pressure_cap_refinement_diagnostic.md
12. 576w3j_stage576_pressure_cap_refinement_summary.csv
13. 576w3j_stage576_pressure_cap_refinement_best_branch.md
14. 576w3k_stage576_pressure_cap_short_extension_diagnostic.md
15. 576w3k_stage576_pressure_cap_short_extension_summary.csv
16. 576w3s_stage576_geometry_freeze_010_extension.md
17. 576w3s_stage576_geometry_freeze_010_extension_summary.csv
18. verify_stage576w3s_results.java
19. 576n12_stage576_full_dynamic_recursive_checked.md
20. verify_stage576n12_checked.java

Interpret 576w3d as the newest physical extension diagnostic, but only
MARGINAL. Interpret 576w3c as the latest clean PASS early-stroke split
milestone and 576n12_checked as the older full-path verified milestone.

After reading them, tell me:
1. what the current latest attempt is,
2. why 576w3d is useful but not a clean PASS,
3. why 576w3c remains the clean PASS baseline,
4. what the last full-path validated reference state is,
5. why the next defensible action is diagnosing the 576w3d load offset before
   extending farther or tuning parameters,
6. why 576w3f shows that decoupling wall-velocity and load-feedback masks is
   insufficient,
7. why 576w3g proves frozen-solid TFF-only pressure over-support,
8. why 576w3h classifies the limiting mechanism as pressure-magnitude
   over-support,
9. why 576w3i shows pressure-limited feedback is promising but still marginal,
10. why 576w3j selects CAP_6P5KPA as the fixed pressure-cap candidate,
11. why 576w3k fails at 7.5% despite passing 6.25%,
12. why 576w3l localizes the hard failure between 7.25% and 7.50% while
    showing an intermittent 7.00% theta/raw-pressure warning,
13. why 576w3m classifies the next bottleneck as coupled handoff rather than
    TFF-only PDE instability,
14. why 576w3n classifies the handoff as solid/geometry-dominant,
15. why 576w3o classifies the solid/geometry issue as gap/mask shift,
16. why 576w3p rules out readout-only Bpress correction,
17. why 576w3q accepts solve-level baseline-geometry freeze as the correction
    path,
18. why 576w3r passes the short geometry-freeze coupled extension,
19. why 576w3s is stable at 10% but only MARGINAL because
    `F_total_support` is below the preferred load window,
20. why the next step is a targeted 10% load-deficit diagnosis, not cap
    increase, gain tuning, friction-model expansion, or full-cycle progression.
```
