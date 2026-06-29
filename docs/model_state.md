# Model State

## Latest checked forward endpoint

- model:
  - `576w5n_stage576_100_from_0875_lower_dn_scan_checked.mph`
- note:
  - `docs/576w5n_stage576_100_from_0875_lower_dn_scan.md`
- builder:
  - `build_stage576w5n_100_from_0875_lower_dn_scan.java`
- verifier:
  - `verify_stage576w5n_results.py`

Current interpretation:

- `576w5n` is the latest checked diagnostic forward endpoint.
- It extends the clean compensated forward path to `100%`.
- The accepted `100%` branch is `DN6` from the `87.5%` `w5l` endpoint:
  - RESET: `F_total_support = 0.0322300492460 N`
  - REUSE: `F_total_support = 0.0321501581771 N`
- The verifier diagnosis is
  `100_FROM_0875_LOWER_DN_SCAN_HAS_PASS_BOTH`.
- Film readouts remain stable: `AvgH = 3 um`, `MinTheta = 1`, local
  `sel_film_swept571` TFF selection.
- This remains diagnostic-only. It is not yet a full forward/backward cycle,
  friction solve, asperity/boundary friction solve, or final mixed-lubrication
  paper model.

## Stable baseline

- Baseline reference explanation:
  - `385_stage200_model_explanation.md`
- Baseline deformation postprocessing probe:
  - `probe_stage200_cornea_surface_deformation.java`

This Stage 200 branch is the trusted reference for:

- target load interpretation around `0.03 N`
- JFO-based thin-film treatment
- measured deformation and load-sharing outputs from the reference model

## Latest experimental attempt

- model:
  - `577i_stage577_fixed_asperity_paper_outputs_results.mph`
- note:
  - `577i_stage577_fixed_asperity_paper_outputs_diagnostic.md`
- builder:
  - `build_stage577i_fixed_asperity_paper_outputs.java`

Current interpretation:

- this is the newest Stage 577 checked postprocessing diagnostic
- it preserves `576w3c` as the input baseline but does not overwrite it
- `577a` passed the local TFF check with conserved `3 um` film thickness
- `577b` passed low-film / rupture activation as a postprocessing diagnostic
- `577c` passed mixed-lubrication / boundary-friction postprocessing
- `577d` is a useful failed sensitivity diagnostic because `mu_total` did not increase monotonically with `dh_deplete`
- `577e` failed because direct depleted film height in `ffp1.hw1` was too slow
- `577f` passed load-sharing boundary-pressure postprocessing
- `577g` passed the asperity-pressure proxy path; direct `solid.Tn` was reported but not accepted
- `577h` first small asperity-calibration scan failed; it is the latest attempt but not a checked milestone
- `577h2` refined asperity-calibration scan passed and is the latest checked postprocessing calibration result
- `577i` fixed the selected 577h2 parameter set and passed as the latest checked paper-output postprocessing result
- it outputs effective `mu_total` diagnostics, but it is not yet a fully coupled solid-contact-TFF result

Latest trusted structural/TFF load-balance baseline:

- model:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`
- note:
  - `576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md`
- verifier:
  - `verify_stage576w3c_checked.java`

This remains the newest checked early-stroke split load-balance result.

Latest physical-coupling extension diagnostic:

- model:
  - `576w3d_stage576_recursive_split010_film_height_release_extended_checked.mph`
- note:
  - `576w3d_stage576_recursive_split010_film_height_release_extended_diagnostic.md`
- builder:
  - `build_stage576w3d_recursive_split010_film_height_release_extended.java`
- verifier:
  - `verify_stage576w3d_checked.java`

Current interpretation:

- this extends the 576w3c mechanism from the checked 5% state to 7.5% and 10%
- it is independently read back as `MARGINAL`, not as a clean `PASS`
- it remained finite, local to `sel_film_swept571`, and did not saturate `drel`
- the final load is high: `F_total = 0.0358037795975 N`
- use it as a diagnostic of early dynamic extension, not as the next final baseline

Latest physical-consistency test:

- model:
  - `576w3s_stage576_geometry_freeze_010_extension_checked.mph`
- note:
  - `576w3s_stage576_geometry_freeze_010_extension.md`
- builder:
  - `build_stage576w3s_geometry_freeze_010_extension.java`
- verifier:
  - `verify_stage576w3s_results.java`
- source failure diagnosis:
  - `576w3e_stage576_failure_diagnosis.md`
  - `576w3f_stage576_micro0625_decoupled_mask_candidate_diagnostic.md`
  - `576w3g_stage576_tff_only_microtarget_diagnostic.md`
  - `576w3h_stage576_tff_pressure_support_diagnostic.md`
  - `576w3i_stage576_pressure_limited_feedback_diagnostic.md`
  - `576w3j_stage576_pressure_cap_refinement_diagnostic.md`
  - `576w3k_stage576_pressure_cap_short_extension_diagnostic.md`
  - `576w3l_stage576_pressure_cap_075_failure_diagnosis.md`
  - `576w3m_stage576_tff_state_transition_diagnosis.md`
  - `576w3n_stage576_coupled_handoff_replay_diagnosis.md`
  - `576w3o_stage576_solid_geometry_handoff_probe.md`
  - `576w3p_stage576_geometry_handoff_correction_microtest.md`
  - `576w3q_stage576_solve_level_geometry_handoff_correction.md`
  - `576w3r_stage576_geometry_freeze_short_coupled_extension.md`

Current interpretation:

- this extends the accepted `576w3q/576w3r` baseline-geometry handoff from
  `7.50%` to `10.00%`
- both solid reset and solid reuse remain finite and stable but are only
  `MARGINAL`
- the verified diagnosis is `GEOMETRY_FREEZE_010_EXTENSION_MARGINAL_BOTH`
- the follow-up `576w3t` diagnosis shows the 10% deficit occurs with a stable
  film state and is dominated by contact-force loss plus reduced capped film
  support area
- the follow-up `576w3u` diagnosis localizes the support-area shrinkage to the
  core/support mask contribution, not the high-gap cutoff
- the follow-up `576w3v` audit shows lagged/frozen core-window readout does
  not change `M_core/Bpress/F_support` in the saved `576w3s` model
- the follow-up `576w3w` audit confirms the saved motion/core expressions are
  coherent and that explicit `READOUT_075/PLUS` restores some film support but
  remains `MARGINAL`
- the follow-up `576w3x` audit confirms both contact-force loss and support-area
  loss contribute; restoring either alone remains `MARGINAL`, restoring both
  gives a readout `PASS`
- the follow-up `576w3y` diagnostic confirms a bounded `2-4 um` inward
  normal-position compensation recovers the `10%` load window in both reset and
  reuse branches
- the follow-up `576w3z` coupled handoff shows fixed `2 um` compensation is
  directionally correct but reset reads back as just `MARGINAL`
- the follow-up `576w4a` refinement confirms fixed `2.25 um` compensation gives
  checked `PASS` in both reset and reuse branches
- the follow-up `576w4b` full handoff confirmation confirms fixed `2.25 um`
  compensation gives checked `PASS` in both reset and reuse branches over the
  actual `7.5% -> 10%` TFF + solid sequence
- the follow-up `576w4c` bounded forward extension from the compensated `10%`
  baseline fails before `25%`: both reset and reuse are only `MARGINAL` at
  `12.5%` and `FAIL` at `15%`, with stable film height/theta and local TFF
  selection
- the follow-up `576w4c_readback` diagnosis classifies the `10% -> 15%` load
  loss as combined contact-force and capped support-area drift, not a film
  state failure
- the follow-up `576w4d` localization classifies the `12.5% -> 15%` mechanism
  as normal/contact unloading plus core/Bpress support shrinkage
- the follow-up `576w4e` microtest shows the next viable correction must
  combine normal/contact recovery with core/Bpress overlap preservation
- the follow-up `576w4f` explicit replay confirms that, on the actual `15%`
  pressure field, contact restored to `12.5%` plus explicit `CORE_0100` reaches
  the load window
- the follow-up `576w4g` solved microtest confirms the combined correction
  direction but only reaches `MARGINAL`: normal + `CORE_0100` gives about
  `0.02808 N`, while baseline, normal-only, and `CORE_0100`-only branches
  remain `FAIL`
- the follow-up `576w4h` deficit diagnosis shows `CORE_0100` support is
  preserved; the remaining `~2.257 mN` gap is contact-side recovery
- the follow-up `576w4i` bounded normal-contact scan finds a `15%` PASS
  candidate at `dn_extra = 4 um`
- the follow-up `576w4j` uses that selected candidate but fails at `20%`;
  `25%` is not attempted
- the follow-up `576w4k` shows bounded normal compensation up to
  `dn_extra = 8 um` cannot recover `20%` without leaving a small active
  contact patch and MPa-level `MaxTn`
- the follow-up `576w4l` shows splitting the path allows `DN8` to pass at
  `17.5%`, but the same branch still fails at `20%`
- the follow-up `576w4m` localizes the stress spike onset to between `17.5%`
  and `18.0%`
- the follow-up `576w4n` shows every tested `dn_extra = 4-8 um` branch fails
  at `18.0%` because `MinGap` jumps to about `-0.36 to -0.37 mm` and
  `MaxTn` reaches about `2.22-2.27 MPa`
- the follow-up `576w4o` shows direct `15% -> 18%` reset reproduces the same
  contact/gap jump, so the failure is tied to the `18%` position/contact
  mapping rather than `17.5%` solid-state inheritance
- the follow-up `576w4q` shows the `18%` `MaxTn` spike has zero high-stress
  area and zero high-stress load fraction, so it is treated as a point
  readout spike rather than a broad contact failure
- the follow-up `576w4s` reaches `25%` with fixed `DN10`, but fails there
  because capped film support collapses to about `0.000617 N`
- the follow-up `576w4t` finds a verified diagnostic `25%` PASS candidate:
  `DN10` through `22.5%` followed by `DN16` solid recovery at `25%`,
  `F_total_support ~= 0.0308078 N`
- the follow-up `576w4u` confirms that candidate as a controlled schedule:
  reset and reuse both pass with `DN10` through `22.5%`, `DN16` at `25%`,
  and `F_total_support ~= 0.0308078 N`
- it is still diagnostic-only and should not be used as a full-cycle physical
  load-closure or mixed-lubrication result

## 2026-06-29-576w5b

### Changed

- Created `build_stage576w5b_075_split_extension_from_0625.java`.
- Created `verify_stage576w5b_results.py`.
- Started from `576w4z_stage576_0625_schedule_confirmation_checked.mph`.
- Tested split `62.5% -> 68.75% -> 75%` with fixed `DN20`.
- Kept `CORE_0100`, `CAP_6P5KPA`, `alpha_pfb = 0.15`,
  `h_TFF = h_calc573`, and local `sel_film_swept571`.

### Observed

- Build completed with `W5B_BUILD_STATUS=PASS`.
- Verifier failed: `075_FORWARD_EXTENSION_NOT_CONFIRMED_BOTH`.
- RESET remained marginal:
  - `68.75%`: `F_total_support ~= 0.0292085927 N`;
  - `75%`: `F_total_support ~= 0.0287286321 N`.
- REUSE TFF did not converge at the first split segment.

### Interpretation

- Splitting the step did not solve the 75% blocker.
- RESET needs additional normal/contact recovery beyond `DN20`.
- REUSE has a pressure-history/TFF convergence blocker.

### Next Step

- Run `576w5c_075_pressure_reset_dn_scan`: reset pressure history from
  `sol493` and scan `DN20/DN22` toward 75%.
- Do not tune pressure cap, gains, friction, roughness, or free-surface
  modeling.

### Files

- `build_stage576w5b_075_split_extension_from_0625.java`
- `576w5b_stage576_075_split_extension_from_0625.md`
- `576w5b_stage576_075_split_extension_from_0625_results.mph`
- `576w5b_stage576_075_split_extension_from_0625_summary.csv`
- `verify_stage576w5b_results.py`
- `576w5b_stage576_075_split_extension_from_0625_verify_summary.csv`

## 2026-06-29-576w4z

### Changed

- Created `build_stage576w4z_0625_schedule_confirmation.java`.
- Created `verify_stage576w4z_results.py`.
- Combined RESET direct `DN20` and REUSE `DN20` continuation at 62.5%.

### Observed

- Build completed with `W4Z_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `0625_NORMAL_RECOVERY_HAS_PASS_BOTH`.
- Saved `576w4z_stage576_0625_schedule_confirmation_checked.mph`.
- RESET direct `DN20`:
  `F_total_support ~= 0.0302246939 N`.
- REUSE continuation `DN20`:
  `F_total_support ~= 0.0300570166 N`.
- Both kept `AvgH = 3 um`, `MinTheta = 1`, and local TFF selection.

### Interpretation

- `62.5%` is now the latest checked diagnostic forward endpoint.
- It is not a full-cycle or mixed-lubrication baseline; capped film support is
  effectively zero and load is carried by solid contact.

### Next Step

- Continue toward 75%, but diagnose pressure-history and normal-load recovery
  if fixed `DN20` fails.

### Files

- `build_stage576w4z_0625_schedule_confirmation.java`
- `576w4z_stage576_0625_schedule_confirmation.md`
- `576w4z_stage576_0625_schedule_confirmation_checked.mph`
- `576w4z_stage576_0625_schedule_confirmation_summary.csv`
- `verify_stage576w4z_results.py`
- `576w4z_stage576_0625_schedule_confirmation_verify_summary.csv`

## 2026-06-29-576w4x

### Changed

- Created `build_stage576w4x_0625_normal_recovery_scan.java`.
- Created `verify_stage576w4x_results.py`.
- Started from `576w4v_stage576_050_forward_extension_diagnostic_checked.mph`.
- Tested only `50% -> 62.5%`.
- Scanned bounded normal compensation `DN = 16, 18, 20 um`.
- Kept `CORE_0100`, `CAP_6P5KPA`, `alpha_pfb = 0.15`,
  `h_TFF = h_calc573`, and local `sel_film_swept571`.

### Observed

- The scan did not complete cleanly because REUSE `DN20` failed solid-solver
  convergence before the builder could save an `.mph`.
- Verifier completed on partial CSV evidence with `VERIFY_STATUS=FAIL`.
- Overall diagnosis:
  `0625_NORMAL_RECOVERY_NOT_CONFIRMED_BOTH`.
- Completed rows:
  - RESET `DN16`: `F_total_support ~= 0.0277989086 N`, `FAIL`;
  - RESET `DN18`: `F_total_support ~= 0.0287736072 N`, `MARGINAL`;
  - RESET `DN20`: `F_total_support ~= 0.0302246939 N`, `PASS`;
  - REUSE `DN16`: `F_total_support ~= 0.0284694655 N`, `MARGINAL`;
  - REUSE `DN18`: `F_total_support ~= 0.0291870132 N`, `MARGINAL`;
  - REUSE `DN20`: solid solve did not converge.
- Completed rows kept `AvgH = 3 um`, `MinTheta = 1`, local TFF selection,
  and effectively zero capped film support.

### Interpretation

- The 62.5% blocker is contact/normal-position dominated, not a film-state or
  TFF-selection failure.
- A simple DN increase is not yet a reset/reuse-consistent schedule:
  RESET is recovered at `DN20`, but REUSE is not confirmed and becomes
  nonconvergent at the same DN.
- Do not continue to `75%` or `100%` until 62.5% has a stable reset/reuse
  checkpoint.

### Next Step

- Create a controlled 62.5% recovery diagnostic around the REUSE branch, for
  example smaller DN continuation near `18-20 um` or solver-state stabilization
  for the REUSE solid solve.
- Do not change pressure cap, gains, friction, roughness, or free-surface
  modeling to force the result.

### Files

- `build_stage576w4x_0625_normal_recovery_scan.java`
- `576w4x_stage576_0625_normal_recovery_scan.md`
- `576w4x_stage576_0625_normal_recovery_scan_summary.csv`
- `verify_stage576w4x_results.py`
- `576w4x_stage576_0625_normal_recovery_scan_verify_summary.csv`

## 2026-06-29-576w4w

### Changed

- Created `build_stage576w4w_075_forward_extension_diagnostic.java`.
- Created `verify_stage576w4w_results.py`.
- Started from `576w4v_stage576_050_forward_extension_diagnostic_checked.mph`.
- Tested `50% -> 62.5% -> 75%` with fixed `DN16`.
- Kept `CORE_0100`, `CAP_6P5KPA`, `alpha_pfb = 0.15`,
  `h_TFF = h_calc573`, and local `sel_film_swept571`.

### Observed

- Build completed with `W4W_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=FAIL`.
- Overall diagnosis:
  `075_FORWARD_EXTENSION_FAILS`.
- `62.5%` RESET failed:
  `F_total_support ~= 0.0277989085 N`.
- `62.5%` REUSE was only marginal:
  `F_total_support ~= 0.0284694690 N`.
- `75%` REUSE failed:
  `F_total_support ~= 0.0263801171 N`.
- Film state remained stable and local:
  `AvgH = 3 um`, `MinTheta = 1`, and TFF selection local.

### Interpretation

- The clean `50%` endpoint does not extend to `75%` with fixed `DN16`.
- The first failed node is already `62.5%`.
- Since capped film support is effectively zero, the failure is dominated by
  normal/contact load recovery.

