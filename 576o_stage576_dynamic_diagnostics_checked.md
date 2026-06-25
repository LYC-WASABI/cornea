# Stage 576o dynamic diagnostics

## Status

```text
Numerical diagnostics: PASS
Load-band check: PASS at all retained states
Physical moving-pressure/cavitation gate: NOT YET PASSED
```

The diagnostics use only the `alpha_pfb576m=0.20` recursive branch. No
`alpha=0.15` results were mixed into the table.

## Retained states

The rolling solver intentionally removed most intermediate solutions. The
states still available for direct verification are:

```text
fraction = 0.10, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70,
           0.865, 0.895, 1.00
```

Therefore this is a sparse dynamic audit, not a reconstructed 200-point time
history.

## Main results

```text
F_total range at retained states = 0.0253441 to 0.0313970 N
maximum retained film load      = 0.00650252 N at fraction 0.865
maximum retained pressure       = 0.957417 MPa at fraction 0.50
minimum retained theta          = 0.999948925
maximum retained fluid shear    = 2.43062e-05 N
maximum retained mu_total       = 8.46582e-04
maximum contact traction        = about 0.387 MPa
minimum pair-gap variable       = about -6.30e-05 mm
                                = about -0.063 um
```

All retained total loads lie inside the current `0.025-0.035 N` acceptance
band. The feedback-active states at fractions `0.865`, `0.895`, and `1.0`
also satisfy the strict field residual limit of `1e-5 N`.

## Physical findings

1. The earlier interpretation of the raw pair-gap value as metres was wrong.
   The model length unit is millimetres, so the minimum is about `-0.063 um`,
   not `-63 um`. No abnormal large penetration is present.
2. Film loading is concentrated near the late scrape: `F_film=6.50 mN` at
   fraction `0.865`, `3.46 mN` at `0.895`, and nearly zero at the endpoint.
3. The retained early pressure fields have high local pressure but zero
   structural film load because the active load mask is not overlapping that
   pressure region at those snapshots.
4. `theta` remains above `0.99994`. This run contains essentially no
   macroscopic JFO cavitation despite solving the JFO variable.
5. Visual comparison shows the pressure hotspot remaining near the right side
   of the local patch from fractions `0.10` through `0.865`, with only late
   unloading/shift. The follow-up centroid probe confirms that this hotspot
   does not follow the moving scrape mask.

## Confirmed cause

The final model definitions are internally consistent:

```text
tau572      = t
M_core573   = M_lid572
M_drain573  = M_lid_x572*M_drain_a573
h_calc573   = Afilm573*h_wet573+(1-Afilm573)*h_background573
Qvent573    = -kvent573*(1-Afilm573)*(tff.p-p_amb573)
p_load573   = M_core573*Bfilm573*(tff.p-p_amb573)
TFF hw1     = h_calc573
```

However, the moving mask does not intersect the local TFF patch during most
of the early scrape:

```text
fraction     core area fraction     drain area fraction
0.10         0                      0
0.50         0                      0
0.70         0                      0
0.865        0.569                  0.830
0.895        0.723                  0.986
1.00         0.990                  0.995
```

At the same time, the analytical lid velocity remains applied across the
entire fixed TFF patch. The open region is only weakly vented with:

```text
kvent573   = 1e-7 kg/(m^2*s*Pa)
kanchor573 = 1e-7 kg/(m^2*s*Pa)
```

Consequently, the uniform `3 um` background film on the uncovered patch is
still driven by wall velocity. Its fixed gap geometry generates pressure even
when both `M_core573` and `M_drain573` are zero. The weak vent/anchor does not
clamp that pressure to ambient, so the hotspot remains tied to the fixed patch
geometry.

Pressure overlap confirms this:

```text
fraction     pressure in core     pressure in drain
0.10-0.70    0                    0
0.865        0.103                0.618
0.895        0.329                0.991
1.00         0.998                1.000
```

## Decision

Do not start Stage 577 strong coupling yet. The next model revision must:

1. use a swept corneal film domain that covers the complete scrape path;
2. gate the analytical lid wall velocity with the moving drain/core mask;
3. enforce ambient pressure outside the moving drain region more strongly;
4. rerun fixed-structure dynamic JFO before restoring recursive feedback.

The primary issue is patch coverage plus ungated wall velocity, not a frozen
time variable.

## Artifacts

```text
576o_stage576_dynamic_diagnostics.csv
576o_diag_f0100_*.png
576o_diag_f0500_*.png
576o_diag_f0700_*.png
576o_diag_f0865_*.png
576o_diag_f0895_*.png
576o_diag_f1000_*.png
build_stage576o_dynamic_diagnostics.java
build_stage576o_dynamic_diagnostics.log
```
