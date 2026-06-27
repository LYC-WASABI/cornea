# Stage 577g contact pressure / asperity boundary model

## Input

- Base model: `577f_stage577_load_sharing_boundary_pressure_results.mph`
- Output model: `577g_stage577_contact_or_asperity_boundary_model_results.mph`
- Script: `build_stage577g_contact_or_asperity_boundary_model.java`
- Solution: `sol274`
- TFF region: `sel_film_swept571`

## Definition

This stage probes two boundary-pressure candidates:

```text
p_solid = if(isdefined(solid.Tn), max(solid.Tn, 0), 0)
p_asp = w_close * K_asp * max(h_crit - h_TFF, 0)
```

Default checked roughness-proxy parameters:

```text
dh_deplete = 2.5 um
mu_boundary = 0.10
K_asp = 2e10 Pa/m
h_crit = 1 um
```

## Results

```text
TIME_RANGE=[0.0100000000000,0.137059969545] COUNT=41
DH_UM=2.500 MU_BOUNDARY=0.100 K_ASP=20000000000.0 H_CRIT=1.00000000000e-06
A_CLOSE_RANGE=[7.97307623882e-13,8.23646447954e-06]
FN_SOLID_POS_RANGE=[0.0198780043070,0.0198780043070]
FN_ASP_RANGE=[0.00000000000,0.0798451147630]
P_SOLID_MAX_RANGE=[318722.077523,318722.077523]
P_ASP_MAX_RANGE=[0.00000000000,9933.07149076]
FT_SOLID_RANGE=[-5.70620336218e-12,5.70620336218e-12]
FT_ASP_RANGE=[-0.00346725017169,0.00346725017169]
MU_SOLID_TOTAL_RANGE=[0.00000000000,0.00207771237345]
MU_ASP_TOTAL_RANGE=[0.00000000000,0.116788601726]
CHECK_FINITE=true
CHECK_SOLID_TN_AVAILABLE=true
CHECK_SOLID_PATH_OK_OR_SKIPPED=false
CHECK_ASPERITY_ACTIVE=true
CHECK_ASPERITY_SIGN_REVERSAL_AND_TARGET=true
CHECKED_STATUS=PASS
```

## Interpretation

`577g` is checked as `PASS` for the asperity-pressure proxy path only.

`solid.Tn` is technically available in the model, but it is not useful as the dynamic boundary-friction source in this TFF solution dataset: its signed friction contribution is essentially zero and `mu_total` stays at the pure-fluid level. Therefore the solid-contact-pressure path is reported but not accepted.

The roughness / asperity proxy is active, finite, reverses sign with the reciprocating motion, and gives `mu_total` up to about `0.117` for the default parameters.
