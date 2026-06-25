# Stage 574h fixed-structure constant-film zero-speed JFO checked

## Source

- Base model: `574g_stage574_local_contact_gap_checked.mph`
- Setup model: `574h_stage574_fixed_structure_constant_zero_jfo_setup.mph`
- Results model: `574h_stage574_fixed_structure_constant_zero_jfo_results.mph`
- Checked model: `574h_stage574_fixed_structure_constant_zero_jfo_checked.mph`
- Build script: `build_stage574h_constant_zero_jfo.java`

## Scope

This stage only checks the fixed-structure JFO pressure field with:

```text
solid = off
ge_force_total111 = off
tff = on
film thickness hw1 = 3[um]
wall velocity vw = (0, 0, 0)
ms_vent573 QudR = 0
membrane pressure feedback = off
friction child under dcnt1 = inactive
```

The structural state is inherited from the Stage 574g checked full-contact solution:

```text
init structure solution = sol109
local patch = [10, 16]
```

The TFF solution created in this stage is:

```text
solution = sol110
```

## Final Checks

```text
q_scale574 = 1.0
Fn_contact570 = 0.04482544453855511 N
local patch area = 8.000431220947516e-6 m^2
finite gap area = 7.980991472866965e-6 m^2
local gap coverage = 0.9975701624645367
signed film load int(p-p_amb573) = 0
absolute film load int(abs(p-p_amb573)) = 0
positive film load int(max(p-p_amb573,0)) = 0
min(tff.p-p_amb573) = 0 Pa
max(tff.p-p_amb573) = 0 Pa
min(tff.theta) = 1
max(tff.theta) = 1
mean(tff.theta) = 1
min(geomgap_dst_cp_lid_cornea) = -1.484692981639327e-4 m
```

## Acceptance

```text
q_scale574 = 1 inherited from Stage 574g: PASS
local patch gap coverage >= 0.95: PASS
zero-speed constant-thickness pressure near ambient: PASS
zero film load: PASS
full-film theta = 1: PASS
checked status: PASS
```

## Notes

- No analytic lid velocity was applied in this stage.
- No true-gap film thickness, rupture factor, cavitation transition, or vent leakage was activated.
- No film pressure feedback was applied to structure.
- The inherited negative gap is the Stage 574g penalty-contact penetration diagnostic, not a physical liquid-film thickness.
- This stage validates only the first fixed-structure JFO gate: a constant 3 um film with zero wall velocity produces no artificial pressure or load on the corrected local patch.