### Next Step

- Diagnose `62.5%` before any `75%` or `100%` attempt.

### Files

- `build_stage576w4w_075_forward_extension_diagnostic.java`
- `576w4w_stage576_075_forward_extension_diagnostic.md`
- `576w4w_stage576_075_forward_extension_diagnostic_results.mph`
- `576w4w_stage576_075_forward_extension_diagnostic_summary.csv`
- `verify_stage576w4w_results.py`
- `576w4w_stage576_075_forward_extension_diagnostic_verify_summary.csv`

## 2026-06-29-576w4v

### Changed

- Created `build_stage576w4v_050_forward_extension_diagnostic.java`.
- Created `verify_stage576w4v_results.py`.
- Started from `576w4u_stage576_025_compensation_schedule_confirmation_checked.mph`.
- Extended `25% -> 37.5% -> 50%` with fixed `DN16`.
- Kept `CORE_0100`, `CAP_6P5KPA`, `alpha_pfb = 0.15`,
  `h_TFF = h_calc573`, and local `sel_film_swept571`.

### Observed

- Build completed with `W4V_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `050_FORWARD_EXTENSION_PASS_BOTH`.
- Saved `576w4v_stage576_050_forward_extension_diagnostic_checked.mph`.
- `50%` RESET:
  `F_total_support ~= 0.0300941459 N`.
- `50%` REUSE:
  `F_total_support ~= 0.0302827948 N`.
- `AvgH = 3 um`, `MinTheta = 1`, and TFF selection remained local.

### Interpretation

- `50%` is the latest clean checked diagnostic forward endpoint.
- It is still diagnostic-only: capped film support is effectively zero, so the
  accepted load is carried almost entirely by solid contact.

### Next Step

- Attempted `576w4w`; it failed at `62.5%`, so the current next step is the
  `62.5%` recovery diagnosis, not direct `75%/100%` continuation.

### Files

- `build_stage576w4v_050_forward_extension_diagnostic.java`
- `576w4v_stage576_050_forward_extension_diagnostic.md`
- `576w4v_stage576_050_forward_extension_diagnostic_checked.mph`
- `576w4v_stage576_050_forward_extension_diagnostic_summary.csv`
- `verify_stage576w4v_results.py`
- `576w4v_stage576_050_forward_extension_diagnostic_verify_summary.csv`

## 2026-06-29-576w4u

### Changed

- Created `build_stage576w4u_025_compensation_schedule_confirmation.java`.
- Created `verify_stage576w4u_results.py`.
- Saved `576w4u_stage576_025_compensation_schedule_confirmation_checked.mph`.
- Converted the `576w4t` one-point `25%` recovery into a fixed compensation
  schedule:
  `DN10` through `22.5%`, then `DN16` at `25%`.
- Ran both reset and reuse branches.
- Kept `CORE_0100`, `CAP_6P5KPA`, `alpha_pfb = 0.15`,
  and `h_TFF = h_calc573`.

### Observed

- Build completed with `W4U_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `025_COMPENSATION_SCHEDULE_PASS_BOTH`.
- Reset and reuse both passed at `25%`:
  - `F_contact ~= 0.0301903962 N`;
  - `F_film_support ~= 0.000617431890 N`;
  - `F_total_support ~= 0.0308078281 N`;
  - `AvgH ~= 3.000543 um`;
  - `MinTheta ~= 0.999983`;
  - `MaxP_raw ~= 124.6 kPa`;
  - `TnAreaGt0p1MPa = 0`;
  - TFF selection remains local.

### Interpretation

- The `576w4t` `25%` solid recovery is confirmed as a reset/reuse-consistent
  compensation schedule endpoint.
- This is still diagnostic-only: at `25%`, capped film support is only about
  `0.617 mN`, so most normal support is carried by solid contact.
- The next defensible step is controlled `25% -> 50%` extension without
  pressure-cap increase, gain tuning, friction, roughness, or free-surface
  modeling.

### Next Step

- Create `576w4v_050_forward_extension_diagnostic`.
- Start from the `576w4u` schedule logic.
- Keep the same pressure cap, gains, TFF selection, and `h_TFF = h_calc573`.
- Diagnose any failure as normal schedule, core/support overlap, capped
  support collapse, or contact readout behavior before attempting `75%` or a
  full cycle.

### Files

- `build_stage576w4u_025_compensation_schedule_confirmation.java`
- `576w4u_stage576_025_compensation_schedule_confirmation_checked.mph`
- `576w4u_stage576_025_compensation_schedule_confirmation.md`
- `576w4u_stage576_025_compensation_schedule_confirmation_summary.csv`
- `verify_stage576w4u_results.py`
- `576w4u_stage576_025_compensation_schedule_confirmation_verify_summary.csv`

## 2026-06-29-576w4t

### Changed

- Created `build_stage576w4t_025_solid_recovery_scan.java`.
- Created `verify_stage576w4t_results.py`.
- Used the `DN10` path through `22.5%`.
- Reused the same `25%` TFF pressure field and scanned the `25%` solid solve:
  `dn_extra = 10, 12, 14, 16, 18 um`.
- Kept `CORE_0100`, `CAP_6P5KPA`, `alpha_pfb = 0.15`,
  and `h_TFF = h_calc573`.

### Observed

