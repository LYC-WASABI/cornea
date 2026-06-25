# Stage 574i fixed-structure constant-film velocity JFO failed

## Source

- Base model: `574h_stage574_fixed_structure_constant_zero_jfo_checked.mph`
- Setup model: `574i_stage574_fixed_structure_constant_velocity_jfo_setup.mph`
- Results model: `574i_stage574_fixed_structure_constant_velocity_jfo_results.mph`
- Build script: `build_stage574i_constant_velocity_jfo.java`

## Scope

This stage kept the structure fixed and only activated analytic lid-wall velocity in the TFF interface:

```text
solid = off
ge_force_total111 = off
tff = on
film thickness hw1 = 3[um]
true gap / rupture / vent = off
membrane pressure feedback = off
friction child under dcnt1 = inactive
v_blink_avg = 0.15[m/s]
time_offset572 = T_pre572+0.5*T_slide572
tau572 = time_offset572
vw = (0, -lambda_v574*omega_lid_rot572*Z, lambda_v574*omega_lid_rot572*Y)
```

The continuation reached `lambda_v574 = 1.0`, but the acceptance failed because the constant-film velocity pressure created excessive hydrodynamic load.

## Continuation Results

```text
lambda_v574    max(p-p_amb573) [Pa]    positive film load [N]    min(theta)
0              0                       0                         1
1e-5           75.7979411370           4.15372464747e-5          0.999999995957
1e-4           757.574716143           4.15258900326e-4          0.999999959581
1e-3           7535.74137940           0.00414134297683          0.999999596993
1e-2           71766.5679025           0.0403825644299           0.999996079460
0.05           306705.151496           0.185485119795            0.999982058959
0.1            542435.715967           0.345175270577            0.999966626613
0.2            945609.351254           0.629110475595            0.999939447355
0.4            1682380.82082           1.13184464301             0.999883070572
0.7            2767823.69846           1.83139458891             0.999765338437
1.0            3902061.21992           2.51965967899             0.999609050330
```

Final diagnostics:

```text
gap coverage = 0.997570162465
min(p-p_amb573) = 0 Pa
max(p-p_amb573) = 3.90206121992 MPa
signed film load = 2.51965967899 N
positive film load = 2.51965967899 N
edge max abs pressure = 3.12875444275e-10 Pa
edge-to-patch pressure ratio = 8.01820952162e-17
theta min = 0.999609050330
theta max = 1
theta mean = 0.999990192986
min(geomgap_dst_cp_lid_cornea) = -1.484692981639327e-4 m
```

## Acceptance

```text
lambda_v574 = 1 converged: PASS
pressure and theta finite: PASS
local gap coverage >= 0.95: PASS
pressure not pinned to artificial patch edge: PASS
pressure grows smoothly with lambda_v574: PASS
film load not excessively large: FAIL
checked status: FAIL
```

## Interpretation

The TFF velocity solve is numerically stable, but the fixed 3 um constant-film velocity case over-bears badly. The first excessive-load crossing occurs between:

```text
lambda_v574 = 0.01, positive film load = 0.0404 N
lambda_v574 = 0.05, positive film load = 0.1855 N
```

At full analytic speed with `v_blink_avg=0.15[m/s]`, the film load is about `2.52 N`, far above the inherited structural contact force `0.0448 N`. This result must not be used as a checked JFO gate and must not be followed by true-gap or feedback stages without diagnosing the velocity scale/sign and local film-domain assumptions.

## Next Diagnostic

Before Stage 574j, run a focused 574i diagnostic branch:

```text
lambda_v574 = 0.02, 0.03, 0.04, 0.05, 0.06, 0.075, 0.09
```

and evaluate:

```text
omega_lid_rot572 at midpoint
wall-speed magnitude on [10,16]
surface divergence / normal component of vw on the patch
pressure peak position relative to contact/gap field
```
