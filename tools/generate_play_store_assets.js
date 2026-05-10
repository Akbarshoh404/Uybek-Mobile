const fs = require("node:fs");
const path = require("node:path");
const sharp = require("sharp");

const outRoot = path.join(process.cwd(), "play-store-assets");
const colors = {
  brand: "#23445D",
  brandDark: "#163042",
  brandLight: "#E9F2F7",
  accentGold: "#D6A24C",
  accentSky: "#A9C9DD",
  accentRose: "#E7B2A8",
  bgLight: "#F6F4EF",
  bgSurface: "#FFFCF8",
  bgDark: "#12181D",
  bgDarkCard: "#1F2B35",
  text: "#1E2933",
  textSoft: "#60717F",
  white: "#FFFFFF"
};

function ensureDir(dir) {
  fs.mkdirSync(dir, { recursive: true });
}

async function renderSvg(svg, outPath, width, height) {
  await sharp(Buffer.from(svg))
    .resize(width, height)
    .png()
    .toFile(outPath);
}

function softShadow() {
  return `
    <filter id="softShadow" x="-20%" y="-20%" width="140%" height="160%">
      <feDropShadow dx="0" dy="18" stdDeviation="22" flood-color="#0d1821" flood-opacity="0.18"/>
    </filter>
  `;
}

function logoMarkSvg(size = 1024) {
  const s = size;
  return `
  <svg width="${s}" height="${s}" viewBox="0 0 ${s} ${s}" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="${colors.brandDark}"/>
        <stop offset="70%" stop-color="${colors.brand}"/>
        <stop offset="100%" stop-color="${colors.accentSky}"/>
      </linearGradient>
      <linearGradient id="roof" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="${colors.accentGold}"/>
        <stop offset="100%" stop-color="${colors.accentRose}"/>
      </linearGradient>
      ${softShadow()}
    </defs>
    <rect width="${s}" height="${s}" rx="${Math.round(s * 0.24)}" fill="url(#bg)"/>
    <circle cx="${s * 0.74}" cy="${s * 0.24}" r="${s * 0.07}" fill="${colors.white}" opacity="0.12"/>
    <g filter="url(#softShadow)" transform="translate(${s * 0.18},${s * 0.17})">
      <rect x="${s * 0.12}" y="${s * 0.28}" width="${s * 0.40}" height="${s * 0.33}" rx="${s * 0.08}" fill="${colors.white}" opacity="0.96"/>
      <path d="M ${s * 0.08} ${s * 0.38} L ${s * 0.32} ${s * 0.16} L ${s * 0.56} ${s * 0.38}" fill="none" stroke="url(#roof)" stroke-width="${s * 0.06}" stroke-linecap="round" stroke-linejoin="round"/>
      <rect x="${s * 0.24}" y="${s * 0.41}" width="${s * 0.16}" height="${s * 0.20}" rx="${s * 0.06}" fill="${colors.brand}"/>
      <path d="M ${s * 0.72} ${s * 0.26}
               C ${s * 0.58} ${s * 0.26}, ${s * 0.50} ${s * 0.36}, ${s * 0.50} ${s * 0.49}
               C ${s * 0.50} ${s * 0.66}, ${s * 0.64} ${s * 0.79}, ${s * 0.72} ${s * 0.87}
               C ${s * 0.80} ${s * 0.79}, ${s * 0.94} ${s * 0.66}, ${s * 0.94} ${s * 0.49}
               C ${s * 0.94} ${s * 0.36}, ${s * 0.86} ${s * 0.26}, ${s * 0.72} ${s * 0.26} Z"
            fill="${colors.white}" opacity="0.95"/>
      <circle cx="${s * 0.72}" cy="${s * 0.48}" r="${s * 0.10}" fill="${colors.brandLight}"/>
      <path d="M ${s * 0.72} ${s * 0.43} L ${s * 0.77} ${s * 0.50} L ${s * 0.72} ${s * 0.57} L ${s * 0.67} ${s * 0.50} Z" fill="${colors.accentGold}"/>
    </g>
  </svg>`;
}

