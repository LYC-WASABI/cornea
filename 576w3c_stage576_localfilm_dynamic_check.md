# Stage 576w3c local-film dynamic check

## Status

PASS

This diagnostic is a short TFF-only dynamic check. It is not a final coupled
solid-contact-flow result and is not a final coefficient-of-friction model.

## Purpose

Verify that the local thin-film variables have sensible dynamic behavior on the
local swept film domain before extending the checked `576w3c` branch.

The check uses:

```text
input model  = 576w3c_stage576_recursive_split005_film_height_release_extended_checked.mph
output model = 576w3c_stage576_recursive_split005_localfilm_dynamic_check_results.mph
script       = build_stage576w3c_localfilm_dynamic_check.java
solution     = sol274
```

The script temporarily restores the Stage 572 transient clock for this check:

```text
tau572 = t + time_offset572
time_offset572 = 0
```

This is needed because the checked `576w3c` continuation model freezes
`tau572 = t_position576p2` for staged load-closure solves.

## Results

```text
TIME_RANGE=[0.0100000000000,0.0735299847726] COUNT=21
OMEGA_RANGE=[-30.2076216691,0.00000000000]
AREA_SWEPT=8.75712633587e-05

H_AVG_RANGE=[7.97193915141e-06,8.38714463232e-06]
H_MIN_RANGE=[0.00497734298055,0.00797234297573]
H_MAX_RANGE=[0.00797234297573,0.0456274930689]

P_INT_RANGE=[0.00000000000,0.140281465042]
P_LOAD_RANGE=[0.00000000000,0.0251514716801]
P_MAX_RANGE=[3.05291292826e-26,23238.4792951]
P_ACTIVE_AREA_RANGE=[0.00000000000,5.08854967907e-05]
P_LOAD_ACTIVE_AREA_RANGE=[0.00000000000,3.17228714194e-06]

CORE_AREA_RANGE=[4.25974226468e-08,8.63530696934e-06]
WET_LOAD_AREA_RANGE=[0.00000000000,3.12377582334e-06]

THETA_MIN_RANGE=[0.999992406980,1.00000000000]
THETA_MAX_RANGE=[1.00000000000,1.00000000000]

FT_SIGNED_RANGE=[-1.69703998349e-05,0.00000000000]
FT_ABS_RANGE=[0.00000000000,1.69703998349e-05]
TAU_SIGNED_RANGE=[-20.9409974104,0.00000000000]
TAU_ABS_MAX_RANGE=[0.00000000000,20.9409974104]
MU_TFF_ALT_RANGE=[0.00000000000,0.000565679994497]

CHECK_H_LOCAL_DYNAMIC=true
CHECK_PRESSURE_LOCAL_DYNAMIC=true
CHECK_TAU_PROXY_DYNAMIC=true
CHECK_MU_PROXY_DYNAMIC=true
CHECK_LOCAL_MASK_DYNAMIC=true
CHECK_REVERSAL_AVAILABLE=false
LOCALFILM_DYNAMIC_STATUS=PASS
```

## Interpretation

The local film check passes the intended gate:

- `h_calc576w3c` is finite and varies during the dynamic pass.
- TFF pressure is not uniformly zero and develops a local peak during motion.
- The load-bearing pressure area is much smaller than the full swept film
  area, so the structural film load remains locally masked.
- The Couette shear proxy and `mu_TFF_alt576w3c` respond dynamically and remain
  in a small pure-fluid-lubrication range.

The current Stage 572 motion law is one-way over this check interval. Therefore
this check cannot verify positive/negative shear sign reversal between forward
and reverse strokes. A separate reciprocating-motion check is required for that
specific criterion.

## Scope

The shear and friction outputs are diagnostic proxies:

```text
tau_tff_signed576w3c
Ft_TFF_signed576w3c
Ft_TFF_abs576w3c
mu_TFF_alt576w3c
```

They should not be interpreted as final experimental COF. They only confirm
that a pure-fluid shear contribution can be extracted from the local TFF field.
