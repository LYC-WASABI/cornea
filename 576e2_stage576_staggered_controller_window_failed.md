# Stage 576e2: History-Preserving Staggered Load Controller

Status: **NUMERICALLY STABLE / LOAD CONTROL FAILED**

No checked model was saved.

## Method

The monolithic transient structure-JFO-controller solve was singular at the initial time. Stage 576e2 therefore used a history-preserving staggered method:

```text
1. Advance TFF one transient step from the previous pfilm/theta solution.
2. Read the latest stationary structural state as the nonsolved gap state.
3. Evaluate F_contact + F_film.
4. Change q_scale574 by at most 0.25.
5. Re-solve stationary contact and continue the next TFF time step.
```

Window:

```text
scrape fraction = 0.805 to 0.860
time step = T_slide572/200
film-pressure structural feedback = off
contact friction = off
```

## Results

| fraction | q | F_contact (N) | F_film (N) | F_total (N) |
|---:|---:|---:|---:|---:|
| 0.810 | -8.75 | 0.02583 | 0.00075 | 0.02657 |
| 0.815 | -8.50 | 0.02658 | 0.00121 | 0.02779 |
| 0.820 | -8.50 | 0.02658 | 0.00258 | 0.02916 |
| 0.825 | -8.50 | 0.02658 | 0.00349 | 0.03007 |
| 0.830 | -8.75 | 0.02583 | 0.00556 | 0.03138 |
| 0.835 | -9.00 | 0.02535 | 0.00867 | 0.03401 |
| 0.840 | -9.25 | 0.02492 | 0.02692 | 0.05184 |
| 0.845 | -9.50 | 0.02448 | 0.02858 | 0.05305 |
| 0.850 | -9.75 | 0.02399 | 0.03213 | 0.05612 |
| 0.855 | -10.00 | 0.02351 | 0.03429 | 0.05780 |
| 0.860 | -10.25 | 0.02303 | 0.04201 | 0.06503 |

All JFO values remained finite:

```text
min(theta) >= 0.99978
max pressure decreased from 2.24 MPa to 1.74 MPa
```

Five of eleven points were outside the `0.025-0.035 N` acceptance band.

## Interpretation

The controller worked before film-load onset. From fraction 0.84 onward, the transient film load rose faster than contact unloading could compensate.

At fraction 0.86:

```text
F_film = 0.04201 N > target total load 0.03 N
```

Therefore no nonnegative contact force can close the total load at that state. Continuing to release the lid using only the contact-force slope is not a valid controller law; the film load has a strong, nonmonotonic dependence on gap and pressure history.

## Decision

Do not enable direct film-pressure structural feedback yet.

The next step is a history-preserving feasibility scan near fractions `0.84-0.86`. It must determine the minimum achievable dynamic film load versus lid displacement before another controller is designed:

```text
q = -9.0, -9.5, -10.0, -10.5, -10.75
```

If the minimum stable `F_film` remains above `0.03 N`, the requested combination of `0.15 m/s`, current geometry/gap, lubricant properties, and `0.03 N` total load is physically incompatible in that interval. The model inputs must then be revisited rather than forcing the controller to manufacture load closure.