- Build completed with `W4T_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `025_SOLID_RECOVERY_HAS_VERIFIED_PASS_CANDIDATE`.
- Best verified branch:
  `NORMAL_PLUS_CORE_0100_DN10P0UM_RESET_SOLIDDN16P0UM`.
- At `25%` with `DN16`:
  - `F_contact ~= 0.0301903962 N`;
  - `F_film_support ~= 0.000617431890 N`;
  - `F_total_support ~= 0.0308078281 N`;
  - `AvgH ~= 3.000543 um`;
  - `MinTheta ~= 0.999983`;
  - `MaxP_raw ~= 124.6 kPa`;
  - `TnAreaGt0p1MPa = 0`;
  - TFF selection remains local.

### Interpretation

- `25%` is now reached as a diagnostic PASS candidate.
- The endpoint is not a final forward-extension baseline because it uses a
  one-point solid-side recovery after the capped film support has collapsed.
- The next necessary step is to convert this into a controlled normal
  compensation schedule and verify reset/reuse consistency.

### Next Step

- Create `576w4u_025_compensation_schedule_confirmation`.
- Test a scheduled compensation path such as:
  `DN10` through `22.5%`, then `DN16` at `25%`.
- Verify reset/reuse branches and keep the same pressure cap, gains, TFF
  selection, and `h_TFF = h_calc573`.

### Files

- `build_stage576w4t_025_solid_recovery_scan.java`
- `576w4t_stage576_025_solid_recovery_scan.md`
- `576w4t_stage576_025_solid_recovery_scan_summary.csv`
- `verify_stage576w4t_results.py`
- `576w4t_stage576_025_solid_recovery_verify_summary.csv`

## 2026-06-29-576w4q

### Changed

- Created `build_stage576w4q_contact_spike_area_audit.java`.
- Added high-stress area and load-fraction readouts for `Tn > 0.1 MPa`,
  `0.5 MPa`, and `1 MPa`.

### Observed

- `18%` still has a large pointwise `MaxTn`, but:
  `TnAreaGt0p1MPa = 0` and `TnLoadFracGt0p1MPa = 0`.

### Interpretation

- The `18%` contact-stress issue is a zero-area point spike, not a broad
  contact failure.

### Files

- `build_stage576w4q_contact_spike_area_audit.java`
- `576w4q_stage576_contact_spike_area_audit.md`
- `576w4q_stage576_contact_spike_area_audit_summary.csv`

## 2026-06-29-576w4o

### Changed

- Created `build_stage576w4o_direct_0180_reset_scan.java`.
- Started from `576w4i_stage576_bounded_normal_contact_recovery_microtest_checked.mph`.
- Tested direct `15% -> 18.0%` reset branches.
- Scanned `dn_extra = 4, 5, 6, 7, 8 um`.
- Kept `CORE_0100`, `CAP_6P5KPA`, `alpha_pfb = 0.15`, and
  `h_TFF = h_calc573`.

### Observed

- Build completed with `W4O_BUILD_STATUS=PASS`.
- Overall diagnosis:
  `DIRECT_0180_RESET_FAILS`.
- All `18.0%` direct-reset branches failed under contact-quality criteria.
- Representative values:
  - `DN4`: `F_total_support ~= 0.02849 N`,
    `MaxTn ~= 2.2205 MPa`, `MinGap ~= -0.361 mm`.
  - `DN6`: `F_total_support ~= 0.03019 N`,
    `MaxTn ~= 2.2472 MPa`, `MinGap ~= -0.366 mm`.
  - `DN8`: `F_total_support ~= 0.03191 N`,
    `MaxTn ~= 2.2713 MPa`, `MinGap ~= -0.370 mm`.
- Film state remained stable:
  `AvgH ~= 3.249 um`, `MinTheta ~= 0.999935`,
  and TFF selection remained `sel_film_swept571`.

### Interpretation

- The `18.0%` failure is not caused by inheriting the `17.5%` solid/contact
  state.
- Several branches have acceptable scalar load, but they are not acceptable
  physical baselines because the contact gap and contact stress are
  pathological.
- The blocker for continuing toward `25%` is now localized to the motion /
  contact-pair / gap mapping around `17.5% -> 18.0%`.

### Next Step

- Create `576w4p_0180_contact_gap_geometry_audit`.
- Audit `disp_lid_time`, `t_position576p2`, `geomgap_dst_cp_lid_cornea`,
  contact-pair source/destination projection, and frame choice around
  `17.5% -> 18.0%`.
- Do not continue to `20%` or `25%` until the `18%` contact/gap jump is
  understood or corrected.

### Files

- `build_stage576w4o_direct_0180_reset_scan.java`
- `576w4o_stage576_direct_0180_reset_scan.md`
- `576w4o_stage576_direct_0180_reset_scan_summary.csv`

## 2026-06-29-576w4n

### Changed

- Created `build_stage576w4n_0180_dn_window_scan.java`.
- Tested `15% -> 17.5% -> 18.0%`.
- Scanned `dn_extra = 4, 5, 6, 7, 8 um`.

### Observed

- At `17.5%`, `DN6-DN8` passed and `DN4-DN5` were marginal.
- At `18.0%`, all branches failed because `MaxTn` jumped to about
  `2.22-2.27 MPa` and `MinGap` jumped to about `-0.36 to -0.37 mm`.

### Interpretation

- There is no valid normal-compensation window at `18.0%` in the current
  contact/gap formulation.

### Files

- `build_stage576w4n_0180_dn_window_scan.java`
- `576w4n_stage576_0180_dn_window_scan.md`
- `576w4n_stage576_0180_dn_window_scan_summary.csv`

## 2026-06-29-576w4j

### Changed

- Created `build_stage576w4j_selected_normal_core_forward_extension_025.java`.
- Started from `576w4i_stage576_bounded_normal_contact_recovery_microtest_checked.mph`.
- Used the selected `15%` branch:
  `NORMAL_PLUS_CORE_0100_DN4P0UM`.
- Fixed:
  `dn_extra = 4 um`, `CORE_0100`, `CAP_6P5KPA`,
  `alpha_pfb = 0.15`, and `h_TFF = h_calc573`.
- Planned `15% -> 20% -> 25%`, with stop-on-fail before `25%`.

### Observed

- Build completed with `W4J_BUILD_STATUS=PASS`.
- Overall diagnosis:
  `SELECTED_NORMAL_CORE_FORWARD_EXTENSION_025_FAILS`.
- Both reset and reuse failed at the first extension segment `15% -> 20%`.
- `25%` was not attempted.
- At `20%`:
  - `F_contact ~= 0.0169454643 N`;
  - `F_film_support ~= 0.00932943146 N`;
  - `F_total_support ~= 0.0262748958 N`;
  - `AvgH ~= 3.21743474870e-06 m`;
  - `MinTheta ~= 0.999969611786`;
  - `MaxP_raw ~= 0.283 MPa`;
  - `MaxTn ~= 1.593 MPa`;
  - `active_contact_area_over_patch ~= 0.0617`.

### Interpretation

- The selected `15%` correction does not naturally carry to `20%`.
- The failure is not a film-height, theta, raw-pressure, or TFF-selection
  failure.
- The limiting behavior at `20%` is contact/solid-state localization with low
  load, low active contact area, and high local `MaxTn`.

### Next Step

- Create `576w4k` as a `20%` failure localization/recovery diagnostic.
- Determine whether the `20%` failure is recoverable by bounded normal/contact
  correction or whether fixed `CORE_0100` becomes inconsistent beyond `15%`.
- Do not push to `25%` until `20%` reaches at least `MARGINAL` without the
  contact spike.

### Files

- `build_stage576w4j_selected_normal_core_forward_extension_025.java`
- `576w4j_stage576_selected_normal_core_forward_extension_025.md`
- `576w4j_stage576_selected_normal_core_forward_extension_025_summary.csv`

## 2026-06-29-576w4i

### Changed

- Created `build_stage576w4i_bounded_normal_contact_recovery_microtest.java`.
- Created `verify_stage576w4i_results.java`.
- Started from `576w4c_stage576_compensated_forward_extension_025_checked.mph`.
- Tested only `12.5% -> 15%`.
- Fixed `NORMAL_PLUS_CORE_0100`, `CAP_6P5KPA`, and `alpha_pfb = 0.15`.
- Scanned:
  `dn_extra = 1, 2, 3, 4, 5 um`.

### Observed

- Build completed and verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `BOUNDED_NORMAL_CONTACT_RECOVERY_HAS_PASS_CANDIDATE`.
- Verified results:
  - `dn_extra = 1-3 um`: `MARGINAL`;
  - `dn_extra = 4 um`: reset/reuse `PASS`,
    `F_total_support ~= 0.030414741 N`;
  - `dn_extra = 5 um`: reset/reuse `PASS`,
    `F_total_support ~= 0.031321600 N`.
- Film state remained stable:
  `AvgH ~= 3.27 um`,
  `MinTheta ~= 0.9998768`,
  `LowThetaAreaRatio02 = 0`,
  `MaxP_raw ~= 0.175 MPa`.

### Interpretation

- The `576w4h` contact-side diagnosis is confirmed.
- A bounded normal/contact correction can recover the `15%` load window.
- The selected candidate is the smallest passing branch:
  `dn_extra = 4 um`.

### Next Step

- `576w4j` was created to test the selected branch toward `20%/25%`.
- It failed at `20%`, so the next step is `576w4k` 20% failure localization.

### Files

- `build_stage576w4i_bounded_normal_contact_recovery_microtest.java`
- `verify_stage576w4i_results.java`
- `576w4i_stage576_bounded_normal_contact_recovery_microtest.md`
- `576w4i_stage576_bounded_normal_contact_recovery_best_branch.md`
- `576w4i_stage576_bounded_normal_contact_recovery_microtest_summary.csv`
- `576w4i_stage576_bounded_normal_contact_recovery_microtest_verify_summary.csv`

## 2026-06-28-576w4h

### Changed

- Created `probe_stage576w4h_forward_combined_deficit_diagnosis.py`.
- Used already verified CSV/readback outputs from `576w4f`, `576w4g`, and
  `576w4d`.
- Compared the w4f read-only target
  `CONTACT_0125 + CORE_0100` against the w4g solved
  `NORMAL_PLUS_CORE_0100` branch.
- Avoided reloading multiple large `.mph` files in one COMSOL process after a
  direct dual-model Java probe exhausted memory.

### Observed

- Probe completed with `PROBE_STATUS=PASS`.
- Overall diagnosis:
  `CONTACT_RECOVERY_INSUFFICIENT_SUPPORT_MATCHES_REPLAY`.
- For both reset and reuse:
  - replay `CORE_0100` support = `0.0121932906876 N`;
  - solved `CORE_0100` support = `0.0121932906876 N`;
  - support deficit = `0 N`.
- The remaining deficit is contact-side:
  - reset contact deficit to `CONTACT_0125`:
    `0.0022570651863 N`;
  - reuse contact deficit to `CONTACT_0125`:
    `0.0022570651469 N`.
- Active contact area also remains below the `12.5%` target:
  `0.295527061048 -> 0.242500704678`.

### Interpretation

- The `576w4g` marginal result is not caused by losing explicit `CORE_0100`
  support.
- It is also not a film-state or pressure-state problem.
- The limiting mechanism is insufficient solved normal/contact recovery after
  the combined branch is inserted into the solid handoff.

### Next Step

- Create `576w4i` as a bounded normal-contact recovery microtest at `15%`.
- Keep `CORE_0100`, `CAP_6P5KPA`, local TFF selection, and existing gains.
- Test whether a small additional normal/contact recovery can close the
  missing `~2.257 mN` without destabilizing the `3 um` film state.
- Do not extend to `20%/25%`, increase cap, tune gains, or add friction.

### Files

- `probe_stage576w4h_forward_combined_deficit_diagnosis.py`
- `576w4h_stage576_forward_combined_deficit_diagnosis.md`
- `576w4h_stage576_forward_combined_deficit_diagnosis_summary.csv`

## 2026-06-28-576w4g

### Changed

- Created `build_stage576w4g_forward_combined_normal_core_solve_microtest.java`.
- Created `verify_stage576w4g_results.java`.
- Started from `576w4c_stage576_compensated_forward_extension_025_checked.mph`.
- Tested only `12.5% -> 15%`.
- Compared baseline actual, normal-only, `CORE_0100`-only, and combined
  normal + `CORE_0100` branches for both reset and reuse.
- Kept `CAP_6P5KPA`, `alpha_pfb = 0.15`, `h_TFF = h_calc573`, and
  `TFF selection = sel_film_swept571`.

### Observed

- Build completed and saved checked results.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `COMBINED_NORMAL_CORE_SOLVE_MARGINAL`.
- Checked read-back:
  - baseline actual reset/reuse: `F_total_support ~= 0.023899870 N`, `FAIL`;
  - normal-only reset/reuse: `F_total_support ~= 0.024719182 N`, `FAIL`;
  - `CORE_0100`-only reset/reuse: `F_total_support ~= 0.027285478 N`, `FAIL`;
  - normal + `CORE_0100` reset/reuse:
    `F_total_support ~= 0.028080830 N`, `MARGINAL`.
- Film state remained stable:
  `AvgH = 3.26999536495e-06 m`,
  `MinTheta = 0.999876804311`,
  `LowThetaAreaRatio02 = 0`,
  `MaxP_raw = 174877.441632 Pa`.
- TFF selection remained local:
  `TFF_SELECTION_EQUALS_SWEPT=true`.

### Interpretation

- The solved result supports the previous w4e/w4f direction: neither
  normal-only nor core-only recovery is enough.
- The combined normal + `CORE_0100` branch improves the load but does not
  reach the preferred `0.030-0.033 N` pass window.
- The remaining problem is a solved contact/support magnitude deficit, not
  film-height drift, theta collapse, raw-pressure spike, or selection drift.

### Next Step

- Create a targeted `576w4h` readback/diagnosis of the combined branch deficit.
- Do not extend to `20%/25%`, increase `CAP_6P5KPA`, tune gains, or add
  friction until the `~0.02808 N` marginal result is localized.

### Files

- `build_stage576w4g_forward_combined_normal_core_solve_microtest.java`
- `verify_stage576w4g_results.java`
- `576w4g_stage576_forward_combined_normal_core_solve_microtest.md`
- `576w4g_stage576_forward_combined_normal_core_solve_microtest_summary.csv`
- `576w4g_stage576_forward_combined_normal_core_solve_microtest_verify_summary.csv`

## 2026-06-28-576w4f

### Changed

- Created `probe_stage576w4f_forward_combined_normal_core_replay.java`.
- Read back the existing `576w4b` and `576w4c` checked models.
- Used the actual `15%` pressure field.
- Explicitly replayed core windows at `CORE_0100`, `CORE_0125`, and
  `CORE_0150`.
- Combined each core replay with `15%`, `12.5%`, and `10%` contact readouts.
- Kept the test read-only.

### Observed

- Probe completed with `PROBE_STATUS=PASS`.
- TFF selection remained local:
  `TFF_SELECTION_EQUALS_SWEPT=true`.
- Overall diagnosis:
  `EXPLICIT_CORE_REPLAY_PLUS_NORMAL_REPLAY_TESTED`.
- Reset branch, actual `15%` pressure field:
  - contact `15%` + core `15%`: `0.0238998702244 N`, `FAIL`;
  - contact `12.5%` + core `15%`: `0.0267152777124 N`, `FAIL`;
  - contact `15%` + core `12.5%`: `0.0261643029491 N`, `FAIL`;
  - contact `12.5%` + core `12.5%`: `0.0289797104371 N`, `MARGINAL`;
  - contact `15%` + core `10%`: `0.0275224873516 N`, `FAIL`;
  - contact `12.5%` + core `10%`: `0.0303378948395 N`, `PASS`.
- Reuse branch gives the same classification.

### Interpretation

- Explicit core replay confirms that `CORE_0125` is not enough for a clean
  load closure.
- The viable combined target is contact near `12.5%` with explicit
  `CORE_0100` support overlap.
- This still does not justify changing `CAP_6P5KPA` or feedback gains.

### Next Step

- Create `576w4g_forward_combined_normal_core_solve_microtest`.
- Keep the solve restricted to `12.5% -> 15%`.
- Branches should include:
  baseline actual, normal-only, `CORE_0100` only, and combined normal +
  `CORE_0100`.
- Do not continue to `20%/25%`, increase cap, tune gains, or add friction.

### Files

- `probe_stage576w4f_forward_combined_normal_core_replay.java`
- `probe_stage576w4f_forward_combined_normal_core_replay_compile_stdout.txt`
- `probe_stage576w4f_forward_combined_normal_core_replay_stdout.txt`
- `576w4f_stage576_forward_combined_normal_core_replay.md`
- `576w4f_stage576_forward_combined_normal_core_replay_contacts.csv`
- `576w4f_stage576_forward_combined_normal_core_replay_core.csv`
- `576w4f_stage576_forward_combined_normal_core_replay_summary.csv`

## 2026-06-28-576w4e

### Changed

- Created `probe_stage576w4e_forward_normal_core_overlap_microtest.java`.
- Read back the existing `576w4b` and `576w4c` checked models.
- Tested read-only `15%` recovery variants:
  contact-only, core/Bpress-only, combined contact plus core/Bpress, and exact
  `10%` support reference.
- Did not solve new states, extend to `20%/25%`, change cap, tune gains, or
  add friction.

### Observed

- Probe completed with `PROBE_STATUS=PASS`.
- Overall diagnosis:
  `COMBINED_NORMAL_AND_CORE_OVERLAP_REQUIRED`.
- Branch diagnoses:
  - reset: `COMBINED_NORMAL_AND_CORE_OVERLAP_REQUIRED`;
  - reuse: `COMBINED_NORMAL_AND_CORE_OVERLAP_REQUIRED`.
- At `15%`, normal-only restoration remains `FAIL`.
- Core/Bpress-only restoration remains `FAIL` or at best `MARGINAL`.
- Contact restored to `12.5%` plus core/Bpress restored to the accepted `10%`
  overlap gives `PASS`:
  - reset: `F_total = 0.0311653235175 N`;
  - reuse: `F_total = 0.0311653234821 N`.
- Contact restored to `12.5%` plus exact `10%` support also gives `PASS`:
  about `0.030167346 N`.

### Interpretation

- The next correction cannot be only normal-position compensation.
- It also cannot be only core/support overlap preservation.
- The viable next coupled microtest must combine:
  contact/normal recovery to at least near the `12.5%` state and
  core/Bpress support overlap closer to the accepted `10%` state.
- This still does not justify increasing cap pressure, tuning gains, or adding
  friction.

### Next Step

- `576w4f_forward_combined_normal_core_replay` has now been completed and
  confirmed that contact `12.5%` plus explicit `CORE_0100` reaches the load
  window on the actual `15%` pressure field.
- Next create `576w4g_forward_combined_normal_core_solve_microtest`.
- Keep `CAP_6P5KPA`, `h_TFF = h_calc573`, local TFF selection, and existing
  gains.

### Files

- `probe_stage576w4e_forward_normal_core_overlap_microtest.java`
- `probe_stage576w4e_forward_normal_core_overlap_microtest_compile_stdout.txt`
- `probe_stage576w4e_forward_normal_core_overlap_microtest_stdout.txt`
- `576w4e_stage576_forward_normal_core_overlap_microtest.md`
- `576w4e_stage576_forward_normal_core_overlap_microtest_base.csv`
- `576w4e_stage576_forward_normal_core_overlap_microtest_variants.csv`
- `576w4e_stage576_forward_normal_core_overlap_microtest_requirements.csv`

## 2026-06-28-576w4d

### Changed

- Created `probe_stage576w4d_forward_contact_support_localization.java`.
- Read back the existing `576w4b` and `576w4c` checked models.
- Compared `10.0%`, `12.5%`, and `15.0%` without solving.
- Added contact-side readouts:
  active contact area, contact density, `MaxTn`, and `MinGap`.
- Added support-side readouts:
  `BpressOverSwept`, `McoreOverSwept`, `BhighOverSwept`, `BlowOverSwept`,
  `ValidOverSwept`, and support density over active `Bpress`.
- Added restoration combinations at `15%`.

### Observed

- Probe completed with `PROBE_STATUS=PASS`.
- Overall diagnosis:
  `NORMAL_UNLOADING_PLUS_CORE_SUPPORT_SHRINK`.
- Branch diagnoses:
  - reset: `NORMAL_UNLOADING_PLUS_CORE_SUPPORT_SHRINK`;
  - reuse: `NORMAL_UNLOADING_PLUS_CORE_SUPPORT_SHRINK`.
- From `12.5%` to `15.0%`:
  - `F_contact` drops by about `2.815 mN`;
  - `F_support` drops by about `2.245 mN`;
  - active contact area over patch drops by about `0.05495`;
  - `MaxTn` drops by about `24.17 kPa`;
  - `BpressOverSwept` drops by about `0.004627`;
  - `McoreOverSwept` drops by about `0.004548`;
  - `BhighOverSwept`, `BlowOverSwept`, and `ValidOverSwept` stay effectively
    unchanged.
- Restoration combinations at `15%`:
  - restoring only `12.5%` contact remains `FAIL`;
  - restoring only `12.5%` support remains `FAIL`;
  - restoring both `12.5%` contact and support is only `MARGINAL`;
  - restoring both `10%` contact and support reaches `PASS`.

### Interpretation

- The `15%` failure is not caused by pressure-cap value, `Bhigh`, `B_low`,
  valid-gap admission, theta collapse, or film-height drift.
- The limiting mechanism is coupled normal/contact unloading plus reduced
  core/Bpress support overlap.
- Fixed `dn_comp = 2.25 um` is adequate at `10%` but does not maintain the
  contact/support configuration through `15%`.

### Next Step

- `576w4e_forward_normal_core_overlap_microtest` has now been completed and
  classified the requirement as `COMBINED_NORMAL_AND_CORE_OVERLAP_REQUIRED`.
- `576w4f_forward_combined_normal_core_replay` has now been completed and
  confirmed that contact `12.5%` plus explicit `CORE_0100` reaches the load
  window on the actual `15%` pressure field.
- Next create `576w4g_forward_combined_normal_core_solve_microtest`.
- Do not extend to `20%/25%`, increase cap, tune gains, or add friction.

### Files

- `probe_stage576w4d_forward_contact_support_localization.java`
- `probe_stage576w4d_forward_contact_support_localization_compile_stdout.txt`
- `probe_stage576w4d_forward_contact_support_localization_stdout.txt`
- `576w4d_stage576_forward_contact_support_localization.md`
- `576w4d_stage576_forward_contact_support_localization_summary.csv`
- `576w4d_stage576_forward_contact_support_localization_delta.csv`
- `576w4d_stage576_forward_contact_support_localization_combos.csv`

## 2026-06-28-576w4c-readback

### Changed

- Created `probe_stage576w4c_readback_diagnosis.java`.
- Read back `576w4b_stage576_compensated_handoff_confirmation_checked.mph` and
  `576w4c_stage576_compensated_forward_extension_025_checked.mph`.
- Compared `10.0%`, `12.5%`, and `15.0%` states for both reset and reuse.
- Kept the probe read-only; no solve, no parameter tuning, no cap change, and
  no friction model was added.

### Observed

- Probe completed with `PROBE_STATUS=PASS`.
- Overall diagnosis:
  `CONTACT_AND_SUPPORT_AREA_DRIFT_BEYOND_010`.
- Branch diagnoses:
  - reset: `CONTACT_AND_SUPPORT_AREA_DRIFT`;
  - reuse: `CONTACT_AND_SUPPORT_AREA_DRIFT`.
- From `10.0%` to `15.0%`, reset total load drops by
  `0.00632487480553 N`.
- Reset load-loss split:
  - `delta_F_contact = -0.00287280640472 N`;
  - `delta_F_support = -0.00345206840081 N`;
  - about `45.4%` contact loss and `54.6%` capped support loss.
- Reuse gives the same pattern:
  total load drops by `0.00632714831462 N`.
- Film state remains stable:
  `AvgH = 3.3388 um -> 3.3220 um -> 3.2700 um`,
  `MinTheta >= 0.999844169519`,
  `LowThetaAreaRatio02 = 0`,
  `MaxP_raw < 0.2 MPa`.
- Support/contact coverage both decrease:
  - active contact area over patch: about `0.357 -> 0.241`;
  - `BpressOverSwept`: about `0.02287 -> 0.01506`;
  - `MaxTn`: about `452.7 kPa -> 84.3 kPa`.

### Interpretation

- The `576w4c` failure is not a TFF PDE instability, theta collapse,
  raw-pressure spike, or selection drift.
- Fixed `dn_comp = 2.25 um` closes the load at `10%` but does not preserve
  enough contact indentation/support coverage by `15%`.
- The next question is whether the drift is caused by insufficient
  normal-position compensation, support/core mask motion, or contact-state
  inheritance/reinitialization.

### Next Step

- `576w4d_forward_contact_support_localization` has now been completed and
  classified the mechanism as `NORMAL_UNLOADING_PLUS_CORE_SUPPORT_SHRINK`.
- `576w4e_forward_normal_core_overlap_microtest` has now been completed and
  classified the requirement as `COMBINED_NORMAL_AND_CORE_OVERLAP_REQUIRED`.
- `576w4f_forward_combined_normal_core_replay` has now been completed and
  confirmed that contact `12.5%` plus explicit `CORE_0100` reaches the load
  window on the actual `15%` pressure field.
- Next create `576w4g_forward_combined_normal_core_solve_microtest`.
- Keep `CAP_6P5KPA`, `dn_comp = 2.25 um`, `h_TFF = h_calc573`, local TFF
  selection, and existing gains unless the next solved microtest explicitly
  shows why a bounded normal-position/core-overlap correction is needed.
- Do not continue to `20%/25%`, tune gains, increase cap, or add friction.

### Files

- `probe_stage576w4c_readback_diagnosis.java`
- `probe_stage576w4c_readback_diagnosis_compile_stdout.txt`
- `probe_stage576w4c_readback_diagnosis_stdout.txt`
- `576w4c_stage576_readback_diagnosis.md`
- `576w4c_stage576_readback_diagnosis_states.csv`
- `576w4c_stage576_readback_diagnosis_deltas.csv`

## 2026-06-28-576w4c

### Changed

- Created `build_stage576w4c_compensated_forward_extension_025.java`.
- Created `verify_stage576w4c_results.java`.
- Started from `576w4b_stage576_compensated_handoff_confirmation_checked.mph`.
- Used the checked compensated `10%` state as the baseline.
- Kept fixed `dn_comp = 2.25 um`, `CAP_6P5KPA`, `alpha_pfb = 0.15`,
  `h_TFF = h_calc573`, and `TFF selection = sel_film_swept571`.
- Tested two branches:
  `COMP2P25UM_SOLID_RESET` and `COMP2P25UM_SOLID_REUSE`.
- Planned segments were `10% -> 12.5% -> 15% -> 20% -> 25%`, but each branch
  stopped after the `15%` failure.

### Observed

- Build completed and saved checked results.
- Verifier completed with `VERIFY_STATUS=PASS`.
- TFF selection remained local:
  `VERIFY_TFF_SELECTION_EQUALS_SWEPT=true`.
- Overall diagnosis:
  `COMPENSATED_FORWARD_EXTENSION_025_FAILS`.
- Checked read-back:
  - reset `12.5%`: `F_total_support = 0.0289599112844 N`, `MARGINAL`;
  - reset `15.0%`: `F_total_support = 0.0238998702244 N`, `FAIL`;
  - reuse `12.5%`: `F_total_support = 0.0289599112490 N`, `MARGINAL`;
  - reuse `15.0%`: `F_total_support = 0.0238998702290 N`, `FAIL`.
- Film state stayed stable:
  `AvgH = 3.32203827403e-06 m` at `12.5%`,
  `AvgH = 3.26999536495e-06 m` at `15.0%`,
  `MinTheta >= 0.999844169519`,
  `LowThetaAreaRatio02 = 0`.
- Raw pressure stayed below the spike limit:
  `MaxP_raw = 130692.068140 Pa` at `12.5%`,
  `MaxP_raw = 174877.441632 Pa` at `15.0%`.
- Contact and support coverage both decreased from `12.5%` to `15%`:
  `F_contact`, `F_film_support`, `active_contact_area_over_patch`, and
  `BpressOverSwept` all dropped.

### Interpretation

- Fixed `dn_comp = 2.25 um` remains a valid compensated `10%` handoff baseline
  through `576w4b`, but it is not sufficient to carry the model to `15%`.
- `576w4c` failure is a load/support/contact drift, not a film-height failure,
  theta collapse, raw-pressure spike, or TFF selection error.
- `576w4c` is the latest bounded extension attempt, but it is not an accepted
  forward-extension baseline.

### Next Step

- `576w4c_readback_diagnosis` has now been completed and classified the
  failure as `CONTACT_AND_SUPPORT_AREA_DRIFT_BEYOND_010`.
- `576w4d_forward_contact_support_localization` has now been completed and
  classified the next mechanism as `NORMAL_UNLOADING_PLUS_CORE_SUPPORT_SHRINK`.
- `576w4e_forward_normal_core_overlap_microtest` has now been completed and
  classified the requirement as `COMBINED_NORMAL_AND_CORE_OVERLAP_REQUIRED`.
- `576w4f_forward_combined_normal_core_replay` has now been completed and
  confirmed that contact `12.5%` plus explicit `CORE_0100` reaches the load
  window on the actual `15%` pressure field.
- Next create `576w4g_forward_combined_normal_core_solve_microtest`.
- Do not extend to `20%/25%`, increase pressure cap, tune
  `alpha/beta/gamma/Keff`, add friction, or put `drel` back into true TFF
  film height before that solve microtest.

### Files

- `build_stage576w4c_compensated_forward_extension_025.java`
- `build_stage576w4c_compensated_forward_extension_025_compile_stdout.txt`
- `build_stage576w4c_compensated_forward_extension_025_stdout.txt`
- `576w4c_stage576_compensated_forward_extension_025_summary.csv`
- `576w4c_stage576_compensated_forward_extension_025_verify_summary.csv`
- `576w4c_stage576_compensated_forward_extension_025.md`
- `verify_stage576w4c_results.java`
- `verify_stage576w4c_results_compile_stdout.txt`
- `verify_stage576w4c_results_stdout.txt`

## 2026-06-28-576w4b

### Changed

- Created `build_stage576w4b_compensated_handoff_confirmation.java`.
- Created `verify_stage576w4b_results.java`.
- Started from `576w3r_stage576_geometry_freeze_short_coupled_extension_checked.mph`.
- Re-ran the full controlled `7.5% -> 10%` TFF + solid handoff with fixed
  `dn_comp = 2.25 um`.
- Kept `CAP_6P5KPA`, `alpha = 0.15`, `h_TFF = h_calc573`, and local TFF
  selection.
- Did not continue beyond `10%`, tune gains, increase cap, add friction, or put
  `drel` back into the true TFF film height.

### Observed

- Build completed with `W4B_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `COUPLED_NORMAL_POSITION_HANDOFF_PASS_BOTH`.
- Checked read-back:
  - reset branch: `F_total = 0.0302247450299 N`, `PASS`;
  - reuse branch: `F_total = 0.0302270185436 N`, `PASS`.
