# Stage 576m2: Strict Recursive Field Relaxation at Alpha 0.20

Status: **PASS**

## Configuration

```text
base dynamic model = 576l_stage576_full_dynamic_feedback_alpha015_checked.mph
scrape fraction = 0.855
JFO history restart = fraction 0.850, sol649
initial structural state = converged alpha 0.15 peak state
alpha_pfb576m = 0.20
beta_relax576m = 0.10
contact friction = off
```

## Recursive Field State

A Boundary ODEs and DAEs interface stores the dimensionless surface field:

```text
rrel576m(x,y)
p_relaxed576m = p_scale576m*rrel576m
```

The initial ODE solution was solved exactly from the verified alpha-0.15 peak
pressure field. Every subsequent iteration initialized the ODE from the previous
relaxed solution and advanced it toward the current alpha-0.20 JFO pressure:

```text
previous relaxed field
-> current JFO target field
-> recursive Boundary ODE update
-> nonlinear structural/contact solve
-> updated gap
-> next JFO solve
```

This is a stored field degree of freedom, not an average of two raw JFO pressure
solutions.

## Results

The first run reached the original increment criteria after 15 iterations. A
strict continuation added the absolute surface-field residual criterion and
converged after 3 additional iterations.

```text
cumulative partitioned iterations = 18
F_contact = 0.0249006761 N
F_film = 0.00681301545 N
F_feedback_relaxed = 0.00135370303 N
F_total = 0.0317136915 N
absolute field residual integral = 9.38926e-6 N
minimum theta = 0.999955006
maximum film pressure = 0.461625 MPa
minimum reported gap = -6.29731e-5 m
```

Final iteration changes:

```text
abs(delta F_contact) = 4.04e-7 N
abs(delta F_film) = 4.42e-6 N
abs(delta F_feedback) = 9.89e-7 N
abs(delta F_total) = 4.82e-6 N
abs(delta min gap) = 4.41e-13 m
```

Acceptance:

```text
all force increments < 1e-5 N: PASS
absolute relaxed-field residual integral < 1e-5 N: PASS
gap increment < 0.01 um: PASS
F_total within 0.025-0.035 N: PASS
pressure, theta, and gap finite: PASS
```

The strict checked model is:

```text
576m2_stage576_recursive_field_relaxation_strict_checked.mph
```

## Interpretation

At the peak scrape position, alpha 0.20 is now a converged two-way partitioned
iterative coupling result. The JFO and structural/contact subproblems remain
separate, but they exchange pressure and gap repeatedly until the stored relaxed
surface field and physical response converge.

This is not yet a monolithic solve and only verifies the peak position. It does
not establish alpha-0.20 convergence over the complete scrape.

## Next Step

Use the recursive Boundary ODE state in a complete alpha-0.20 dynamic scrape.
Each physical time step must contain an inner JFO/relaxation/structure iteration
with the relaxed field carried from the previous physical time step. Contact
friction remains off until that complete scrape passes.
