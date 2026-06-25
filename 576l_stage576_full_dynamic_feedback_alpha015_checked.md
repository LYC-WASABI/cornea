# Stage 576l: Full Dynamic Film-Pressure Feedback at Alpha 0.15

Status: **PASS**

## Configuration

```text
base = 576h2_stage576_full_dynamic_safe_speed_checked.mph
v_blink_avg = 0.03 m/s
q_scale574 = -9
alpha_pfb576i = 0.15
scrape fraction = 0 to 1
time step = T_slide572/200
staggered increments = 200
contact friction = off
```

The full scrape was restarted from the original environmental-pressure JFO
state and the original converged contact state. No state from the alpha-0.04
full scrape was reused.

Each increment used:

```text
advance transient JFO from the previous pressure/theta state
apply 15% of the current physical film pressure to the structure
solve nonlinear stationary contact and update the gap
use the retained JFO history and updated structure at the next time step
```

## Results

```text
F_total range = 0.0255953908 to 0.0336796765 N
points outside 0.025-0.035 N = 0 / 200
numerical stability = PASS
pressure = finite over the complete scrape
gap = finite over the complete scrape
minimum theta >= 0
```

End-of-scrape state:

```text
fraction = 1.0
F_contact = 0.0255953895 N
F_film = 2.91134e-8 N
F_total = 0.0255954186 N
minimum theta = 0.9999999997
```

The checked model is:

```text
576l_stage576_full_dynamic_feedback_alpha015_checked.mph
```

## Comparison With Alpha 0.04

```text
alpha = 0.04: maximum F_total = 0.0345443426 N
alpha = 0.15: maximum F_total = 0.0336796765 N
peak reduction = 0.0008646661 N
```

The stronger structural feedback reduced the maximum combined load without
changing speed or prescribed indentation and without causing a numerical
instability.

## Limitation

This remains an explicit history-preserving staggered weak-coupling model:

```text
film-pressure feedback = 15%, not 100%
structure = quasistatic at each time step
contact friction = off
fluid and structure = not solved monolithically
```

The result verifies a complete dynamic scrape at the highest peak feedback
level currently shown to converge. It does not yet establish a fully coupled
alpha-1 physical solution.

## Next Step

Use Stage 576l as the full-stroke weak-coupling reference. The next solver task
is recursive field relaxation or a segregated coupled solve at the peak before
increasing feedback beyond alpha 0.15. Do not enable contact friction in the
same solver change.
