# Latest Model Guide

This file identifies the latest local model state in this workspace.

Do not assume that "latest by timestamp" and "latest validated result" are the same thing.

## Short Answer

- Latest local model attempt by timestamp:
  - `576u2_stage576_recursive_split005_alpha020_results.mph`
- Latest local diagnostic note for that attempt:
  - `576u2_stage576_recursive_split005_alpha020_diagnostic.md`
- Latest local builder script for that attempt:
  - `build_stage576u2_recursive_split_segment_005_alpha020.java`

- Latest clearly validated checked milestone:
  - `576n12_stage576_full_dynamic_recursive_checked.mph`
- Latest checked milestone note:
  - `576n12_stage576_full_dynamic_recursive_checked.md`
- Latest checked milestone verifier:
  - `verify_stage576n12_checked.java`

## Recommended Interpretation

Use the following distinction when asking another model to guide work:

- Use `576u2...` when you want the newest experimental main-line attempt.
- Use `576n12...checked` when you want the newest explicitly verified and accepted result.

## Latest Experimental Main-Line Attempt

### Model

```text
576u2_stage576_recursive_split005_alpha020_results.mph
```

### Companion files

```text
576u2_stage576_recursive_split005_alpha020_diagnostic.md
build_stage576u2_recursive_split_segment_005_alpha020.java
576u2_stage576_recursive_split005_alpha020_setup.mph
576u2_stage576_recursive_split005_alpha020_checkpoint.mph
```

### What it is

This is the newest local Stage 576 branch attempt by file timestamp. It is a
split early-stroke recursive feedback test with:

```text
alpha_pfb576u2 = 0.20
beta_relax576u2 = 0.15
segments: 0 -> 2.5% -> 5%
```

### Current conclusion

This is not the best setting so far.

According to `576u2_stage576_recursive_split005_alpha020_diagnostic.md`:

- the split-segment method remains stable,
- but increasing `alpha` from `0.15` to `0.20` made the second segment worse,
- so `alpha=0.20` is not the preferred next direction on this branch.

Key reported values from the diagnostic note:

```text
segment 1 final Ftotal = 0.0487846 N
segment 2 final Ftotal = 0.0645431 N
```

The note explicitly says the best tested split-path setting so far remains:

```text
alpha = 0.15
beta = 0.15
segments = 0 -> 2.5% -> 5%
```

and recommends the next main-line test should reduce the relaxed-field update
aggressiveness rather than increase direct pressure feedback further.

## Latest Explicitly Verified Checked Result

### Model

```text
576n12_stage576_full_dynamic_recursive_checked.mph
```

### Companion files

```text
576n12_stage576_full_dynamic_recursive_checked.md
verify_stage576n12_checked.java
build_stage576m_recursive_field_relaxation.java
```

### What it is

This is the newest Stage 576 result in the repository that is both:

- marked as checked in the note, and
- paired with a dedicated verification script.

### Reported acceptance state

From `576n12_stage576_full_dynamic_recursive_checked.md`, the full dynamic path
reached fraction `1.0000` with:

```text
alpha_pfb576m = 0.20
beta_relax576m = 0.10
```

Reported final checks include:

```text
F_contact       = 0.0253440421858 N
F_film          = 2.91827459255e-08 N
F_total         = 0.0253440713686 N
field residual  = 9.16350621805e-06 N
min(theta)      = 0.999999999712
all values finite = true
```

and the note marks the acceptance checks as PASS.

## Which Model To Use For Guidance

If you want help deciding the next research or debugging step, tell the model:

- the latest experimental attempt is `576u2...`, but
- the latest verified checked baseline is `576n12...checked`.

That gives the model both:

- the newest unsuccessful or inconclusive branch state, and
- the newest trusted validated reference point.

## Recommended Prompt For Another ChatGPT/Codex Session

```text
Do not stop at repository metadata.

Use LATEST_MODEL.md as the entry point:
https://github.com/LYC-WASABI/cornea/blob/main/LATEST_MODEL.md

Then read these files:
1. CONTEXT.md
2. 576u2_stage576_recursive_split005_alpha020_diagnostic.md
3. build_stage576u2_recursive_split_segment_005_alpha020.java
4. 576n12_stage576_full_dynamic_recursive_checked.md
5. verify_stage576n12_checked.java

Interpret 576u2 as the latest experimental attempt and 576n12_checked as the
latest validated milestone.

After reading them, tell me:
1. what the current latest attempt is,
2. why it is not yet the preferred setting,
3. what the last validated reference state is,
4. what the next most defensible modeling step should be.
```
