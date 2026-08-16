START TRANSACTION;

-- Part 246: Red Monkey Head (type 0)
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (246, 0, '[{"dx":4,"dy":0,"id":1909},{"dx":6,"dy":0,"id":1922},{"dx":5,"dy":-3,"id":1923},{"dx":2,"dy":-1,"id":1924}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

-- Part 247: Red Monkey Body (type 2)
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (247, 2, '[{"dx":0,"dy":0,"id":1911},{"dx":0,"dy":2,"id":1925},{"dx":0,"dy":1,"id":1912},{"dx":1,"dy":3,"id":1913},{"dx":1,"dy":1,"id":1914},{"dx":0,"dy":2,"id":1915},{"dx":-1,"dy":1,"id":1915},{"dx":0,"dy":0,"id":1926},{"dx":-2,"dy":-2,"id":1927},{"dx":-2,"dy":-2,"id":1928}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

-- Part 248: Red Monkey Leg (type 1)
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (248, 1, '[{"dx":0,"dy":5,"id":1916},{"dx":2,"dy":7,"id":1917},{"dx":0,"dy":6,"id":1918},{"dx":2,"dy":5,"id":1919},{"dx":0,"dy":5,"id":1920},{"dx":1,"dy":6,"id":1921},{"dx":0,"dy":6,"id":1931},{"dx":-1,"dy":6,"id":1930},{"dx":0,"dy":5,"id":1940},{"dx":0,"dy":5,"id":1939}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

-- Part 249: Monkey Weapon / Stick (type 3)
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (249, 3, '[{"dx":19,"dy":3,"id":1932},{"dx":18,"dy":8,"id":1933},{"dx":15,"dy":2,"id":1934},{"dx":7,"dy":15,"id":1935},{"dx":-6,"dy":6,"id":1936},{"dx":20,"dy":12,"id":1937},{"dx":21,"dy":22,"id":1938}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

-- Part 250: Yellow Monkey Head (type 0)
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (250, 0, '[{"dx":4,"dy":0,"id":1941},{"dx":6,"dy":0,"id":1942},{"dx":5,"dy":-2,"id":1943},{"dx":2,"dy":0,"id":1944}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

-- Part 251: Yellow Monkey Body (type 2)
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (251, 2, '[{"dx":0,"dy":0,"id":1945},{"dx":0,"dy":2,"id":1963},{"dx":0,"dy":1,"id":1946},{"dx":1,"dy":3,"id":1947},{"dx":1,"dy":1,"id":1948},{"dx":0,"dy":2,"id":1949},{"dx":-1,"dy":1,"id":1949},{"dx":0,"dy":0,"id":1950},{"dx":-2,"dy":-2,"id":1951},{"dx":-2,"dy":-2,"id":1952}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

-- Part 252: Yellow Monkey Leg (type 1)
INSERT INTO `avatar_parts` (`id`, `type`, `part_data`)
VALUES (252, 1, '[{"dx":0,"dy":5,"id":1953},{"dx":2,"dy":7,"id":1954},{"dx":0,"dy":6,"id":1955},{"dx":2,"dy":5,"id":1956},{"dx":0,"dy":5,"id":1957},{"dx":1,"dy":6,"id":1958},{"dx":0,"dy":6,"id":1960},{"dx":-1,"dy":6,"id":1959},{"dx":0,"dy":5,"id":1962},{"dx":0,"dy":5,"id":1961}]')
ON DUPLICATE KEY UPDATE `type` = VALUES(`type`), `part_data` = VALUES(`part_data`);

COMMIT;
