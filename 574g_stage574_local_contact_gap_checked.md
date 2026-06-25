# Stage 574g local contact-gap checked

## Source

- Base model: `574f_stage574_local_cornea_patch_structure_setup.mph`
- Setup model: `574g_stage574_local_contact_gap_setup.mph`
- Part 1 results: `574g_stage574_local_contact_gap_results_part1.mph`
- Results model: `574g_stage574_local_contact_gap_results.mph`
- Checked model: `574g_stage574_local_contact_gap_checked.mph`
- Build script: `build_stage574g_local_contact_reinit.java`

## What changed

- Rebound `sel_local_cornea_patch574` from the previous noncontact patch `[11, 12, 19, 20]` to the start-position contact patch `[10, 16]`.
- Rebuilt local patch edge selections and rebound TFF/intop selections to the corrected local patch for later Stage 574h work.
- Kept `cp_lid_cornea` as the explicit contact pair and kept `solid/dcnt1` active.
- Disabled the friction child `fric_partitioned_stabilizer`.
- Kept `tff` and `ge_force_total111` off during this stage.
- Used prescribed indentation only:
  `q_scale574*q_fixed574*1[mm]`.

## Continuation

Part 1:

```text
q_scale574 = 0, 0.005, 0.01, 0.02, 0.03, 0.05, 0.075, 0.1
```

Part 2:

```text
q_scale574 = 0.15, 0.2, 0.3, 0.5, 0.75, 1.0
```

The continuation reached `q_scale574 = 1.0`.

## Final checks at q_scale574 = 1.0

```text
Fn_contact570 = 0.04482544453855547 N
q_scale574*q_fixed574*1[mm] = 0.0017756348994417612
local patch area = 8.000431220947516e-6 m^2
finite gap area = 7.980991472866965e-6 m^2
local gap coverage = 0.9975701624645367
min(geomgap_dst_cp_lid_cornea) = -1.484692981639327e-4
max(solid.Tn) on local patch = 912459.5111039054 Pa
```

## Acceptance

```text
q_scale574 = 1 reached: PASS
contact force is 0.03 N order: PASS
local patch gap coverage >= 0.95: PASS
checked status: PASS
```

## Notes

- No JFO solve was run in Stage 574g.
- No film pressure feedback was applied.
- The final contact force is higher than the nominal `0.03 N`; this stage only restores stable local contact/gap on the corrected local patch. Total-load closure remains a later stage.
- The negative gap is penalty-contact numerical penetration, not a liquid-film thickness.
