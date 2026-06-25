# Stage 574n: Quasistatic Indentation Recalibration

## Purpose

Redo the structure-only lid-wiper indentation from a clean local-contact setup and select a structural state with

```text
Fn_contact570 ~= 0.03 N
```

This replaces the earlier frozen-contact preload state for the next fixed-structure JFO checks.

## Base Model

```text
574g_stage574_local_contact_gap_setup.mph
```

TFF/JFO was not enabled in this step. This is a pure structural contact recalibration.

## Output Files

```text
574n_stage574_quasistatic_indent_recalibration_setup.mph
574n_stage574_quasistatic_indent_recalibration_results.mph
574n_stage574_quasistatic_indent_contact_003N_checked.mph
574n_stage574_quasistatic_indent_contact_003N_checked.md
```

## Scan Summary

| q_scale574 | displacement | Fn_contact570 [N] | min gap | max solid.Tn [Pa] | patch Tn integral [N] | active area | solution |
|---:|---:|---:|---:|---:|---:|---:|---|
| -20.0 | -0.0355126979888 | 0.519905170631 | -0.000302602084123 | 1859725.56719 | 0.0582839780702 | 9.89477551588e-07 | sol96 |
| -15.0 | -0.0266345234916 | 0.0143962008944 | -6.01761340467e-05 | 369829.227533 | 0.0145238195002 | 1.59613459716e-06 | sol97 |
| -10.0 | -0.0177563489944 | 0.0235093160496 | -6.27776472116e-05 | 385817.552796 | 0.0237582546669 | 3.53874062244e-06 | sol98 |
| -7.5 | -0.0133172617458 | 0.0285909645369 | -6.37673186858e-05 | 391899.855068 | 0.0289170793772 | 4.04360431392e-06 | sol99 |
| -5.0 | -0.00887817449721 | 0.0335865388897 | -6.49737277612e-05 | 399314.178761 | 0.0340066593123 | 4.28157218320e-06 | sol100 |
| -3.0 | -0.00532690469833 | 0.0375880684794 | -6.59612461691e-05 | 405383.248763 | 0.0380997583372 | 4.54438782002e-06 | sol101 |
| -2.0 | -0.00355126979888 | 0.0395782891462 | -6.64181609717e-05 | 408191.346212 | 0.0401414213464 | 4.65106822708e-06 | sol102 |
| -1.5 | -0.00266345234916 | 0.0409516603080 | -6.66478862341e-05 | 409603.186931 | 0.0402556752105 | 4.71470167512e-06 | sol103 |
| -1.0 | -0.00177563489944 | 0.0412936409592 | -6.68713365554e-05 | 410976.463247 | 0.0410143889913 | 4.75238223557e-06 | sol104 |
| -0.5 | -0.000887817449721 | 0.0423175709142 | -6.70974263892e-05 | 412365.961425 | 0.0420362955921 | 4.75737268940e-06 | sol105 |
| 0.0 | 0.0 | 0.0434031688800 | -6.73365675777e-05 | 413835.670345 | 0.0428841610667 | 4.78702780214e-06 | sol106 |
| 0.25 | 0.000443908724860 | 0.0439210510392 | -6.74489709086e-05 | 414526.476388 | 0.0433990943742 | 4.81233313374e-06 | sol107 |
| 0.5 | 0.000887817449721 | 0.0444384026210 | -6.75612267078e-05 | 415216.375734 | 0.0439134060484 | 4.81233313374e-06 | sol108 |
| 0.75 | 0.00133172617458 | 0.0449549889455 | -6.76732912844e-05 | 415905.099868 | 0.0444269404882 | 4.84085230260e-06 | sol109 |
| 1.0 | 0.00177563489944 | 0.0454714576720 | -6.77852303454e-05 | 416593.052610 | 0.0449403831709 | 4.90598384354e-06 | sol110 |

## Selected State

The selected structural state is:

```text
q_scale574 = -7.5
solution = sol99
Fn_contact570 = 0.0285909645369 N
patch Tn integral = 0.0289170793772 N
min gap = -6.37673186858e-05
max solid.Tn = 391899.855068 Pa
active area = 4.04360431392e-06
```

This brackets the target because:

```text
q_scale574 = -7.5 -> Fn_contact570 = 0.02859 N
q_scale574 = -5.0 -> Fn_contact570 = 0.03359 N
```

## Check Result

```text
PASS
```

The selected contact force is close enough to the 0.03 N target for the next fixed-structure JFO step. The model is still structure-only: no membrane pressure feedback, no TFF load balance, and no dynamic fluid-structure coupling were evaluated here.

## Next Step

Use this checked structure state as the frozen structure baseline for the next fixed-structure true-gap JFO rerun. The next JFO check should not reuse the old 574j frozen structure, because that state retained about 0.043 N structural contact before fluid pressure was added.
