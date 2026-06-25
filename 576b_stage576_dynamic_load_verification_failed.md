# Stage 576b: Dynamic Load Verification

Base:

```text
576a_stage576_load_controller_closure_checked.mph
```

Purpose:

Check whether the Stage 576a load-closed point remains load-closed during the full dynamic scraping/JFO process.

## Output Files

```text
build_stage576b_dynamic_load_verification.java
576b_stage576_dynamic_load_verification_setup.mph
576b_stage576_dynamic_load_verification_results.mph
576b_stage576_dynamic_load_verification_failed.md
```

No `576b checked` model was saved.

## Settings

```text
q_scale574 = -10
alpha_pfb576a = 0.183
tau572 = t
lambda_h574 = 1
lambda_v574 = 1
solid = off
tff = on
fixed structural solution = sol201
dynamic tff solution = sol236
```

The structure was held fixed at the Stage 576a selected load-closure state. The contact load was therefore evaluated as the constant structural value from `sol201`, while the film load was evaluated dynamically from `p_load573(t)`.

## Dynamic Results

| quantity | value |
|---|---:|
| time range | 0.0100000000000 to 0.0735299847726 s |
| steps | 201 |
| F_contact constant | 0.0222318631842 N |
| F_film min/max/mean | 0 / 0.0166499703093 / 0.00171138731421 N |
| F_total min/max/mean | 0.0222318631842 / 0.0388818334935 / 0.0239432504984 N |
| F_total outside 0.025-0.035 N | 179 steps |
| max pressure range | 7.67845364930 to 4021635.45277 Pa |
| min theta range | 0.999661558983 to 0.999999999812 |
| mean M_core range | 0 to 0.989606838032 |

## Check

```text
NUMERIC_STATUS = PASS
LOAD_BAND_STATUS = FAIL
CHECKED_STATUS = FAIL
```

## Interpretation

The dynamic JFO solve is numerically stable:

- `pfilm`, `theta`, and load integrals remained finite.
- `min(theta)` stayed positive.
- The moving mask covered the local patch during part of the motion.

But the fixed `q=-10` load closure is only valid near the selected Stage 576a active time. During most of the dynamic stroke, the total load is below the target band because the film load is small or zero when the moving core is not strongly over the local patch.

Therefore this result should be used as a diagnostic dynamic run, not as a checked dynamic load-control base.

## Decision

Do not proceed directly to Stage 577 full two-way coupling from this dynamic result.

The next correction should be **Stage 576c: dynamic constant-load controller**, where `q_scale574(t)` or an equivalent normal displacement degree of freedom is adjusted over time to enforce:

```text
F_contact(t) + F_film(t) ~= 0.03 N
```

The current fixed value:

```text
q_scale574 = -10
```

is a valid single-time closure but not a valid full-stroke constant-load condition.
