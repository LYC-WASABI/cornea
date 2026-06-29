import csv
import math

INPUT = "576w5d_stage576_075_pressure_reset_high_dn_scan_summary.csv"
OUTPUT = "576w5d_stage576_075_pressure_reset_high_dn_scan_verify_summary.csv"


def f(row, key):
    value = row.get(key, "")
    try:
        return float(value)
    except ValueError:
        return math.nan


def ok_bool(row, key):
    return row.get(key, "").strip().lower() == "true"


with open(INPUT, newline="") as handle:
    rows = list(csv.DictReader(handle))

verify_rows = []
passed_modes = set()
for row in rows:
    target = f(row, "target")
    if abs(target - 0.75) > 1e-12:
        continue
    solid_mode = row.get("solid_mode", "")
    total = f(row, "F_total_support")
    avg_h = f(row, "AvgH")
    min_theta = f(row, "MinTheta")
    low_theta = f(row, "LowThetaAreaRatio02")
    max_p = f(row, "MaxP_raw")
    tn_area = f(row, "TnAreaGt0p1MPa")
    tn_load = f(row, "TnLoadFracGt0p1MPa")
    dn_extra = f(row, "dn_extra_um")
    tff_local = ok_bool(row, "TffSelectionLocal")
    status = row.get("status", "")
    finite_core = all(math.isfinite(x) for x in [
        total, avg_h, min_theta, low_theta, max_p, tn_area, tn_load, dn_extra
    ])
    verified = (
        status == "PASS"
        and solid_mode in {"RESET", "REUSE"}
        and finite_core
        and 22.0 <= dn_extra <= 24.0
        and 0.030 <= total <= 0.033
        and 2.5e-6 <= avg_h <= 5.0e-6
        and min_theta > 0.9
        and low_theta < 1e-4
        and max_p < 1.0e6
        and tn_area <= 1e-5
        and tn_load <= 1e-3
        and tff_local
    )
    out = {
        "branch": row.get("branch", ""),
        "solid_mode": solid_mode,
        "target": row.get("target", ""),
        "dn_extra_um": row.get("dn_extra_um", ""),
        "F_contact": row.get("F_contact", ""),
        "F_film_support": row.get("F_film_support", ""),
        "F_total_support": row.get("F_total_support", ""),
        "AvgH": row.get("AvgH", ""),
        "MinTheta": row.get("MinTheta", ""),
        "MaxP_raw": row.get("MaxP_raw", ""),
        "TnAreaGt0p1MPa": row.get("TnAreaGt0p1MPa", ""),
        "TnLoadFracGt0p1MPa": row.get("TnLoadFracGt0p1MPa", ""),
        "TffSelectionLocal": row.get("TffSelectionLocal", ""),
        "source_status": status,
        "VERIFY_STATUS": "PASS" if verified else "FAIL",
    }
    verify_rows.append(out)
    if verified:
        passed_modes.add(solid_mode)

fieldnames = [
    "branch", "solid_mode", "target", "dn_extra_um", "F_contact",
    "F_film_support", "F_total_support", "AvgH", "MinTheta", "MaxP_raw",
    "TnAreaGt0p1MPa", "TnLoadFracGt0p1MPa", "TffSelectionLocal",
    "source_status", "VERIFY_STATUS",
]
with open(OUTPUT, "w", newline="") as handle:
    writer = csv.DictWriter(handle, fieldnames=fieldnames)
    writer.writeheader()
    writer.writerows(verify_rows)

if {"RESET", "REUSE"}.issubset(passed_modes):
    print("VERIFY_STATUS=PASS")
    print("OVERALL_DIAGNOSIS=075_PRESSURE_RESET_DN_SCAN_HAS_PASS_BOTH")
    for row in verify_rows:
        if row["VERIFY_STATUS"] == "PASS":
            print(
                "PASS_BRANCH={branch},MODE={solid_mode},F_TOTAL={F_total_support},DN={dn_extra_um}".format(
                    **row
                )
            )
else:
    print("VERIFY_STATUS=FAIL")
    print("OVERALL_DIAGNOSIS=075_PRESSURE_RESET_DN_SCAN_NOT_CONFIRMED_BOTH")
    raise SystemExit(2)





