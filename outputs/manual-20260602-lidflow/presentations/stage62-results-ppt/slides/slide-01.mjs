import { C, text, rule, image, title, footer, bullet, box } from "./common.mjs";
const A = "C:/Users/l1363/Documents/复现/outputs/manual-20260602-lidflow/presentations/stage62-results-ppt/assets/";
export async function slide01(presentation) {
  const slide = presentation.slides.add(); slide.background.fill = C.white;
  title(slide, "角膜-眼睑动态混合润滑刮擦模型", 1);
  text(slide, "模型视图：角膜与 lid wiper 位移", 70, 120, 590, 28, { size: 20, bold: true });
  text(slide, "COMSOL Results: pg_solid_disp", 70, 148, 590, 20, { size: 13, color: C.muted });
  box(slide, 70, 180, 650, 420, C.white, C.line);
  image(slide, A + "01_model_displacement.png", 84, 194, 622, 392, "COMSOL cornea and lid wiper displacement result");

  text(slide, "模型设置", 780, 122, 300, 30, { size: 21, bold: true });
  const rows = [["角膜", "3D 实体球冠"], ["Lid wiper", "柔性弧形实体"], ["接触面尺寸", "弧长 8 mm，宽 1 mm"], ["角膜后表面", "15 mmHg + 弹性基础"], ["运动范围", "-35° → +35°"], ["动态时长", "0.53 s"], ["目标法向载荷", "0.030 N"], ["基准液膜厚度", "h₀,tear = 3 μm"]];
  let y = 172;
  for (const [k, v] of rows) {
    rule(slide, 780, y + 36, 420, 1);
    text(slide, k, 790, y, 165, 34, { size: 15, color: C.muted });
    text(slide, v, 970, y, 220, 34, { size: 16, bold: true });
    y += 45;
  }
  bullet(slide, "刮擦头作为完整柔体绕角膜球心连续转动", 780, 558, 420);
  bullet(slide, "液膜与角膜接触共同承担法向载荷", 780, 590, 420);
  footer(slide, 1); return slide;
}