function logoLockupSvg() {
  return `
  <svg width="1600" height="640" viewBox="0 0 1600 640" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <linearGradient id="panel" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="${colors.bgLight}"/>
        <stop offset="100%" stop-color="${colors.bgSurface}"/>
      </linearGradient>
      ${softShadow()}
    </defs>
    <rect width="1600" height="640" rx="56" fill="url(#panel)"/>
    <g transform="translate(72,72)">
      ${logoMarkSvg(496).replace("<svg width=\"496\" height=\"496\" viewBox=\"0 0 496 496\" xmlns=\"http://www.w3.org/2000/svg\">", "").replace("</svg>", "")}
    </g>
    <text x="650" y="270" font-size="170" font-family="Arial, sans-serif" font-weight="800" fill="${colors.brandDark}">Uybek</text>
    <text x="654" y="350" font-size="54" font-family="Arial, sans-serif" font-weight="600" fill="${colors.textSoft}">Real estate marketplace for Uzbekistan</text>
    <text x="654" y="430" font-size="42" font-family="Arial, sans-serif" fill="${colors.textSoft}">Search. List. Connect.</text>
  </svg>`;
}

function featureGraphicSvg() {
  return `
  <svg width="1024" height="500" viewBox="0 0 1024 500" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="${colors.brandDark}"/>
        <stop offset="55%" stop-color="${colors.brand}"/>
        <stop offset="100%" stop-color="${colors.accentSky}"/>
      </linearGradient>
      <linearGradient id="phoneGradient" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="${colors.bgDark}"/>
        <stop offset="100%" stop-color="${colors.bgDarkCard}"/>
      </linearGradient>
      ${softShadow()}
    </defs>
    <rect width="1024" height="500" rx="0" fill="url(#bg)"/>
    <circle cx="930" cy="82" r="62" fill="${colors.white}" opacity="0.08"/>
    <circle cx="120" cy="430" r="88" fill="${colors.white}" opacity="0.05"/>

    <g transform="translate(66,70)">
      <rect x="0" y="0" width="460" height="360" rx="34" fill="${colors.white}" opacity="0.11"/>
      <text x="34" y="84" font-size="82" font-family="Arial, sans-serif" font-weight="800" fill="${colors.white}">Uybek</text>
      <text x="34" y="142" font-size="30" font-family="Arial, sans-serif" fill="${colors.white}" opacity="0.84">Smart real estate marketplace</text>
      <text x="34" y="212" font-size="26" font-family="Arial, sans-serif" fill="${colors.white}">Discover homes with fast filters</text>
      <text x="34" y="252" font-size="26" font-family="Arial, sans-serif" fill="${colors.white}">Post listings in minutes</text>
      <text x="34" y="292" font-size="26" font-family="Arial, sans-serif" fill="${colors.white}">Chat directly with sellers</text>
      <g transform="translate(34,320)">
        <rect width="136" height="16" rx="8" fill="${colors.accentGold}"/>
        <rect x="152" width="102" height="16" rx="8" fill="${colors.white}" opacity="0.35"/>
        <rect x="270" width="118" height="16" rx="8" fill="${colors.white}" opacity="0.2"/>
      </g>
    </g>

    <g filter="url(#softShadow)" transform="translate(664,42)">
      <rect x="0" y="0" width="248" height="416" rx="34" fill="url(#phoneGradient)"/>
      <rect x="14" y="14" width="220" height="388" rx="28" fill="${colors.bgDark}"/>
      <rect x="88" y="26" width="72" height="8" rx="4" fill="${colors.white}" opacity="0.18"/>
      <rect x="30" y="52" width="160" height="22" rx="11" fill="${colors.accentSky}" opacity="0.18"/>
      <rect x="30" y="86" width="188" height="56" rx="22" fill="${colors.bgDarkCard}"/>
      <rect x="46" y="102" width="90" height="12" rx="6" fill="${colors.accentSky}" opacity="0.9"/>
      <rect x="144" y="102" width="52" height="12" rx="6" fill="${colors.white}" opacity="0.2"/>
      <rect x="30" y="158" width="188" height="110" rx="24" fill="${colors.white}" opacity="0.96"/>
      <rect x="42" y="170" width="164" height="62" rx="18" fill="${colors.brandLight}"/>
      <rect x="42" y="244" width="90" height="10" rx="5" fill="${colors.brandDark}"/>
      <rect x="42" y="260" width="122" height="8" rx="4" fill="${colors.textSoft}" opacity="0.7"/>
      <rect x="30" y="284" width="188" height="110" rx="24" fill="${colors.white}" opacity="0.96"/>
      <rect x="42" y="296" width="164" height="62" rx="18" fill="${colors.accentSky}" opacity="0.32"/>
      <rect x="42" y="370" width="82" height="10" rx="5" fill="${colors.brandDark}"/>
      <rect x="42" y="386" width="136" height="8" rx="4" fill="${colors.textSoft}" opacity="0.7"/>
    </g>
  </svg>`;
}

