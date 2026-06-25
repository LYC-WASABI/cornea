# Stage 574o: Fixed-Structure True-Gap JFO from 0.03 N Indentation

## Purpose

Rerun fixed-structure JFO using the recalibrated quasistatic indentation state:

```text
base model = 574n_stage574_quasistatic_indent_contact_003N_checked.mph
initial structural solution = sol99
q_scale574 = -7.5
Fn_contact570 = 0.0285909645369 N
```

This stage enables true gap, rupture, and venting in the TFF pressure solve, but keeps structure frozen and keeps membrane pressure feedback off.

## Files

```text
build_stage574o_true_gap_jfo_from_003N_indent.java
574o_stage574_fixed_structure_true_gap_from_003N_setup.mph
574o_stage574_fixed_structure_true_gap_from_003N_results.mph
574o_stage574_fixed_structure_true_gap_from_003N_checked.mph
574o_stage574_fixed_structure_true_gap_from_003N_checked.md
```

## Continuation Path

The direct path

```text
constant 3 um full velocity -> true gap
```

failed at `lambda_h574 = 0.05` because the constant 3 um full-film state still over-carried load.

The accepted path was:

```text
1. lambda_v574 = 0, ramp lambda_h574 to 1
2. lambda_h574 = 1, ramp lambda_v574 to 1
```

Velocity steps were refined and Newton damping was reduced to improve continuation stability.

## Final Result

Final full-speed true-gap state:

```text
lambda_v574 = 1
lambda_h574 = 1
positive pressure integral = 0.238758721770 N
physical membrane load intop_film(p_load573) = 0.0216536416567 N
max(tff.p-p_amb573) = 2.83872041831 MPa
min(tff.p-p_amb573) = 0 Pa
mean(Bfilm573) = 0.372839530206
mean(Afilm573) = 0.372839530206
mean(tff.theta) = 0.992365477467
min(tff.theta) = 0.661437038421
gap coverage on local patch = 0.995986159605
```

The checked model was saved:

```text
574o_stage574_fixed_structure_true_gap_from_003N_checked.mph
```

## Load Interpretation

The frozen structural contact baseline is:

```text
Fn_contact570 = 0.0285909645369 N
```

The final true-gap fluid pressure adds:

```text
F_film = 0.0216536416567 N
```

So the fixed-structure apparent total is approximately:

```text
F_contact + F_film = 0.0502446061936 N
```

This is not yet a load-balanced result. It means that after adding fluid pressure, the structure would need to release indentation or deform under pressure feedback to return the total normal support toward the intended 0.03 N external load.

## Check Result

```text
PASS
```

Acceptance basis:

- final full-speed true-gap state converged;
- local gap coverage remained above 0.95;
- pressure, film fraction, and theta stayed finite;
- physical membrane load stayed below the previous constant-film overbearing regime;
- checked model was saved.

## Next Step

Use this result to run a load-closure/release correction:

```text
target: F_contact + F_film ~= 0.03 N
starting from: 574o_stage574_fixed_structure_true_gap_from_003N_checked.mph
```

This should adjust the structural indentation or introduce one-way pressure feedback, rather than manually judging the frozen-structure pressure field as final.
