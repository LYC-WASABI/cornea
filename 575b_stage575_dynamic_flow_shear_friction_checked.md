# Stage 575b: Dynamic Flow, Shear, and Friction Diagnostics

## Purpose

Add diagnostic fluid shear and effective friction estimates to the Stage 575a fixed-structure dynamic JFO result.

## Base

```text
575a_stage575_dynamic_fixed_patch_jfo_checked.mph
```

Dynamic solution:

```text
sol139
```

## Diagnostic Formulas

Couette shear diagnostic:

```text
v_lid_mag = sqrt((-lambda_v574*omega_lid_rot572*Z)^2
               +( lambda_v574*omega_lid_rot572*Y)^2)

tau_couette = 1e-3[Pa*s]*v_lid_mag/max(h_calc573,h_num573)

tau_active = M_core573*Bfilm573*tau_couette
```

Integrated shear:

```text
F_shear = intop_film(tau_active)
```

Effective friction diagnostics:

```text
mu_film_only = F_shear/(F_film+1e-12[N])
mu_total = F_shear/(Fn_contact570+F_film+1e-12[N])
```

These are diagnostic fluid-shear ratios, not solid contact friction coefficients.

## Results

```text
time range = 0.0100000000000 .. 0.0735299847726
time steps = 201
Fn_contact used = 0.0285716205813 N
F_film range = 1.26541139585e-07 .. 0.0217279742752 N
F_shear range = 0 .. 0.000279454264332 N
tau_mean range = 0 .. 81.8924630097 Pa
tau_max range = 0 .. 3316.64753587 Pa
v_mean range = 0 .. 0.225425466372 m/s
v_max range = 0 .. 0.235619449019 m/s
mu_film_only range = 0 .. 0.0128618642894
mu_total range = 0 .. 0.00555579553122
mean Bfilm573 range = 0.372839530206 .. 0.372839530206
```

## Status

```text
PASS
```

## Outputs

```text
575b_stage575_dynamic_flow_shear_friction_results.mph
575b_stage575_dynamic_flow_shear_friction_checked.md
```
