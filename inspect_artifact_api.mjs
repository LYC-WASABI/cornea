import { Presentation } from "file:///C:/Users/l1363/.cache/codex-runtimes/codex-primary-runtime/dependencies/node/node_modules/@oai/artifact-tool/dist/artifact_tool.mjs";
const p = Presentation.create({ slideSize: { width: 1280, height: 720 } });
const s = p.slides.add();
function keys(o) {
  return [...new Set([...Object.keys(o), ...Object.getOwnPropertyNames(Object.getPrototypeOf(o) || {})])].sort();
}
console.log("slide", keys(s));
console.log("shapes", keys(s.shapes));
console.log("images", s.images ? keys(s.images) : "NO_IMAGES");
console.log("images.add", String(s.images.add));
