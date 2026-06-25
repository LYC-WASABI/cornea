# Stage 576i4: Peak-Window Film-Pressure Feedback at Alpha 0.04

Status: **PASS**

## Configuration

```text
base = 576h2_stage576_full_dynamic_safe_speed_checked.mph
v_blink_avg = 0.03 m/s
q_scale574 = -9
alpha_pfb576i = 0.04
scrape fraction = 0.82 to 0.90
time step = T_slide572/200
contact friction = off
```

The calculation used history-preserving staggered feedback:

```text
advance transient JFO one step
apply 4% of current film pressure to the structure
solve stationary contact/gap
use the updated structure in the next JFO step
```

## Results

```text
F_total range = 0.0265215 to 0.0345443 N
points outside 0.025-0.035 N = 0
peak fraction = 0.855
peak F_contact = 0.0254010 N
peak F_film = 0.00914334 N
peak F_total = 0.0345443 N
minimum theta > 0.99994
pressure finite
gap finite
```

The checked model is:

```text
576i4_stage576_peak_feedback_alpha004_checked.mph
```

## Feedback Comparison

```text
alpha = 0.01: PASS
alpha = 0.02: PASS
alpha = 0.04: PASS
alpha = 0.05: FAIL, one point at 0.03508275 N
```

Therefore `alpha_pfb = 0.04` is the highest feedback level currently verified over the peak-load window with a strict `0.035 N` upper bound.

## Limitation

This is explicit staggered one-way feedback, not a monolithic fully coupled structure-JFO solve. The result covers only the peak interval, not the complete scrape with feedback.

## Next Stage

Extend `alpha_pfb = 0.04` to the complete scrape while retaining the same history-preserving staggered update. Only after the full stroke passes should stronger two-way coupling or solid contact friction be introduced.
