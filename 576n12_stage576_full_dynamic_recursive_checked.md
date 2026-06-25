# Stage 576n12 full dynamic recursive coupling check

## Status

PASS

The full dynamic path reached fraction `1.0000` with recursive partitioned
coupling at:

```text
alpha_pfb576m = 0.20
beta_relax576m = 0.10
```

The difficult interval was continued with local half steps from fraction
`0.8950` through `0.9200`, followed by the original `T_slide572/200` step
size through fraction `1.0000`. Every accepted target was written to the
rolling checkpoint before the next target started.

## Final-state verification

The final checked model was reopened read-only and evaluated from the final
pressure, relaxed-field, and solid solutions (`sol4984`, `sol4985`, and
`sol4986`).

```text
F_contact              = 0.0253440421858 N
F_film                 = 2.91827459255e-08 N
F_total                = 0.0253440713686 N
F_feedback             = 9.16934276724e-06 N
field residual         = 9.16350621805e-06 N
min(theta)             = 0.999999999712
max(tff.p-p_amb573)    = 1.84885233076 Pa
min(pair gap variable) = -6.30397546955e-05 mm = -0.0630397546955 um
all values finite      = true
```

Acceptance checks:

```text
field residual < 1e-5 N                         PASS
0.025 N <= F_contact + F_film <= 0.035 N       PASS
theta >= 0                                      PASS
pressure, load, theta, and gap finite           PASS
full path reached fraction 1.0000               PASS
```

The near-zero final film load is the end-of-path state after the active
pressure region has unloaded. It does not mean that film load was zero during
the scrape. The model length unit is millimetres, so the raw gap value is about
`-0.063 um`, not `-63 um`. Its sign still follows the contact-pair convention,
but this is not an abnormally large penetration.

## Artifacts

```text
576n12_stage576_halfstep_iter60_resume_setup.mph
576n12_stage576_halfstep_iter60_resume_checkpoint.mph
576n12_stage576_halfstep_iter60_resume_results.mph
576n12_stage576_full_dynamic_recursive_checked.mph
verify_stage576n12_checked.java
verify_stage576n12_checked.log
```
