from __future__ import annotations

from pathlib import Path
from typing import Iterable

from PIL import Image, ImageChops, ImageColor, ImageDraw, ImageEnhance, ImageFilter, ImageFont, ImageOps


ROOT = Path(__file__).resolve().parents[1]
OUT_ROOT = ROOT / "play-store-assets"
LOGO_DIR = OUT_ROOT / "logo"
FEATURE_DIR = OUT_ROOT / "feature"
SCREENSHOT_DIR = OUT_ROOT / "screenshots"

COLORS = {
    "brand": "#23445D",
    "brand_dark": "#163042",
    "brand_light": "#E9F2F7",
    "accent_gold": "#D6A24C",
    "accent_sky": "#A9C9DD",
    "accent_rose": "#E7B2A8",
    "bg_light": "#F6F4EF",
    "bg_surface": "#FFFCF8",
    "bg_dark": "#12181D",
    "bg_dark_card": "#1F2B35",
    "text": "#1E2933",
    "text_soft": "#60717F",
    "text_dark": "#F5F1EA",
    "white": "#FFFFFF",
}

FONT_REGULAR = "C:/Windows/Fonts/segoeui.ttf"
FONT_SEMIBOLD = "C:/Windows/Fonts/segoeuib.ttf"
FONT_DISPLAY = "C:/Windows/Fonts/bahnschrift.ttf"


def rgba(value: str, alpha: int = 255) -> tuple[int, int, int, int]:
    r, g, b = ImageColor.getrgb(value)
    return r, g, b, alpha


def font(path: str, size: int) -> ImageFont.FreeTypeFont:
    return ImageFont.truetype(path, size=size)


def ensure_dirs() -> None:
    for directory in (LOGO_DIR, FEATURE_DIR, SCREENSHOT_DIR):
        directory.mkdir(parents=True, exist_ok=True)


def diagonal_gradient(size: tuple[int, int], start: str, end: str, accent: str | None = None) -> Image.Image:
    width, height = size
    base = Image.new("RGBA", size, rgba(start))
    overlay = Image.new("RGBA", size, rgba(end))
    mask = Image.linear_gradient("L").rotate(45, expand=True)
    left = (mask.width - width) // 2
    top = (mask.height - height) // 2
    mask = mask.crop((left, top, left + width, top + height))
    result = Image.composite(overlay, base, mask)

    if accent is not None:
        accent_img = Image.new("RGBA", size, rgba(accent, 180))
        accent_mask = Image.linear_gradient("L").rotate(-45, expand=True)
        left = (accent_mask.width - width) // 2
        top = (accent_mask.height - height) // 2
        accent_mask = accent_mask.crop((left, top, left + width, top + height))
        result = Image.alpha_composite(result, ImageChops.multiply(accent_img, Image.merge("RGBA", (*accent_img.split()[:3], accent_mask))))

    return result


def shadow_layer(size: tuple[int, int], box: tuple[int, int, int, int], radius: int, fill: tuple[int, int, int, int], blur: int) -> Image.Image:
    layer = Image.new("RGBA", size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(layer)
    draw.rounded_rectangle(box, radius=radius, fill=fill)
    return layer.filter(ImageFilter.GaussianBlur(blur))


def add_blur_glow(canvas: Image.Image, box: tuple[int, int, int, int], color: str, blur: int, alpha: int) -> None:
    layer = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    draw = ImageDraw.Draw(layer)
    draw.ellipse(box, fill=rgba(color, alpha))
    canvas.alpha_composite(layer.filter(ImageFilter.GaussianBlur(blur)))


def rounded_image(image: Image.Image, size: tuple[int, int], radius: int) -> Image.Image:
    resized = image.resize(size, Image.Resampling.LANCZOS).convert("RGBA")
    mask = Image.new("L", size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, size[0], size[1]), radius=radius, fill=255)
    result = Image.new("RGBA", size, (0, 0, 0, 0))
    result.paste(resized, mask=mask)
    return result


def screenshot_card(source: Path, width: int = 620) -> Image.Image:
    with Image.open(source) as raw:
        screenshot = raw.convert("RGBA")
    height = round(width * screenshot.height / screenshot.width)
    screenshot = screenshot.resize((width, height), Image.Resampling.LANCZOS)
    screenshot = ImageEnhance.Contrast(screenshot).enhance(1.03)
    screenshot = ImageEnhance.Color(screenshot).enhance(1.02)
    frame_pad = 14
    card = Image.new("RGBA", (width + frame_pad * 2, height + frame_pad * 2), (0, 0, 0, 0))
    frame = Image.new("RGBA", card.size, rgba(COLORS["white"], 245))
    mask = Image.new("L", card.size, 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, card.width, card.height), radius=66, fill=255)
    card.paste(frame, mask=mask)
    card.paste(rounded_image(screenshot, (width, height), 56), (frame_pad, frame_pad), rounded_image(screenshot, (width, height), 56))
    return card


