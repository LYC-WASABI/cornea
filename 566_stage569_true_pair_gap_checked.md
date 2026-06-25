# Stage 569 true deformed lid-cornea pair gap

Source model: `558v_stage567_structure_balance_results.mph`

Checked model: `566c_stage569_true_pair_gap_checked.mph`

## Implemented

- Reused contact pair `cp_lid_cornea`.
- Used its deformed destination gap:
  `geomgap_dst_cp_lid_cornea`.
- Defined the true-gap variables on `sel_film_track`:
  - `pair_gap_native569`
  - `pair_map_valid569`
  - `h_pair_raw569`
  - `h_pair_open569`
  - `h_pair_penetration569`
  - `h_pair_regular569`
  - `pair_contact_state569`
  - `pair_contact_pressure569`
  - `h_proxy_error569`
- Added result plots for true gap, valid pair coverage, and the difference
  from the old proxy gap.
- Recompiled the unchanged Stage 567 structural balance as `sol92` so the
  new variables are available for postprocessing.

## Checked values

- Total Stage 510 film-track area: `9.91227222658e-5 m^2`
- Area with a valid opposing lid surface: `8.56155013713e-6 m^2`
- Valid coverage fraction: `8.6373%`
- Active contact area: `1.59515294756e-6 m^2`
- Active contact fraction of mapped area: `18.6316%`
- Average raw mapped gap: `18.1314 um`
- Raw mapped gap range: `-3.68183 to 99.6510 um`
- Average regularized gap: `18.2248 um`
- Average absolute difference from `h_geom555`: `14.4649 um`
- Average active-contact gap: `-0.235890 um`
- Maximum contact pressure: `5205.62 Pa`

The negative gap is penalty-contact penetration and is retained separately in
`h_pair_penetration569`. The regularized gap has a `0.05 um` residual
thickness and a `0.02 um` smoothing width.

## Unchanged physics

- Thin-film thickness remains `h_film566`.
- Film pressure replay remains `p_feedback567`.
- No Stage 569 gap variable is connected to JFO.
- The checked load balance remains:
  - film load: `0.02893775465 N`
  - contact load: `0.00106220812 N`
  - total load: `0.02999996277 N`

## Required boundary before JFO coupling

The current 9 mm film track is much larger than the opposing lid projection.
The true pair gap is valid only where `pair_map_valid569 = 1`. Do not replace
the full-track JFO thickness with `h_pair_regular569`; first restrict or
rebuild the film domain to the actual mapped overlap.
