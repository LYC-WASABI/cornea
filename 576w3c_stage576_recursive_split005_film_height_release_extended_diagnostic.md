# Stage 576w3c recursive split 0-5% film-height release extended diagnostic

## Status

CHECKED AND INDEPENDENTLY VERIFIED

This note records the `576w3c` branch as a checked follow-on to the earlier
Stage 576 split-segment release experiments.

## Artifacts

```text
576w3c_stage576_recursive_split005_film_height_release_extended_setup.mph
576w3c_stage576_recursive_split005_film_height_release_extended_checkpoint.mph
576w3c_stage576_recursive_split005_film_height_release_extended_results.mph
576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph
build_stage576w3c_recursive_split005_film_height_release_extended.java
verify_stage576w3c_checked.java
probe_stage576w3c_local_tff_region.java
build_stage576w3c_localfilm_dynamic_check.java
576w3c_stage576_localfilm_dynamic_check.md
build_stage576w3c_localfilm_recip_check.java
576w3c_stage576_localfilm_recip_check.md
```

## Purpose

This branch extends the split-segment release path with an additional
film-height release adjustment. Use this note to record whether the extended
release improves early-stroke load closure relative to the earlier:

- `576u` split path
- `576u2` stronger-alpha split path
- `576w` / `576w2` / `576w3` release variants

## Record Here

Final measured values from the checked state, independently read back by
`verify_stage576w3c_checked.java`:

```text
pressure solution = sol271
relaxed solution  = sol272
solid solution    = sol273

Fcontact = 0.0196747609743 N
Ffilm    = 0.0132800398588 N
Ftotal   = 0.0329548008331 N
loadError= 0.00295480083306 N
Ffeedback= 0.00205328086587 N
Residual = 0.000726197089437 N
drel     = 0.00497234297573 m
MaxP     = 7045.48341511 Pa
MinTheta = 0.999997361935
MinH     = 0.00499843225488 m
AvgH     = 8.38751874329e-06 m
MinGap   = -5.18603210152e-05 m

VERIFY_FINITE=true
VERIFY_STATUS=PASS
```

## Local TFF Region Check

`probe_stage576w3c_local_tff_region.java` independently checked that the TFF
physics and the key film variables are restricted to the local swept film
domain rather than the entire anterior corneal surface.

Read-back result:

```text
TFF_SELECTION_EQUALS_SWEPT=true
CHECK_TFF_SELECTION_LOCAL=true
CHECK_H_USES_LOCAL_RELEASE=true
CHECK_LOCAL_MASKS_NONTRIVIAL=true
CHECK_LOAD_PRESSURE_MASKED_TO_CORE=true
LOCAL_TFF_REGION_STATUS=PASS
```

Structural selection evidence:

```text
sel_film_swept571 entities = [6, 7, 10, 15, 16, 18]
tff selection entities     = [6, 7, 10, 15, 16, 18]
sel_local_cornea_patch574  = [10, 16]

ffp1 active on             = [6, 7, 10, 15, 16, 18]
ms_vent573 active on       = [6, 7, 10, 15, 16, 18]
intop_film active on       = [6, 7, 10, 15, 16, 18]
var_cornea_dynamic_regions573 active on = [6, 7, 10, 15, 16, 18]
var_hrelease576w3c active on            = [6, 7, 10, 15, 16, 18]
```

Final TFF-field evidence from `sol271`:

```text
AREA_SWEPT                         = 8.75712633587e-05
AREA_PATCH                         = 8.00043122095e-06
AREA_CORE_INT_MCORE                = 8.23783139247e-06
AREA_DRAIN_INT_MDRAIN              = 1.20160186169e-05
AREA_ACTIVE_INT_AFILM              = 3.26163179121e-06
AREA_WET_LOAD_INT_MCORE_BFILM      = 3.15162680719e-06
MCORE_MIN                          = 0
MCORE_MAX                          = 1
INT_POSITIVE_TFF_PRESSURE          = 0.0457391595402 N
INT_LOAD_PRESSURE_P_LOAD573        = 0.0132800398588 N
LOAD_TO_POSITIVE_PRESSURE_RATIO    = 0.290342892005
MIN_PRESSURE                       = 0 Pa
MAX_PRESSURE                       = 7045.48341511 Pa
MIN_H_CALC576W3C                   = 0.00499843225488 m
MAX_H_CALC576W3C                   = 0.0456194593496 m
AVG_H_CALC576W3C                   = 8.38751874329e-06 m
MIN_THETA                          = 0.999997361935
MAX_THETA                          = 1
```

