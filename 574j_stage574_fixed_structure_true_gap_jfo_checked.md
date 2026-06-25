# Stage 574j fixed-structure true-gap JFO checked

## Source

- Base model: `574h_stage574_fixed_structure_constant_zero_jfo_checked.mph`
- Setup model: `574j_stage574_fixed_structure_true_gap_jfo_setup.mph`
- Results model: `574j_stage574_fixed_structure_true_gap_jfo_results.mph`
- Checked model: `574j_stage574_fixed_structure_true_gap_jfo_checked.mph`
- Build script: `build_stage574j_true_gap_jfo.java`

## Scope

This stage keeps structure fixed and does not apply membrane pressure feedback:

```text
solid = off
ge_force_total111 = off
tff = on
v_blink_avg = 0.15[m/s]
lambda_v574 = 1 final
lambda_h574 = 1 final
wall velocity = (0, -lambda_v574*omega_lid_rot572*Z, lambda_v574*omega_lid_rot572*Y)
film thickness = (1-lambda_h574)*3[um]+lambda_h574*h_calc573
vent source = lambda_h574*Qvent573
friction child under dcnt1 = inactive
```

Stage 574g corrected the local patch to `[10,16]`. The original moving mask `M_lid572` did not overlap this corrected patch at the 574i midpoint, so this stage locally overrides the active film-domain mask on `sel_local_cornea_patch574`:

```text
M_core573 = 1
M_drain573 = 1
M_open573 = 0
```

This makes `h_calc573`, `Bfilm573`, and `Qvent573` active on the corrected local membrane domain.

## Continuation

Velocity continuation at constant 3 um:

```text
lambda_v574 = 0, 1e-4, 1e-3, 1e-2, 0.05, 0.1, 0.2, 0.4, 0.7, 1.0
lambda_h574 = 0
```

True-gap continuation at full velocity:

```text
lambda_v574 = 1.0
lambda_h574 = 1e-4, 1e-3, 1e-2, 0.05, 0.1, 0.2, 0.4, 0.7, 1.0
```

## Key Results

Constant 3 um, full velocity:

```text
positive pressure integral = 2.51965967899 N
p_load573 integral = 0.543020371298 N
```

True gap / rupture / vent, full velocity:

```text
positive pressure integral = 0.250880464160 N
p_load573 integral = 0.00894245394326 N
min(tff.p-p_amb573) = 0 Pa
max(tff.p-p_amb573) = 3.80493527322 MPa
mean(Bfilm573) = 0.261592840768
mean(Afilm573) = 0.261592840768
mean(h_calc573) = 1.15905470361e-5 m
mean(tff.theta) = 0.992124650479
min(tff.theta) = 0.654308488917
max(tff.theta) = 1
gap coverage = 0.997570162465
```

## Acceptance

```text
lambda_v574 = 1 reached: PASS
lambda_h574 = 1 reached: PASS
pressure and theta finite: PASS
gap coverage >= 0.95: PASS
physical p_load573 below 0.3 N: PASS
true-gap/rupture/vent reduced physical load from 0.543 N to 0.00894 N: PASS
checked status: PASS
```

## Notes

- The diagnostic `positive pressure integral` is still `0.2509 N`, but this includes pressure in regions not counted as physical load after `Bfilm573` masking.
- The physically admissible membrane load for later feedback is `intop_film(p_load573) = 0.00894 N`.
- `h_calc573` has a large local maximum outlier in the mapped gap field. The mean film thickness and `p_load573` are finite, but the next stage should inspect the spatial location of the large `h_calc573` values before using this branch dynamically.
- This stage is still fixed-structure only. It does not prove total load balance with the external `0.03 N`; it only proves that true gap / rupture / vent can reduce the physical film load to a reasonable order without pressure feedback.
