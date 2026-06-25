# Stage 540: Local JFO and Joint Static Load Balance

## Parent

- `533_stage530_local_film_stationary_checked.mph`

## Rollback chain

1. `540_stage540_jfo_joint_input.mph`
2. `541_stage540_jfo_joint_static_setup.mph`
3. `542_stage540_jfo_joint_static_results.mph`
4. `543_stage540_jfo_joint_static_checked.mph`

## Setup

- Local TFF remains restricted to the imprinted cornea track.
- JFO/Elrod-Adams cavitation is enabled.
- The converged Stage 530 Reynolds solution initializes the JFO solve.
- Corrected full-width track uses uniform dynamic separation:
  `h_sep_uniform540 = 21 um`.
- Midpoint effective film thickness is approximately `24 um`
  (`h0_tear = 3 um` plus the Stage 540 separation calibration).
- Local JFO pressure is applied to the same cornea track.
- The global indentation variable adjusts solid contact load so that:

  `Wfilm540 + Fn_contact119 = 0.03 N`

## Verified result

- JFO film load: `0.02914331 N`.
- Solid contact load: `0.00088046 N`.
- Total normal load: `0.03002377 N`.
- Relative load error: `0.0792%`.
- Mean JFO fractional content: `0.999971`.
- Film shear force: `0.000397169 N`.
- Film-only friction coefficient: `0.0132285`.
- Calibrated inward indentation variable: `-0.0297191 mm` in the existing
  sign convention.

## Result groups

- Local JFO pressure.
- JFO fractional film content.
- Cornea contact pressure.
- Cornea and lid displacement.
- Cornea and lid von Mises stress.

## Remaining scope

Stage 540 is a converged midpoint static load-sharing state. It is not yet the
full `-35 deg` to `+35 deg` continuous transient scratch solution.
