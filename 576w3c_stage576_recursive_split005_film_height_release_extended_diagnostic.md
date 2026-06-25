# Stage 576w3c recursive split 0-5% film-height release extended diagnostic

## Status

IN PROGRESS

This note tracks the `576w3c` branch as a follow-on to the earlier Stage 576
split-segment release experiments.

## Artifacts

```text
576w3c_stage576_recursive_split005_film_height_release_extended_setup.mph
576w3c_stage576_recursive_split005_film_height_release_extended_checkpoint.mph
576w3c_stage576_recursive_split005_film_height_release_extended_results.mph
576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph
build_stage576w3c_recursive_split005_film_height_release_extended.java
```

## Purpose

This branch extends the split-segment release path with an additional
film-height release adjustment. Use this note to record whether the extended
release improves early-stroke load closure relative to the earlier:

- `576u` split path
- `576u2` stronger-alpha split path
- `576w` / `576w2` / `576w3` release variants

## Record Here

Fill in the final measured values from the current checked or result state:

```text
Fcontact =
Ffilm    =
Ftotal   =
Ffeedback=
MaxP     =
MinTheta =
MinGap   =
```

## Interpretation

Record whether this branch:

- improves total load closure,
- reduces excessive film support,
- preserves finite and physically acceptable pressure and theta behavior,
- is better or worse than the previous preferred branch.

## Next Step

After the quantities above are filled in, state one of:

- continue this branch,
- keep the builder but change one parameter,
- revert to an earlier better branch,
- treat this branch as a dead end.
