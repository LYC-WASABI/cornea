# Stage 550 Five-Position Quasi-Static Check

## Scope

- Parent model: `543_stage540_jfo_joint_static_checked.mph`
- Positions: `+35`, `+17.5`, `0`, `-17.5`, `-35 deg`
- Target joint normal load: `0.03 N`
- Film model: local-track JFO thin-film solution
- Structural model: cornea/lid contact with solved indentation
- Acceptance limit: absolute joint-load error below `2%`

## Rollback Files

1. `550_stage550_five_position_input.mph`
2. `551_stage550_five_position_setup.mph`
3. `552_stage550_five_position_results.mph`
4. `553_stage550_five_position_checked_9mm_track.mph`

## Reloaded Validation

| Position (deg) | Film load (N) | Contact load (N) | Total load (N) | Relative error |
|---:|---:|---:|---:|---:|
| +35.0 | 0 | 0.03001205 | 0.03001205 | +0.0402% |
| +17.5 | 0.01434089 | 0.01566770 | 0.03000860 | +0.0287% |
| 0 | 0.02914331 | 0.00088238 | 0.03002569 | +0.0856% |
| -17.5 | 0.02948372 | 0.00054407 | 0.03002779 | +0.0926% |
| -35.0 | 0 | 0.03000192 | 0.03000192 | +0.0064% |

Maximum absolute relative error: `0.0926%`.

## Endpoint Correction

The corrected track has a `4.5 mm` transverse half width and `9 mm` total
width. It covers the complete `8 mm` lid footprint plus `0.5 mm` lateral
drainage margin on each side. Both endpoints use zero wall speed.

The midpoint uses `h_sep_uniform540 = 21 um`. The `-17.5 deg` partitioned
position uses `25 um`, because the current weak coupling does not yet solve
film separation and indentation simultaneously.

## Limitation

Stage 550 is a partitioned five-position quasi-static validation. It is not yet
a continuously coupled transient JFO/contact solution. Intermediate positions
where the frozen film load alone exceeds `0.03 N` are not valid load-balance
states; resolving those states requires film thickness/indentation feedback in
the next continuous coupling stage.
