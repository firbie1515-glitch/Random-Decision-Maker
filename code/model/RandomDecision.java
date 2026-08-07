package model;

import db.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * UML: RandomDecision 類別
 * --------------------------------------------------
 * 1. 從資料庫依「決策類型」讀取所有可用選項
 * 2. 不考慮任何偏好或權重
 * 3. 純隨機選出其中一個作為決策結果
 *
 * UML：繼承 Decision 抽象類別
 */
public class RandomDecision extends Decision {
     private final String userId;
    /**
     * 儲存目前決策類型下
     * 從資料庫撈出的所有選項
     */
    private List<Option> options = new ArrayList<>();

    /**
     * 建構子
     */
    public RandomDecision(String decisionType, String userId) {
        this.decisionType = decisionType;
        this.userId = userId;

    }

    /**
     * UML: loadOptions(type) : List<Option>
     * -------------------------
     * 1. 清空舊資料（避免殘留）
     * 2. 依 optionType 查詢資料庫
     * 3. 每一筆資料轉成 Option 物件
     * 4. 存入 options 清單
     */
    public List<Option> loadOptions(String type) {

        // 清空舊的選項資料
        options.clear();

        String sql = "SELECT * FROM options " +
            "WHERE optionType=? AND (userId=? OR userId='SYSTEM')";

        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 設定查詢條件（決策類型）
            ps.setString(1, type);
            ps.setString(2, userId);

            // 執行查詢
            try (ResultSet rs = ps.executeQuery()) {

                // 將查詢結果逐筆轉成 Option 物件
                while (rs.next()) {
                    options.add(new Option(
                            rs.getInt("optionId"),
                            rs.getString("userId"),
                            rs.getString("optionType"),
                            rs.getString("name"),
                            rs.getString("location"),
                            rs.getString("temperature"),
                            rs.getString("timeCost"),
                            rs.getString("distance"),
                            rs.getString("priceLevel"),
                            rs.getString("mood")
                    ));
                }
            }

            return options;

        } catch (Exception e) {
            // 資料庫連線或查詢異常
            e.printStackTrace();
            return null;
        }
    }

    /**
     * UML: makeDecision() : String
     * -------------------------
     * 1. 若 options 為 null → 表示資料庫讀取失敗
     * 2. 若 options 為空 → 表示尚未有任何選項
     * 3. 否則從 options 中「隨機選一個」
     */
    @Override
    public String makeDecision() {

        // 資料庫讀取失敗
        if (options == null) {
            return "讀取失敗，請稍後嘗試";
        }

        // 尚未有任何選項
        if (options.isEmpty()) {
            return "目前沒有任何選項，請先新增資料";
        }

        // 從清單中隨機選一個
        Option pick = options.get(
                new Random().nextInt(options.size())
        );

        // 組合顯示給使用者的結果文字
        this.result =
                "【隨機決策】\n\n"
                + pick.getDescription();

        // 紀錄決策時間（供歷史紀錄使用）
        this.decisionTime = LocalDateTime.now();

        return result;
    }
}
