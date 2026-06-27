# Stage 577e weakly coupled depleted TFF

## Input

- Base model: `577a_stage577_conserved_3um_local_tff_check_results.mph`
- Intended output model: `577e_stage577_weakly_coupled_depleted_tff_results.mph`
- Script: `build_stage577e_weakly_coupled_depleted_tff.java`
- TFF region: `sel_film_swept571`

## Intended change

This stage attempted to put a spatially depleted film height directly into the TFF film-height feature:

```text
h_raw577e = max(h_min577e, 3[um] - dh_deplete577e*M_core573)
h_min577e = 0.05[um]
ffp1.hw1 = h_raw577e
```

Two runs were attempted:

```text
dh_deplete577e = 2.5 um
dh_deplete577e = 2.0 um
```

The second run used `2.0 um`, the first 577d candidate with a nontrivial low-film area.

## Observed behavior

Both runs became impractically slow in the early transient solve. The `2.0 um` run was manually terminated after more than 4 minutes. At termination, the log showed it had only advanced to about:

```text
t ≈ 0.02385 s
Current Progress ≈ 14 %
```

The solver was repeatedly assembling matrices/residuals with very small internal time steps.

## Status

```text
CHECK_SOLVE_COMPLETED=false
CHECK_H_FLOOR=not evaluated
CHECK_PRESSURE_DYNAMIC=not evaluated
CHECK_THETA_RESPONSE=not evaluated
CHECK_TAU_SIGN_REVERSAL=not evaluated
CHECKED_STATUS=FAIL
```

## Interpretation

`577e` is not a checked result. Directly inserting the spatially depleted film-height expression into `ffp1.hw1` is currently too slow for the same reciprocating TFF check that worked in 577a.

The next weak-coupling attempt should not use this exact setup unchanged. Safer follow-ups are:

- shorter micro-window solve before the full reciprocal cycle,
- smoother depletion mask,
- external mass-normalization parameter `C_mass577e`,
- continuation in `dh_deplete`,
- looser first diagnostic that checks only one forward segment before demanding full sign reversal.
