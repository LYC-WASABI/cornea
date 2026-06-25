# Stage 576k: Peak Feedback Continuation Diagnostic

Status: **PARTIAL PASS**

## Configuration

```text
scrape fraction = 0.855
JFO history restart = fraction 0.850, sol649
v_blink_avg = 0.03 m/s
q_scale574 = -9
contact friction = off
load band = 0.025 to 0.035 N
fixed-point convergence = abs(delta F_total) < 1e-5 N
```

At each feedback level the JFO state at fraction 0.855 was recomputed from the
retained fraction-0.850 history, followed by a nonlinear stationary contact
solve. Thus the film pressure was not frozen when the structural gap changed.

## Verified Levels

```text
alpha = 0.10
F_contact = 0.0251209593 N
F_film = 0.00900227690 N
F_total = 0.0341232362 N
status = converged, load band PASS

alpha = 0.12
F_contact = 0.0250643546 N
F_film = 0.00845935943 N
F_total = 0.0335237140 N
status = converged at iteration 9, load band PASS

alpha = 0.15, pressure under-relaxation beta = 0.5
F_contact = 0.0249977155 N
F_film = 0.00767539627 N
F_total = 0.0326731117 N
status = converged at iteration 6, load band PASS
```

The highest strictly converged local feedback level is therefore:

```text
alpha_pfb = 0.15
```

## Failed Or Incomplete Levels

```text
alpha = 0.06
F_total = 0.0351295661 N
status = converged, strict load upper bound FAIL by 0.000129566 N

alpha = 0.20, beta = 0.5
status = finite and inside load band, but fixed-point oscillation did not converge

alpha = 0.20, beta = 0.25
iteration-10 F_total = 0.0314290216 N
iteration-10 abs(delta F_total) = 0.000116402 N
status = finite and inside load band, but convergence FAIL
```

For `alpha=0.20`, reducing beta changed the oscillation but did not provide a
recursive relaxed pressure state. More raw iterations with the same update are
not accepted as a coupling solution.

## Numerical State

Across the continuation runs:

```text
pressure = finite
gap = finite
minimum theta > 0.99995 approximately
nonlinear solid solve = stable
contact friction = off
```

## Artifacts

```text
576k_stage576_peak_feedback_continuation_results.mph
576k2_stage576_peak_feedback_continuation_012_020_results.mph
576k3_stage576_peak_feedback_relaxed_015_020_results.mph
576k4_stage576_peak_feedback_relaxed_020_results.mph
```

No overall checked MPH was produced because the requested continuation through
`alpha=0.20` did not meet the fixed-point convergence criterion.

## Next Step

Use `alpha=0.15` as the highest verified peak state. Before increasing feedback,
implement a genuinely recursive field relaxation or a segregated/monolithic
coupled solver. Do not extend `alpha=0.20` to the full scrape with the current
two-state update.
