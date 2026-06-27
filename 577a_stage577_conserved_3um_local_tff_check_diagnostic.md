# Stage 577a conserved 3 um local TFF check

## Status

PASS

## Purpose

Replace the previous geometry-gap film height with a conserved `3 um` local
film-height baseline and verify that the local reciprocating TFF diagnostics
still work.

## Artifacts

```text
build_stage577a_conserved_3um_local_tff_check.java
577a_stage577_conserved_3um_local_tff_check_results.mph
```

## Setup

```text
input model = 576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph
TFF region  = sel_film_swept571
TFF hw1     = h_TFF577a
h_TFF577a   = 3[um]
motion      = diagnostic reciprocating 0 -> 1 -> 0
solid       = off
contact     = off
```

## Results

```text
SOLUTION=sol274
TIME_RANGE=[0.0100000000000,0.137059969545] COUNT=41
H_AVG_RANGE=[3.00000000000e-06,3.00000000000e-06]
H_MIN_RANGE=[0.00300000000000,0.00300000000000]
H_MAX_RANGE=[0.00300000000000,0.00300000000000]
P_MAX_RANGE=[1.89748329375e-10,137336.440741]
THETA_MIN_RANGE=[0.100145674579,0.999997361935]
FT_SIGNED_RANGE=[-6.23313681838e-05,6.23313681838e-05]
TAU_SIGNED_RANGE=[-46.1522271284,46.1522271284]
MU_TFF_ALT_RANGE=[0.00000000000,0.00207771227279]
CHECK_FINITE=true
CHECK_LOCAL_TFF=true
CHECK_H_CONSERVATION=true
CHECK_PRESSURE_DYNAMIC=true
CHECK_TAU_SIGN_REVERSAL=true
CHECKED_STATUS=PASS
```

## Interpretation

The `3 um` conserved film-height baseline works as a local TFF diagnostic:

- area-averaged film height is exactly `3 um`,
- pressure is nonzero and dynamic,
- theta remains finite and responds to the local film solve,
- signed shear and fluid-friction proxies reverse sign under reciprocating motion.

This stage is a baseline check only. It does not include depletion, rupture, or
mixed-lubrication boundary friction.
