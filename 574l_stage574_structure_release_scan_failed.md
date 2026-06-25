# Stage 574l structure-only release scan failed

## Source

- Base model: `574j_stage574_fixed_structure_true_gap_jfo_checked.mph`
- Setup model: `574l_stage574_structure_release_scan_setup.mph`
- Results model: `574l_stage574_structure_release_scan_results.mph`
- Build script: `build_stage574l_structure_release_scan.java`

## Scope

This scan disabled TFF and membrane feedback, and varied only the prescribed release displacement:

```text
solid = on
tff = off
ge_force_total111 = off
friction child inactive
q_scale574 = 0, -0.015, -0.025, -0.035, -0.05, -0.075, -0.10, -0.15, -0.20, -0.30, -0.40
```

The goal was to find a structural state with:

```text
Fn_contact570 ~= 0.02-0.03 N
```

## Results

```text
q_scale574    displacement       Fn_contact570 [N]    min gap            max solid.Tn [Pa]
0             0                  0.0427868295         -1.4568647e-4     895356.890
-0.015        -2.66345e-5        0.0427559604         -1.4564256e-4     895086.984
-0.025        -4.43909e-5        0.0427355379         -1.4561326e-4     894906.910
-0.035        -6.21472e-5        0.0427151150         -1.4558396e-4     894726.869
-0.05         -8.87817e-5        0.0426844793         -1.4554003e-4     894456.872
-0.075        -1.33173e-4        0.0426338738         -1.4546684e-4     894007.046
-0.10         -1.77563e-4        0.0425834274         -1.4539376e-4     893557.916
-0.15         -2.66345e-4        0.0424829437         -1.4524767e-4     892660.069
-0.20         -3.55127e-4        0.0423824328         -1.4510171e-4     891763.051
-0.30         -5.32690e-4        0.0421813842         -1.4481021e-4     889971.570
-0.40         -7.10254e-4        0.0419802266         -1.4451925e-4     888183.408
```

Best point in this scan:

```text
q_scale574 = -0.40
Fn_contact570 = 0.0419802266 N
```

## Acceptance

```text
Fn_contact570 in 0.02-0.035 N: FAIL
contact solution finite: PASS
checked status: FAIL
```

No `574l checked` model was saved.

## Interpretation

The release displacement controlled by `q_scale574` has a weak effect on the current contact load. Releasing from `q=0` to `q=-0.4` reduced `Fn_contact570` only from:

```text
0.0427868295 N -> 0.0419802266 N
```

This means the current ~0.042 N contact load is not mainly controlled by the additional radial displacement term. It is likely coming from the base geometry/contact state, contact-pair mapping, or a residual/preloaded configuration inherited from the previous branch.

## Recommended Next Step

Do not continue blindly to larger negative `q_scale574`. First diagnose the structural preload source:

```text
1. Evaluate contact load by boundary/source-destination subsets to see where the 0.042 N originates.
2. Check whether `disp_lid_time` is the only active displacement on the lid/contact body.
3. Compare contact load using original 574g q=0 solution versus 574j-derived q=0 solution.
4. Inspect `solid.Tn` and `geomgap_dst_cp_lid_cornea` spatial overlap at q=0 and q=-0.4.
5. If the pre-load is geometric, create a new release parameter that moves the lid base position, not just the additional radial term.
```

Only after the structural preload source is understood should TFF be rerun near a plausible `F_contact ~= 0.02-0.03 N` state.
