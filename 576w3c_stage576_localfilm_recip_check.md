# Stage 576w3c local-film reciprocating check

## Status

PASS

This diagnostic is a TFF-only reciprocating-motion check. It is not a final
coupled contact-flow-friction solve and does not replace the checked `576w3c`
load-closure result.

## Purpose

Verify that the local thin-film shear and friction proxy reverse sign when the
lid motion is made reciprocating.

The check uses:

```text
input model  = 576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph
output model = 576w3c_stage576_recursive_split005_localfilm_recip_check_results.mph
script       = build_stage576w3c_localfilm_recip_check.java
solution     = sol274
```

The script temporarily replaces the Stage 572 one-way stroke with a diagnostic
reciprocating stroke:

```text
slide_fraction572: 0 -> 1 -> 0
omega_lid_rot572: negative on the forward stroke, positive on the return stroke
```

This modification is local to the diagnostic output model.

## Results

```text
TIME_RANGE=[0.0100000000000,0.137059969545] COUNT=41
OMEGA_MEAN_RANGE=[-30.2076216691,30.2076216691]
AREA_SWEPT=8.75712633587e-05

H_AVG_RANGE=[7.97193915141e-06,8.38714463232e-06]
H_MIN_RANGE=[0.00497734298055,0.00797234297573]
H_MAX_RANGE=[0.00797234297573,0.0456274930689]

P_INT_RANGE=[0.00000000000,0.140281465039]
P_LOAD_RANGE=[0.00000000000,0.0251514716795]
P_MAX_RANGE=[1.60607881547e-31,23238.4792951]

CORE_AREA_RANGE=[4.25974226468e-08,8.63530696934e-06]
WET_LOAD_AREA_RANGE=[0.00000000000,3.12377582334e-06]

THETA_MIN_RANGE=[0.521442137546,1.00000000000]
THETA_MAX_RANGE=[1.00000000000,1.00000000000]

FT_SIGNED_RANGE=[-1.69703998349e-05,1.69703998349e-05]
FT_ABS_RANGE=[0.00000000000,1.69703998349e-05]
TAU_SIGNED_RANGE=[-20.9409974104,20.9409974104]
TAU_ABS_MAX_RANGE=[0.00000000000,20.9409974104]
MU_TFF_ALT_RANGE=[0.00000000000,0.000565679994497]

CHECK_FINITE=true
CHECK_REVERSAL_AVAILABLE=true
CHECK_TAU_SIGN_REVERSAL=true
CHECK_PRESSURE_RECIP_DYNAMIC=true
CHECK_H_RECIP_DYNAMIC=true
CHECK_MU_RECIP_DYNAMIC=true
CHECK_LOCAL_MASK_RECIP=true
LOCALFILM_RECIP_STATUS=PASS
```

## Interpretation

The reciprocating diagnostic passes the intended sign-reversal gate:

- `omega_lid_rot572` spans both signs.
- The signed shear proxy `tau_tff_signed576w3c_recip` spans both signs.
- The signed friction proxy `Ft_TFF_signed` spans both signs.
- `mu_TFF_alt` remains finite and in the expected small pure-fluid range.
- The local mask remains active only on a small subset of the swept film domain.

The minimum `theta` drops to about `0.521` during the reciprocating diagnostic.
That is acceptable for this diagnostic because the purpose is sign reversal and
local response, not final JFO acceptance. A final reciprocating production model
would need a separate cavitation/rewetting acceptance check.
