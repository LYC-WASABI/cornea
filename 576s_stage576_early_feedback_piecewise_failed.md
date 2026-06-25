# Stage 576s early-feedback piecewise diagnostic

## Status

Stage 576s is not checked. It verified that the quarter-motion workflow can
run with physical exterior pressure boundaries and a direct segment-to-segment
film-pressure feedback load, but `alpha_pfb576s = 0.02` is not sufficient to
stabilize the first quarter of the scratch.

Artifacts:

```text
576s_stage576_early_feedback_piecewise_setup.mph
576s_stage576_early_feedback_piecewise_checkpoint.mph
576s_stage576_early_feedback_piecewise_results.mph
build_stage576s_early_feedback_piecewise.java
```

## Method

Base model:

```text
576p2r_stage576_moving_structure_sparse_jfo_results.mph
```

Per segment:

```text
1. transient JFO over 0->5->10->15->20->25%
2. apply p_feedback576s = alpha_pfb576s*withsol(segment_tff,p_load573)
3. stationary structure solve at the segment end position
```

Settings:

```text
alpha_pfb576s = 0.02
wc_open_anchor573 = off
full exterior TFF edge pressure = ambient
solid structural transient behavior = Quasistatic
```

## Results

```text
5%:  Fcontact = 0.02541 N, Ffilm = 0.04011 N, Ftotal = 0.06552 N
     MaxP = 106.4 kPa, MinTheta = 0.99990

10%: Fcontact = 0.02076 N, Ffilm = 0.10240 N, Ftotal = 0.12315 N
     MaxP = 134.4 kPa, MinTheta = 0.99986

15%: Fcontact = 0.01592 N, Ffilm = 0.11326 N, Ftotal = 0.12918 N
     MaxP = 155.0 kPa, MinTheta = 0.99992

20%: Fcontact = 0.01445 N, Ffilm = 0.19454 N, Ftotal = 0.20899 N
     MaxP = 463.96 kPa, MinTheta = 0.04047

25%: Fcontact = 0.01563 N, Ffilm = 34.91903 N, Ftotal = 34.93466 N
     MaxP = 381.0 MPa, MinTheta = 0.04119
```

## Interpretation

- The pressure history is still excessive from the start of the motion.
- Direct one-shot segment feedback does not keep `F_contact + F_film` near
  the `0.03 N` target.
- The first quarter remains numerically finite, but the 25% state is already
  far outside physical load scale.
- The pressure center still tracks the moving scratch region, so the main
  remaining defect is load growth, not a mask-direction regression.

## Required next action

Do not continue Stage 576s by only increasing `alpha_pfb576s` on this direct
one-step feedback path.

Next main-line change:

```text
Stage 576t: recursive relaxed pressure feedback inside each segment
```

That stage should:

```text
1. preserve a relaxed pressure field between iterations
2. iterate TFF <-> relaxed load <-> structure within each segment
3. stop each segment only after Ftotal and min gap stabilize
4. start at 0->5% before extending to 10% and beyond
```
