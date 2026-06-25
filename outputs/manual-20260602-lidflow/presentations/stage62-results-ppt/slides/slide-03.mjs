import { C, text, image, title, footer, label, box, rule } from "./common.mjs";
const A = "C:/Users/l1363/Documents/复现/outputs/manual-20260602-lidflow/presentations/stage62-results-ppt/assets/";
export async function slide03(presentation) {
  const slide = presentation.slides.add(); slide.background.fill = C.white;
  title(slide, "完整膜压反馈收敛，并保持约 0.03 N 总法向载荷", 3);
  label(slide, "液膜与角膜接触的载荷共享", "Normal-load sharing over t_replay", 70, 118, 540);
  box(slide, 70, 168, 540, 310, C.white, C.line);
  image(slide, A + "05_load_sharing.png", 82, 180, 516, 286, "COMSOL load sharing curve");
  label(slide, "摩擦力与表观摩擦系数", "Friction force and apparent coefficient", 670, 118, 540);
  box(slide, 670, 168, 540, 310, C.white, C.line);
  image(slide, A + "06_friction.png", 682, 180, 516, 286, "COMSOL friction curve");

  rule(slide, 70, 522, 1140, 1, C.line);
  text(slide, "液膜承载峰值", 88, 546, 170, 22, { size: 14, color: C.muted });
  text(slide, "0.030704 N", 88, 574, 190, 30, { size: 23, bold: true, color: C.blue });
  text(slide, "液膜剪切力峰值", 340, 546, 170, 22, { size: 14, color: C.muted });
  text(slide, "0.002178 N", 340, 574, 190, 30, { size: 23, bold: true, color: C.green });
  text(slide, "总摩擦力峰值", 590, 546, 170, 22, { size: 14, color: C.muted });
  text(slide, "0.002330 N", 590, 574, 190, 30, { size: 23, bold: true, color: C.orange });
  text(slide, "表观摩擦系数", 840, 546, 170, 22, { size: 14, color: C.muted });
  text(slide, "0.0200 – 0.07767", 840, 574, 250, 30, { size: 22, bold: true, color: C.blue });
  text(slide, "总法向载荷范围：0.030000 – 0.030704 N；峰值相对目标偏差约 2.35%。", 88, 626, 950, 24, { size: 15 });
  footer(slide, 3); return slide;
}