- Film state remains stable:
  `AvgH = 3.33875957936e-06 m`,
  `MinTheta = 0.999905690467`,
  `LowThetaAreaRatio02 = 0`,
  `MaxP_raw = 103713.446598 Pa`.
- TFF selection remained local:
  `VERIFY_TFF_SELECTION_EQUALS_SWEPT=true`.

### Interpretation

- Fixed `2.25 um` inward normal-position compensation cleanly recovers the
  `10%` load window in the full `7.5% -> 10%` TFF + solid handoff.
- The earlier `10%` contact-force drop is best explained as a small normal
  position / indentation handoff offset, not as a pressure cap, gain, or
  friction-model issue.
- `576w4b` is the preferred compensated `10%` diagnostic baseline, but still
  not a full-cycle or mixed-lubrication result.

### Next Step

- Keep `dn_comp = 2.25 um` fixed for any subsequent bounded extension.
- Do not increase pressure cap, tune `alpha/beta/gamma/Keff`, add boundary or
  asperity friction, or put `drel` back into the true TFF film height.

### Files

- `build_stage576w4b_compensated_handoff_confirmation.java`
- `build_stage576w4b_compensated_handoff_confirmation_stdout.txt`
- `576w4b_stage576_compensated_handoff_confirmation_summary.csv`
- `576w4b_stage576_compensated_handoff_confirmation_verify_summary.csv`
- `576w4b_stage576_compensated_handoff_confirmation.md`
- `verify_stage576w4b_results.java`
- `verify_stage576w4b_results_stdout.txt`

## 2026-06-28-576w4a

### Changed

- Created `build_stage576w4a_normal_position_compensation_refinement.java`.
- Created `verify_stage576w4a_results.java`.
- Started from `576w3z_stage576_coupled_normal_position_handoff_checked.mph`.
- Reused the checked `10%` TFF pressure fields and re-ran solid handoff with
  fixed `dn_comp = 2.25 um`.
- Set `dn_comp576w3z = 0` during this replay to avoid double-counting.
- Kept `CAP_6P5KPA`, `alpha = 0.15`, `h_TFF = h_calc573`, and local TFF
  pressure readout.

### Observed

- Build completed with `W4A_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `NORMAL_POSITION_COMPENSATION_2P25UM_PASS_BOTH`.
- Checked read-back:
  - reset branch: `F_total = 0.0302247450299 N`, `PASS`;
  - reuse branch: `F_total = 0.0302270185436 N`, `PASS`.
- Film state remains stable:
  `AvgH = 3.33875957936e-06 m`,
  `MinTheta = 0.999905690467`,
  `LowThetaAreaRatio02 = 0`,
  `MaxP_raw = 103713.446598 Pa`.

### Interpretation

- Fixed `2.25 um` inward normal-position compensation is the current best
  candidate for the compensated `10%` handoff.
- The original `10%` contact-force drop is best interpreted as a small
  normal-position/indentation handoff offset, not a cap/gain/friction problem.
- `576w4a` is still diagnostic-only; it validates the fixed compensation at
  `10%`, but it is not a longer-stroke or full-cycle baseline.

### Next Step

- Use fixed `dn_comp = 2.25 um` in the next bounded `7.5% -> 10%` handoff
  confirmation.
- Do not increase cap pressure, tune `alpha/beta/gamma/Keff`, add boundary or
  asperity friction, or put `drel` back into the true TFF film height.

### Files

- `build_stage576w4a_normal_position_compensation_refinement.java`
- `build_stage576w4a_normal_position_compensation_refinement_stdout.txt`
- `576w4a_stage576_normal_position_compensation_refinement_summary.csv`
- `576w4a_stage576_normal_position_compensation_refinement_verify_summary.csv`
- `576w4a_stage576_normal_position_compensation_refinement.md`
- `verify_stage576w4a_results.java`
- `verify_stage576w4a_results_stdout.txt`

## 2026-06-28-576w3z

### Changed

- Created `build_stage576w3z_coupled_normal_position_handoff.java`.
- Created `verify_stage576w3z_results.java`.
- Inserted fixed `dn_comp = 2 um` into the controlled `7.5% -> 10%` coupled
  handoff.

### Observed

- Build completed with `W3Z_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Checked read-back diagnosis:
  `COUPLED_NORMAL_POSITION_HANDOFF_MIXED_PASS_MARGINAL`.
- Reset branch: `F_total = 0.0299831618432 N`, `MARGINAL`.
- Reuse branch: `F_total = 0.0300526665332 N`, `PASS`.
- Film state stayed stable.

### Interpretation

- Fixed `2 um` compensation confirms the normal-position correction mechanism,
  but it is too close to the lower load threshold for a clean reset/reuse PASS.
- This motivated the small fixed refinement to `2.25 um` in `576w4a`.

## 2026-06-28-576w3y

### Changed

- Created `build_stage576w3y_normal_position_compensation_diagnostic.java`.
- Created `verify_stage576w3y_results.java`.
- Replayed the `10%` solid state from `576w3s` with bounded inward
  normal-position compensation:
  `dn_comp576w3y = 0, 2, 4, 6, 8, 10 um`.
- Kept `CAP_6P5KPA`, `alpha = 0.15`, `h_TFF = h_calc573`, and local TFF
  selection.
- Did not tune gains, increase cap, add roughness/friction, or continue beyond
  `10%`.

### Observed

- Build completed with `W3Y_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `NORMAL_POSITION_COMPENSATION_RECOVERS_010_LOAD_BOTH`.
- Both reset and reuse branches:
  - `dn_comp = 0 um` is `MARGINAL`, `F_total ~= 0.0282543 N`;
  - `dn_comp = 2 um` is `PASS`, `F_total ~= 0.0301140 N`;
  - `dn_comp = 4 um` is `PASS`, `F_total ~= 0.0320753 N`;
  - `dn_comp = 6 um` is `MARGINAL`, `F_total ~= 0.0339083 N`;
  - `dn_comp = 8/10 um` over-compensates and fails.
- Film state remains stable:
  `AvgH = 3.33875957936e-06 m`,
  `MinTheta = 0.999905690467`,
  `LowThetaAreaRatio02 = 0`.

### Interpretation

- The `10%` contact-force drop is consistent with a small normal-position or
  indentation offset.
- The least-intrusive PASS candidate is `dn_comp = 2 um`.
- `576w3y` is diagnostic-only; it does not yet prove a final coupled
  compensated handoff.

### Next Step

- Create `576w3z` as a coupled `7.5% -> 10%` handoff test using fixed small
  normal-position compensation, preferably `dn_comp = 2 um`.
- Do not increase pressure cap, tune gains, or add boundary/asperity friction.

### Files

- `build_stage576w3y_normal_position_compensation_diagnostic.java`
- `build_stage576w3y_normal_position_compensation_diagnostic_stdout.txt`
- `576w3y_stage576_normal_position_compensation_diagnostic_summary.csv`
- `576w3y_stage576_normal_position_compensation_verify_summary.csv`
- `576w3y_stage576_normal_position_compensation_diagnostic.md`
- `verify_stage576w3y_results.java`
- `verify_stage576w3y_results_stdout.txt`

## 2026-06-28-576w3x

### Changed

- Created `probe_stage576w3x_contact_force_deficit_audit.java`.
- Compared `7.50%` and `10.00%` contact-force readouts.
- Tested load combinations:
  - current `10%` contact + current `10%` support;
  - current `10%` contact + `7.50%` core-window support;
  - `7.50%` contact + current `10%` support;
  - `7.50%` contact + `7.50%` core-window support.
- Did not solve a new segment.
- Did not change cap, gains, roughness, asperity, or boundary friction.

### Observed

- Probe completed with `W3X_PROBE_STATUS=PASS`.
- Overall diagnosis:
  `CONTACT_FORCE_DROP_PLUS_SUPPORT_AREA_DEFICIT`.
- Reset branch:
  - `Fn_contact570` drops by `0.00152016964997 N`.
  - `MaxTn` drops by about `212032.744883 Pa`.
  - active contact area fraction drops by `0.0345463047852`.
- Reuse branch:
  - `Fn_contact570` drops by `0.00143625129390 N`.
  - `MaxTn` drops by about `212031.497575 Pa`.
  - active contact area fraction drops by `0.0345463047852`.
- Combination readout:
  - restoring support only remains `MARGINAL`;
  - restoring contact only remains `MARGINAL`;
  - restoring both gives `PASS`.

### Interpretation

- The 10% deficit is a coupled contact-force and support-area deficit.
- Contact unloading is visible in peak contact pressure, active contact area,
  and less negative minimum gap.
- Increasing pressure cap would bypass the diagnosed contact/support balance
  problem.

### Next Step

- Create `576w3y` as a bounded normal-position compensation diagnostic.
- Estimate the small indentation/normal-position correction required to recover
  the remaining `1.1-1.5 mN` contact load.
- Do not tune gains, increase cap, add friction, or continue beyond `10%`.

### Files

- `probe_stage576w3x_contact_force_deficit_audit.java`
- `probe_stage576w3x_contact_force_deficit_audit_stdout.txt`
- `576w3x_stage576_contact_force_deficit_audit_summary.csv`
- `576w3x_stage576_contact_force_deficit_audit_delta.csv`
- `576w3x_stage576_contact_force_deficit_audit_combinations.csv`
- `576w3x_stage576_contact_force_deficit_audit.md`

## 2026-06-28-576w3w

### Changed

- Created `probe_stage576w3w_motion_core_expression_audit.java`.
- Printed saved motion/core expressions from `576w3s`.
- Tested explicit readout core windows:
  - `READOUT_075`
  - `READOUT_0875`
  - `READOUT_100`
- Tested both `PLUS` and `MINUS` sign conventions.
- Did not solve a new segment.
- Did not change cap, gains, roughness, asperity, or boundary friction.

### Observed

- Probe completed with `W3W_PROBE_STATUS=PASS`.
- Overall diagnosis:
  `MOTION_EXPRESSIONS_AND_EXPLICIT_CORE_READOUT_TESTED`.
- Saved expressions:
  - `tau572 = t_position576p2`
  - `theta_lid_spatial572 = theta_lid_physical572 + lid_mask_aoffset572`
  - `M_core573 = M_lid572`
- The active convention is `PLUS`.
- `MINUS` gives zero film support and is not the active support convention.
- Best explicit readout:
  `READOUT_075 / PLUS`.
- Reset branch:
  `0.0280442246329 -> 0.0287974966962 N`.
- Reuse branch:
  `0.0281427122780 -> 0.0288959843414 N`.
- The improvement is `0.000753272063358 N`, but both branches remain
  `MARGINAL`.

### Interpretation

- Core-window readout correction helps but does not recover the target load.
- The remaining 10% deficit is mainly tied to reduced solid contact force.
- The earlier `576w3v` post-hoc `t_position576p2` readout did not expose this
  because the saved solution context kept the current core state; explicit
  readout expressions are needed for this kind of postprocessing test.

### Next Step

- Create `576w3x` as a contact-force deficit audit.
- Compare `7.50%` and `10.00%` contact state and pressure-support readouts.
- Determine whether the contact-force drop is a physical target-position
  unloading effect or a handoff artifact.
- Do not tune gains, increase cap, add friction, or continue beyond `10%`.

### Files

- `probe_stage576w3w_motion_core_expression_audit.java`
- `probe_stage576w3w_motion_core_expression_audit_stdout.txt`
- `576w3w_stage576_motion_core_expression_audit_expressions.csv`
- `576w3w_stage576_motion_core_expression_audit_summary.csv`
- `576w3w_stage576_motion_core_expression_audit_branch_summary.csv`
- `576w3w_stage576_motion_core_expression_audit.md`

## 2026-06-28-576w3v

### Changed

- Created `probe_stage576w3v_core_window_audit.java`.
- Tested `10.00%` readout alternatives:
  - `FROZEN_075`
  - `LAG_0875`
  - `CURRENT_100`
- Used the same saved `576w3s` pressure and solid datasets.
- Did not solve a new segment.
- Did not change cap, gains, roughness, asperity, or boundary friction.

### Observed

- Probe completed with `W3V_PROBE_STATUS=PASS`.
- Overall diagnosis:
  `CORE_WINDOW_LAG_READOUT_TESTED`.
- All core-window variants produced the same support result.
- Reset branch:
  `F_total_variant = 0.0280442246329 N` for all variants.
- Reuse branch:
  `F_total_variant = 0.0281427122780 N` for all variants.
- `frozen075_minus_current_support = 0`.
- `core/A_swept = 0.0905577860756`.
- `Bpress/A_swept = 0.0228749423002`.

### Interpretation

- The 10% load deficit is not recovered by post-hoc lagged/frozen `M_core573`
  readout.
- The saved `576w3s` model/readout does not respond to `t_position576p2`
  changes in the way needed for this simple correction.
- The next question is whether `tau572`, `slide_fraction572`,
  `theta_lid_spatial572`, `M_lid572`, and `M_core573` are tied to the intended
  position parameter in the saved model context.

### Next Step

- Create `576w3w` as a motion/core-expression audit.
- Print actual expressions for the relevant motion and core-window variables.
- Test an explicit readout variable parameterized by a separate
  `core_fraction576w3w`.
- Keep the next step read-only until the expression state is clear.

### Files

- `probe_stage576w3v_core_window_audit.java`
- `probe_stage576w3v_core_window_audit_stdout.txt`
- `576w3v_stage576_core_window_audit_summary.csv`
- `576w3v_stage576_core_window_audit_branch_summary.csv`
- `576w3v_stage576_core_window_audit.md`

## 2026-06-28-576w3u

### Changed

- Created `probe_stage576w3u_support_area_diagnosis.java`.
- Decomposed support-area terms between `7.50%` and `10.00%`.
- Did not solve a new segment.
- Did not increase pressure cap.
- Did not tune `alpha/beta/gamma/Keff`.
- Did not add asperity or boundary friction.

### Observed

- Probe completed with `W3U_PROBE_STATUS=PASS`.
- Overall diagnosis:
  `SUPPORT_AREA_SHRINK_WITH_CONTACT_LOSS`.
- `Bpress/A_swept` decreased:
  `0.0249676348216 -> 0.0228749423002`.
- `core/A_swept` decreased:
  `0.0950677282709 -> 0.0905577860756`.
- `Bhigh/A_swept` stayed unchanged:
  `0.981603420071`.
- `B_low573`, `g_pair_valid573`, and `g_pair_safe573` readouts stayed
  unchanged in this probe.
- Effective `area_Bpress` decreased by `1.83259727921e-07 m^2`.
- Extra support area needed at `CAP_6P5KPA`:
  - reset: `3.00888518023e-07 m^2`
  - reuse: `2.85736572611e-07 m^2`

### Interpretation

- The 10% support deficit is a small-area deficit combined with contact-force
  loss.
- The high-gap cutoff is not the limiting mask component in this readout.
- The core/support window is the component that changes from `7.50%` to
  `10.00%`.
- Increasing cap pressure would bypass the actual diagnosis.

### Next Step

- Create `576w3v` as a support-mask/core-window audit.
- Inspect `M_core573` and test whether it should be fixed, lagged, or
  target-position dependent during geometry-freeze handoff.
- Do not tune gains, add friction, or continue beyond `10%`.

### Files

- `probe_stage576w3u_support_area_diagnosis.java`
- `probe_stage576w3u_support_area_diagnosis_stdout.txt`
- `576w3u_stage576_support_area_diagnosis_summary.csv`
- `576w3u_stage576_support_area_diagnosis_delta.csv`
- `576w3u_stage576_support_area_diagnosis.md`

## 2026-06-28-576w3t

### Changed

- Created `probe_stage576w3t_010_load_deficit_diagnosis.java`.
- Compared:
  - `576w3r` `7.50%` PASS state,
  - `576w3s` `10.00%` MARGINAL state.
- Did not solve a new segment.
- Did not tune `alpha/beta/gamma/Keff`.
- Did not increase `CAP_6P5KPA`.
- Did not add asperity or boundary friction.

### Observed

- Probe completed with `W3T_PROBE_STATUS=PASS`.
- Overall diagnosis:
  `010_LOAD_DEFICIT_WITH_STABLE_FILM_STATE`.
- `7.50%` reset:
  `F_total_support = 0.0302889718282 N`.
- `10.00%` reset:
  `F_total_support = 0.0280442246329 N`.
- `7.50%` reuse:
  `F_total_support = 0.0303035411173 N`.
- `10.00%` reuse:
  `F_total_support = 0.0281427122780 N`.
- Delta from `7.50%` to `10.00%`:
  - reset:
    `delta_F_contact = -0.00152016964997 N`,
    `delta_F_support = -0.000724577545398 N`.
  - reuse:
    `delta_F_contact = -0.00143625129390 N`,
    `delta_F_support = -0.000724577545398 N`.
- `Bpress/A_swept` decreased from `0.0249676348216` to `0.0228749423002`.
- `AvgH` stayed near `3.34 um`.
- `MinTheta` stayed near `0.9999`.

### Interpretation

- The 10% load deficit is not due to film-height contamination, theta collapse,
  or selection drift.
- The dominant change is contact-force loss, with capped film support also
  decreasing.
- Raw masked pressure increases, but the fixed cap and smaller effective
  support area prevent that raw pressure increase from recovering the lost
  normal load.
- `AvgGap` returned `NaN` in this probe and was not used for the conclusion.

### Next Step

- Create `576w3u` as a support-area interpretation diagnosis.
- Compare spatial `Bpress` support area at `7.50%` and `10.00%`.
- Test whether the deficit can be explained by target-position geometry
  opening and support-area shrinkage without increasing cap pressure.
- Do not tune gains, add friction, or continue beyond `10%`.

### Files

- `probe_stage576w3t_010_load_deficit_diagnosis.java`
- `probe_stage576w3t_010_load_deficit_diagnosis_stdout.txt`
- `576w3t_stage576_010_load_deficit_diagnosis_summary.csv`
- `576w3t_stage576_010_load_deficit_diagnosis_delta.csv`
- `576w3t_stage576_010_load_deficit_diagnosis.md`

## 2026-06-28-576w3s

### Changed

- Created `576w3s_stage576_geometry_freeze_010_extension`.
- Started from `576w3r_stage576_geometry_freeze_short_coupled_extension_checked.mph`.
- Extended the accepted baseline-geometry handoff from `7.50%` to `10.00%`.
- Tested two branches:
  - `BASE_GEOM_FREEZE_SOLID_RESET`
  - `BASE_GEOM_FREEZE_SOLID_REUSE`
- Kept `h_TFF = h_calc573`.
- Kept `TFF geometry source = sol282`.
- Kept `TFF selection = sel_film_swept571`.
- Kept `cap_pressure = 6.5 kPa`.
- Kept `alpha_pfb576w3s = 0.15`.
- Did not tune `alpha/beta/gamma/Keff`.
- Did not add asperity or boundary friction.

### Observed

- Build completed with `W3S_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `GEOMETRY_FREEZE_010_EXTENSION_MARGINAL_BOTH`.
- Independent verifier values:
  - `BASE_GEOM_FREEZE_SOLID_RESET`:
    `F_total_support = 0.0280442246329 N`,
    `AvgH = 3.33875957936e-06 m`,
    `MinTheta = 0.999905690467`.
  - `BASE_GEOM_FREEZE_SOLID_REUSE`:
    `F_total_support = 0.0281427122780 N`,
    `AvgH = 3.33875957936e-06 m`,
    `MinTheta = 0.999905690467`.
