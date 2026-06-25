# Stage 574 fixed-cornea midpoint JFO gate report

## Status

Stage 573 setup passed. Stage 574 stopped at the midpoint JFO gate.
Stages 575-578 were not started because the film load and continuation
results violate the required acceptance conditions.

## Saved models

- `573_stage573_cornea_dynamic_regions_checked.mph`
- `574a_stage574_cornea_midpoint_baseline.mph`
- `574b_stage574_cornea_midpoint_velocity.mph`

## Stage 573 implementation

- TFF remains on `sel_film_swept571`.
- `M_core573` is the moving lid projection.
- `M_drain573` extends the projection by `0.25 mm` in both motion
  directions.
- The lid wall velocity is multiplied by `M_core573`; the open track is
  not treated as a moving lid wall.
- The contact-pair Destination gap is
  `geomgap_dst_cp_lid_cornea`.
- `h_break573=0.05 um` is a rupture threshold, not a residual film.
- `h_background573=3 um` is used outside the intact core film.
- The drainage-only region remains a `3 um` flow region but contributes
  no structural film load.
- Open and ruptured regions use `Qvent573`.
- `p_load573=M_core573*Bfilm573*(tff.p-p_amb573)`.
- A separate weak open-region pressure anchor was added because the JFO
  formulation removes pressure equations in fully cavitated open
  regions. It is numerical regularization only and is excluded from the
  structural load.

## Stage 573 checks

- Central Destination-gap coverage: `0.970895`.
- Central intact-film fraction: `0.282563`.
- Central rupture fraction: `0.717437`.
- Core projected area over the path: `7.91-8.81 mm^2`.
- Drainage projected area over the path: `11.35-11.85 mm^2`.

The approximately `2.9%` unmapped core strip is retained as background,
non-loading film and must be checked against contact-pair mesh refinement.

## Stage 574 midpoint results

### Constant 3 um, zero velocity

- Minimum gauge pressure: approximately `0 Pa`.
- Maximum gauge pressure: `1.6e-14 Pa`.
- Integrated pressure: approximately zero.

This baseline passed.

### Constant 3 um, analytic velocity continuation

The velocity was applied only in `M_core573`.

At `lambda_v574=0.4`:

- Maximum pressure: `233884 Pa`.
- Whole-track integrated pressure: `2.65275 N`.
- Core loading pressure integral: `0.228082 N`.
- Average JFO filling fraction: `0.999987`.

The core film load is already about `7.6` times the total target load
of `0.03 N`, although only 40 percent of the analytic speed is applied.
This is an abnormal load magnitude and fails the Stage 574 gate.

### Transition toward the true gap

At `lambda_v574=0.4`, the thickness was continued from `3 um` toward
`h_calc573`. The solve failed near `lambda_h574=0.2` with NaN/Inf in
the open-region pressure residual.

Increasing `kvent573` did not remove this failure. Setting all outer
zero-pressure borders to fully supplied `theta=1` also did not remove
it. A weak pressure anchor removed empty matrix rows but did not make
the high-load continuation physically acceptable.

## Required correction before continuing

The current fixed full-track JFO domain still creates excessive
hydrodynamic pumping. Do not start Stage 575-578 from this state.

The next correction must reduce the active Reynolds-equation domain,
not merely mask its structural load. Recommended options:

1. Use a fixed corneal track but solve JFO only on the moving
   `M_drain573` neighborhood through an active-domain formulation that
   removes inactive JFO degrees of freedom.
2. Use a moving local corneal subdomain/remeshed patch whose geometric
   boundary follows the lid and drainage buffer.
3. If retaining the full track, replace the current JFO interface in
   inactive regions with a formulation that has a well-posed ambient
   pressure equation and zero wall velocity, then verify that the
   regularization load tends to zero as its coefficient is reduced.

Any correction must first repeat the midpoint three-level gate and
show a core film load below the target-load scale before running all
six positions.
