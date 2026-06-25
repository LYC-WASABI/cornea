# Stage 576h2: Full Dynamic Safe-Speed JFO Check

Status: **PASS**

## Selected Operating Point

```text
v_blink_avg = 0.03 m/s
q_scale574 = -9
film-pressure structural feedback = off
solid contact friction = off
JFO transient history = on
moving mask = on
active-gap regularization = on
```

The structural contact state is fixed at:

```text
F_contact = 0.0255953907551 N
```

## Velocity Feasibility Results

At scrape fraction `0.84`, the refined threshold scan found:

| speed (m/s) | q | F_total (N) | status |
|---:|---:|---:|---|
| 0.035 | -9 | 0.03310 | PASS |
| 0.040 | -9 | 0.03411 | PASS at fraction 0.84 |
| 0.045 | -9 | 0.03510 | FAIL |

The local interpolated threshold was approximately `0.0445 m/s`. A full-stroke run showed that the true peak occurs later, at fraction `0.855`, so the local threshold alone was not sufficient.

## Full Dynamic Comparison

At `0.04 m/s`:

```text
F_film range = 0 to 0.0107706 N
F_total range = 0.0255954 to 0.0363660 N
points outside 0.025-0.035 N = 3
status = FAIL
```

At `0.03 m/s`:

```text
time range = 0.0100000 to 0.3276499 s
time points = 201
F_film range = 0 to 0.00817412 N
F_total range = 0.0255954 to 0.0337695 N
points outside 0.025-0.035 N = 0
peak scrape fraction = 0.855
min(theta) = 0.9999404
max pressure = 0.957379 MPa
status = PASS
```

## Output Files

```text
576g_stage576_velocity_q_feasibility_checked.mph
576g2_stage576_velocity_threshold_refined_checked.mph
576h2_stage576_full_dynamic_safe_speed_setup.mph
576h2_stage576_full_dynamic_safe_speed_results.mph
576h2_stage576_full_dynamic_safe_speed_checked.mph
576h2_stage576_full_dynamic_safe_speed_checked.md
```

## Meaning

This is the first complete dynamic scrape in the current branch that simultaneously preserves transient JFO history and keeps:

```text
0.025 N <= F_contact + F_film <= 0.035 N
```

for every output time.

It is still a one-way fixed-structure result. Film pressure has not yet been applied back to the structure, and solid contact friction remains disabled.

## Next Stage

Use this checked model as the base for gradual one-way film-pressure structural feedback:

```text
alpha_pfb = 0.01 -> 0.02 -> 0.05
```

The first feedback test should cover the peak interval around scrape fractions `0.82-0.90`. Only after that interval remains stable and load-closed should feedback be extended to the full scrape.
