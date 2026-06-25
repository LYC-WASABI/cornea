import fs from "node:fs";

const FONT = "Microsoft YaHei";
export const C = { white: "#FFFFFF", ink: "#1F2937", muted: "#6B7280", line: "#D1D5DB", pale: "#F7F9FC", blue: "#2563EB", bluePale: "#EAF2FF", green: "#0F766E", orange: "#C2410C" };
export function text(slide, value, x, y, w, h, opts = {}) {
  const s = slide.shapes.add({ geometry: "textbox", position: { left: x, top: y, width: w, height: h } });
  s.text.set(value);
  s.text.style = { typeface: FONT, fontSize: opts.size ?? 20, bold: opts.bold ?? false, color: opts.color ?? C.ink, alignment: opts.align ?? "left", lineSpacing: opts.leading ?? 1.12 };
  s.text.verticalAlignment = opts.valign ?? "middle";
  return s;
}
export function rule(slide, x, y, w, h = 1, fill = C.line) {
  return slide.shapes.add({ geometry: "rect", position: { left: x, top: y, width: w, height: h }, fill, line: { style: "solid", width: 0, fill } });
}
export function box(slide, x, y, w, h, fill = C.white, line = C.line) {
  return slide.shapes.add({ geometry: "rect", position: { left: x, top: y, width: w, height: h }, fill, line: { style: "solid", width: 1, fill: line } });
}
export function image(slide, path, x, y, w, h, alt) {
  const dataUrl = `data:image/png;base64,${fs.readFileSync(path).toString("base64")}`;
  return slide.images.add({ dataUrl, fit: "contain", position: { left: x, top: y, width: w, height: h }, alt });
}
export function title(slide, value, page) {
  text(slide, value, 58, 34, 1090, 48, { size: 30, bold: true });
  text(slide, `0${page}`, 1174, 40, 46, 24, { size: 15, bold: true, color: C.muted, align: "right" });
  rule(slide, 58, 94, 1162, 1);
}
export function footer(slide, page) {
  rule(slide, 58, 676, 1162, 1);
  text(slide, "Source: 148_lid8mm_stage62_h3um_full_partitioned_feedback_results.mph", 58, 684, 850, 18, { size: 10, color: C.muted });
  text(slide, `${page} / 3`, 1130, 684, 90, 18, { size: 10, color: C.muted, align: "right" });
}
export function label(slide, cn, variable, x, y, w) {
  text(slide, cn, x, y, w, 24, { size: 17, bold: true });
  text(slide, variable, x, y + 23, w, 20, { size: 13, color: C.muted });
}
export function bullet(slide, value, x, y, w) {
  slide.shapes.add({ geometry: "ellipse", position: { left: x, top: y + 10, width: 6, height: 6 }, fill: C.blue, line: { style: "solid", width: 0, fill: C.blue } });
  text(slide, value, x + 16, y, w - 16, 28, { size: 16 });
}
