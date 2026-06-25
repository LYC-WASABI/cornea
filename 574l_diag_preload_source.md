# Stage 574l preload source diagnostic

## Source

- Base model: `574l_stage574_structure_release_scan_results.mph`
- Diagnostic model: `574l_diag_preload_source.mph`
- Diagnostic script: `diagnose_stage574l_preload_source.java`

## Contact Pair

```text
cp_lid_cornea source = [42, 44, 47, 48]
cp_lid_cornea destination = [1, 3, 6, 7, 8, 9, 10, 11, 12, 13, 15, 16, 18, 19, 20, 21, 22, 23]
current local patch = [10, 16]
```

## Active Solid Features Relevant To Preload

```text
dcnt1: Pair contact, active on destination + source contact boundaries
dgcnt1: General Contact 1 exists
dcont1: Continuity on [6,7,10,11,12,15,16,18,19,20]
fix_limbus: fixed constraint on [2]
press_iop: IOP load on posterior boundaries [4,5,14,17]
ef_posterior: posterior elastic foundation on [4,5,14,17]
disp_lid_time: prescribed displacement on lid/support boundaries [36,37,38,39,43,46,49,50,51]
```

`disp_lid_time` is not directly applied to the current contact patch `[10,16]`; it moves the lid/support boundary. That explains why changing `q_scale574` only weakly changes the local contact force.

## Load Breakdown

At `q_scale574 = 0`:

```text
Fn_contact570 = 0.0427832217750 N
local patch [10,16] Tn integral = 0.0429176698145 N
```

Boundary split:

```text
boundary 10: 0.0244336680104 N
boundary 16: 0.0184840018041 N
sum [10,16] = 0.0429176698145 N
```

At `q_scale574 = -0.4`:

```text
Fn_contact570 = 0.0419802265603 N
local patch [10,16] Tn integral = 0.0421071325015 N
```

Boundary split:

```text
boundary 10: 0.0239652537449 N
boundary 16: 0.0181418787566 N
sum [10,16] = 0.0421071325015 N
```

## Interpretation

The `~0.042 N` structural preload is real contact pressure on the corrected local patch `[10,16]`. It is not coming from the old noncontact patch, and it is not a postprocessing artifact in `Fn_contact570`.

The weak sensitivity to `q_scale574` means the additional radial displacement term is not the main control for this preload. The current load is dominated by the inherited base geometry/contact state and the way the lid/support displacement transmits to the contact pair.

## Consequence

Continuing to push `q_scale574` more negative is not the right next move. The model needs a new preload-control parameter that changes the base lid/contact geometry more directly, or a force-control/static equilibrium branch.

## Recommended Next Step

Create a new structural setup branch:

```text
Stage 574m: structural preload recalibration
```

Options:

1. Add a direct normal release/offset parameter on the lid/contact body reference position, not only the support-boundary `disp_lid_time`.
2. Or switch to a force-control/static equilibrium target:

```text
Fn_contact570 ~= 0.03 N
```

Then re-run fixed-structure TFF only after the structural contact force is genuinely near the desired preload.
