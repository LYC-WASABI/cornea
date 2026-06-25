# Stage 576c: Partitioned Constant-Load Control

Status: **FAIL**

No checked model was saved.

## Files

```text
build_stage576c_partitioned_constant_load.java
resume_stage576c_partitioned_constant_load.java
probe_stage576c_results.java
576c_stage576_partitioned_constant_load_setup.mph
576c_stage576_partitioned_constant_load_results.mph
576c_stage576_partitioned_constant_load_failed.md
```

## Method

Eleven quasi-static time nodes were used over the complete scrape. At each node, the structural contact solution and the stationary full-velocity JFO solution were alternated. The controller changed `q_scale574`, which changes the prescribed radial lid displacement, to target:

```text
F_contact + F_film = 0.03 N
```

Fixed settings:

```text
alpha_pfb576a = 0.183
v_blink_avg = 0.15 m/s
h_active_max573 = 50 um
dh_active573 = 5 um
moving mask = on
solid contact friction = off
```

Acceptance band:

```text
0.025 N <= F_total <= 0.035 N
```

## Results

| node | time fraction | q_scale574 | F_contact (N) | F_film (N) | F_total (N) | status |
|---:|---:|---:|---:|---:|---:|---|
| 0 | 0.0 | -7.5 | 0.0279353 | 0 | 0.0279353 | PASS |
| 1 | 0.1 | -7.5 | 0.0285883 | 0 | 0.0285883 | PASS |
| 2 | 0.2 | -7.5 | 0.0285883 | 0 | 0.0285883 | PASS |
| 3 | 0.3 | -7.5 | 0.0285883 | 0 | 0.0285883 | PASS |
| 4 | 0.4 | -7.5 | 0.0285883 | 0 | 0.0285883 | PASS |
| 5 | 0.5 | -7.5 | 0.0285883 | 0 | 0.0285883 | PASS |
| 6 | 0.6 | -7.5 | 0.0285883 | 0 | 0.0285883 | PASS |
| 7 | 0.7 | -7.5 | 0.0285883 | 0 | 0.0285883 | PASS |
| 8 | 0.8 | -7.5 | 0.0285883 | 0.0000305 | 0.0286188 | PASS |
| 9 | 0.9 | -10.75 | 0.0208885 | 0.0239625 | 0.0448510 | FAIL: high load |
| 10 | 1.0 | -8.5 | 0.0266020 | approximately 0 | 0.0266020 | PASS |

At node 9, repeated partition iterations at `q=-10.75` produced total loads between approximately `0.0449 N` and `0.0538 N`. The best result remained `0.01485 N` above the target.

## Stability Boundary

An extended release test was performed at node 9:

```text
q_scale574 = -11.0
velocity continuation = 0 -> 1
```

The JFO solve failed at:

```text
lambda_v574 = 0.2
146 pfilm degrees of freedom contained NaN/Inf
```

Therefore the controller cannot reach the target by further lid release under the current active-gap and moving-mask formulation. The current stable lower-load boundary is between `q=-10.75` and `q=-11.0`.

## Decision

Do not use this result as the Stage 577 two-way coupling base.

The next stage should diagnose and regularize the node-9 pressure/load spike near `time fraction = 0.9`. Required checks are:

```text
pressure peak coordinates and distance to moving-mask boundaries
h_calc573 and Bfilm573 at the pressure peak
M_core573 and M_drain573 transition widths
Qvent573 magnitude near the trailing/leading edge
time refinement over fractions 0.82 to 0.95
```

Only after the node-9 film load can be reduced or smoothly redistributed while preserving the physical gap should constant-load control be repeated.
