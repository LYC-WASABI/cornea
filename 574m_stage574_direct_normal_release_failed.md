# Stage 574m direct normal release failed

## Source

- Base model: `574j_stage574_fixed_structure_true_gap_jfo_checked.mph`
- Setup model: `574m_stage574_direct_normal_release_setup.mph`
- Results model: `574m_stage574_direct_normal_release_results.mph`
- Build script: `build_stage574m_direct_normal_release.java`

## Scope

This stage added a new prescribed displacement directly on the contact source boundaries:

```text
disp_lid_normal574m selection = [42, 44, 47, 48]
delta_lid_normal574m = 0, 5, 10, 20, 30, 50, 75, 100, 150, 200, 300, 400, 600 um
U0 = (0,
      delta_lid_normal574m*Y/sqrt(Y^2+Z^2),
      delta_lid_normal574m*Z/sqrt(Y^2+Z^2))
```

The intent was to directly release the lid/contact source relative to the corneal destination patch.

## Results

The contact force did not respond:

```text
release [um]    Fn_contact570 [N]    min gap            max solid.Tn [Pa]
0               0.0427868295         -1.4568647e-4     895356.890
5               0.0427865904         -1.4568652e-4     895357.157
10              0.0427865903         -1.4568652e-4     895357.157
50              0.0427865903         -1.4568652e-4     895357.157
100             0.0427865903         -1.4568652e-4     895357.157
600             0.0427865903         -1.4568652e-4     895357.157
```

## Acceptance

```text
Fn_contact570 in 0.02-0.035 N: FAIL
contact solution finite: PASS
checked status: FAIL
```

No `574m checked` model was saved.

## Interpretation

The added displacement on contact source boundaries `[42,44,47,48]` does not change the effective contact state. This suggests one of the following:

```text
1. The source boundary displacement is not the active control of the pair gap in this assembly/contact setup.
2. The contact pair uses an assembled/mapped frame that is not updated by this boundary displacement in the expected way.
3. The inherited contact pressure is dominated by the existing solved displacement field/geometric state, not by newly added boundary displacement on the contact source.
```

Therefore, adding more displacement features is not useful until the actual source/destination motion used by the pair gap is identified.

## Recommended Next Step

Run a source-motion diagnostic:

```text
1. Evaluate source-side displacement components and mapped destination gap before/after delta_lid_normal574m.
2. Check whether geomgap_dst_cp_lid_cornea changes when source boundary displacement changes.
3. Probe contact pair frame settings and source/destination current coordinates.
4. If the pair gap is not affected by source displacement, recalibrate by rebuilding the source geometry/contact pair position rather than applying a new displacement feature.
```

The likely next robust branch is:

```text
Stage 574n: contact-pair geometry offset / source position rebuild
```

rather than another displacement-only release scan.
