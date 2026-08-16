import os, sys, json, re
from PIL import Image

sys.stdout.reconfigure(encoding='utf-8')

with open('sql/army3_system_clean.sql', 'r', encoding='utf-8') as f:
    sql = f.read()

def get_parts(sql_text):
    parts = {}
    for m in re.finditer(r'\((\d+)\s*,\s*(\d+)\s*,\s*\'(\[.*?\])\'\)', sql_text):
        pid = int(m.group(1))
        ptype = int(m.group(2))
        pdata = json.loads(m.group(3).replace('\\"', '"'))
        parts[pid] = {'type': ptype, 'data': pdata}
    return parts

def get_sprites(sql_text):
    sprites = {}
    for m in re.finditer(r'\((\d+),(\d+),(\d+),(\d+),(\d+),(\d+)\)', sql_text):
        sid = int(m.group(1))
        sprites[sid] = {
            'file': int(m.group(2)),
            'x': int(m.group(3)),
            'y': int(m.group(4)),
            'w': int(m.group(5)),
            'h': int(m.group(6))
        }
    return sprites

parts = get_parts(sql)
sprites = get_sprites(sql)

scale = 2
big_images = {}
for i in range(5):
    p = f"res/data/{scale}/Big{i}.png"
    if os.path.exists(p):
        big_images[i] = Image.open(p).convert('RGBA')

# Let's inspect Part 250 (Head), Part 251 (Leg), Part 252 (Body) frame 0:
# Head sprite: 1941 (16x19)
# Leg sprite: 1945 (13x12)
# Body sprite: 1953 (13x8)
# Look at sprites_overview:
# Sprite 1941: Head of Yellow Monkey
# Sprite 1945: Leg / foot of Yellow Monkey
# Sprite 1953: Body / belly / hand of Yellow Monkey

# Let's render separate images of Leg 1945, Body 1953, Head 1941 with a grid
canvas = Image.new('RGBA', (200, 200), (255, 255, 255, 255))
cx, cy = 100, 100

# Leg at (cx, cy)
s_leg = sprites[1945]
crop_leg = big_images[s_leg['file']].crop((s_leg['x'], s_leg['y'], s_leg['x'] + s_leg['w'], s_leg['y'] + s_leg['h']))
canvas.paste(crop_leg, (cx, cy), crop_leg)

# Body: where does body 1953 attach to leg 1945?
s_body = sprites[1953]
crop_body = big_images[s_body['file']].crop((s_body['x'], s_body['y'], s_body['x'] + s_body['w'], s_body['y'] + s_body['h']))
# If leg is at (cx, cy), body should be on top of leg:
# Leg top is at cy. Body height is 8. So body should be at cy - 8 + overlap
canvas.paste(crop_body, (cx - 1, cy - 7), crop_body)

# Head 1941: where does head attach to body?
s_head = sprites[1941]
crop_head = big_images[s_head['file']].crop((s_head['x'], s_head['y'], s_head['x'] + s_head['w'], s_head['y'] + s_head['h']))
# Head height is 19. Body top is at cy - 7. Head should be at cy - 7 - 16 = cy - 23
canvas.paste(crop_head, (cx - 1, cy - 23), crop_head)

os.makedirs('output/test_assemble', exist_ok=True)
canvas.save('output/test_assemble/assembled_relative.png')
print("Saved assembled_relative.png")
