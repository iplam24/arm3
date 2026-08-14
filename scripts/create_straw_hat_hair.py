from pathlib import Path
from collections import deque
import argparse
import json

from PIL import Image, ImageDraw


SPRITE_START_ID = 2143
FRAME_ANGLES = (0, 18, 38, 58)
SOURCE_BOXES = (
    (100, 196, 475, 518),
    (634, 181, 996, 536),
    (1155, 145, 1513, 563),
    (1675, 145, 2007, 589),
)
SOURCE_PIXELS_PER_GAME_PIXEL = 14


def is_panel_background(pixel):
    red, green, blue, alpha = pixel
    return alpha and min(red, green, blue) > 175 and max(red, green, blue) - min(red, green, blue) < 18


def extract_head(source, source_box, padding=18):
    left, top, right, bottom = source_box
    head = source.crop((left - padding, top - padding, right + padding, bottom + padding)).convert("RGBA")
    width, height = head.size
    background = set()
    queue = deque()

    for x in range(width):
        for y in (0, height - 1):
            if is_panel_background(head.getpixel((x, y))) and (x, y) not in background:
                background.add((x, y))
                queue.append((x, y))
    for y in range(height):
        for x in (0, width - 1):
            if is_panel_background(head.getpixel((x, y))) and (x, y) not in background:
                background.add((x, y))
                queue.append((x, y))

    while queue:
        x, y = queue.popleft()
        for next_x, next_y in ((x - 1, y), (x + 1, y), (x, y - 1), (x, y + 1)):
            if not (0 <= next_x < width and 0 <= next_y < height):
                continue
            if (next_x, next_y) in background:
                continue
            if is_panel_background(head.getpixel((next_x, next_y))):
                background.add((next_x, next_y))
                queue.append((next_x, next_y))

    alpha = Image.new("L", head.size, 255)
    alpha_pixels = alpha.load()
    for x, y in background:
        alpha_pixels[x, y] = 0
    head.putalpha(alpha)
    visible_bbox = head.getchannel("A").getbbox()
    if visible_bbox is None:
        raise ValueError("Reference frame contains no visible head")
    return head.crop(visible_bbox)


def native_size(head):
    return (
        max(1, round(head.width / SOURCE_PIXELS_PER_GAME_PIXEL)),
        max(1, round(head.height / SOURCE_PIXELS_PER_GAME_PIXEL)),
    )


def render_frame(head, scale):
    base_width, base_height = native_size(head)
    target_size = (base_width * scale, base_height * scale)
    resampling = Image.Resampling.BOX if scale == 1 else Image.Resampling.LANCZOS
    return head.convert("RGBa").resize(target_size, resampling).convert("RGBA")



def checkerboard(width, height, cell=12):
    canvas = Image.new("RGBA", (width, height), (244, 244, 244, 255))
    draw = ImageDraw.Draw(canvas)
    for y in range(0, height, cell):
        for x in range(0, width, cell):
            if (x // cell + y // cell) % 2:
                draw.rectangle(
                    (x, y, min(width - 1, x + cell - 1), min(height - 1, y + cell - 1)),
                    fill=(218, 218, 218, 255),
                )
    return canvas


def save_preview(output_root, preview_path):
    frames = [
        Image.open(output_root / "4" / f"Small{SPRITE_START_ID + offset}.png").convert("RGBA")
        for offset in range(len(FRAME_ANGLES))
    ]
    cell_width = max(frame.width for frame in frames) + 54
    cell_height = max(frame.height for frame in frames) + 76
    preview = checkerboard(cell_width * len(frames), cell_height)
    draw = ImageDraw.Draw(preview)
    for offset, frame in enumerate(frames):
        left = offset * cell_width
        x = left + (cell_width - frame.width) // 2
        y = 42 + (cell_height - 52 - frame.height) // 2
        preview.alpha_composite(frame, (x, y))
        draw.text(
            (left + 10, 10),
            f"Original {FRAME_ANGLES[offset]} degree",
            fill=(18, 18, 18, 255),
        )
        draw.text(
            (left + 10, cell_height - 24),
            f"{frame.width}x{frame.height} native x4",
            fill=(18, 18, 18, 255),
        )
    preview_path.parent.mkdir(parents=True, exist_ok=True)
    preview.save(preview_path, optimize=True)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("source")
    parser.add_argument("output_root")
    parser.add_argument("--selected-preview")
    parser.add_argument("--preview")
    args = parser.parse_args()

    source = Image.open(args.source).convert("RGBA")
    heads = [extract_head(source, source_box) for source_box in SOURCE_BOXES]
    output_root = Path(args.output_root)
    metadata = {}

    for scale in range(1, 5):
        output_dir = output_root / str(scale)
        output_dir.mkdir(parents=True, exist_ok=True)
        frames = []
        for offset, head in enumerate(heads):
            frame = render_frame(head, scale)
            frame.save(output_dir / f"Small{SPRITE_START_ID + offset}.png", optimize=True)
            frames.append(frame)
        metadata[str(scale)] = [list(frame.size) for frame in frames]

    if args.preview:
        save_preview(output_root, Path(args.preview))
    print(json.dumps(metadata, separators=(",", ":")))


if __name__ == "__main__":
    main()