- `LowThetaAreaRatio02 = 0`.
- `MaxP_raw = 103713.446598 Pa`.
- TFF selection stayed equal to `sel_film_swept571`.

### Interpretation

- The geometry-freeze handoff remains stable at `10.00%`.
- The fixed `CAP_6P5KPA` branch no longer cleanly closes the normal load at
  `10.00%`.
- The failure mode is not film-height contamination, selection drift, or theta
  collapse; it is a load-support deficit under the capped pressure support.
- This stage is diagnostic-only `MARGINAL`, not a clean `PASS`.

### Next Step

- Create `576w3t` as a targeted `7.50% -> 10.00%` load-deficit diagnosis.
- Compare contact-force loss, capped support area, `Bpress/A_swept`, gap shift,
  and geometry state between the passing 7.50% state and marginal 10.00% state.
- Do not increase pressure cap, tune `alpha/beta/gamma/Keff`, continue beyond
  `10%`, or add asperity/boundary friction.

### Files

- `build_stage576w3s_geometry_freeze_010_extension.java`
- `build_stage576w3s_geometry_freeze_010_extension_stdout.txt`
- `verify_stage576w3s_results.java`
- `verify_stage576w3s_results_stdout.txt`
- `576w3s_stage576_geometry_freeze_010_extension_summary.csv`
- `576w3s_stage576_geometry_freeze_010_extension_results.mph`
- `576w3s_stage576_geometry_freeze_010_extension_checked.mph`
- `576w3s_stage576_geometry_freeze_010_extension.md`

## 2026-06-28-576w3r

### Changed

- Created `576w3r_stage576_geometry_freeze_short_coupled_extension`.
- Started from `576w3q_stage576_solve_level_geometry_handoff_correction_checked.mph`.
- Converted the accepted baseline-geometry TFF handoff into a short coupled
  extension:
  `6.25% -> 7.00% -> 7.50%`.
- Tested two branches:
  - `BASE_GEOM_FREEZE_SOLID_RESET`
  - `BASE_GEOM_FREEZE_SOLID_REUSE`
- Kept `h_TFF = h_calc573`.
- Kept `TFF geometry source = sol282`.
- Kept `TFF selection = sel_film_swept571`.
- Kept `cap_pressure = 6.5 kPa`.
- Kept `alpha_pfb576w3r = 0.15`.
- Did not tune `alpha/beta/gamma/Keff`.
- Did not continue to `10%`.
- Did not add asperity or boundary friction.

### Observed

- Build completed with `W3R_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `GEOMETRY_FREEZE_SHORT_EXTENSION_PASS_BOTH`.
- Independent verifier values:
  - `BASE_GEOM_FREEZE_SOLID_RESET`, segment 1 / `7.00%`:
    `F_total_support = 0.0304135223232 N`,
    `MinTheta = 0.999941451754`.
  - `BASE_GEOM_FREEZE_SOLID_RESET`, segment 2 / `7.50%`:
    `F_total_support = 0.0302889718282 N`,
    `MinTheta = 0.999936198097`.
  - `BASE_GEOM_FREEZE_SOLID_REUSE`, segment 1 / `7.00%`:
    `F_total_support = 0.0304135223232 N`,
    `MinTheta = 0.999941451754`.
  - `BASE_GEOM_FREEZE_SOLID_REUSE`, segment 2 / `7.50%`:
    `F_total_support = 0.0303035411173 N`,
    `MinTheta = 0.999936198097`.
- `AvgH` stayed at about `3.354-3.355 um`.
- `MaxP_raw` stayed about `73.8-78.8 kPa`.
- `LowThetaAreaRatio02 = 0`.
- TFF selection stayed equal to `sel_film_swept571`.

### Interpretation

- The baseline-geometry freeze handoff remains stable across the short coupled
  path to `7.50%`.
- Solid reset and solid reuse both pass, so the accepted correction is not
  dependent on contact-state reset.
- The next risk is whether the same controlled handoff remains stable when
  extended from `7.50%` to `10.00%`.

### Next Step

- Create `576w3s_stage576_geometry_freeze_010_extension`.
- Test only `7.50% -> 10.00%` or the full controlled chain to `10.00%` using
  the same frozen-geometry handoff.
- Keep `CAP_6P5KPA` and `alpha=0.15`.
- Keep `h_TFF = h_calc573`; do not add `drel` to real TFF film height.
- Do not continue beyond `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3r_geometry_freeze_short_coupled_extension.java`
- `build_stage576w3r_geometry_freeze_short_coupled_extension_stdout.txt`
- `verify_stage576w3r_results.java`
- `verify_stage576w3r_results_stdout.txt`
- `576w3r_stage576_geometry_freeze_short_coupled_extension_summary.csv`
- `576w3r_stage576_geometry_freeze_short_coupled_extension_results.mph`
- `576w3r_stage576_geometry_freeze_short_coupled_extension_checked.mph`
- `576w3r_stage576_geometry_freeze_short_coupled_extension.md`

## 2026-06-28-576w3q

### Changed

- Created `576w3q_stage576_solve_level_geometry_handoff_correction`.
- Started from `576w3p_stage576_geometry_handoff_correction_microtest_checked.mph`.
- Tested `7.00%` and `7.50%` with:
  - current failing geometry,
  - baseline/frozen geometry `sol282`,
  - solid reset,
  - solid reuse.
- Kept `h_TFF = h_calc573`.
- Kept `TFF selection = sel_film_swept571`.
- Kept `cap_pressure = 6.5 kPa`.
- Kept feedback scale `alpha_pfb576w3q = 0.15`.
- Did not tune `alpha/beta/gamma/Keff`.
- Did not continue to `10%`.
- Did not add asperity or boundary friction.

### Observed

- Build completed with `W3Q_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `SOLVE_LEVEL_GEOMETRY_FREEZE_ACCEPTED_CONTACT_REUSE_OK`.
- Current failing geometry remained failed:
  - `CURR_GEOM_700_READ`:
    `F_total_support = 0.0275151306616 N`,
    `MinTheta = 0.212263220474`.
  - `CURR_GEOM_700_SOLID_RESET`:
    `F_total_support = 0.0279152511889 N`,
    `MinTheta = 0.213629709416`.
  - `CURR_GEOM_750_READ`:
    `F_total_support = 0.0253228646089 N`.
  - `CURR_GEOM_750_SOLID_RESET`:
    `F_total_support = 0.0255055921720 N`.
- Baseline/frozen geometry passed:
  - `BASE_GEOM_700_READ`:
    `F_total_support = 0.0319050341945 N`.
  - `BASE_GEOM_700_SOLID_RESET`:
    `F_total_support = 0.0308579973370 N`.
  - `BASE_GEOM_700_SOLID_REUSE`:
    `F_total_support = 0.0308280893527 N`.
  - `BASE_GEOM_750_READ`:
    `F_total_support = 0.0318979909491 N`.
  - `BASE_GEOM_750_SOLID_RESET`:
    `F_total_support = 0.0309150827390 N`.
  - `BASE_GEOM_750_SOLID_REUSE`:
    `F_total_support = 0.0308837268578 N`.

### Interpretation

- The correction works when the TFF pressure solve uses baseline/frozen
  geometry `sol282`.
- Contact reset is not the main requirement: both solid reset and solid reuse
  pass when the TFF geometry source is frozen.
- Current failing geometry remains bad even with solid reset, so the limiting
  mechanism is solve-level TFF geometry handoff.
- This validates a geometry-freeze or geometry-limited handoff branch for the
  next short coupled extension.

### Next Step

- Create `576w3r_stage576_geometry_freeze_short_coupled_extension`.
- Test only `6.25% -> 7.00% -> 7.50%`.
- Use the accepted geometry-freeze handoff from `576w3q`.
- Keep `CAP_6P5KPA` and `alpha=0.15`.
- Keep `h_TFF = h_calc573`; do not add `drel` to real TFF film height.
- Do not continue to `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3q_solve_level_geometry_handoff_correction.java`
- `build_stage576w3q_solve_level_geometry_handoff_correction_stdout.txt`
- `verify_stage576w3q_results.java`
- `verify_stage576w3q_results_stdout.txt`
- `576w3q_stage576_solve_level_geometry_handoff_correction_summary.csv`
- `576w3q_stage576_solve_level_geometry_handoff_correction_results.mph`
- `576w3q_stage576_solve_level_geometry_handoff_correction_checked.mph`
- `576w3q_stage576_solve_level_geometry_handoff_correction.md`

## 2026-06-28-576w3p

### Changed

- Created `576w3p_stage576_geometry_handoff_correction_microtest`.
- Started from `576w3o_stage576_solid_geometry_handoff_probe_checked.mph`.
- Tested readout-only `Bpress` correction variants on failing coupled solid
  states:
  - `CURRENT`
  - `FROZEN_BASE`
  - `SMOOTH_50`
  - `BOUNDED_10PCT`
- Did not run a new continuation.
- Did not tune `alpha/beta/gamma/Keff`.
- Did not increase cap pressure.
- Did not add asperity or boundary friction.

### Observed

- Build completed with `W3P_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `READOUT_BPRESS_CORRECTION_FAILS`.
- At `7.00%`, all variants failed:
  - `CURRENT`: `F_total_support = 0.0275151306489 N`,
    `MinTheta = 0.213681426078`.
  - `FROZEN_BASE`: `F_total_support = 0.0255550347501 N`.
  - `SMOOTH_50`: `F_total_support = 0.0265350826995 N`.
  - `BOUNDED_10PCT`: `F_total_support = 0.0255873128434 N`.
- At `7.50%`, all variants failed:
  - `CURRENT`: `F_total_support = 0.0253228646089 N`,
    `MinTheta = 0.999922397890`.
  - `FROZEN_BASE`: `F_total_support = 0.0242007691806 N`.
  - `SMOOTH_50`: `F_total_support = 0.0247618168948 N`.
  - `BOUNDED_10PCT`: `F_total_support = 0.0241464785922 N`.
- TFF selection stayed equal to `sel_film_swept571`.
- `AvgH` stayed at about `3.27 um`.

### Interpretation

- Freezing, smoothing, or bounding `Bpress` only at the readout/support
  integration level does not recover load closure.
- At `7.00%`, the bad theta state is already present, so a later mask readout
  correction cannot repair the pressure/theta branch.
- At `7.50%`, theta is normal but capped support remains too low.
- The next correction must intervene before or during geometry/contact handoff
  in the solve chain, not after the failing solid state has already been
  produced.

### Next Step

- Create `576w3q_stage576_solve_level_geometry_handoff_correction`.
- Test solve-level geometry/contact handoff correction:
  - freeze or smooth gap state before TFF pressure solve,
  - reset or reinitialize solid contact state before replay,
  - add a bounded solid-gap or `Bpress` handoff limiter before pressure solve,
  - separate structural contact geometry from TFF support-mask geometry.
- Do not continue to `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not increase pressure cap just to recover load.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3p_geometry_handoff_correction_microtest.java`
- `build_stage576w3p_geometry_handoff_correction_microtest_stdout.txt`
- `verify_stage576w3p_results.java`
- `verify_stage576w3p_results_stdout.txt`
- `576w3p_stage576_geometry_handoff_correction_microtest_summary.csv`
- `576w3p_stage576_geometry_handoff_correction_microtest_results.mph`
- `576w3p_stage576_geometry_handoff_correction_microtest_checked.mph`
- `576w3p_stage576_geometry_handoff_correction_microtest.md`

## 2026-06-28-576w3o

### Changed

- Created `576w3o_stage576_solid_geometry_handoff_probe`.
- Started from `576w3n_stage576_coupled_handoff_replay_diagnosis_checked.mph`.
- Read only the relevant replay datasets:
  - baseline `sol282` at `7.00%`
  - failing `sol306` at `7.00%`
  - baseline `sol282` at `7.50%`
  - failing `sol330` at `7.50%`
- Did not run a new continuation.
- Did not tune `alpha/beta/gamma/Keff`.
- Did not increase cap pressure.
- Did not add asperity or boundary friction.

### Observed

- Build completed with `W3O_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `GAP_MASK_GEOMETRY_SHIFT_DOMINANT`.
- At `7.00%`:
  - baseline `sol282`:
    `F_total_support = 0.0319050339490 N`,
    `MinGap = -61.1962052459 um`,
    `Bpress/A_swept = 0.0249934656940`.
  - failing `sol306`:
    `F_total_support = 0.0275151306489 N`,
    `MinGap = -91.0026728393 um`,
    `Bpress/A_swept = 0.0306078772540`,
    `MinTheta = 0.213681426078`.
- At `7.50%`:
  - baseline `sol282`:
    `F_total_support = 0.0318979910947 N`,
    `MinGap = -61.1962052459 um`,
    `Bpress/A_swept = 0.0249676348216`.
  - failing `sol330`:
    `F_total_support = 0.0253228646089 N`,
    `MinGap = -103.505959932 um`,
    `Bpress/A_swept = 0.0280890428946`.

