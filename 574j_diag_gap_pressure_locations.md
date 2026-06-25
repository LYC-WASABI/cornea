# Stage 574j gap and pressure location diagnostic

## Source

- Base model: `574j_stage574_fixed_structure_true_gap_jfo_checked.mph`
- Diagnostic model: `574j_diag_gap_pressure_locations.mph`
- Solution: `sol129`
- Local patch: `[10, 16]`
- Diagnostic script: `diagnose_stage574j_gap_pressure_locations.java`

## Patch Bounds

```text
area = 8.00043122095e-6 m^2
X range = -3.82698652421 .. 3.82698799381
Y range = -4.87400299960 .. -3.47854835684
Z range = 5.26582471772 .. 6.66285485320
```

## Extrema

```text
h_calc573 = 7.22195276179e-05 .. 0.0984386991268 m
geomgap_dst_cp_lid_cornea raw = -0.000148469298164 .. Infinity m
geomgap_dst_cp_lid_cornea finite abs<1m max = 0.498458803839 m
tff.p-p_amb573 = 0 .. 3804935.27322 Pa
max(p_load573) = 127908.550372 Pa
tff.theta = 0.654308488917 .. 1
Bfilm573 = 0 .. 1
```

## Near-Extremum Regions

### h_calc573 > 1 mm

```text
area = 0
status = empty or nonfinite region
```

This means the reported `max(h_calc573)=0.0984 m` is a point/measure-zero mapped-gap outlier rather than a finite-area region in the surface integration.

### h_calc573 > 50 um

```text
area = 6.62327707078e-07 m^2
centroid X,Y,Z = (0.000216852918154, -0.00408319201695, 0.00623602959323)
mean theta_surface572 = -0.581085926709 rad
mean geomgap_dst_cp_lid_cornea = NaN
mean h_calc573 = 7.10032864724e-05 m
mean Bfilm573 = 1
mean Afilm573 = 1
mean pressure = 244.973262966 Pa
mean p_load573 = 244.973262966 Pa
mean theta = 0.999998301435
```

This large-thickness region has very low pressure and low physical load. It is not the source of the pressure peak.

### finite max geomgap region

```text
criterion = finite gap near max >= 90%
area = 0
status = empty or nonfinite region
```

The finite maximum gap is also effectively point-like. The raw pair gap also contains `Infinity`, confirming invalid pair-map values exist on the local patch.

### max pressure region

```text
criterion = pressure near max >= 90%
area = 0
status = empty or nonfinite region
```

The absolute maximum pressure is point-like or too localized for this threshold integration.

### max p_load573 region

```text
area = 2.22592438207e-08 m^2
centroid X,Y,Z = (0.00253065338008, -0.00444770076416, 0.00588671941439)
mean theta_surface572 = -0.645620737544 rad
mean geomgap_dst_cp_lid_cornea = NaN
mean h_calc573 = 2.21887551618e-07 m
mean Bfilm573 = 1
mean Afilm573 = 1
mean pressure = 124301.205915 Pa
mean p_load573 = 124301.205915 Pa
mean theta = 1
```

The physical load peak is in a very small finite area, with `h_calc573` about `0.222 um`, full film mask, and full theta. This is the effective load-bearing micro-region.

### min theta region

```text
criterion = near min
area = 0
status = empty or nonfinite region
```

The minimum theta value is localized, not a broad cavitation region.

## Interpretation

- The `h_calc573` large maximum and raw `geomgap` Infinity are pair-map/gap-field outliers.
- The finite `h_calc573 > 50 um` region carries almost no pressure: mean pressure is only about `245 Pa`.
- The physical load peak is not located in the large-gap region. It is located in a small, thin-film region with `h_calc573 ~ 0.222 um`, `Bfilm573=1`, and `theta=1`.
- The current 574j result is usable as a fixed-structure true-gap gate, but before feedback/dynamic use the gap field should be regularized so invalid/large mapped gaps are treated as open non-load-bearing regions.

## Recommended 574k Fix

Add an upper active-gap gate before feedback:

```text
h_active_max573 = 30[um] or 50[um]
dh_active573 = 5[um]
B_high573 = 0.5*(1-tanh((g_pair_safe573-h_active_max573)/dh_active573))
Bfilm573 = g_pair_valid573*B_low573*B_high573
```

Also cap or sanitize the calculation gap:

```text
g_pair_physical573 = min(g_pair_safe573,h_active_max573)
h_wet573 = smoothmax(g_pair_physical573,h_num573)
```

This should make large-gap and invalid pair-map regions behave as open/vented non-load-bearing zones instead of remaining in the effective film mask.
