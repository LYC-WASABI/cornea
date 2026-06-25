import { C, image, title, footer, label, box } from "./common.mjs";
const A = "C:/Users/l1363/Documents/复现/outputs/manual-20260602-lidflow/presentations/stage62-results-ppt/assets/";
function panel(slide, x, y, w, h, cn, variable, file) {
  label(slide, cn, variable, x, y, w);
  box(slide, x, y + 48, w, h - 48, C.white, C.line);
  image(slide, A + file, x + 8, y + 56, w - 16, h - 64, cn);
}
export async function slide02(presentation) {
  const slide = presentation.slides.add(); slide.background.fill = C.white;
  title(slide, "局部接触区的膜厚、gap 与压力分布", 2);
  panel(slide, 68, 120, 550, 250, "角膜前表面接触压力", "Contact pressure: solid.Tn", "02_contact_pressure.png");
  panel(slide, 662, 120, 550, 250, "液膜压力", "Tear-film pressure: pfilm", "04_film_pressure.png");
  panel(slide, 68, 390, 550, 250, "液膜厚度", "Film thickness: h_film_input", "03_film_thickness.png");
  panel(slide, 662, 390, 550, 250, "几何间隙", "Gap distance: gap_replay_tear", "07_gap.png");
  footer(slide, 2); return slide;
}