### Interpretation

- The coupled solid states shift local gap/mask geometry.
- The MinGap shift is about `30 um` at `7.00%` and about `42 um` at `7.50%`
  relative to baseline `sol282`.
- `Bpress/A_swept` increases while capped support drops.
- The next corrective target is the geometry-to-mask handoff, not pressure cap,
  feedback gains, or friction.

### Next Step

- Create `576w3p_stage576_geometry_handoff_correction_microtest`.
- Test a controlled geometry handoff correction around `7.00%` and `7.50%`.
- Candidate corrections:
  - freeze or smooth the geometry/gap state used by `Bpress`
  - separate structural contact geometry from TFF support-mask geometry
  - add a bounded gap-to-mask update limiter
- Keep `h_TFF = h_calc573`.
- Keep `drel` out of real TFF film height.
- Do not continue to `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3o_solid_geometry_handoff_probe.java`
- `build_stage576w3o_solid_geometry_handoff_probe_stdout.txt`
- `verify_stage576w3o_results.java`
- `verify_stage576w3o_results_stdout.txt`
- `576w3o_stage576_solid_geometry_handoff_probe_checked.mph`
- `576w3o_stage576_solid_geometry_handoff_probe_results.mph`
- `576w3o_stage576_solid_geometry_handoff_probe_summary.csv`
- `576w3o_stage576_solid_geometry_handoff_probe.md`

## 2026-06-28-576w3n

### Changed

- Created `576w3n_stage576_coupled_handoff_replay_diagnosis`.
- Started from `576w3m_stage576_tff_state_transition_diagnosis_checked.mph`.
- Kept `CAP_6P5KPA`, `h_TFF = h_calc573`, and
  `TFF wall velocity = M_drain573*Bfilm573`.
- Did not tune `alpha/beta/gamma/Keff`.
- Did not add asperity or boundary friction.
- Read back historical `576w3l` coupled iterations and ran cross TFF replays:
  pressure-history-only versus solid/geometry-state-only.

### Observed

- Build completed with `W3N_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `SOLID_GEOMETRY_HANDOFF_DOMINANT`.
- At `7.00%`:
  - `REPLAY_700_BASEP_SOLIDI2` failed using baseline pressure `sol280` and
    coupled solid state `sol306`:
    `F_total_support = 0.0275151306489 N`,
    `MinTheta = 0.213681426078`.
  - `REPLAY_700_PRESSI2_BASESOLID` stayed stable using coupled pressure
    history `sol304` and baseline solid `sol282`:
    `F_total_support = 0.0319050339490 N`,
    `MinTheta = 0.999941451515`.
- At `7.50%`:
  - `REPLAY_750_BASEP_SOLIDI1` failed using baseline pressure `sol280` and
    coupled solid state `sol330`:
    `F_total_support = 0.0253228646089 N`.
  - `REPLAY_750_PRESSI1_BASESOLID` stayed stable using coupled pressure
    history `sol328` and baseline solid `sol282`:
    `F_total_support = 0.0318979910947 N`.
- TFF selection stayed equal to `sel_film_swept571`.

### Interpretation

- Pressure history alone does not reproduce the failure.
- Coupled solid/geometry state alone does reproduce the failure.
- The next bottleneck is the solid/geometry handoff: gap distribution, mask
  area, contact state, and drel/indentation-release mapping.

### Next Step

- Create `576w3o_stage576_solid_geometry_handoff_probe`.
- Compare `sol282`, `sol306`, and `sol330`.
- Inspect gap distribution, Bpress/Bwall area, contact force, contact-area
  proxy, and drel/geometry mapping.
- Do not continue to `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3n_coupled_handoff_replay_diagnosis.java`
- `build_stage576w3n_coupled_handoff_replay_diagnosis_stdout.txt`
- `verify_stage576w3n_results.java`
- `verify_stage576w3n_results_stdout.txt`
- `576w3n_stage576_coupled_handoff_replay_diagnosis_checked.mph`
- `576w3n_stage576_coupled_handoff_replay_diagnosis_results.mph`
- `576w3n_stage576_coupled_handoff_replay_diagnosis_summary.csv`
- `576w3n_stage576_coupled_handoff_replay_diagnosis.md`

## 2026-06-28-576w3m

### Changed

- Created `576w3m_stage576_tff_state_transition_diagnosis`.
- Started from `576w3l_stage576_pressure_cap_075_failure_diagnosis_checked.mph`.
- Used the stable `6.25%` inherited state:
  `pressureInit=sol280` and `solidState=sol282`.
- Compared:
  `FROZEN_TFF_ONLY` against `W3L_COUPLED_FINAL`.
- Tested:
  `7.00%`, `7.25%`, and `7.50%`.
- Kept `CAP_6P5KPA`, `h_TFF = h_calc573`, and
  `TFF wall velocity = M_drain573*Bfilm573`.
- Did not tune `alpha/beta/gamma/Keff`.
- Did not add asperity or boundary friction.

### Observed

- Build completed with `W3M_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `COUPLED_HANDOFF_TRANSITION_SUSPECT`.
- Frozen TFF-only replay stayed stable:
  - `7.00%`: `F_total_support = 0.0319050341945 N`,
    `MaxP_raw = 73.8 kPa`, `MinTheta = 0.999941451754`.
  - `7.25%`: `F_total_support = 0.0319066370296 N`,
    `MaxP_raw = 76.3 kPa`, `MinTheta = 0.999938982831`.
  - `7.50%`: `F_total_support = 0.0318979909491 N`,
    `MaxP_raw = 78.8 kPa`, `MinTheta = 0.999936198706`.
- Coupled final read-back reproduced the `576w3l` transition:
  - `7.00%`: `THETA_PRESSURE_WARNING`,
    `MaxP_raw = 16.7 MPa`, `MinTheta = 0.0634422225228`.
  - `7.25%`: `STABLE_LOAD_WINDOW`.
  - `7.50%`: `LOAD_SUPPORT_FAIL`,
    `F_total_support = 0.0259962587863 N`.
- TFF selection stayed equal to `sel_film_swept571`.

### Interpretation

- The current evidence does not support TFF PDE-only instability as the primary
  cause.
- The warning/failure appears after coupled handoff through relaxed pressure,
  solid state, contact state, or geometry/drel inheritance.
- Increasing cap pressure or tuning `alpha/beta/gamma/Keff` would hide the
  mechanism rather than diagnose it.

### Next Step

- Create `576w3n_stage576_coupled_handoff_replay_diagnosis`.
- Focus on `7.00%`, `7.25%`, and `7.50%`.
- Compare relaxed-pressure history, solid-state inheritance, and contact-state
  reset/reuse.
- Do not continue to `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3m_tff_state_transition_diagnosis.java`
- `build_stage576w3m_tff_state_transition_diagnosis_stdout.txt`
- `verify_stage576w3m_results.java`
- `verify_stage576w3m_results_stdout.txt`
- `576w3m_stage576_tff_state_transition_diagnosis_checked.mph`
- `576w3m_stage576_tff_state_transition_diagnosis_results.mph`
- `576w3m_stage576_tff_state_transition_diagnosis_summary.csv`
- `576w3m_stage576_tff_state_transition_diagnosis.md`

## 2026-06-28-576w3l

### Changed

- Created `576w3l_stage576_pressure_cap_075_failure_diagnosis`.
- Started from `576w3k_after_segment_3_0625.mph`.
- Restarted each microtarget independently from the stable `6.25%` state:
  `pressureInit=sol280`, `relaxedInit=sol281`, and `solidState=sol282`.
- Tested:
  `6.25% -> 6.50%`,
  `6.25% -> 6.75%`,
  `6.25% -> 7.00%`,
  `6.25% -> 7.25%`,
  and `6.25% -> 7.50%`.
- Kept `CAP_6P5KPA`, `h_TFF = h_calc573`, and `drel` as solid
  indentation-release only.
- Kept `alpha=0.15`, `beta=0.15`, `gamma=0.12`, and `Keff=5000 N/m`.
- Structural feedback used relaxed `p_support576w3l`, not raw `tff.p` and not
  `solid.Tn`.

### Observed

- Build completed with `W3L_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=FAIL`.
- Overall diagnosis:
  `FAILURE_BETWEEN_0725_AND_0750_WITH_INTERMITTENT_THETA_PRESSURE_WARNING`.
- Target `6.50%` passed:
  `F_total_support = 0.0316369305909 N`,
  `AvgH = 3.33345613554 um`,
  `MinTheta = 0.999951696006`.
- Target `6.75%` passed:
  `F_total_support = 0.0319811986933 N`,
  `AvgH = 3.30935231613 um`,
  `MinTheta = 0.999973288651`.
- Target `7.00%` was `MARGINAL`:
  `F_total_support = 0.0314431827624 N`,
  `AvgH = 3.29000229718 um`,
  `MaxP_raw = 16.7 MPa`,
  `MinTheta = 0.0634422225228`,
  `LowThetaAreaRatio02 = 0.000862191600078`.
- Target `7.25%` passed:
  `F_total_support = 0.0325208397726 N`,
  `AvgH = 3.27111605913 um`,
  `MinTheta = 0.999932467166`.
- Target `7.50%` failed:
  `F_total_support = 0.0259962587863 N`,
  `AvgH = 3.29008432837 um`,
  `MinTheta = 0.999816021934`.
- TFF selection stayed equal to `sel_film_swept571`.
- `DrelSaturationRatio` stayed at `0.124308574393`.

### Interpretation

- The hard load-support failure is localized between `7.25%` and `7.50%`.
- The failure is not caused by drel saturation, TFF selection drift, or loss of
  the `3 um`-scale average film height.
- `7.00%` is a warning point: the final load is acceptable, but raw TFF
  pressure and local theta become briefly suspect.
- The next diagnostic should isolate a TFF state/branch transition, not tune
  cap size or feedback gains.

### Next Step

- Create `576w3m_stage576_tff_state_transition_diagnosis`.
- Focus on `7.00%`, `7.25%`, and `7.50%`.
- Compare frozen TFF-only, frozen solid/release, and capped coupled feedback
  behavior to determine whether the transition is caused by the TFF PDE state,
  boundary velocity/mask geometry, or solid-load handoff.
- Do not continue to `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3l_pressure_cap_075_failure_diagnosis.java`
- `build_stage576w3l_pressure_cap_075_failure_diagnosis_stdout.txt`
- `verify_stage576w3l_results.java`
- `verify_stage576w3l_results_stdout.txt`
- `576w3l_stage576_pressure_cap_075_failure_diagnosis_checked.mph`
- `576w3l_stage576_pressure_cap_075_failure_diagnosis_results.mph`
- `576w3l_stage576_pressure_cap_075_failure_diagnosis_summary.csv`
- `576w3l_stage576_pressure_cap_075_failure_diagnosis_iterations.csv`
- `576w3l_stage576_pressure_cap_075_failure_diagnosis.md`

## 2026-06-28-576w3k

### Changed

- Created `576w3k_stage576_pressure_cap_short_extension`.
- Started from `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`.
- Used the selected `576w3j` fixed cap:
  `CAP_6P5KPA`.
- Tested only:
  `5% -> 6.25% -> 7.5%`.
- Kept `h_TFF = h_calc573`; `drel` remains solid indentation release only.
- Kept `alpha=0.15`, `beta=0.15`, `gamma=0.12`, and `Keff=5000 N/m`.
- Structural feedback used relaxed `p_support576w3k`, not raw `tff.p` and not
  `solid.Tn`.

### Observed

- Build completed with `W3K_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=FAIL`.
- Overall diagnosis:
  `CAP_6P5_FAILS_AT_075`.
- Segment 3 / `6.25%` remained `PASS`:
  `F_total_support = 0.0315863923117 N`,
  `AvgH = 3.35121338445 um`,
  `MinTheta = 0.999948453776`.
- Segment 4 / `7.5%` failed:
  `F_total_support = 0.0244334008270 N`,
  `F_film_raw_swept = 9.35661253392 N`,
  `F_film_raw_masked = 3.08212020049 N`,
  `MaxP_raw = 58.4 MPa`,
  `MinTheta = 0.000681273035266`,
  `AvgH = 3.28377925627 um`,
  `DrelSaturationRatio = 0.124308574393`,
  `TFF selection = sel_film_swept571`.

### Interpretation

- `CAP_6P5KPA` remains valid for `5% -> 6.25%`.
- It cannot be promoted across `7.5%` as-is.
- The `7.5%` failure is not from drel saturation, selection drift, or loss of
  average 3 um-scale film height.
- The failure is a position-triggered raw TFF pressure/theta collapse. The cap
  prevents the raw spike from overloading the solid, but the capped support
  then under-carries the load.

### Next Step

- Create `576w3l_stage576_pressure_cap_075_failure_diagnosis`.
- Keep `CAP_6P5KPA` and the same gains.
- Localize the onset using smaller targets between `6.25%` and `7.5%`:
  `6.50%`, `6.75%`, `7.00%`, `7.25%`, and `7.50%`.
- Do not continue to `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3k_pressure_cap_short_extension.java`
- `build_stage576w3k_pressure_cap_short_extension_stdout.txt`
- `verify_stage576w3k_results.java`
- `verify_stage576w3k_results_stdout.txt`
- `576w3k_stage576_pressure_cap_short_extension_results.mph`
- `576w3k_stage576_pressure_cap_short_extension_summary.csv`
- `576w3k_stage576_pressure_cap_short_extension_iterations.csv`
- `576w3k_stage576_pressure_cap_short_extension_diagnostic.md`

## 2026-06-28-576w3j

### Changed

- Created `576w3j_stage576_pressure_cap_refinement_microtest`.
- Started from `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`.
- Tested only `5% -> 6.25%`.
- Kept `h_TFF = h_calc573`; `drel` remains solid indentation release only.
- Kept `alpha=0.15`, `beta=0.15`, `gamma=0.12`, and `Keff=5000 N/m`.
- Compared independent branches:
  `CAP_6P5KPA`, `CAP_7P0KPA`, `CAP_7P5KPA`, and `LOAD_LIMITED_REF`.
- Structural feedback used relaxed `p_support576w3j`, not raw `tff.p` and not
  `solid.Tn`.

### Observed

- Build completed with `W3J_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `FIXED_CAP_PASS_SELECT_BEST_BRANCH`.
- Fixed cap monotonicity check passed.
- `CAP_6P5KPA` was `PASS`:
  `F_total_support = 0.0315863923117 N`,
  `Residual = 0.00114312384144 N`.
- `CAP_7P0KPA` was `PASS`:
  `F_total_support = 0.0325100989913 N`,
  `Residual = 0.00122916284895 N`.
- `CAP_7P5KPA` was `MARGINAL`:
  `F_total_support = 0.0334552214752 N`,
  `Residual = 0.00131017716771 N`.
- `LOAD_LIMITED_REF` was `MARGINAL` and remains a reference only:
  `F_total_support = 0.0292133418041 N`.
- All branches preserved `AvgH = 3.35-3.37 um`, `MinTheta ~ 0.99995`,
  `LowThetaAreaRatio02 = 0`, and TFF selection `sel_film_swept571`.

### Interpretation

- A fixed pressure cap can close the short coupled `5% -> 6.25%` load target
  without using self-referential load-limited scaling.
- `CAP_6P5KPA` is the preferred next candidate because it gives the cleanest
  margin inside the `0.030-0.033 N` preferred window.
- `CAP_7P0KPA` is a useful backup candidate.
- `CAP_7P5KPA` remains slightly high and should not be the default next branch.

### Next Step

- Create `576w3k_stage576_pressure_cap_short_extension`.
- Use `CAP_6P5KPA` as the default selected cap.
- Test only `5% -> 6.25% -> 7.5%`.
- Do not proceed directly to `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3j_pressure_cap_refinement_microtest.java`
- `build_stage576w3j_pressure_cap_refinement_microtest_stdout.txt`
- `verify_stage576w3j_results.java`
- `verify_stage576w3j_results_stdout.txt`
- `576w3j_stage576_pressure_cap_refinement_microtest_results.mph`
- `576w3j_stage576_pressure_cap_refinement_summary.csv`
- `576w3j_stage576_pressure_cap_refinement_iterations.csv`
- `576w3j_stage576_pressure_cap_refinement_diagnostic.md`
- `576w3j_stage576_pressure_cap_refinement_best_branch.md`

## 2026-06-27-576w3i

### Changed

- Created `576w3i_stage576_pressure_limited_feedback_microtest`.
- Started from `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`.
- Reused the `576w3f` short coupled structure for `5% -> 6.25%`.
- Kept `h_TFF = h_calc573`; `drel` remains solid indentation release only.
- Kept `alpha=0.15`, `beta=0.15`, `gamma=0.12`, and `Keff=5000 N/m`.
- Compared independent branches:
  `CAP_7P5KPA` and `LOAD_LIMITED`.
- Structural feedback used relaxed `p_support576w3i`, not raw `tff.p` and not
  `solid.Tn`.

### Observed

- Build completed with `W3I_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Overall diagnosis:
  `BOTH_ACCEPTED_COMPARE_STABILITY`.
- `CAP_7P5KPA` was `MARGINAL`:
  `F_total_support = 0.0334552214753 N`,
  `Residual = 0.00131017716771 N`.
