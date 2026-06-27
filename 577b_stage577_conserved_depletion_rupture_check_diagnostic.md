# Stage 577b conserved 3 um depletion / rupture activation check

## Input

- Base model: `577a_stage577_conserved_3um_local_tff_check_results.mph`
- Output model: `577b_stage577_conserved_depletion_rupture_check_results.mph`
- Script: `build_stage577b_conserved_depletion_rupture_check.java`
- Solution: `sol274`
- Dataset: `dset577b`
- TFF region: `sel_film_swept571`

## Definition

This stage is a postprocessing activation check on the already passed 577a TFF solution. It does not feed the depleted film thickness back into the TFF PDE.

```text
h0_577b = 3 um
h_min577b = 0.05 um
h_cut577b = 1 um
h_eps577b = 0.2 um
dh_deplete577b = 2.8 um
shell577b = max(M_drain573 - M_core573, 0)
pileup_gain577b = 2.18
h_TFF577b = max(0.05 um, 3 um - 2.8 um*M_core573 + 2.18*2.8 um*shell577b)
w_close577b = 0.5*(1+tanh((1 um - h_TFF577b)/0.2 um))
```

`h_TFF577b` and `w_close577b` were evaluated as inline expressions because newly added COMSOL variables were not reliably visible to saved-solution numerical features.

## Results

```text
TIME_RANGE=[0.0100000000000,0.137059969545] COUNT=41
H_AVG_RANGE=[2.93624816900e-06,3.09774303352e-06]
H_MIN_RANGE=[0.000200000000000,0.00139742790382]
H_MAX_RANGE=[0.00655173927304,0.00910400000000]
A_CLOSE_RANGE=[1.93616419977e-12,8.36458790333e-06]
THETA_MIN_RANGE=[0.100145674579,0.999997361935]
P_MAX_RANGE=[1.89748329375e-10,137336.440741]
FT_SIGNED_RANGE=[-6.23313681838e-05,6.23313681838e-05]
MU_TFF_ALT_RANGE=[0.00000000000,0.00207771227279]
DH_DEPLETE_SCAN_UM=0.5 H_AVG_RANGE=[2.98861574446e-06,3.01745411313e-06] H_MIN_RANGE=[0.00250000000000,0.00271382641140] H_MAX_RANGE=[0.00363423915590,0.00409000000000] A_CLOSE_RANGE=[1.78154399569e-13,2.65331541498e-12]
DH_DEPLETE_SCAN_UM=1.0 H_AVG_RANGE=[2.97723148893e-06,3.03490822626e-06] H_MIN_RANGE=[0.00200000000000,0.00242765282279] H_MAX_RANGE=[0.00426847831180,0.00518000000000] A_CLOSE_RANGE=[1.81846939420e-13,3.65672442800e-10]
DH_DEPLETE_SCAN_UM=2.0 H_AVG_RANGE=[2.95446297786e-06,3.06981645251e-06] H_MIN_RANGE=[0.00100000000000,0.00185530564559] H_MAX_RANGE=[0.00553695662360,0.00736000000000] A_CLOSE_RANGE=[2.90343479773e-13,4.00402592873e-06]
DH_DEPLETE_SCAN_UM=2.8 H_AVG_RANGE=[2.93624816900e-06,3.09774303352e-06] H_MIN_RANGE=[0.000200000000000,0.00139742790382] H_MAX_RANGE=[0.00655173927304,0.00910400000000] A_CLOSE_RANGE=[1.93616419977e-12,8.36458790333e-06]
CHECK_FINITE=true
CHECK_LOCAL_TFF=true
CHECK_H_CONSERVATION=true
CHECK_H_FLOOR=true
CHECK_CLOSE_NONTRIVIAL=true
CHECK_THETA_FINITE=true
CHECK_PRESSURE_FINITE=true
CHECK_TAU_FINITE=true
CHECKED_STATUS=PASS
```

Note: COMSOL `MinSurface` / `MaxSurface` reported `H_MIN_RANGE` and `H_MAX_RANGE` in its display unit convention, while the conservation acceptance used `H_AVG_RANGE` in SI units.

## Conclusion

`577b` is checked as `PASS` for a postprocessed low-film / rupture activation diagnostic:

- Area-average film thickness stays close to the intended 3 um baseline.
- The low-film activation area is nonzero and not global.
- The depletion scan shows weak activation at 0.5-1.0 um, clear local activation at 2.0 um, and the strongest checked activation at 2.8 um.
- `theta`, `pfilm`, `Ft_TFF_signed`, and `mu_TFF_alt` remain finite.
- The inherited fluid shear force still reverses sign during reciprocating motion.
