# Stage 576j: Full Dynamic Film-Pressure Feedback at Alpha 0.04

Status: **PASS**

## Configuration

```text
base = 576h2_stage576_full_dynamic_safe_speed_checked.mph
v_blink_avg = 0.03 m/s
q_scale574 = -9
alpha_pfb576i = 0.04
scrape fraction = 0 to 1
time step = T_slide572/200
solved staggered increments = 200
contact friction = off
```

The calculation retained the same history-preserving staggered update verified in
Stage 576i4:

```text
advance transient JFO one time step from the previous pressure/theta state
apply 4% of the current physical film pressure to the structure
solve stationary nonlinear contact and update the gap
use the updated structure and retained JFO state at the next time step
```

No speed, prescribed indentation, or feedback coefficient was adjusted during
the scrape.

## Results

```text
F_total range = 0.0255953908 to 0.0345443426 N
points outside 0.025-0.035 N = 0 / 200
numerical stability = PASS
peak scrape fraction = approximately 0.855
peak F_contact = approximately 0.0254010 N
peak F_film = approximately 0.00914334 N
minimum theta > 0.99994
pressure finite over the complete scrape
gap finite over the complete scrape
```

The end-of-scrape state returned to:

```text
fraction = 1.0
F_contact = 0.0255953904 N
F_film = 2.91848e-8 N
F_total = 0.0255954196 N
minimum theta = 0.9999999997
```

The checked model is:

```text
576j_stage576_full_dynamic_feedback_alpha004_checked.mph
```

## Interpretation

The peak-window result was not an artifact of starting directly at fraction
0.82. With pressure/cavitation history propagated from the beginning of the
scrape, `alpha_pfb = 0.04` remains inside the strict load band for the complete
stroke. The previous stationary node reconstruction failure is also absent.

This remains an explicit staggered weak-coupling calculation. It is not yet a
monolithic two-way structure-JFO solve, and solid contact friction remains off.

## Next Stage

Use this checked full-stroke state as the reference for a feedback-continuation
test. Increase coupling strength only in a midpoint or peak-window restart
before attempting full-stroke stronger coupling. Do not enable solid contact
friction in the same change.
