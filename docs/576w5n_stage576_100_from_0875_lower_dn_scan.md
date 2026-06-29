# 576w5n Stage 576 100% From 87.5% Lower-DN Scan

## Purpose

Extend the clean compensated diagnostic forward path from the checked `87.5%`
endpoint to `100%` without changing the physics frame.

Fixed modeling frame:

- `CORE_0100`
- `CAP_6P5KPA`
- `alpha_pfb = 0.15`
- `h_TFF = h_calc573`
- local `sel_film_swept571` TFF selection
- no friction, no roughness, no gain/cap tuning

## Input

- `576w5l_stage576_0875_from_08125_low_dn_scan_checked.mph`

Accepted `87.5%` input branch:

- RESET endpoint: pressure `sol551`, solid `sol552`
- REUSE endpoint: pressure `sol561`, solid `sol562`

## Output

- checked model:
  - `576w5n_stage576_100_from_0875_lower_dn_scan_checked.mph`
- builder:
  - `build_stage576w5n_100_from_0875_lower_dn_scan.java`
- summary:
  - `576w5n_stage576_100_from_0875_lower_dn_scan_summary.csv`
- verifier:
  - `verify_stage576w5n_results.py`
  - `576w5n_stage576_100_from_0875_lower_dn_scan_verify_summary.csv`

## Result

Verifier:

```text
VERIFY_STATUS=PASS
OVERALL_DIAGNOSIS=100_FROM_0875_LOWER_DN_SCAN_HAS_PASS_BOTH
```

Accepted branch:

```text
DN6 at 100%
RESET F_total_support = 0.0322300492460 N
REUSE F_total_support = 0.0321501581771 N
AvgH = 3 um
MinTheta = 1
TFF selection local = true
```

Nearby diagnostic behavior:

- `DN8` is marginal high:
  - RESET `F_total_support = 0.0331214073474 N`
  - REUSE `F_total_support = 0.0330321712836 N`
- `DN10` and higher are too high on the RESET side.
- The previous `w5m` low-DN scan was stopped after showing that
  `DN12-20.5` were above the load window at `100%`.

## Interpretation

`576w5n` completes the clean diagnostic forward path to `100%`. It should be
used as the latest forward endpoint for the next stage.

It is not a final mixed-lubrication model. The next stage is the backward
stroke from `100% -> 0%`, then fluid shear friction, then asperity/boundary
friction and parameter scans.
