# Error Log

This file is a stable entry point for recurring modeling issues.

## Known recent issue pattern

Early Stage 576 split-segment recursive runs remain stable, but load release in
the second early segment is still insufficient.

Evidence:

- `576u_stage576_recursive_split005_diagnostic.md`
- `576u2_stage576_recursive_split005_alpha020_diagnostic.md`

## Current conclusion

- stronger `alpha` was not automatically better
- `alpha = 0.20` worsened the second split segment relative to `alpha = 0.15`
- the next lever should be more conservative relaxed-field inheritance or lower
  relaxed-field aggressiveness, not simply stronger direct feedback

## Logging rule

Future failures should be recorded in this format:

1. failing stage or script
2. observable symptom
3. suspected cause
4. action taken
5. outcome
