package com.vxl.giftcode;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.vxl.loi.VXLCoSoDuLieu;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class VXLGiftcodeService {
    private static volatile boolean schemaChecked = false;

    private VXLGiftcodeService() {}

    public static class GiftItem {
        public final int id;
        public final int quantity;

        public GiftItem(int id, int quantity) {
            this.id = id;
            this.quantity = quantity;
        }
    }

    public static class GiftResult {
        public final boolean success;
        public final String message;
        public final int gold;
        public final int gem;
        public final List<GiftItem> items;

        public GiftResult(boolean success, String message, int gold, int gem, List<GiftItem> items) {
            this.success = success;
            this.message = message;
            this.gold = gold;
            this.gem = gem;
            this.items = items != null ? items : Collections.emptyList();
        }

        public GiftResult(boolean success, String message, int gold, int gem) {
            this(success, message, gold, gem, Collections.emptyList());
        }
    }

    private static void ensureSchema(Connection conn) {
        if (schemaChecked) {
            return;
        }
        try (Statement st = conn.createStatement()) {
            st.execute("CREATE TABLE IF NOT EXISTS `giftcode` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                    "`code` VARCHAR(32) NOT NULL UNIQUE, " +
                    "`gold` INT NOT NULL DEFAULT 0, " +
                    "`gem` INT NOT NULL DEFAULT 0, " +
                    "`item_id` INT NOT NULL DEFAULT 0, " +
                    "`item_quantity` INT NOT NULL DEFAULT 1, " +
                    "`items_json` TEXT DEFAULT NULL, " +
                    "`max_use` INT NOT NULL DEFAULT 1, " +
                    "`used_count` INT NOT NULL DEFAULT 0, " +
                    "`expires_at` DATETIME DEFAULT NULL, " +
                    "`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

            st.execute("CREATE TABLE IF NOT EXISTS `giftcode_usage` (" +
                    "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                    "`code_id` INT NOT NULL, " +
                    "`user_id` INT NOT NULL, " +
                    "`used_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "INDEX `idx_code_user` (`code_id`, `user_id`), " +
                    "FOREIGN KEY (`code_id`) REFERENCES `giftcode`(`id`), " +
                    "FOREIGN KEY (`user_id`) REFERENCES `accounts`(`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

            boolean hasItemsJson = false;
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM information_schema.COLUMNS " +
                    "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'giftcode' AND COLUMN_NAME = 'items_json'")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasItemsJson = true;
                }
            }
            if (!hasItemsJson) {
                try {
                    st.execute("ALTER TABLE `giftcode` ADD COLUMN `items_json` TEXT DEFAULT NULL;");
                } catch (Exception ignored) {}
            }

            schemaChecked = true;
        } catch (Exception ex) {
            Logger.getLogger(VXLGiftcodeService.class.getName()).log(Level.WARNING, "Giftcode ensureSchema error", ex);
        }
    }

    public static GiftResult redeem(int userId, String code) {
        if (code == null || code.trim().isEmpty()) {
            return new GiftResult(false, "Mã giftcode không hợp lệ.", 0, 0);
        }
        code = code.trim().toUpperCase();
        try (Connection conn = VXLCoSoDuLieu.getConnection()) {
            ensureSchema(conn);
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, gold, gem, item_id, item_quantity, items_json, max_use, used_count, expires_at " +
                    "FROM giftcode WHERE code = ? FOR UPDATE")) {
                stmt.setString(1, code);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return new GiftResult(false, "Mã giftcode không tồn tại.", 0, 0);
                    }
                    int id = rs.getInt("id");
                    int gold = rs.getInt("gold");
                    int gem = rs.getInt("gem");
                    int itemId = rs.getInt("item_id");
                    int itemQty = rs.getInt("item_quantity");
                    String itemsJson = rs.getString("items_json");
                    int maxUse = rs.getInt("max_use");
                    int usedCount = rs.getInt("used_count");
                    Timestamp expires = rs.getTimestamp("expires_at");

                    if (expires != null && expires.before(new Timestamp(System.currentTimeMillis()))) {
                        conn.rollback();
                        return new GiftResult(false, "Mã giftcode đã hết hạn.", 0, 0);
                    }
                    if (usedCount >= maxUse) {
                        conn.rollback();
                        return new GiftResult(false, "Mã giftcode đã hết lượt sử dụng.", 0, 0);
                    }

                    try (PreparedStatement check = conn.prepareStatement(
                            "SELECT 1 FROM giftcode_usage WHERE code_id = ? AND user_id = ?")) {
                        check.setInt(1, id);
                        check.setInt(2, userId);
                        try (ResultSet usedRs = check.executeQuery()) {
                            if (usedRs.next()) {
                                conn.rollback();
                                return new GiftResult(false, "Bạn đã sử dụng mã này rồi.", 0, 0);
                            }
                        }
                    }

                    try (PreparedStatement update = conn.prepareStatement(
                            "UPDATE giftcode SET used_count = used_count + 1 WHERE id = ?")) {
                        update.setInt(1, id);
                        update.executeUpdate();
                    }

                    try (PreparedStatement log = conn.prepareStatement(
                            "INSERT INTO giftcode_usage(code_id, user_id) VALUES (?, ?)")) {
                        log.setInt(1, id);
                        log.setInt(2, userId);
                        log.executeUpdate();
                    }

                    List<GiftItem> items = new ArrayList<>();
                    if (itemId > 0 && itemQty > 0) {
                        items.add(new GiftItem(itemId, itemQty));
                    }
                    if (itemsJson != null && !itemsJson.isBlank()) {
                        try {
                            JSONArray arr = JSON.parseArray(itemsJson);
                            if (arr != null) {
                                for (int i = 0; i < arr.size(); i++) {
                                    JSONObject obj = arr.getJSONObject(i);
                                    if (obj != null) {
                                        int itId = obj.getIntValue("id");
                                        int itQty = obj.getIntValue("quantity", 1);
                                        if (itId > 0 && itQty > 0) {
                                            items.add(new GiftItem(itId, itQty));
                                        }
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(VXLGiftcodeService.class.getName()).log(Level.WARNING, "Parse items_json error", ex);
                        }
                    }

                    conn.commit();
                    return new GiftResult(true, "Nhận giftcode thành công!", gold, gem, items);
                }
            } catch (Exception ex) {
                conn.rollback();
                throw ex;
            }
        } catch (Exception ex) {
            Logger.getLogger(VXLGiftcodeService.class.getName()).log(Level.SEVERE, null, ex);
            return new GiftResult(false, "Lỗi hệ thống, vui lòng thử lại.", 0, 0);
        }
    }
}