function phoneShell(inner) {
  return `
    <g filter="url(#softShadow)" transform="translate(140,120)">
      <rect x="0" y="0" width="800" height="1680" rx="80" fill="${colors.brandDark}"/>
      <rect x="26" y="26" width="748" height="1628" rx="60" fill="${colors.bgLight}"/>
      <rect x="314" y="52" width="172" height="18" rx="9" fill="${colors.brandDark}" opacity="0.18"/>
      ${inner}
    </g>`;
}

function screenshotSvg(title, subtitle, body) {
  return `
  <svg width="1080" height="1920" viewBox="0 0 1080 1920" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <linearGradient id="screenBg" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="${colors.bgLight}"/>
        <stop offset="100%" stop-color="${colors.bgSurface}"/>
      </linearGradient>
      <linearGradient id="panelBg" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="${colors.brandDark}"/>
        <stop offset="100%" stop-color="${colors.brand}"/>
      </linearGradient>
      ${softShadow()}
    </defs>
    <rect width="1080" height="1920" fill="url(#screenBg)"/>
    <text x="78" y="120" font-size="76" font-family="Arial, sans-serif" font-weight="800" fill="${colors.brandDark}">${title}</text>
    <text x="78" y="176" font-size="34" font-family="Arial, sans-serif" fill="${colors.textSoft}">${subtitle}</text>
    <g transform="translate(0,0)">
      ${phoneShell(body)}
    </g>
  </svg>`;
}

function homeBody() {
  return `
    <rect x="60" y="120" width="628" height="34" rx="17" fill="${colors.brandDark}" opacity="0.85"/>
    <rect x="60" y="190" width="628" height="80" rx="32" fill="${colors.white}"/>
    <rect x="88" y="220" width="200" height="18" rx="9" fill="${colors.accentSky}"/>
    <rect x="544" y="206" width="96" height="48" rx="18" fill="${colors.brandLight}"/>
    <rect x="60" y="300" width="628" height="220" rx="42" fill="url(#panelBg)"/>
    <rect x="94" y="344" width="180" height="16" rx="8" fill="${colors.white}" opacity="0.42"/>
    <rect x="94" y="384" width="346" height="26" rx="13" fill="${colors.white}"/>
    <rect x="94" y="426" width="240" height="18" rx="9" fill="${colors.white}" opacity="0.74"/>
    <rect x="60" y="564" width="150" height="46" rx="23" fill="${colors.brand}"/>
    <rect x="226" y="564" width="166" height="46" rx="23" fill="${colors.white}"/>
    <rect x="408" y="564" width="154" height="46" rx="23" fill="${colors.white}"/>
    ${listingCard(60, 650, true)}
    ${listingCard(60, 1034, false)}
    ${bottomNav()}
  `;
}

