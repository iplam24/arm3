START TRANSACTION;

-- Fix Hulk (Part 221) jump animation: use sprite 1861 for Frame 7 (Jump Up)
INSERT INTO vatar_parts (id, 	ype, part_data)
VALUES (221, 1, '[{\"dx\":-1,\"dy\":-6,\"id\":1854},{\"dx\":-4,\"dy\":-8,\"id\":1855},{\"dx\":-6,\"dy\":-7,\"id\":1856},{\"dx\":0,\"dy\":-6,\"id\":1857},{\"dx\":0,\"dy\":-8,\"id\":1858},{\"dx\":-1,\"dy\":-7,\"id\":1859},{\"dx\":-2,\"dy\":-7,\"id\":1862},{\"dx\":-4,\"dy\":-14,\"id\":1861},{\"dx\":-2,\"dy\":-10,\"id\":1864},{\"dx\":-4,\"dy\":-8,\"id\":1865}]')
ON DUPLICATE KEY UPDATE 	ype = VALUES(	ype), part_data = VALUES(part_data);

COMMIT;
