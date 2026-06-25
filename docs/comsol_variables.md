# COMSOL Variables

This file is a stable index, not a full variable dump.

## Baseline quantities

From the Stage 200 line, important reported quantities include:

- `Fn_contact119`
- `Wfilm199`
- `Ftotal199`
- `dr_indent119`
- `tff.p`
- `tff.theta`

See:

- `385_stage200_model_explanation.md`
- `probe_stage200_cornea_surface_deformation.java`

## Stage 576 checked-state quantities

The Stage 576 checked verification uses:

- `Fn_contact570`
- `p_load573`
- `tff.theta`
- `tff.p-p_amb573`
- `geomgap_dst_cp_lid_cornea`

See:

- `576n12_stage576_full_dynamic_recursive_checked.md`
- `verify_stage576n12_checked.java`

## Stage 576 recursive branch controls

The Stage 576 recursive split and relaxed-field branches use controls such as:

- `alpha_pfb576m`
- `beta_relax576m`
- `alpha_pfb576u`
- `beta_relax576u`
- `alpha_pfb576u2`
- `beta_relax576u2`

For exact script usage, read the corresponding `build_*` Java file instead of
guessing from the variable name alone.