function searchBody() {
  return `
    <rect x="60" y="120" width="110" height="52" rx="26" fill="${colors.brandLight}"/>
    <rect x="190" y="120" width="498" height="52" rx="26" fill="${colors.white}"/>
    <rect x="222" y="138" width="188" height="16" rx="8" fill="${colors.textSoft}" opacity="0.8"/>
    <rect x="60" y="220" width="156" height="44" rx="22" fill="${colors.brand}"/>
    <rect x="232" y="220" width="164" height="44" rx="22" fill="${colors.white}"/>
    <rect x="412" y="220" width="152" height="44" rx="22" fill="${colors.white}"/>
    <rect x="60" y="306" width="628" height="86" rx="28" fill="${colors.white}"/>
    <rect x="90" y="334" width="120" height="12" rx="6" fill="${colors.textSoft}"/>
    <rect x="560" y="328" width="98" height="20" rx="10" fill="${colors.brandLight}"/>
    ${gridCard(60, 432)}
    ${gridCard(382, 432)}
    ${gridCard(60, 796)}
    ${gridCard(382, 796)}
    ${bottomNav()}
  `;
}

function sellerBody() {
  return `
    <rect x="60" y="120" width="628" height="300" rx="46" fill="url(#panelBg)"/>
    <circle cx="180" cy="236" r="66" fill="${colors.white}" opacity="0.16"/>
    <rect x="284" y="188" width="212" height="24" rx="12" fill="${colors.white}"/>
    <rect x="284" y="230" width="148" height="18" rx="9" fill="${colors.white}" opacity="0.78"/>
    <rect x="94" y="318" width="182" height="48" rx="24" fill="${colors.white}" opacity="0.14"/>
    <rect x="294" y="318" width="182" height="48" rx="24" fill="${colors.white}" opacity="0.14"/>
    <rect x="60" y="460" width="628" height="90" rx="30" fill="${colors.white}"/>
    <rect x="90" y="492" width="240" height="16" rx="8" fill="${colors.brandDark}"/>
    <rect x="90" y="516" width="164" height="10" rx="5" fill="${colors.textSoft}"/>
    <rect x="560" y="484" width="98" height="34" rx="17" fill="${colors.brandLight}"/>
    ${listingCard(60, 590, true)}
    ${bottomNav()}
  `;
}

function chatBody() {
  return `
    <rect x="60" y="120" width="340" height="24" rx="12" fill="${colors.brandDark}"/>
    <rect x="60" y="162" width="408" height="16" rx="8" fill="${colors.textSoft}" opacity="0.8"/>
    ${chatBubble(80, 270, 270, colors.white, colors.text)}
    ${chatBubble(360, 390, 270, colors.accentSky, colors.brandDark)}
    ${chatBubble(80, 520, 330, colors.white, colors.text)}
    ${chatBubble(312, 668, 318, colors.accentGold, colors.brandDark)}
    <rect x="60" y="1450" width="490" height="78" rx="30" fill="${colors.white}"/>
    <rect x="92" y="1480" width="220" height="16" rx="8" fill="${colors.textSoft}" opacity="0.7"/>
    <rect x="574" y="1458" width="114" height="78" rx="28" fill="${colors.brand}"/>
    ${bottomNav()}
  `;
}

function listingCard(x, y, bright) {
  const hero = bright ? colors.brandLight : colors.accentSky;
  return `
    <rect x="${x}" y="${y}" width="628" height="340" rx="38" fill="${colors.white}"/>
    <rect x="${x + 20}" y="${y + 20}" width="588" height="190" rx="30" fill="${hero}"/>
    <rect x="${x + 34}" y="${y + 230}" width="190" height="16" rx="8" fill="${colors.brandDark}"/>
    <rect x="${x + 34}" y="${y + 258}" width="128" height="12" rx="6" fill="${colors.textSoft}"/>
    <rect x="${x + 34}" y="${y + 290}" width="144" height="18" rx="9" fill="${colors.accentGold}"/>
  `;
}

function gridCard(x, y) {
  return `
    <rect x="${x}" y="${y}" width="306" height="320" rx="34" fill="${colors.white}"/>
    <rect x="${x + 18}" y="${y + 18}" width="270" height="200" rx="24" fill="${colors.brandLight}"/>
    <rect x="${x + 30}" y="${y + 238}" width="136" height="14" rx="7" fill="${colors.brandDark}"/>
    <rect x="${x + 30}" y="${y + 266}" width="102" height="10" rx="5" fill="${colors.textSoft}"/>
    <rect x="${x + 30}" y="${y + 286}" width="126" height="14" rx="7" fill="${colors.accentGold}"/>
  `;
}

