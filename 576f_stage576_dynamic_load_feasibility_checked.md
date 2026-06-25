# Stage 576f: Dynamic Film-Load Feasibility Scan

Status: **SCAN PASSED / 0.03 N LOAD CLOSURE NOT AVAILABLE**

## Purpose

Determine whether lid displacement alone can close the dynamic total load at scrape fraction `0.84`, while preserving the same transient JFO history from fraction `0.835`.

Every branch used:

```text
pressure-history source = sol248
structural source = sol249
velocity = 0.15 m/s
film-pressure structural feedback = off
contact friction = off
```

For each q, the structure was solved first and the TFF solution was then advanced over the same `0.835 -> 0.840` interval.

## Release-Direction Scan

| q | F_contact (N) | F_film (N) | F_total (N) |
|---:|---:|---:|---:|
| -9.00 | 0.02560 | 0.02692 | 0.05251 |
| -9.50 | 0.02448 | 0.03075 | 0.05523 |
| -10.00 | 0.02351 | 0.03444 | 0.05795 |
| -10.50 | 0.02255 | 0.03861 | 0.06116 |
| -10.75 | 0.02207 | 0.04038 | 0.06245 |

Releasing the lid reduces contact force but increases dynamic film load more strongly.

## Compression-Direction Scan

| q | F_contact (N) | F_film (N) | F_total (N) |
|---:|---:|---:|---:|
| -8.50 | 0.02658 | 0.02351 | 0.05009 |
| -8.00 | 0.02759 | 0.02163 | 0.04921 |
| -7.50 | 0.02859 | 0.02045 | 0.04904 |
| -7.00 | 0.02960 | 0.01749 | 0.04708 |
| -6.50 | 0.03060 | 0.01781 | 0.04841 |

Compressing the lid initially reduces film load, but contact force rises. The total-load curve reaches a minimum near `q=-7` and then rises again.

## Best Achievable Point

```text
q = -7.0
F_contact = 0.0295957 N
F_film = 0.0174862 N
F_total = 0.0470820 N
target = 0.0300000 N
excess = 0.0170820 N
```

All ten branches were numerically stable:

```text
theta remained positive
pressure remained finite
h_calc573 remained finite
```

## Conclusion

Within the tested displacement family:

```text
-10.75 <= q <= -6.5
```

there is no dynamic load-closure point at `0.15 m/s`. Tuning the q controller cannot solve this because q transfers load between contact and film while their sum remains above `0.047 N` near the optimum.

This conclusion applies to the current one-way structural state and current lubricant/gap model. Full film-pressure structural feedback has not yet been enabled and should not be used merely to force the answer toward `0.03 N`.

## Next Decision

Before Stage 577, perform a velocity feasibility scan while preserving the same pressure-history construction:

```text
v_blink_avg = 0.03, 0.05, 0.075, 0.10, 0.15 m/s
q near -9, -8, -7
```

The scan will determine whether `0.03 N` can be retained by lowering speed. If `0.15 m/s` is mandatory, the target load, viscosity, gap geometry, or lubricant model must change.
