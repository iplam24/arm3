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

# CharInfo base offsets for standing frame 0:
# Head [0]: (-9, -33)
# Leg  [1]: (-5, -8)
# Body [2]: (-6, -15)
# Wp   [3]: (-17, -24)

# Exact offset values for DB:
# Head 246 (Red):   [(4, 1), (6, 1), (5, -2), (2, 0)]
# Leg 247 (Red):    [(0, 0), (0, 2), (0, 1), (1, 3), (1, 1), (0, 2), (-1, 1), (0, 0), (-2, -2), (-2, -2)]
# Body 248 (Red):   [(0, 1), (2, 3), (0, 2), (2, 1), (0, 1), (1, 2), (0, 2), (-1, 2), (0, 1), (0, 1)]

# Head 250 (Yellow):[(5, 1), (6, 1), (5, -2), (2, 0)]
# Leg 251 (Yellow): [(0, 0), (0, 2), (0, 1), (1, 3), (1, 1), (0, 2), (-1, 1), (0, 0), (-2, -2), (-2, -2)]
# Body 252 (Yellow):[(0, 1), (2, 3), (0, 2), (2, 1), (0, 1), (1, 2), (0, 2), (-1, 2), (0, 1), (0, 1)]

# Wp 249 (Gậy):     [(19, 3), (18, 8), (15, 2), (7, 15), (-6, 6), (20, 12), (21, 22)]

head_246 = [(4, 1), (6, 1), (5, -2), (2, 0)]
leg_247 = [(0, 0), (0, 2), (0, 1), (1, 3), (1, 1), (0, 2), (-1, 1), (0, 0), (-2, -2), (-2, -2)]
body_248 = [(0, 1), (2, 3), (0, 2), (2, 1), (0, 1), (1, 2), (0, 2), (-1, 2), (0, 1), (0, 1)]

head_250 = [(5, 1), (6, 1), (5, -2), (2, 0)]
leg_251 = [(0, 0), (0, 2), (0, 1), (1, 3), (1, 1), (0, 2), (-1, 1), (0, 0), (-2, -2), (-2, -2)]
body_252 = [(0, 1), (2, 3), (0, 2), (2, 1), (0, 1), (1, 2), (0, 2), (-1, 2), (0, 1), (0, 1)]

wp_249 = [(19, 3), (18, 8), (15, 2), (7, 15), (-6, 6), (20, 12), (21, 22)]

def render_client_simulation(head_id, leg_id, body_id, wp_id, h_offs, l_offs, b_offs, w_offs, out_path):
    canvas = Image.new('RGBA', (400, 120), (255, 255, 255, 255))
    
    # Render 6 frames:
    # Frame 0: Standing with Gậy (249)
    # Frame 1: Standing with Súng chuối (58)
    # Frame 2: Walk frame 1
    # Frame 3: Shooting angle 0
    # Frame 4: Shooting angle 3
    # Frame 5: Shooting angle 6
    
    for col in range(6):
        cx, cy = col * 65 + 35, 60
        
        f_idx = 0 if col in [0, 1, 3, 4, 5] else 1
        b_idx = 0 if col in [0, 1, 2] else (0 if col==3 else (3 if col==4 else 6))
        w_idx = 0 if col in [0, 1, 2] else (0 if col==3 else (3 if col==4 else 6))
        cur_wp = wp_id if col != 1 else 58
        
        # 1. Weapon (CharInfo -17, -24)
        if cur_wp in parts:
            entry = parts[cur_wp]['data'][min(w_idx, len(parts[cur_wp]['data'])-1)]
            s = sprites[entry['id']]
            crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
            w_dx, w_dy = (w_offs[w_idx] if cur_wp == 249 else (entry['dx'], entry['dy']))
            canvas.paste(crop, (int(cx - 17 + w_dx), int(cy - 24 + w_dy)), crop)
            
        # 2. Head (CharInfo -9, -33)
        if head_id in parts:
            entry = parts[head_id]['data'][min(f_idx, len(parts[head_id]['data'])-1)]
            s = sprites[entry['id']]
            crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
            h_dx, h_dy = h_offs[f_idx]
            canvas.paste(crop, (int(cx - 9 + h_dx), int(cy - 33 + h_dy)), crop)
            
        # 3. Leg (CharInfo -5, -8)
        if leg_id in parts:
            entry = parts[leg_id]['data'][min(f_idx, len(parts[leg_id]['data'])-1)]
            s = sprites[entry['id']]
            crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
            l_dx, l_dy = l_offs[f_idx]
            canvas.paste(crop, (int(cx - 5 + l_dx), int(cy - 8 + l_dy)), crop)
            
        # 4. Body (CharInfo -6, -15)
        if body_id in parts:
            entry = parts[body_id]['data'][min(b_idx, len(parts[body_id]['data'])-1)]
            s = sprites[entry['id']]
            crop = big_images[s['file']].crop((s['x'], s['y'], s['x'] + s['w'], s['y'] + s['h']))
            b_dx, b_dy = b_offs[b_idx]
            canvas.paste(crop, (int(cx - 6 + b_dx), int(cy - 15 + b_dy)), crop)

    canvas.save(out_path)

os.makedirs('output/test_client_perfect', exist_ok=True)
render_client_simulation(246, 247, 248, 249, head_246, leg_247, body_248, wp_249, 'output/test_client_perfect/red_monkey_perfect.png')
render_client_simulation(250, 251, 252, 249, head_250, leg_251, body_252, wp_249, 'output/test_client_perfect/yellow_monkey_perfect.png')

print("Rendered client simulation perfect.")