- `LOAD_LIMITED` was `MARGINAL`:
  `F_total_support = 0.0292133418040 N`,
  `Residual = 0.000410391416565 N`.
- Both branches preserved `AvgH = 3.35-3.37 um`, `MinTheta ~ 0.99995`,
  `LowThetaAreaRatio02 = 0`, and TFF selection `sel_film_swept571`.

### Interpretation

- Pressure-magnitude limiting works inside the short coupled chain well enough
  to remove the severe `576w3f` overload.
- `CAP_7P5KPA` is simpler and less self-referential but slightly high.
- `LOAD_LIMITED` is closer to the target load but is controller-like.
- Because both are only `MARGINAL`, neither should be promoted as a final
  physical baseline.

### Next Step

- Create `576w3j_stage576_pressure_cap_refinement_microtest`.
- Keep the test restricted to `5% -> 6.25%`.
- Compare fixed caps around `6.5-7.5 kPa`, with `LOAD_LIMITED` as reference.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3i_pressure_limited_feedback_microtest.java`
- `build_stage576w3i_pressure_limited_feedback_microtest_stdout.txt`
- `verify_stage576w3i_results.java`
- `verify_stage576w3i_results_stdout.txt`
- `576w3i_stage576_pressure_limited_feedback_microtest_results.mph`
- `576w3i_stage576_pressure_limited_feedback_iterations.csv`
- `576w3i_stage576_pressure_limited_feedback_diagnostic.md`

## 2026-06-27-576w3h

### Changed

- Created `576w3h_stage576_tff_pressure_support_diagnostic`.
- Started from `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`.
- Inherited `pressure=sol271`, `solid=sol273`, and frozen
  `drel=4.97234297573 um`.
- Kept solid update, relaxation/bode feedback, and `drel` update disabled.
- Solved independent TFF-only targets `0.055`, `0.060`, and `0.0625`.
- Tested two height modes: `GAP_HTFF` and `FIXED_3UM_HEIGHT`.
- Tested support variants:
  `RAW_H20D5`, `MASK_H15D5`, `MASK_H12D4`, `CAP_5KPA`,
  `CAP_7P5KPA`, `CAP_10KPA`, and `LOAD_LIMITED`.

### Observed

- Build completed with `W3H_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Diagnostic class:
  `DIAGNOSIS_CLASS=PRESSURE_MAGNITUDE_OVER_SUPPORT`.
- `GAP_HTFF` raw frozen totals stayed high:
  `0.0470957506334 N` to `0.0506865935341 N`.
- Mask-only tightening reduced but did not fix overload:
  `MASK_H12D4 = 0.0411622338956 N` to `0.0439588760751 N`.
- `CAP_7P5KPA` returned the `GAP_HTFF` frozen totals to the preferred window:
  `0.0326466833365 N` to `0.0327105881254 N`.
- `LOAD_LIMITED` returned `F_total_frozen` to about `0.030000000000 N`.
- Fixed `h_TFF=3 um` worsened raw pressure support:
  `F_total_frozen = 0.0551962568507 N` to `0.0592931738057 N`.
- `MinTheta` stayed near 1 and TFF selection remained `sel_film_swept571`.

### Interpretation

- The failure is not primarily a gap-to-height mapping issue because fixed
  `h_TFF=3 um` increases raw pressure support.
- The failure is not fixed by mask-only support tightening.
- The pressure magnitude admitted from the local TFF field is too large to pass
  directly as normal support.
- The next controlled physical-coupling step should compare fixed pressure-cap
  feedback, especially `CAP_7P5KPA`, against load-limited pressure feedback.

### Next Step

- Create `576w3i_stage576_pressure_limited_feedback_microtest`.
- Limit the test to `5% -> 6.25%`.
- Compare `CAP_7P5KPA` and `LOAD_LIMITED` pressure-feedback/readout paths.
- Do not continue to `7.5%` or `10%`.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction.

### Files

- `build_stage576w3h_tff_pressure_support_diagnostic.java`
- `build_stage576w3h_tff_pressure_support_diagnostic_stdout.txt`
- `verify_stage576w3h_results.java`
- `verify_stage576w3h_results_stdout.txt`
- `576w3h_stage576_tff_pressure_support_diagnostic_results.mph`
- `576w3h_stage576_tff_pressure_support_summary.csv`
- `576w3h_stage576_tff_pressure_support_diagnostic.md`

## 2026-06-27-576w3g

### Changed

- Created `576w3g_stage576_tff_only_microtarget_diagnostic`.
- Started from `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`.
- Inherited `pressure=sol271`, `solid=sol273`, and frozen `drel=4.97234297573 um`.
- Kept `h_calc576w3g = h_calc573`.
- Disabled solid update and relaxed pressure feedback; only TFF was recomputed.
- Ran independent TFF-only targets `0.055`, `0.060`, and `0.0625`, each initialized from the same 5% state.
- Reported both pressure-support and wall-velocity masks:
  `Bpress576w3g` and `Bwall576w3g`.

### Observed

- Build completed with `W3G_BUILD_STATUS=PASS`.
- Verifier completed with `VERIFY_STATUS=PASS`.
- Diagnostic class:
  `DIAGNOSIS_CLASS=PRESSURE_SOURCE_OVER_SUPPORT`.
- TFF-only frozen totals:
  `0.0470957506334 N` at `5.5%`,
  `0.0494792630215 N` at `6.0%`,
  `0.0506865935341 N` at `6.25%`.
- `AvgH = 3.41160 to 3.41185 um`.
- `MinTheta = 0.999913687 to 0.999925927`.
- TFF selection remained `sel_film_swept571`.
- Edge/center pressure ratio stayed moderate, about `1.29-1.34`, so the result does not primarily indicate a mask-edge artifact.

### Interpretation

- The local TFF pressure source is already over-supporting normal load under frozen-solid and frozen-release conditions.
- The overload is not caused by solid inheritance, `drel` update, relaxed pressure feedback accumulation, selection drift, theta collapse, or missing asperity friction.
- The next change should target the local TFF pressure-support formulation, not controller gains or mixed-lubrication friction.

### Next Step

- Create a controlled `576w3h` diagnostic for TFF pressure support.
- Candidate checks should include pressure support/load mask smoothing, pressure cap or load-limited pressure readout, readout-only TFF pressure, and fixed `h=3 um` comparison against gap-derived `h_calc573`.
- Do not continue to 7.5% or 10%.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- Do not add asperity or boundary friction yet.

### Files

- `build_stage576w3g_tff_only_microtarget_diagnostic.java`
- `build_stage576w3g_tff_only_microtarget_diagnostic_stdout.txt`
- `verify_stage576w3g_results.java`
- `verify_stage576w3g_results_stdout.txt`
- `576w3g_stage576_tff_only_microtarget_diagnostic_results.mph`
- `576w3g_stage576_tff_only_microtarget_summary.csv`
- `576w3g_stage576_tff_only_microtarget_diagnostic.md`

## 2026-06-27-576w3f

### Changed

- Created `576w3f_stage576_recursive_micro0625_3um_decoupled_mask_candidate`.
- Kept `h_calc576w3f = h_calc573`, so `drel` is not added to TFF film height.
- Kept `drel576w3f` only as solid indentation release.
- Kept structural pressure feedback as `max(p_load576w3f,0)`.
- Changed TFF wall velocity back to the original drain gating path:
  `M_drain573*Bfilm573`, instead of `Bfilm576w3f`.
- Shortened the test to `5% -> 6.25%`; did not attempt 7.5% or 10%.

### Observed

- The solve ran 32 iterations and failed segment 3.
- Best observed iterate was the first iterate:
  `F_contact = 0.0192061368645 N`,
  `F_film = 0.0310118370285 N`,
  `F_total = 0.0502179738930 N`.
- Final independent read-back:
  `F_contact = 0.00653342006671 N`,
  `F_film = 0.0774412745719 N`,
  `F_film_swept = 0.200200084724 N`,
  `F_total = 0.0839746946386 N`,
  `AvgH = 3.94192970705e-06 m`,
  `MinTheta = 0.999976153096`,
  `LowThetaAreaRatio02 = 0`,
  `DrelSaturationRatio = 0.349308574393`,
  `VERIFY_TFF_SELECTION_EQUALS_SWEPT = true`,
  `VERIFY_STATUS = FAIL`.

### Interpretation

- The 3um physical film-height part still works.
- The load-closure part still fails badly, even before 7.5%.
- The failure is not due to non-finite values, selection drift, broad theta
  collapse, or drel saturation.
- Separating TFF wall-velocity gating from structural load-feedback masking is
  insufficient. The remaining issue appears to be the 3um TFF pressure source
  itself over-producing swept film load during early dynamic targets.

### Next Step

- Do not continue to 7.5% or 10%.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff`.
- The `576w3g` TFF-only pressure-source diagnostic has now been run and
  classified the overload as `PRESSURE_SOURCE_OVER_SUPPORT`.
- Next create a controlled `576w3h` pressure-support diagnostic rather than
  continuing coupled travel or tuning controller parameters.

### Files

- `build_stage576w3f_recursive_micro0625_3um_decoupled_mask_candidate.java`
- `build_stage576w3f_recursive_micro0625_3um_decoupled_mask_candidate_stdout.txt`
- `verify_stage576w3f_results.java`
- `verify_stage576w3f_results_stdout.txt`
- `576w3f_stage576_recursive_micro0625_3um_decoupled_mask_candidate_results.mph`
- `576w3f_stage576_micro0625_decoupled_mask_candidate_iterations.csv`
- `576w3f_stage576_micro0625_decoupled_mask_candidate_diagnostic.md`

## 2026-06-27-576w3e

### Changed

- Created `576w3e_stage576_recursive_split010_3um_gap_window_candidate`.
- Kept `drel576w3e` as a solid indentation-release variable.
- Removed `drel` from the TFF film-height expression:
  `h_calc576w3e = h_calc573`.
- Added the fixed candidate gap-window mask:
  `h_active_max576w3e = 20 um`,
  `dh_active576w3e = 5 um`,
  `Bfilm576w3e = g_pair_valid573*B_low573*B_high576w3e`.
- Used `p_load576w3e` as the recursive pressure-feedback source.
- Kept `alpha=0.15`, `beta=0.15`, `gamma=0.12`, and `Keff=5000 N/m`.

### Observed

- Segment 3, `5% -> 7.5%`, failed after 32 iterations.
- The run did not continue to segment 4 / 10%.
- Best segment-3 iterate:
  `F_total = 0.0513478165398 N`.
- Final independent read-back:
  `F_contact = 0.00568746688190 N`,
  `F_film = 0.0748136883950 N`,
  `F_total = 0.0805011552769 N`,
  `AvgH = 3.86495287430e-06 m`,
  `MinTheta = 0.736878212489`,
  `LowThetaAreaRatio02 = 0`,
  `DrelSaturationRatio = 0.361808574393`,
  `VERIFY_TFF_SELECTION_EQUALS_SWEPT = true`,
  `VERIFY_STATUS = FAIL`.

### Interpretation

- The physical-consistency part succeeded: TFF film height returned to the
  intended `3 um` scale.
- The load-closure part failed: the coupled 3 um TFF pressure field carried too
  much film load in segment 3.
- The failure is not due to non-finite values, selection drift, drel saturation,
  or broad theta collapse.
- The postprocessed `20 um / 5 um` upper-gap candidate is insufficient when
  coupled into the solved pressure-feedback loop.
- Read-only failure diagnosis shows:
  `inherited_5pct F_total = 0.0287348411563 N`, but
  `iter01_first_075 F_total = 0.0562919179888 N`.
  This argues against inherited 5% pressure initialization as the primary cause.
- The supported mechanism is immediate 7.5% 3um TFF pressure over-production,
  admitted by the wet-load mask while `drel` reduces solid contact. Coupled use
  of `Bfilm576w3e` in both wall velocity and load feedback is now the main
  controlled variant to isolate.

### Next Step

- Do not promote `576w3e` to a checked baseline.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff` immediately.
- The narrow `576w3f` micro-continuation candidate has now been run and also
  failed. Next use the 576w3f evidence to isolate the 3um TFF pressure source
  itself with a read-only or TFF-only `576w3g` diagnostic.

### Files

- `build_stage576w3e_recursive_split010_3um_gap_window_candidate.java`
- `build_stage576w3e_recursive_split010_3um_gap_window_candidate_stdout.txt`
- `verify_stage576w3e_results.java`
- `verify_stage576w3e_results_stdout.txt`
- `probe_stage576w3e_failure_diagnosis.java`
- `probe_stage576w3e_failure_diagnosis_stdout.txt`
- `576w3e_stage576_failure_diagnosis.csv`
- `576w3e_stage576_recursive_split010_3um_gap_window_candidate_results.mph`
- `576w3e_stage576_recursive_split010_3um_gap_window_candidate_diagnostic.md`
- `576w3e_stage576_failure_diagnosis.md`

## 2026-06-27-576w3d

### Changed

- Created `577i_stage577_fixed_asperity_closeout.md` to freeze 577i as a postprocessing mixed-lubrication proxy.
- Created Stage 576w3d load-closure extension from the checked 576w3c state.
- Continued with the 576w3c settings and did not retune `alpha`, `beta`, `gamma`, or `Keff`.
- Set old `drel576w3c` to zero inside the continuation model and carried the checked release magnitude through `drel576w3d` to avoid double-counting imposed displacement release.

### Observed

- Inherited segments:
  `0 -> 2.5%` and `2.5% -> 5%` remain PASS from 576w3c.
- Segment 3, `5% -> 7.5%`, ended `MARGINAL` with
  `F_total = 0.0354296305390 N` and `drel = 7.56356336653 um`.
- Segment 4, `7.5% -> 10%`, ended `MARGINAL`.
- Independent final read-back:
  `F_contact = 0.0107429622941 N`,
  `F_film = 0.0250608173034 N`,
  `F_total = 0.0358037795975 N`,
  `DrelSaturationRatio = 0.277447704960`,
  `MinTheta = 0.999999599159`,
  `LowThetaAreaRatio02 = 0`,
  `VERIFY_TFF_SELECTION_EQUALS_SWEPT = true`,
  `VERIFY_STATUS = MARGINAL`.
- Read-only load-offset probe:
  `F_film_load/F_film_swept = 0.239039697675`,
  `A_wet_load/A_swept = 0.0517822557137`,
  `A_wet_load/A_core = 0.572869486842`,
  `MaxLoadPressure = 11319.0757691 Pa`,
  `MeanLoadPressure = 286.176267673 Pa`.
- Read-only mask-review probe comparing 7.5% and 10%:
  `F_contact` changed by `-0.0045674421867 N`,
  `F_film_load_masked` changed by `+0.0049415912453 N`,
  `F_total` changed by `+0.0003741490585 N`,
  `A_wet_load/A_swept` increased from `0.0454293294855` to
  `0.0517822557137`, and mean wet-mask load pressure increased from
  `5057.23725186 Pa` to `5526.53150638 Pa`.

### Interpretation

- 576w3d did not fail by non-finite values, theta collapse, TFF selection drift, or release saturation.
- The limiting issue is load closure: final `F_total` is inside the hard `0.027-0.036 N` window but above the preferred `0.030-0.033 N` pass window.
- Because `F_film_swept = 0.104839562412 N` is much larger than the load-masked `F_film`, the next diagnostic should focus on pressure masking/load handoff rather than blind parameter tuning.
- The load-offset probe shows that the mask is restrictive but the absolute load pressure inside `M_core573*Bfilm573` is still high enough to keep the total load marginal.
- The mask-review probe shows that the 10% marginal offset is not a pressure-spike
  problem. Peak load pressure decreases from 7.5% to 10%, but the wet-load mask
  expands and the mean pressure inside that mask increases, so film load rises
  slightly more than contact load falls.
- Exported mask-review plots show that `M_core573*Bfilm573` and
  `max(p_load573,0)` remain local, but the 10% wet-load mask is more connected
  along the central load band than the 7.5% state.
- Mask-definition review shows the mask expansion is not a direct `tff.p` or
  `h_calc576w3d` effect. `Bfilm573` is
  `g_pair_valid573*B_low573*B_high573`, controlled by the mapped solid/contact
  gap `g_pair_safe573`. From 7.5% to 10%, `M_core573` shrinks slightly, but
  `Bfilm > 0.5` area over the core increases from `0.480489888885` to
  `0.577035660672`.
- Mask-candidate review scanned alternative upper-gap gates without re-solving.
  No candidate gave clean PASS for both 7.5% and 10%, but two useful families
  were identified: `h_cap=15 um, dh_cap=5 um` gives
  `F_total_alt = 0.0317624666 N` at 7.5% and `0.0275761014 N` at 10%;
  `h_cap=20 um` gives better 10% closure but leaves 7.5% slightly high.
- Mask-candidate spatial review shows both candidate families remain local.
  `h_cap=15 um` likely over-corrects the 10% load band, while
  `h_cap=20 um, dh_cap=5 um` preserves more central swept load and removes
  localized high-gap over-support patches.
- 576w3d is useful as the newest non-FAIL physical extension diagnostic, while 576w3c remains the latest clean early-stroke PASS baseline.

### Next Step

