# Stage 520: Local Thin-Film Domain and Drainage

## Parent

- `513_stage510_local_film_track_checked.mph`
- Required ancestry: Stage 500 checked baseline.

## Rollback chain

1. `520_stage520_local_tff_input.mph`
2. `521_stage520_local_tff_drainage_setup.mph`
3. `522_stage520_local_tff_drainage_results.mph`
4. `523_stage520_local_tff_drainage_checked.mph`

## Changes

- Thin-Film Flow root selection moved from the four whole-cornea anterior
  boundaries to `sel_film_track`.
- `Fluid Film Properties` and `Initial Values` moved to the same local track.
- `intop_film` now integrates only over the local film track.
- Four independent zero-gauge-pressure drainage borders were created:
  inlet, outlet, left side, and right side.
- COMSOL's mandatory default `bdr1` remains present with an empty selection and
  therefore applies no boundary condition.
- Initial film pressure is `0 Pa` gauge.
- JFO border fractional content is retained at `theta_feed520 = 0.6`.
- Generic pair continuity `dcont1` uses an explicit empty pair list because
  the local film track is one continuous surface.
- Identity pair `ap1` is retained for later solid-film field mapping.

## Not changed

- Solid Mechanics and lid-cornea contact definitions.
- Cornea and lid materials.
- Local film geometry and mesh.
- Film thickness law `h_jfo197`.
- Dynamic load control and sliding solution.

## Acceptance

- TFF, `ffp1`, `init1`, and `intop_film` all select only the cornea-side
  imprinted film-track patches.
- Each drainage node selects its complete nonempty perimeter edge group.
- All four drainage conditions are `ZeroPressure`.
- `cp_lid_cornea` and `ap1` remain present.
