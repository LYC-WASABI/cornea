# Stage 576r piecewise transient JFO diagnostic

## Status

Stage 576r is not checked. It verified that a transient JFO formulation can
run on the complete swept corneal mesh without the distributed pressure
anchor, but the fixed-structure one-way pressure history becomes excessive
before 20% travel.

## Topology findings

- Swept TFF surfaces: `[6, 7, 10, 15, 16, 18]`.
- Their surface mesh is one connected component with 237 vertices.
- There are 51 nodes shared across face boundaries.
- No coincident-but-duplicated mesh vertices were found.
- Therefore the six-face mesh is connected; `dcont1` and identity pair `ap1`
  must not be enabled as a speculative topology fix.

Stationary JFO without a distributed weak anchor still produces empty
`pfilm` rows. A time-dependent JFO does not have this defect, so the dynamic
main line should use transient pressure evolution.

## Frozen 25% tests

With physical ambient pressure on all exterior TFF edges and
`wc_open_anchor573 = off`:

- Instant full-speed transient reached 1 ms but stalled during continuation
  near 1.34 ms.
- A 1 ms half-cosine speed ramp completed to 3 ms.
- At 3 ms:

  ```text
  Ffilm = 0.22866 N
  max pressure = 8.97 MPa
  min(theta) = 0.05757
  ```

The pressure and cavitation had not stabilized. Holding one frozen gap at
full speed is therefore not a valid representation of the moving scratch.

## Piecewise moving test

The first-quarter piecewise model inherited pressure between moving
structural states:

```text
5% -> 10% -> 15% -> 20% -> 25%
```

It completed through 15%. The 15% to 20% segment stalled near physical time
`20.58 ms` because the pressure history and the newly switched gap field were
incompatible.

Checkpoint:

```text
576r_stage576_first_quarter_piecewise_jfo_checkpoint.mph
```

The attempted 1.25% structure/JFO refinement also stalled immediately after
15%. Its solver pressure scale was approximately `7 GPa`, showing that the
one-way fixed-structure film pressure had already become nonphysical before
the refinement began.

## Required next action

The ready diagnostic script is:

```text
probe_stage576r_checkpoint_history.java
```

It must first evaluate the saved 5%, 10%, and 15% states:

```text
Fcontact
Ffilm
Ftotal
max pressure
min(theta)
```

After locating the first excessive-pressure segment, restart from the last
acceptable state using one of these controlled changes:

1. Introduce low-strength structural pressure feedback before pressure grows
   beyond the applied normal load.
2. Continue feedback strength from a small value instead of retaining a
   frozen gap.
3. Keep physical exterior pressure boundaries and leave
   `wc_open_anchor573` disabled.

Do not continue the current one-way pressure history to 25%; smaller position
steps alone do not correct the excessive pressure.