- Do not extend past 10% and do not tune controller parameters yet.
- If proceeding beyond diagnostics, create a controlled
  `576w3e_stage576_recursive_split010_gap_window_candidate` solve using fixed
  `h_active_max576w3e=20 um` and `dh_active576w3e=5 um`.
- Keep `alpha`, `beta`, `gamma`, `Keff`, TFF selection, and `tff.p` pressure
  source unchanged; do not extend beyond 10% in the same branch.
- Do not tune `alpha`, `beta`, `gamma`, or `Keff` until the pressure handoff, load masking, and drel-contact-film tradeoff are inspected.

### Files

- `577i_stage577_fixed_asperity_closeout.md`
- `build_stage576w3d_recursive_split010_film_height_release_extended.java`
- `verify_stage576w3d_checked.java`
- `probe_stage576w3d_load_offset.java`
- `probe_stage576w3d_mask_review.java`
- `probe_stage576w3d_mask_definition_review.java`
- `probe_stage576w3d_mask_candidate_review.java`
- `export_stage576w3d_mask_candidate_spatial_figures.java`
- `export_stage576w3d_mask_review_figures.java`
- `576w3d_stage576_load_offset_probe_diagnostic.md`
- `576w3d_stage576_load_offset_probe.csv`
- `576w3d_stage576_mask_review_diagnostic.md`
- `576w3d_stage576_mask_review.csv`
- `576w3d_stage576_mask_definition_review_diagnostic.md`
- `576w3d_stage576_mask_definition_review.csv`
- `576w3d_stage576_mask_candidate_review_diagnostic.md`
- `576w3d_stage576_mask_candidate_review.csv`
- `576w3d_stage576_mask_candidate_spatial_review_diagnostic.md`
- `576w3d_candidate_segment4_100_mask_h20.png`
- `576w3d_candidate_segment4_100_removed_h20.png`
- `576w3d_mask_segment3_075_wet_mask.png`
- `576w3d_mask_segment4_100_wet_mask.png`
- `576w3d_stage576_recursive_split010_film_height_release_extended_diagnostic.md`
- `576w3d_stage576_recursive_split010_film_height_release_extended_checked.mph`

## 2026-06-26

### Changed

- Created Stage 577a conserved `3 um` local TFF check from `576w3c`.
- Created Stage 577b low-film / rupture activation diagnostic from 577a.
- Created Stage 577c mixed-lubrication / boundary-friction postprocess from 577b.
- Kept all changes in new files; `576w3c` was not overwritten.

### Observed

- `577a` passed with `h_avg = 3.000e-6 m`, finite pressure/theta, and signed shear reversal.
- `577b` passed with `h_avg = 2.936e-6 to 3.098e-6 m` for the strongest `dh_deplete = 2.8 um` branch.
- `577b` depletion scan showed increasing low-film activation from `dh_deplete = 0.5` to `2.8 um`.
- `577c` passed with boundary friction sign reversal and monotonic `mu_total`.
- `577c` maximum `mu_total` values:
  `0.010095`, `0.023418`, `0.045622`, and `0.090030`
  for `mu_boundary = 0.02`, `0.05`, `0.10`, and `0.20`.

### Interpretation

- Pure-fluid `mu_TFF_alt` remains small, with maximum `0.0020777`, as expected.
- The Stage 577 mixed-lubrication postprocess raises the effective coefficient into the desired diagnostic range without retuning TFF pressure.
- The current limitation is that `577b` and `577c` are postprocessing checks; depleted film height and boundary friction are not yet coupled back into the governing solve.

### Next Step

- Preserve 577a/577b/577c as checked diagnostics.
- Next decide whether to calibrate `mu_boundary577c` against a target experimental COF, or to implement a weakly coupled version where low-film activation affects the TFF solve.

### Files

- `build_stage577a_conserved_3um_local_tff_check.java`
- `build_stage577b_conserved_depletion_rupture_check.java`
- `build_stage577c_mixed_lubrication_boundary_friction.java`
- `577a_stage577_conserved_3um_local_tff_check_diagnostic.md`
- `577b_stage577_conserved_depletion_rupture_check_diagnostic.md`
- `577c_stage577_mixed_lubrication_boundary_friction_diagnostic.md`
- `577a_stage577_conserved_3um_local_tff_check_results.mph`
- `577b_stage577_conserved_depletion_rupture_check_results.mph`
- `577c_stage577_mixed_lubrication_boundary_friction_results.mph`

## 2026-06-27

### Changed

- Created Stage 577d postprocess calibration and sensitivity scan.
- Created Stage 577e weakly coupled depleted TFF attempt.
- Created Stage 577f load-sharing boundary-pressure postprocess.
- Created Stage 577g contact-pressure / asperity-pressure boundary model.

### Observed

- `577d` found target-COF candidates for `dh_deplete >= 2.0 um`, but failed the monotonic-with-`dh_deplete` criterion.
- `577e` was manually terminated twice because the depleted-height TFF solve became impractically slow in the early transient.
- `577f` passed with `Fn_boundary = max(Fn_ref - Fn_fluid_pos, 0)` and `mu_total` about `0.089-0.090` at `mu_boundary = 0.20`.
- `577g` found that `solid.Tn` is available but not useful for dynamic friction in the current TFF dataset; the asperity proxy passed with `mu_total max = 0.116789`.

### Interpretation

- `577d` confirms that `p_boundary = Fn_ref/A_close` can flatten or slightly reverse the `dh_deplete` trend because increasing low-film area lowers pressure.
- `577e` shows that directly inserting spatially depleted film height into `ffp1.hw1` is not yet practical without continuation, smoothing, or shorter micro-window testing.
- `577f` is the preferred load-sharing postprocess branch.
- `577g` is the preferred roughness/asperity proxy branch; do not use direct `solid.Tn` from the current dataset as final boundary pressure.

### Next Step

- Do not continue with direct full-cycle 577e as written.
- Next either tune the 577g asperity proxy parameters, or create a short-window/continuation 577e2 weak-coupling attempt.

### Files

- `build_stage577d_postprocess_calibration_sensitivity.java`
- `build_stage577e_weakly_coupled_depleted_tff.java`
- `build_stage577f_load_sharing_boundary_pressure.java`
- `build_stage577g_contact_or_asperity_boundary_model.java`
- `577d_stage577_postprocess_calibration_sensitivity_diagnostic.md`
- `577e_stage577_weakly_coupled_depleted_tff_diagnostic.md`
- `577f_stage577_load_sharing_boundary_pressure_diagnostic.md`
- `577g_stage577_contact_or_asperity_boundary_model_diagnostic.md`
- `577d_stage577_postprocess_calibration_sensitivity_results.mph`
- `577f_stage577_load_sharing_boundary_pressure_results.mph`
- `577g_stage577_contact_or_asperity_boundary_model_results.mph`

## 2026-06-27-577h

### Changed

- Created Stage 577h first small asperity-calibration scan.
- Used `K_asp_eff` in kPa and scanned 24 postprocessing parameter combinations.
- Did not re-solve TFF and did not use direct `solid.Tn`.

### Observed

- All 24 combinations were finite.
- `mu_total` increased monotonically with `mu_boundary`.
- `mu_total` increased monotonically with `K_asp_eff`.
- No combination passed all filters.
- `h_crit = 0.5 um` did not activate asperity pressure in the tested window.
- The closest useful case was `dh=2.5 um`, `h_crit=1.0 um`, `K_asp_eff=30 kPa`, `mu_boundary=0.05`, with `mu_total_max=0.08847`, but `Fn_asp/Fn_ref_max=4.02`.

### Interpretation

- The first 577h window brackets the problem: low `h_crit` is underactive, while `h_crit=1.0 um` plus current `K_asp_eff` overproduces asperity normal load.
- The next scan should lower `K_asp_eff` and add intermediate `h_crit` values.

### Next Step

- Create `577h2` adjusted small scan:
  `dh_deplete = 2.3, 2.5, 2.8 um`,
  `h_crit = 0.7, 0.8, 0.9, 1.0 um`,
  `K_asp_eff = 5, 10, 20, 30 kPa`,
  `mu_boundary = 0.05, 0.10, 0.15`.

### Files

- `build_stage577h_asperity_calibration.java`
- `577h_stage577_asperity_calibration_diagnostic.md`
- `577h_stage577_asperity_calibration_summary.csv`
- `577h_stage577_asperity_calibration_best_params.md`
- `577h_stage577_asperity_calibration_results.mph`

## 2026-06-27-577h2

### Changed

- Created Stage 577h2 refined asperity-calibration scan.
- Reduced `K_asp_eff` and increased `mu_boundary` to preserve friction scale while reducing asperity normal-load proxy.
- Added `PARAM_SCORE` and `PASS_LEVEL` to the calibration CSV.

### Observed

- `SCAN_COUNT = 180`.
- `STRONG_PASS_COUNT = 53`.
- `CANDIDATE_PASS_COUNT = 15`.
- All values were finite.
- `mu_total` remained monotonic with `mu_boundary`.
- `mu_total` remained monotonic with `K_asp_eff`.
- Best candidate:
  `dh_deplete = 2.5 um`,
  `h_crit = 1.0 um`,
  `K_asp_eff = 7.5 kPa`,
  `mu_boundary = 0.20`,
  `mu_total_max = 0.0884728`,
  `A_close/A_film_mean = 0.0739728`,
  `Fn_asp/Fn_ref_max = 1.00473`.

### Interpretation

- Stage 577h2 fixed the main Stage 577h failure mode.
- The preferred candidate keeps the target friction scale while reducing the asperity normal-load proxy from about `4.02*Fn_ref` to about `1.00*Fn_ref`.
- This is still a postprocessing calibration, not a fully coupled mixed-lubrication solve.

### Next Step

- Create Stage 577i using the selected fixed parameter set.
- Output paper-facing time curves and spatial plots:
  `mu_TFF_alt(t)`, `mu_total(t)`, `Ft_fluid(t)`, `Ft_asp(t)`, `A_close(t)`, `Fn_asp(t)`, `theta_min(t)`, `pfilm_max(t)`, `h_eff_min(t)`, plus spatial fields for `h_eff`, `theta`, `pfilm`, `w_close`, and `p_asp`.

### Files

- `build_stage577h2_asperity_calibration_refined.java`
- `577h2_stage577_asperity_calibration_refined_diagnostic.md`
- `577h2_stage577_asperity_calibration_refined_summary.csv`
- `577h2_stage577_asperity_calibration_refined_best_params.md`
- `577h2_stage577_asperity_calibration_refined_results.mph`

## 2026-06-27-577i

### Changed

- Created Stage 577i fixed-parameter paper-output postprocess.
- Used the selected 577h2 candidate without further parameter tuning.
- Added checked time-series CSV and result plot groups for paper-facing interpretation.

### Observed

- `TIME_COUNT = 41`.
- `MU_TOTAL_MAX = 0.0884728`.
- `MU_TOTAL_MEAN = 0.0117808`.
- `A_CLOSE_OVER_AFILM_MEAN = 0.0739728`.
- `FN_ASP_OVER_FNREF_MAX = 1.00473`.
- `CHECK_FINITE = true`.
- `CHECK_SIGN_REVERSAL_FT_ASP = true`.
- `CHECK_SIGN_REVERSAL_FT_TOTAL = true`.
- `CHECKED_STATUS = PASS`.

### Interpretation

- Stage 577i reproduces the selected Stage 577h2 calibration metrics exactly enough for paper-output use.
- It remains a postprocessing calibration on top of the local TFF solution, not a fully coupled solid-contact-TFF mixed-lubrication solve.
- The next step should be figure review/export, not more blind parameter scanning.

### Next Step

- Inspect and export the checked Stage 577i time curves and spatial plot groups.
- If the spatial plots reveal nonlocal asperity activation or a load inconsistency, move to Stage 577h3 load-constrained asperity proxy.

### Files

- `build_stage577i_fixed_asperity_paper_outputs.java`
- `577i_stage577_fixed_asperity_paper_outputs_diagnostic.md`
- `577i_stage577_fixed_asperity_time_series.csv`
- `577i_stage577_fixed_asperity_paper_outputs_results.mph`

## 2026-06-25-22.42

### Changed

- Created Stage 576w3c film-height release extended test.
- Kept the 576u split route:
  `segments = 0 -> 2.5% -> 5%`.
- Kept `alpha = 0.15` and `beta = 0.15`.
- Added explicit imposed-indentation release and explicit TFF film-height release:
  `h_calc576w3c = h_calc573 + drel576w3c`.
- Increased inner iterations to 32.
- Used `gamma = 0.12`, `Keff = 5000 N/m`,
  `drel_step_max = 0.5 um`, and `drel_max = 40 um`.

### Observed

- Segment 1, `0 -> 2.5%`, passed:
  `F_contact = 0.0221469 N`,
  `F_film = 0.0106927 N`,
  `F_total = 0.0328395 N`,
  `drel = 4.4197 um`.
- Segment 2, `2.5% -> 5%`, passed:
  `F_contact = 0.0196748 N`,
  `F_film = 0.0132800 N`,
  `F_total = 0.0329548 N`,
  `drel = 4.9723 um`.
- `CHECKED_STATUS=PASS`.

### Interpretation

- Stage 576w3c is the first Stage 576 branch that closed both early split segments to the `0.03 N` target range using explicit load release.
- The earlier 576w and 576w2 branches showed that releasing only the solid indentation was insufficient because the film load did not decrease.
- The successful change was adding the release displacement directly into the TFF film height:
  `h_calc576w3c = h_calc573 + drel576w3c`.

### Next Step

- Do not continue tuning `alpha`, `beta`, `segment size`, or release gain immediately.
- Treat 576w3c as the current checked milestone.
- Create and run `verify_stage576w3c_checked.java` to independently verify the checked `.mph` result.

### Files

- `576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph`
- `576w3c_stage576_recursive_split005_film_height_release_extended_results.mph`
- `576w3c_stage576_recursive_split005_film_height_release_extended_diagnostic.md`
- `build_stage576w3c_recursive_split005_film_height_release_extended.java`

## 2026-06-25

### Changed

- Created Stage 576v fine-segment recursive test.
- Kept `alpha_pfb576v = 0.15`.
- Kept `beta_relax576v = 0.15`.
- Changed segment endpoints from `0 -> 2.5% -> 5%` to
  `0 -> 1.25% -> 2.5% -> 3.75% -> 5%`.

### Observed

- Segment 1, `0 -> 1.25%`, converged with `F_contact = 0.0280043 N`,
  `F_film = 0.0100258 N`, and `F_total = 0.0380302 N`.
- Segment 2, `1.25% -> 2.5%`, ended with `F_contact = 0.0262712 N`,
  `F_film = 0.0358211 N`, and `F_total = 0.0620923 N`.
- Segment 2 failed acceptance before the run advanced to `3.75%` or `5%`.
- Pressure/cavitation remained finite: `MaxP = 56.99 kPa`,
  `MinTheta = 0.9999710`, and `MinGap = -57.22 um` at the failed segment.

### Interpretation

- Finer `1.25%` segmentation improves the very first segment but worsens the
  next pressure-history handoff.
- The remaining issue is not gross numerical instability, mask direction, or
  exterior pressure anchoring.
- `alpha`, `beta`, and segment size alone have not closed
  `F_contact + F_film` to the `0.03 N` target.

### Next Step

- Return to the `576u` two-segment baseline.
- Test explicit load-control/release on the imposed indentation instead of
  continuing to tune `alpha`, `beta`, or segment size alone.

### Files

- `build_stage576v_recursive_fine_segment_005.java`
- `576v_stage576_recursive_fine005_diagnostic.md`
- `576v_stage576_recursive_fine005_results.mph`

## Latest full-path validated checked milestone

- model:
  - `576n12_stage576_full_dynamic_recursive_checked.mph`
- note:
  - `576n12_stage576_full_dynamic_recursive_checked.md`
- verifier:
  - `verify_stage576n12_checked.java`

Current interpretation:

- this is the older clearly checked and verified full-path Stage 576 result
- it reached fraction `1.0000`
- it passed the acceptance checks recorded in the note
- it remains the full-path reference, while `576w3c` is the newest verified
  early-stroke split milestone

## Working rule

When discussing "the latest model", always clarify whether the meaning is:

1. latest experimental attempt by timestamp, or
2. latest explicitly verified checked result

## Update Log Template

Use the following template for each new modeling session. Add the newest entry
at the top of the log.

```md
## YYYY-MM-DD

### Changed

- what geometry, physics, solver, variable, boundary, or script was changed
- what file names were updated

### Observed

- what happened after the change
- include concrete quantities when available
- examples: `F_contact`, `F_film`, `F_total`, `theta`, `max pressure`, `min gap`

### Interpretation

- what the change appears to mean physically or numerically
- whether the result is better, worse, inconclusive, or only partially useful

### Next Step

- the single most defensible next action
- if needed, list a fallback action after that

### Files

- `note.md`
- `build_or_probe.java`
- `result_or_checkpoint.mph`
```

## Recommended Logging Rules

- Record only one coherent modeling step per dated block.
- Prefer measured quantities over narrative impressions.
- If a result is not validated, say so explicitly.
- If a branch becomes a dead end, mark that clearly so later sessions do not
  mistake it for the preferred path.
- When possible, name both the note file and the matching Java script.
