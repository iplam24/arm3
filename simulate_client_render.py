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

# Client CharInfo base offsets for Frame 0 (Standing):
# CharInfo[0][0] (Head): dx = -9, dy = -33
# CharInfo[0][1] (Leg): dx = -5, dy = -8
# CharInfo[0][2] (Body): dx = -6, dy = -15
# CharInfo[0][3] (Weapon): dx = -17, dy = -24

def render_client_exact(head_id, leg_id, body_id, wp_id, head_p_dx, head_p_dy, leg_p_dx, leg_p_dy, body_p_dx, body_p_dy, wp_p_dx, wp_p_dy):
    canvas = Image.new('RGBA', (120, 120), (255, 255, 255, 255))
    cx, cy = 60, 60
    
    # 1. Weapon (CharInfo[0][3] = -17, -24)
    if wp_id in parts:
        entry = parts[wp_id]['data'][0]
        s = sprites[entry['id']]
        crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
        px = cx - 17 + entry['dx'] + wp_p_dx
        py = cy - 24 + entry['dy'] + wp_p_dy
        canvas.paste(crop, (int(px), int(py)), crop)
        
    # 2. Head (CharInfo[0][0] = -9, -33)
    if head_id in parts:
        entry = parts[head_id]['data'][0]
        s = sprites[entry['id']]
        crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
        px = cx - 9 + entry['dx'] + head_p_dx
        py = cy - 33 + entry['dy'] + head_p_dy
        canvas.paste(crop, (int(px), int(py)), crop)

    # 3. Leg (CharInfo[0][1] = -5, -8)
    if leg_id in parts:
        entry = parts[leg_id]['data'][0]
        s = sprites[entry['id']]
        crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
        px = cx - 5 + entry['dx'] + leg_p_dx
        py = cy - 8 + entry['dy'] + leg_p_dy
        canvas.paste(crop, (int(px), int(py)), crop)

    # 4. Body (CharInfo[0][2] = -6, -15)
    if body_id in parts:
        entry = parts[body_id]['data'][0]
        s = sprites[entry['id']]
        crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
        px = cx - 6 + entry['dx'] + body_p_dx
        py = cy - 15 + entry['dy'] + body_p_dy
        canvas.paste(crop, (int(px), int(py)), crop)

    return canvas

os.makedirs('output/test_client_exact', exist_ok=True)

# Test 1: Gunner (0, 2, 1, 27) with (0,0) offsets
img_g = render_client_exact(0, 2, 1, 27, 0, 0, 0, 0, 0, 0, 0, 0)
img_g.save('output/test_client_exact/gunner_exact.png')

# Test 2: Yellow Monkey with dx=4, dy=1 for head; dx=3, dy=4 for leg; dx=0, dy=1 for body; dx=19, dy=3 for wp
img_m1 = render_client_exact(250, 251, 252, 249, 4, 1, 3, 4, 0, 1, 19, 3)
img_m1.save('output/test_client_exact/yellow_monkey_exact.png')

print("Rendered exact client simulation.")
