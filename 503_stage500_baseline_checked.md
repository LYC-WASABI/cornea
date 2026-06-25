# Stage 500: Frozen Baseline Audit

## Parent

- `385_lid8mm_stage200_official_jfo_joint_load_results_Model.mph`
- SHA-256: `384B217A6A7B7BC1DC957D901025F0CBB231803C1BC1204A7261C71C2CF0B953`

## Rollback chain

1. `500_stage500_baseline_input.mph`
2. `501_stage500_baseline_freeze_setup.mph`
3. `502_stage500_baseline_audit_results.mph`
4. `503_stage500_baseline_checked.mph`

## Purpose

Stage 500 freezes and audits the Stage 200 model before geometry changes. It
does not change the governing equations, contact definitions, film domain,
material properties, mesh, studies, or stored solutions.

## Acceptance checks

- Solid Mechanics and Thin-Film Flow remain present.
- Explicit contact pair `cp_lid_cornea` remains present.
- Film study/solution `std_jfofilm199` / `sol48` remain present.
- Structural study/solution `std_jfobalance199` / `sol49` remain present.
- Solid Mechanics still contains two solid domains.
- Stage 200 Thin-Film Flow still contains four whole-cornea boundaries.
- Stored total normal load remains close to the `0.03 N` target.

## Dependency rule

All Stage 510 files must be built from
`503_stage500_baseline_checked.mph`, never directly from Stage 200.
