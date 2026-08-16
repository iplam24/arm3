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

# Now let's simulate Client rendering with the correct SWAP:
# Head: 250 (Type 0) -> head slot (CharInfo -9, -33)
# Body: 251 (Type 2, Sprite 1945 13x12) -> body slot (CharInfo -6, -15)
# Leg:  252 (Type 1, Sprite 1953 13x8)  -> leg slot  (CharInfo -5, -8)
# Weapon: 249 (Type 3) -> wp slot (CharInfo -17, -24)

# Let's test dx/dy offsets:
# Body (251) at body slot:
# Base body pos: x - 6, y - 15. Sprite 1945 is 13x12.
# Body top is at y - 15, bottom is at y - 3.
# Leg (252) at leg slot:
# Base leg pos: x - 5, y - 8. Sprite 1953 is 13x8.
# If leg dx = 0, dy = 5: leg top is at y - 3, bottom is at y + 5!
# Head (250) at head slot:
# Base head pos: x - 9, y - 33. Sprite 1941 is 16x19.
# If head dx = 4, dy = 0: head top is at y - 33, bottom is at y - 14 (right on top of body at y - 15)!

def render_client_swapped(head_id, leg_id, body_id, wp_id, h_dx, h_dy, l_dx, l_dy, b_dx, b_dy, w_dx, w_dy, out_path):
    canvas = Image.new('RGBA', (120, 120), (255, 255, 255, 255))
    cx, cy = 60, 60
    
    # 1. Weapon (CharInfo -17, -24)
    if wp_id in parts:
        entry = parts[wp_id]['data'][0]
        s = sprites[entry['id']]
        crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
        px = cx - 17 + entry['dx'] + w_dx
        py = cy - 24 + entry['dy'] + w_dy
        canvas.paste(crop, (int(px), int(py)), crop)
        
    # 2. Head (CharInfo -9, -33)
    if head_id in parts:
        entry = parts[head_id]['data'][0]
        s = sprites[entry['id']]
        crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
        px = cx - 9 + entry['dx'] + h_dx
        py = cy - 33 + entry['dy'] + h_dy
        canvas.paste(crop, (int(px), int(py)), crop)

    # 3. Leg (CharInfo -5, -8) -> uses LEG part (252 for Yellow, 248 for Red)
    if leg_id in parts:
        entry = parts[leg_id]['data'][0]
        s = sprites[entry['id']]
        crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
        px = cx - 5 + entry['dx'] + l_dx
        py = cy - 8 + entry['dy'] + l_dy
        canvas.paste(crop, (int(px), int(py)), crop)

    # 4. Body (CharInfo -6, -15) -> uses BODY part (251 for Yellow, 247 for Red)
    if body_id in parts:
        entry = parts[body_id]['data'][0]
        s = sprites[entry['id']]
        crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
        px = cx - 6 + entry['dx'] + b_dx
        py = cy - 15 + entry['dy'] + b_dy
        canvas.paste(crop, (int(px), int(py)), crop)

    canvas.save(out_path)

os.makedirs('output/test_swapped', exist_ok=True)

# Yellow Monkey: Head=250, Body=251, Leg=252, Wp=249
img_ym = render_client_swapped(250, 252, 251, 249, 
                               h_dx=4, h_dy=0, 
                               l_dx=0, l_dy=5, 
                               b_dx=0, b_dy=0, 
                               w_dx=19, w_dy=3, 
                               out_path='output/test_swapped/yellow_monkey_swapped.png')

# Red Monkey: Head=246, Body=247, Leg=248, Wp=249
img_rm = render_client_swapped(246, 248, 247, 249, 
                               h_dx=4, h_dy=0, 
                               l_dx=0, l_dy=5, 
                               b_dx=0, b_dy=0, 
                               w_dx=19, w_dy=3, 
                               out_path='output/test_swapped/red_monkey_swapped.png')

print("Rendered swapped test images.")
