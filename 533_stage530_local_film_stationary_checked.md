# Stage 530: Local Film Stationary Validation

## Parent

- `523_stage520_local_tff_drainage_checked.mph`

## Rollback chain

1. `530_stage530_local_film_input.mph`
2. `531_stage530_local_film_stationary_setup.mph`
3. `532_stage530_local_film_stationary_results.mph`
4. `533_stage530_local_film_stationary_checked.mph`

## Setup

- Snapshot time: `t_replay = 0.28 s`.
- Wall speed: `v_wall530 = v_blink_avg = 180 mm/s`.
- Wall velocity is a three-component tangent vector around the x axis.
- Film thickness: `h_jfo197`.
- Equation: standard Reynolds equation, without cavitation.
- Four local perimeter groups remain at zero gauge pressure.

## Verified results

- Corrected local film area: `99.12272 mm^2`.
- Mean film thickness: `3.13957 um`.
- Net and positive film load: `0.549405 N`.
- Film shear force: `0.00159031 N`.
- Film-only coefficient relative to `0.03 N`: `0.0530104`.

The large Reynolds load is retained as a diagnostic result. It demonstrates
that the old 2 mm track truncated the 8 mm lid footprint. Stage 540 therefore
recalibrates the dynamic separation on the complete 9 mm track.

## Meaning

Stage 530 verifies that the imprinted local geometry, moving-wall velocity,
film-thickness field, mesh, and drainage boundaries form a nonsingular local
Reynolds problem. JFO cavitation and structural feedback are deferred to
Stage 540.
