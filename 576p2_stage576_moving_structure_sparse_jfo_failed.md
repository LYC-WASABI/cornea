# Stage 576p2 moving-structure sparse JFO diagnostic

## Status

Stage 576p2 completed the prescribed moving-contact trajectory but is **not a
checked JFO result**. The moving hotspot behavior is verified; the pressure
magnitude and cavitation state are not accepted because they depend strongly
on a distributed numerical pressure anchor.

## Completed structural trajectory

- Restored scraper rotation plus the calibrated radial indentation.
- Corrected the moving-mask direction:

  ```text
  theta_lid_spatial572 = theta_lid_physical572 + lid_mask_aoffset572
  ```

- Completed the structure from 0% to 100% travel.
- The original 5% step failed at 65%; continuation from `sol158` used 1.25%
  steps to 75%, then 2.5% steps to 100%.
- Final structural state: `sol182`.
- Final contact force: `0.0306379 N`.

Results model:

```text
576p2r_stage576_moving_structure_sparse_jfo_results.mph
```

## Moving-hotspot result

With the temporary full-surface weak pressure anchor, the pressure centroid
moved with the scraper mask:

| Travel | Core Y | Pressure Y | Film load | Peak pressure |
|---:|---:|---:|---:|---:|
| 25% | -3.090 mm | -2.731 mm | 0.02964 N | 80.98 kPa |
| 50% | 0.029 mm | 0.192 mm | 0.13569 N | 302.44 kPa |
| 75% | 3.114 mm | 3.379 mm | 0.09171 N | 243.93 kPa |

This resolves the earlier observation that the hotspot remained fixed on the
right side of the local patch. It now follows the moving scraper region.

At 0% and 100%, prescribed speed is zero and the computed hydrodynamic film
load is zero or numerical noise, as expected.

## Pressure-anchor failure

The complete swept TFF domain is:

```text
[6, 7, 10, 15, 16, 18]
```

The old zero-pressure border nodes had been rebound to local-patch edges
instead of the Stage 571 swept edges. Restoring swept-edge zero pressure and
disabling the distributed weak anchor still produced empty `pfilm` equations
at 25% travel.

Anchor sensitivity at 50% travel:

| `kanchor576p` | Film load | Peak pressure |
|---:|---:|---:|
| 1e-4 | 0.13569 N | 302.44 kPa |
| 2e-4 | 0.09121 N | 200.75 kPa |
| 5e-4 | 0.04660 N | 99.37 kPa |
| 1e-3 | 0.02625 N | 53.96 kPa |

The pressure magnitude is therefore controlled by the numerical anchor. The
anchored pressure and film load cannot be used for load closure.

## Topology diagnosis

- Surfaces `6` and `15` fail when tested without the distributed anchor.
- Surfaces `7`, `10`, `16`, and `18` pass individually.
- Surfaces `6` and `15` cannot be removed: both carry significant core and
  drainage area at 25% and 50% travel.
- The TFF continuity feature `dcont1` is inactive with an empty pair list, an
  assumption inherited from the earlier single local surface.
- The full swept region crosses multiple partitioned corneal faces, so its
  pressure DOF continuity and external border topology must be rebuilt.
- Replacing true gap by constant `3 um` did not remove the empty rows; this is
  not an active-gap regularization failure.

## Gap and cavitation observations

- Raw core gap coverage is about 0.848 at 0%, 0.943 at 25%, 0.954 at 50%,
  0.967 at 75%, and 0.674 at 100%.
- Interior moving positions have useful pair coverage; endpoint coverage is
  incomplete but coincides with zero speed.
- `min(theta)` remains approximately 1 in the anchored runs. Cavitation has
  not yet been demonstrated.

## Required next stage

Build a continuous full-path TFF topology across the partitioned corneal
surfaces before further load closure:

1. Rebuild the swept-film surface/mesh connectivity across surfaces
   `[6, 7, 10, 15, 16, 18]`.
2. Recreate the complete exterior-edge selection from that connected surface.
3. Apply ambient pressure only on the physical exterior edges.
4. Solve 25%, 50%, and 75% with `wc_open_anchor573` disabled.
5. Accept only if no empty pressure rows occur and pressure is insensitive to
   removal of numerical anchoring.

Do not continue to load closure or bidirectional coupling from the anchored
pressure field.
