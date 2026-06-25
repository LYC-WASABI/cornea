# Stage 576o mask-pressure cause check

## Result

The stationary pressure hotspot is caused by incomplete moving-mask coverage
of the local TFF patch combined with wall velocity applied across the entire
fixed patch.

It is not caused by a frozen motion time: `tau572=t` in the final model.

During fractions `0.10`, `0.50`, and `0.70`, both `M_core573` and
`M_drain573` integrate to zero on `sel_local_cornea_patch574`. Nevertheless,
the fixed patch still solves TFF with analytical lid velocity and a `3 um`
background film. The open-region vent and weak pressure anchor both use only
`1e-7 kg/(m^2*s*Pa)`, allowing a geometry-controlled pressure field to remain.

At fraction `0.865`, only 10.3% of positive pressure lies in the core and
61.8% lies in the drain region. At `0.895`, these rise to 32.9% and 99.1%.
This explains why structural film loading appears only late even though the
fixed patch contains pressure much earlier.

## Required correction

```text
full swept film-domain coverage
+ moving-mask-gated wall velocity
+ strong ambient-pressure enforcement outside M_drain573
```

After correction, rerun fixed-structure JFO and require the mask centroid,
pressure centroid, and load centroid to move together before re-enabling
recursive structural feedback.