def draw_text_block(draw: ImageDraw.ImageDraw, xy: tuple[int, int], title: str, subtitle: str, title_fill: str, subtitle_fill: str) -> None:
    title_font = font(FONT_DISPLAY, 86)
    subtitle_font = font(FONT_REGULAR, 34)
    x, y = xy
    draw.text((x, y), title, font=title_font, fill=rgba(title_fill))
    title_box = draw.multiline_textbbox((x, y), title, font=title_font, spacing=0)
    draw.multiline_text((x, title_box[3] + 18), subtitle, font=subtitle_font, fill=rgba(subtitle_fill), spacing=8)


def pill(draw: ImageDraw.ImageDraw, box: tuple[int, int, int, int], text: str, fill: str, text_fill: str, font_path: str = FONT_SEMIBOLD, font_size: int = 24) -> None:
    draw.rounded_rectangle(box, radius=(box[3] - box[1]) // 2, fill=rgba(fill))
    fnt = font(font_path, font_size)
    text_box = draw.textbbox((0, 0), text, font=fnt)
    text_x = box[0] + (box[2] - box[0] - (text_box[2] - text_box[0])) / 2
    text_y = box[1] + (box[3] - box[1] - (text_box[3] - text_box[1])) / 2 - 2
    draw.text((text_x, text_y), text, font=fnt, fill=rgba(text_fill))


def add_feature_tags(draw: ImageDraw.ImageDraw, y: int, labels: Iterable[str]) -> None:
    tag_font = font(FONT_SEMIBOLD, 22)
    x = 80
    for label in labels:
        text_box = draw.textbbox((0, 0), label, font=tag_font)
        width = max(172, text_box[2] - text_box[0] + 56)
        box = (x, y, x + width, y + 54)
        pill(draw, box, label, COLORS["white"], COLORS["brand_dark"], font_size=22)
        x += width + 16


def build_store_screenshot(source: Path, title: str, subtitle: str, tag: str, out_path: Path, dark: bool = False) -> None:
    size = (1080, 1920)
    bg = diagonal_gradient(
        size,
        COLORS["brand_dark"] if dark else COLORS["bg_light"],
        COLORS["brand"] if dark else COLORS["brand_light"],
        COLORS["accent_sky"] if dark else COLORS["bg_surface"],
    )
    add_blur_glow(bg, (-120, 1180, 420, 1720), COLORS["accent_gold"], 80, 54)
    add_blur_glow(bg, (740, 120, 1130, 510), COLORS["accent_sky"], 64, 70)
    add_blur_glow(bg, (650, 1220, 1120, 1790), COLORS["white"], 96, 60 if dark else 42)

    draw = ImageDraw.Draw(bg)
    pill(
        draw,
        (80, 76, 244, 130),
        "Uybek",
        COLORS["brand_dark"] if not dark else COLORS["white"],
        COLORS["white"] if not dark else COLORS["brand_dark"],
        font_size=24,
    )
    draw_text_block(
        draw,
        (80, 172),
        title,
        subtitle,
        COLORS["text_dark"] if dark else COLORS["text"],
        COLORS["brand_light"] if dark else COLORS["text_soft"],
    )

    card = screenshot_card(source)
    card_x = (size[0] - card.width) // 2
    card_y = 452
    card_box = (card_x, card_y, card_x + card.width, card_y + card.height)

    accent_layer = Image.new("RGBA", size, (0, 0, 0, 0))
    accent_draw = ImageDraw.Draw(accent_layer)
    accent_draw.rounded_rectangle(
        (card_box[0] - 26, card_box[1] + 42, card_box[2] - 8, card_box[3] - 36),
        radius=86,
        fill=rgba(COLORS["brand"], 230 if dark else 216),
    )
    accent_draw.rounded_rectangle(
        (card_box[0] - 62, card_box[1] + 90, card_box[2] - 44, card_box[3] + 12),
        radius=86,
        fill=rgba(COLORS["white"], 36 if dark else 80),
    )
    accent_layer = accent_layer.rotate(-4 if dark else 4, resample=Image.Resampling.BICUBIC, center=(540, 1120))
    bg.alpha_composite(accent_layer)

    bg.alpha_composite(shadow_layer(size, card_box, 64, (10, 18, 24, 120), 36))
    bg.alpha_composite(card, (card_x, card_y))

    draw = ImageDraw.Draw(bg)
    pill(draw, (760, 332, 958, 386), tag, COLORS["accent_gold"], COLORS["brand_dark"], font_size=22)
    add_feature_tags(
        draw,
        340,
        ("Smart search", "Local sellers", "Fast filters") if not dark else ("Dark mode", "Direct chat", "Fresh listings"),
    )

    bg.save(out_path)


def draw_logo_mark(size: int) -> Image.Image:
    canvas = diagonal_gradient((size, size), COLORS["brand_dark"], COLORS["brand"], COLORS["accent_sky"])
    add_blur_glow(canvas, (size * 0.62, size * 0.10, size * 0.98, size * 0.46), COLORS["white"], int(size * 0.04), 26)
    add_blur_glow(canvas, (-size * 0.08, size * 0.66, size * 0.38, size * 1.10), COLORS["white"], int(size * 0.05), 18)

    house_shadow = shadow_layer(
        (size, size),
        (int(size * 0.18), int(size * 0.20), int(size * 0.82), int(size * 0.82)),
        int(size * 0.18),
        (6, 16, 26, 86),
        int(size * 0.045),
    )
    canvas.alpha_composite(house_shadow)

    draw = ImageDraw.Draw(canvas)
    draw.rounded_rectangle(
        (int(size * 0.28), int(size * 0.44), int(size * 0.62), int(size * 0.76)),
        radius=int(size * 0.075),
        fill=rgba(COLORS["white"], 245),
    )
    roof_width = max(20, size // 17)
    draw.line(
        ((int(size * 0.24), int(size * 0.54)), (int(size * 0.45), int(size * 0.33)), (int(size * 0.66), int(size * 0.54))),
        fill=rgba(COLORS["accent_gold"]),
        width=roof_width,
        joint="curve",
    )
    draw.rounded_rectangle(
        (int(size * 0.40), int(size * 0.56), int(size * 0.50), int(size * 0.76)),
        radius=int(size * 0.038),
        fill=rgba("#294C67"),
    )
    draw.ellipse(
        (int(size * 0.57), int(size * 0.38), int(size * 0.85), int(size * 0.66)),
        fill=rgba(COLORS["white"], 240),
    )
    draw.ellipse(
        (int(size * 0.64), int(size * 0.45), int(size * 0.78), int(size * 0.59)),
        fill=rgba(COLORS["brand_light"]),
    )
    diamond = [
        (int(size * 0.71), int(size * 0.46)),
        (int(size * 0.76), int(size * 0.52)),
        (int(size * 0.71), int(size * 0.58)),
        (int(size * 0.66), int(size * 0.52)),
    ]
    draw.polygon(diamond, fill=rgba("#D9A843"))

    mask = Image.new("L", (size, size), 0)
    ImageDraw.Draw(mask).rounded_rectangle((0, 0, size, size), radius=int(size * 0.24), fill=255)
    clipped = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    clipped.paste(canvas, mask=mask)
    return clipped


def logo_mark_svg() -> str:
    return f"""
<svg width="1024" height="1024" viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="{COLORS['brand_dark']}"/>
      <stop offset="72%" stop-color="{COLORS['brand']}"/>
      <stop offset="100%" stop-color="{COLORS['accent_sky']}"/>
    </linearGradient>
    <filter id="shadow" x="-20%" y="-20%" width="160%" height="160%">
      <feDropShadow dx="0" dy="20" stdDeviation="24" flood-color="#0b1620" flood-opacity="0.28"/>
    </filter>
  </defs>
  <rect width="1024" height="1024" rx="246" fill="url(#bg)"/>
  <circle cx="820" cy="208" r="92" fill="#FFFFFF" opacity="0.10"/>
  <circle cx="102" cy="884" r="154" fill="#FFFFFF" opacity="0.06"/>
  <g filter="url(#shadow)">
    <rect x="286" y="450" width="348" height="328" rx="76" fill="#FFFFFF" fill-opacity="0.96"/>
    <path d="M 246 552 L 460 338 L 674 552" fill="none" stroke="{COLORS['accent_gold']}" stroke-width="62" stroke-linecap="round" stroke-linejoin="round"/>
    <rect x="410" y="570" width="104" height="208" rx="38" fill="#294C67"/>
    <circle cx="728" cy="528" r="142" fill="#FFFFFF" fill-opacity="0.94"/>
    <circle cx="728" cy="528" r="72" fill="{COLORS['brand_light']}"/>
    <path d="M728 466 L780 528 L728 590 L676 528 Z" fill="#D9A843"/>
  </g>
</svg>
""".strip()


def lockup_svg() -> str:
    return f"""
<svg width="1600" height="640" viewBox="0 0 1600 640" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="panel" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="{COLORS['bg_light']}"/>
      <stop offset="100%" stop-color="{COLORS['bg_surface']}"/>
    </linearGradient>
  </defs>
  <rect width="1600" height="640" rx="56" fill="url(#panel)"/>
  <rect x="72" y="72" width="496" height="496" rx="108" fill="{COLORS['brand_dark']}"/>
  <image href="uybek-logo-mark-512.png" x="72" y="72" width="496" height="496"/>
  <text x="650" y="268" font-size="170" font-family="Bahnschrift, Segoe UI, Arial, sans-serif" font-weight="700" fill="{COLORS['brand_dark']}">Uybek</text>
  <text x="654" y="350" font-size="54" font-family="Segoe UI, Arial, sans-serif" font-weight="600" fill="{COLORS['text_soft']}">Find homes faster in Uzbekistan</text>
  <text x="654" y="430" font-size="42" font-family="Segoe UI, Arial, sans-serif" fill="{COLORS['text_soft']}">Clear filters. Real listings. Direct chat.</text>
</svg>
""".strip()


def draw_logo_lockup() -> Image.Image:
    canvas = diagonal_gradient((1600, 640), COLORS["bg_light"], COLORS["bg_surface"], COLORS["brand_light"])
    mark = draw_logo_mark(512)
    canvas.alpha_composite(shadow_layer(canvas.size, (78, 84, 590, 596), 110, (10, 18, 24, 48), 28))
    canvas.alpha_composite(mark, (78, 64))

    draw = ImageDraw.Draw(canvas)
    display = font(FONT_DISPLAY, 172)
    headline = font(FONT_SEMIBOLD, 56)
    body = font(FONT_REGULAR, 44)
    draw.text((650, 168), "Uybek", font=display, fill=rgba(COLORS["brand_dark"]))
    draw.text((654, 328), "Find homes faster in Uzbekistan", font=headline, fill=rgba(COLORS["text_soft"]))
    draw.text((654, 408), "Clear filters. Real listings. Direct chat.", font=body, fill=rgba(COLORS["text_soft"]))
    return canvas


def build_feature_graphic(out_path: Path) -> None:
    canvas = diagonal_gradient((1024, 500), COLORS["brand_dark"], COLORS["brand"], COLORS["accent_sky"])
    add_blur_glow(canvas, (722, 68, 1088, 434), COLORS["white"], 82, 58)
    add_blur_glow(canvas, (-96, 328, 260, 612), COLORS["white"], 80, 30)

    panel = Image.new("RGBA", canvas.size, (0, 0, 0, 0))
    panel_draw = ImageDraw.Draw(panel)
    panel_draw.rounded_rectangle((52, 64, 462, 428), radius=34, fill=rgba(COLORS["white"], 34))
    canvas.alpha_composite(panel)

    draw = ImageDraw.Draw(canvas)
    draw.text((84, 84), "Uybek", font=font(FONT_DISPLAY, 92), fill=rgba(COLORS["white"]))
    draw.text((84, 168), "Find homes faster\nin Uzbekistan", font=font(FONT_SEMIBOLD, 40), fill=rgba(COLORS["white"]))
    draw.text((84, 282), "Clear filters", font=font(FONT_SEMIBOLD, 28), fill=rgba(COLORS["white"]))
    draw.text((84, 322), "Real listings", font=font(FONT_SEMIBOLD, 28), fill=rgba(COLORS["white"]))
    draw.text((84, 362), "Direct chat", font=font(FONT_SEMIBOLD, 28), fill=rgba(COLORS["white"]))
    draw.rounded_rectangle((84, 396, 208, 412), radius=8, fill=rgba(COLORS["accent_gold"]))
    draw.rounded_rectangle((224, 396, 330, 412), radius=8, fill=rgba(COLORS["white"], 88))
    draw.rounded_rectangle((346, 396, 440, 412), radius=8, fill=rgba(COLORS["white"], 52))

    collage_specs = [
        (SCREENSHOT_DIR / "photo_2_2026-05-10_04-23-57.jpg", 566, 124, 172, -8),
        (SCREENSHOT_DIR / "photo_6_2026-05-10_04-23-57.jpg", 688, 38, 208, 0),
        (SCREENSHOT_DIR / "photo_1_2026-05-10_04-23-57.jpg", 836, 114, 164, 9),
    ]
    for source, x, y, width, angle in collage_specs:
        card = screenshot_card(source, width=width)
        rotated = card.rotate(angle, expand=True, resample=Image.Resampling.BICUBIC)
        box = (x, y, x + rotated.width, y + rotated.height)
        canvas.alpha_composite(shadow_layer(canvas.size, box, 38, (10, 18, 24, 120), 24))
        canvas.alpha_composite(rotated, (x, y))

    canvas.save(out_path)


def feature_svg() -> str:
    return f"""
<svg width="1024" height="500" viewBox="0 0 1024 500" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <linearGradient id="bg" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="{COLORS['brand_dark']}"/>
      <stop offset="60%" stop-color="{COLORS['brand']}"/>
      <stop offset="100%" stop-color="{COLORS['accent_sky']}"/>
    </linearGradient>
  </defs>
  <rect width="1024" height="500" fill="url(#bg)"/>
  <rect x="52" y="64" width="410" height="364" rx="34" fill="#FFFFFF" fill-opacity="0.10"/>
  <text x="84" y="146" font-size="92" font-family="Bahnschrift, Segoe UI, Arial, sans-serif" font-weight="700" fill="#FFFFFF">Uybek</text>
  <text x="84" y="220" font-size="40" font-family="Segoe UI, Arial, sans-serif" font-weight="600" fill="#FFFFFF">Find homes faster</text>
  <text x="84" y="266" font-size="40" font-family="Segoe UI, Arial, sans-serif" font-weight="600" fill="#FFFFFF">in Uzbekistan</text>
  <text x="84" y="322" font-size="28" font-family="Segoe UI, Arial, sans-serif" font-weight="600" fill="#FFFFFF">Clear filters</text>
  <text x="84" y="362" font-size="28" font-family="Segoe UI, Arial, sans-serif" font-weight="600" fill="#FFFFFF">Real listings</text>
  <text x="84" y="402" font-size="28" font-family="Segoe UI, Arial, sans-serif" font-weight="600" fill="#FFFFFF">Direct chat</text>
  <rect x="84" y="396" width="124" height="16" rx="8" fill="{COLORS['accent_gold']}"/>
  <rect x="224" y="396" width="106" height="16" rx="8" fill="#FFFFFF" fill-opacity="0.35"/>
  <rect x="346" y="396" width="94" height="16" rx="8" fill="#FFFFFF" fill-opacity="0.20"/>
</svg>
""".strip()


def save_logo_assets() -> None:
    mark_1024 = draw_logo_mark(1024)
    mark_512 = draw_logo_mark(512)
    lockup = draw_logo_lockup()

    mark_1024.save(LOGO_DIR / "uybek-logo-mark-1024.png")
    mark_512.save(LOGO_DIR / "uybek-logo-mark-512.png")
    lockup.save(LOGO_DIR / "uybek-logo-lockup.png")

    (LOGO_DIR / "uybek-logo-mark.svg").write_text(logo_mark_svg(), encoding="utf-8")
    (LOGO_DIR / "uybek-logo-lockup.svg").write_text(lockup_svg(), encoding="utf-8")


def save_store_screenshots() -> None:
    build_store_screenshot(
        SCREENSHOT_DIR / "photo_6_2026-05-10_04-23-57.jpg",
        "Find homes fast",
        "Browse fresh listings with real prices and clean cards.",
        "Live listings",
        SCREENSHOT_DIR / "01-home-feed.png",
    )
    build_store_screenshot(
        SCREENSHOT_DIR / "photo_2_2026-05-10_04-23-57.jpg",
        "Filter with clarity",
        "Deal type, property type, price, and rooms in one quick flow.",
        "Fast filters",
        SCREENSHOT_DIR / "02-smart-search.png",
    )
    build_store_screenshot(
        SCREENSHOT_DIR / "photo_1_2026-05-10_04-23-57.jpg",
        "Know the seller",
        "Profiles, saved listings, and account actions stay close at hand.",
        "Seller profile",
        SCREENSHOT_DIR / "03-seller-profile.png",
    )
    build_store_screenshot(
        SCREENSHOT_DIR / "photo_5_2026-05-10_04-23-57.jpg",
        "Chat directly",
        "Talk with buyers and sellers without leaving the app.",
        "In-app chat",
        SCREENSHOT_DIR / "04-chat-directly.png",
        dark=True,
    )


def main() -> None:
    ensure_dirs()
    save_logo_assets()
    build_feature_graphic(FEATURE_DIR / "uybek-feature-graphic.png")
    (FEATURE_DIR / "uybek-feature-graphic.svg").write_text(feature_svg(), encoding="utf-8")
    save_store_screenshots()

    print(f"Created Play Store assets in {OUT_ROOT}")


if __name__ == "__main__":
    main()
