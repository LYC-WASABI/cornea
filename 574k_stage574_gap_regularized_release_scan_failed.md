# Stage 574k gap-regularized release scan failed

## Source

- Base model: `574j_stage574_fixed_structure_true_gap_jfo_checked.mph`
- Setup model: `574k_stage574_gap_regularized_release_scan_setup.mph`
- Results model: `574k_stage574_gap_regularized_release_scan_results.mph`
- Build script: `build_stage574k_gap_regularized_release_scan.java`

## Scope

This scan tested:

```text
v_blink_avg = 0.15[m/s]
solid release scan q_scale574 = -0.02, -0.015, -0.01, -0.0075, -0.005, -0.0025, 0
TFF true-gap solve with h_active_max573 = 100, 50, 30 um
membrane pressure feedback = off
```

The regularized gap definitions were:

```text
B_low573 = g_pair_valid573*0.5*(1+tanh((g_pair_safe573-h_break573)/dh_break573))
B_high573 = 0.5*(1-tanh((g_pair_safe573-h_active_max573)/dh_active573))
Bfilm573 = B_low573*B_high573
g_pair_physical573 = min(g_pair_safe573,h_active_max573)
h_wet573 = smoothmax(g_pair_physical573,h_num573)
h_calc573 = Afilm573*h_wet573+(1-Afilm573)*h_background573
```

## Results

`h_active_max573 = 100 um` mostly failed to converge.

`h_active_max573 = 50 um` only converged for part of the release range:

```text
q_scale574    F_contact [N]      F_film [N]       F_total [N]
-0.0075       0.0427712751       0.0462426661     0.0890139412
-0.005        0.0427763803       0.0467785238     0.0895549041
-0.0025       0.0427814853       0.0472723671     0.0900538524
```

`h_active_max573 = 30 um` converged across the tested release range but produced excessive film load:

```text
q_scale574    F_contact [N]      F_film [N]       F_total [N]
-0.02         0.0427460243       0.2000751658     0.2428211900
-0.015        0.0427559597       0.2020306306     0.2447865903
-0.01         0.0427661700       0.2036994150     0.2464655851
-0.0075       0.0427712751       0.2044446180     0.2472158932
-0.005        0.0427763803       0.2051383128     0.2479146931
-0.0025       0.0427814853       0.2057851448     0.2485666301
0             0.0427865903       0.2063891239     0.2491757142
```

The closest row to `0.03 N` under the requested `h_active_max573 = 50 um` branch was:

```text
q_scale574 = -0.0075
additional displacement = -1.33172617458e-5 model length
F_contact = 0.0427712751 N
F_film = 0.0462426661 N
F_total = 0.0890139412 N
```

## Acceptance

```text
h_calc573 mm/cm-scale outliers removed: PARTIAL
F_total in 0.025-0.04 N: FAIL
TFF convergence across h_active_max scan: FAIL
checked status: FAIL
```

No `574k checked` model was saved.

## Interpretation

- The upper-gap regularization changed the TFF behavior strongly. The tested `h_active_max573` values are not yet robust.
- `h_active_max573 = 30 um` is too aggressive in this setup and creates excessive film load.
- `h_active_max573 = 50 um` is closer but still gives `F_total ~0.089 N`, well above the target `0.03 N`.
- The tested negative release range is not enough to reduce `F_contact` below about `0.0427 N`.
- `Fn_contact570` was verified to be the current-dataset integral:

```text
Fn_contact570 = intop_contact(if(isdefined(solid.Tn),solid.Tn,0))
```

## Recommended Next Step

Do not proceed to feedback yet. First run a wider structure-only release scan to find where `Fn_contact570` approaches `0.02-0.03 N`:

```text
q_scale574 = -0.20, -0.15, -0.10, -0.075, -0.05, -0.035, -0.025, -0.015, 0
```

Then rerun TFF only near the structurally plausible q values, using a gentler upper-gap gate:

```text
h_active_max573 = 50, 75, 100 um
dh_active573 = 10, 20 um
```

The current 574k results should be treated as a failed diagnostic, not a physical initialization state.