function chatBubble(x, y, w, fill, textFill) {
  return `
    <rect x="${x}" y="${y}" width="${w}" height="88" rx="28" fill="${fill}"/>
    <rect x="${x + 24}" y="${y + 24}" width="${w - 70}" height="14" rx="7" fill="${textFill}" opacity="0.85"/>
    <rect x="${x + 24}" y="${y + 48}" width="${w - 110}" height="12" rx="6" fill="${textFill}" opacity="0.55"/>
  `;
}

function bottomNav() {
  return `
    <rect x="80" y="1544" width="588" height="82" rx="30" fill="${colors.bgDarkCard}" opacity="0.94"/>
    <circle cx="154" cy="1586" r="12" fill="${colors.accentSky}"/>
    <circle cx="286" cy="1586" r="12" fill="${colors.white}" opacity="0.22"/>
    <rect x="388" y="1562" width="56" height="48" rx="16" fill="${colors.accentSky}"/>
    <circle cx="548" cy="1586" r="12" fill="${colors.white}" opacity="0.22"/>
    <circle cx="622" cy="1586" r="12" fill="${colors.white}" opacity="0.22"/>
  `;
}

async function main() {
  const logoDir = path.join(outRoot, "logo");
  const featureDir = path.join(outRoot, "feature");
  const screenshotDir = path.join(outRoot, "screenshots");
  ensureDir(logoDir);
  ensureDir(featureDir);
  ensureDir(screenshotDir);

  fs.writeFileSync(path.join(logoDir, "uybek-logo-mark.svg"), logoMarkSvg(1024));
  fs.writeFileSync(path.join(logoDir, "uybek-logo-lockup.svg"), logoLockupSvg());
  fs.writeFileSync(path.join(featureDir, "uybek-feature-graphic.svg"), featureGraphicSvg());
  fs.writeFileSync(path.join(screenshotDir, "01-home-feed.svg"), screenshotSvg("Find homes faster", "Clean feed with smart highlights", homeBody()));
  fs.writeFileSync(path.join(screenshotDir, "02-smart-search.svg"), screenshotSvg("Filter with confidence", "Fast search and clear results", searchBody()));
  fs.writeFileSync(path.join(screenshotDir, "03-seller-profile.svg"), screenshotSvg("Know the seller", "Profiles and listings in one place", sellerBody()));
  fs.writeFileSync(path.join(screenshotDir, "04-chat-directly.svg"), screenshotSvg("Chat instantly", "Speak with sellers in the app", chatBody()));

  await renderSvg(logoMarkSvg(1024), path.join(logoDir, "uybek-logo-mark-1024.png"), 1024, 1024);
  await renderSvg(logoMarkSvg(1024), path.join(logoDir, "uybek-logo-mark-512.png"), 512, 512);
  await renderSvg(logoLockupSvg(), path.join(logoDir, "uybek-logo-lockup.png"), 1600, 640);
  await renderSvg(featureGraphicSvg(), path.join(featureDir, "uybek-feature-graphic.png"), 1024, 500);
  await renderSvg(screenshotSvg("Find homes faster", "Clean feed with smart highlights", homeBody()), path.join(screenshotDir, "01-home-feed.png"), 1080, 1920);
  await renderSvg(screenshotSvg("Filter with confidence", "Fast search and clear results", searchBody()), path.join(screenshotDir, "02-smart-search.png"), 1080, 1920);
  await renderSvg(screenshotSvg("Know the seller", "Profiles and listings in one place", sellerBody()), path.join(screenshotDir, "03-seller-profile.png"), 1080, 1920);
  await renderSvg(screenshotSvg("Chat instantly", "Speak with sellers in the app", chatBody()), path.join(screenshotDir, "04-chat-directly.png"), 1080, 1920);

  console.log(`Created assets in ${outRoot}`);
}

main().catch((error) => {
  console.error(error);
  process.exit(1);
});
