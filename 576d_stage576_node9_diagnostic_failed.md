# Stage 576d: Node-9 Load-Spike Diagnostic

Status: **DIAGNOSIS COMPLETE / REGULARIZATION FAILED**

No checked load-closure model was saved.

## Files

```text
diagnose_stage576d_node9_pressure_spike.java
probe_stage576d_time_window.java
576d_stage576_node9_pressure_spike_diagnostic.mph
576d_stage576_node9_regularization_results.mph
576d_stage576_node9_branch_continuation_results.mph
576d_stage576_node9_diagnostic_failed.md
```

## Spatial Diagnosis

For the stable node-9 solution `sol358`:

```text
F_film = 0.0239625 N
max pressure = 0.920 MPa
```

The positive film-load weighted state is:

```text
centroid = (-0.0763, -4.4045, 6.1867) mm
safe gap = 4.756 um
h_calc573 = 4.691 um
Bfilm573 = 0.9928
M_core573 = 0.9988
M_drain573 = 1.0000
weighted pressure = 78.65 kPa
```

Therefore the node-9 load is generated inside the active core. It is not a moving-mask boundary artifact and must not be removed by reducing `M_core573` or clipping the physical pressure region.

The largest raw `tff.p` at node 8 lies in the non-active region (`M_core=0`, `M_drain=0`) and does not contribute to `p_load573`. Raw maximum pressure is therefore not a suitable load diagnostic by itself.

## Time-Window Diagnosis

The existing 201-step transient solution `sol236`, with fixed structural state `sol201`, gives:

```text
film-load maximum = 0.0166500 N
peak time fraction = 0.855
film load at fraction 0.900 = 0.0122992 N
```

The high-load interval extends approximately from fractions 0.84 to 0.93. It is not a single-time numerical spike.

## Regularization Tests

Mild edge regularization:

```text
h_active_max573 = 30 um
dh_active573 = 5 um
dh_break573 = 0.01 um
kvent573 = 1e-6 kg/(m^2*s*Pa)
```

Result: `q=-11` failed at `lambda_v574=0.65` with NaN/Inf in `pfilm`.

Strong edge regularization:

```text
h_active_max573 = 30 um
dh_active573 = 10 um
dh_break573 = 0.02 um
kvent573 = 1e-5 kg/(m^2*s*Pa)
```

At `q=-11` it converged, but changed the physical solution:

```text
F_contact = 0.020750 N
F_film = 0.047561 N
F_total = 0.068311 N
min(theta) = 0.0929
```

At `q=-11.5` the linear solve failed with NaN/Inf. The strong regularization is rejected because it increases film load and introduces strong cavitation rather than preserving the baseline solution.

## Stationary Versus Transient History

At time fraction 0.9 and the same fixed structural state `sol201`:

```text
transient JFO film load = 0.012299 N
stationary reconstructed JFO film load = 0.020581 N
```

After stationary structure-pressure iterations at `q=-10`, the total load oscillated:

```text
0.048806 N
0.042137 N
0.051574 N
```

This proves that the partitioned stationary-node controller does not preserve the transient JFO pressure history. It creates a different pressure/gap branch and cannot be used as the full-stroke constant-load controller.

## Decision

Do not continue tuning masks, active-gap caps, or vent coefficients to force Stage 576c to pass.

The next implementation must use a **time-dependent normal-load controller** inside the transient solve. The controller variable must evolve continuously and enforce:

```text
F_contact(t) + F_film(t) = 0.03 N
```

while retaining the previous time-step values of `pfilm`, `theta`, structure displacement, contact state, and controller displacement. This replaces the failed stationary-node method and is the required bridge to Stage 577 two-way transient coupling.