Interpretation: TFF is solved only on `sel_film_swept571`; the load-bearing film
pressure `p_load573` is further masked by `M_core573*Bfilm573`, so only a
subset of the swept film domain contributes to the structural film load.

## Local Film Dynamic Check

`build_stage576w3c_localfilm_dynamic_check.java` ran a short TFF-only dynamic
check to verify that the local film variables respond during motion.

Result:

```text
LOCALFILM_DYNAMIC_STATUS=PASS
CHECK_H_LOCAL_DYNAMIC=true
CHECK_PRESSURE_LOCAL_DYNAMIC=true
CHECK_TAU_PROXY_DYNAMIC=true
CHECK_MU_PROXY_DYNAMIC=true
CHECK_LOCAL_MASK_DYNAMIC=true
CHECK_REVERSAL_AVAILABLE=false
```

Key ranges:

```text
H_AVG_RANGE=[7.97193915141e-06,8.38714463232e-06]
P_MAX_RANGE=[3.05291292826e-26,23238.4792951]
P_LOAD_ACTIVE_AREA_RANGE=[0,3.17228714194e-06]
TAU_SIGNED_RANGE=[-20.9409974104,0]
MU_TFF_ALT_RANGE=[0,0.000565679994497]
```

The checked `576w3c` continuation model freezes `tau572=t_position576p2`; the
dynamic check temporarily restores `tau572=t+time_offset572` with
`time_offset572=0` in the diagnostic output model only. The current Stage 572
motion law is one-way, so this check does not test forward/reverse shear sign
reversal.

## Local Film Reciprocating Check

`build_stage576w3c_localfilm_recip_check.java` ran a TFF-only reciprocating
diagnostic with a temporary `0 -> 1 -> 0` motion law to test shear sign
reversal.

Result:

```text
LOCALFILM_RECIP_STATUS=PASS
CHECK_REVERSAL_AVAILABLE=true
CHECK_TAU_SIGN_REVERSAL=true
CHECK_PRESSURE_RECIP_DYNAMIC=true
CHECK_H_RECIP_DYNAMIC=true
CHECK_MU_RECIP_DYNAMIC=true
CHECK_LOCAL_MASK_RECIP=true
```

Key ranges:

```text
OMEGA_MEAN_RANGE=[-30.2076216691,30.2076216691]
FT_SIGNED_RANGE=[-1.69703998349e-05,1.69703998349e-05]
TAU_SIGNED_RANGE=[-20.9409974104,20.9409974104]
MU_TFF_ALT_RANGE=[0,0.000565679994497]
THETA_MIN_RANGE=[0.521442137546,1]
```

The `theta` minimum is lower in this reciprocating diagnostic than in the
one-way local-film check. That is acceptable for the sign-reversal gate, but a
production reciprocating model would need separate cavitation and rewetting
acceptance checks.

## Interpretation

Stage 576w3c is the first Stage 576 split branch in this series that closes
both early-stroke segments, `0 -> 2.5%` and `2.5% -> 5%`, to the target-load
window around `0.03 N`.

The important modeling change relative to `576w`, `576w2`, and `576w3` is that
the release displacement is applied to both:

```text
imposed solid indentation release
TFF film height: h_calc576w3c = h_calc573 + drel576w3c
```

This matters because solid-only release reduced contact force but let film load
remain too high. In `576w3c`, film support and contact support are both in a
reasonable early-stroke range, and the read-back verification reports finite
pressure, finite gap-related quantities, and `theta` close to 1.

## Next Step

Do not resume tuning `alpha`, `beta`, segment size, or release gain immediately.

Use `576w3c` as the current verified early-stroke checked milestone and make the
next modeling step a conservative extension of the same mechanism:

```text
0 -> 2.5% -> 5% -> 7.5% -> 10%
```

Keep the `576w3c` settings initially:

```text
alpha = 0.15
beta = 0.15
gamma = 0.12
Keff = 5000 N/m
drel_step_max = 0.5 um
drel_max = 40 um
h_calc = h_calc573 + drel
```

Only if the added segment fails should the release law or segment continuation
be modified.
