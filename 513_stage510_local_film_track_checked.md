# Stage 510: Local Curved Film Track

## Parent model

- Source: `503_stage500_baseline_checked.mph`
- Source SHA-256: `F0E2AE86F38D52CEBE4E39F6B37E597EC1E53C7B9D7A81F51D090D3A949A985A`
- Dependency: completed and reloaded Stage 500 baseline audit.

## Rollback chain

1. `510_stage510_local_film_track_input.mph`
2. `511_stage510_local_film_track_setup.mph`
3. `512_stage510_local_film_track_results.mph`
4. `513_stage510_local_film_track_checked.mph`

## Geometry

- Construction: `ParametricSurface` imprint tool with `Form Assembly +
  Imprints`.
- `sel_film_track` is the cornea-side imprinted boundary set, so boundary
  physics assemble directly on the solid-supported surface.
- The parametric surface tool is retained as `sel_film_surface_tool`.
- Corneal radius: `Rcor = 7.8 mm`.
- Lid motion range: `-35 deg` to `+35 deg`.
- End buffer: `0.75 mm` arc length at each end.
- Film-track angular extent: approximately `-40.5092 deg` to `+40.5092 deg`.
- Lateral buffer beyond the lid: `0.5 mm` per side.
- Film-track half width: `s_lid/2 + 0.5 mm = 4.5 mm`.
- Film-track total transverse width: `9 mm`.
- Finalization remains `Form Assembly`.

## Named selections

- `sel_film_track`: local curved rectangular film surface.
- `sel_film_edges_all`: all four film-track edges.
- `sel_film_inlet`: negative-angle edge.
- `sel_film_outlet`: positive-angle edge.
- `sel_film_side_left`: `x = -4.5 mm` drainage edge.
- `sel_film_side_right`: `x = +4.5 mm` drainage edge.
- `sel_cornea_contact_target`: unchanged cornea anterior contact target.

## Scope boundary

Stage 510 does not move the Thin-Film Flow physics to the new track and does
not change film boundary conditions. Those changes belong to the next stage.
The existing contact pair `cp_lid_cornea`, structural domains, studies, and
stored Stage 200 solutions are retained.

## Acceptance checks

- A curved film-track imprint exists on the cornea anterior surface.
- The track may contain several surface patches because it crosses existing
  cornea partitions; together they form one continuous rectangular track.
- Inlet, outlet, left side, and right side each select a nonempty perimeter
  edge group.
- Solid Mechanics still selects two solid domains.
- Existing explicit contact pair `cp_lid_cornea` remains present.
- Geometry and mesh rebuild successfully.
