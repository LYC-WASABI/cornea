# Next Tasks

## Current priority

Use the split-segment Stage 576 path as the active development branch, but keep
`576n12_stage576_full_dynamic_recursive_checked` as the validated reference.

## Recommended next modeling action

Do not push `alpha` above `0.20` on the current split setup.

Instead test:

- split path retained
- feedback strength returned to the better split-path regime
- more conservative relaxed-field update in segment 2

This recommendation comes from:

- `576u_stage576_recursive_split005_diagnostic.md`
- `576u2_stage576_recursive_split005_alpha020_diagnostic.md`

## Suggested reading before acting

1. `LATEST_MODEL.md`
2. `576u2_stage576_recursive_split005_alpha020_diagnostic.md`
3. `build_stage576u2_recursive_split_segment_005_alpha020.java`
4. `576n12_stage576_full_dynamic_recursive_checked.md`
5. `verify_stage576n12_checked.java`

## Suggested collaboration split

- Web ChatGPT:
  - explain tradeoffs
  - review stage logic
  - propose next-step prompts
- Codex:
  - update docs
  - inspect scripts
  - implement small repo changes
  - prepare concrete task prompts
