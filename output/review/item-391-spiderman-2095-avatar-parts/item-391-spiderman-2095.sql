-- Spider-Man remap for existing ARM3 item 391
-- Uses free sprite IDs 2095-2118 and keeps runtime parts 204/205/206.

UPDATE `items`
SET `name` = 'Spider-Man',
    `description` = 'Biến thành Spider-Man',
    `icon` = 2095
WHERE `id` = 391;

UPDATE `avatar_parts` SET `type` = 0, `part_data` = '[{"dx":-2,"dy":-2,"id":2095},{"dx":-3,"dy":-1,"id":2096},{"dx":-3,"dy":-1,"id":2097},{"dx":-4,"dy":0,"id":2098}]' WHERE `id` = 204;
UPDATE `avatar_parts` SET `type` = 1, `part_data` = '[{"dx":0,"dy":1,"id":2099},{"dx":0,"dy":-1,"id":2100},{"dx":0,"dy":0,"id":2101},{"dx":0,"dy":0,"id":2102},{"dx":0,"dy":0,"id":2103},{"dx":0,"dy":0,"id":2104},{"dx":0,"dy":-3,"id":2105},{"dx":0,"dy":-4,"id":2106},{"dx":0,"dy":-4,"id":2107},{"dx":0,"dy":-3,"id":2108}]' WHERE `id` = 205;
UPDATE `avatar_parts` SET `type` = 2, `part_data` = '[{"dx":3,"dy":-1,"id":2109},{"dx":0,"dy":0,"id":2110},{"dx":0,"dy":0,"id":2111},{"dx":0,"dy":0,"id":2112},{"dx":0,"dy":0,"id":2113},{"dx":0,"dy":0,"id":2114},{"dx":0,"dy":0,"id":2115},{"dx":0,"dy":0,"id":2116},{"dx":0,"dy":0,"id":2117},{"dx":0,"dy":0,"id":2118}]' WHERE `id` = 206;

-- Required next step: add sprite_images rows after packing these PNGs into the client atlas.
-- Fill image_id/x/y from the atlas packer; base dimensions are:
-- 2095 (source 1702): 22x23
-- 2096 (source 1703): 24x21
-- 2097 (source 1704): 25x21
-- 2098 (source 1705): 26x22
-- 2099 (source 1706): 14x13
-- 2100 (source 1707): 15x11
-- 2101 (source 1708): 21x9
-- 2102 (source 1709): 15x10
-- 2103 (source 1710): 19x9
-- 2104 (source 1711): 15x10
-- 2105 (source 1712): 20x12
-- 2106 (source 1713): 20x15
-- 2107 (source 1714): 18x16
-- 2108 (source 1715): 15x20
-- 2109 (source 1726): 8x13
-- 2110 (source 1717): 14x12
-- 2111 (source 1718): 20x10
-- 2112 (source 1719): 14x10
-- 2113 (source 1720): 21x10
-- 2114 (source 1721): 16x10
-- 2115 (source 1722): 17x11
-- 2116 (source 1723): 17x9
-- 2117 (source 1724): 13x9
-- 2118 (source 1725): 11x8
