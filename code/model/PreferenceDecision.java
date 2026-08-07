package model;

import db.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;

/**
 * UML: PreferenceDecision 類別
 * -------------------------
 * 1. 從資料庫讀取指定類型的選項
 * 2. 根據使用者設定的 FilterCondition 計算每個選項的分數
 * 3. 選出分數最高的選項作為決策結果
 * 4. 若分數相同，則隨機選擇其中一個
 */
public class PreferenceDecision extends Decision {
    private final String userId;
    
    // 使用者設定的偏好條件（由 UI 傳入）
    private FilterCondition filterCondition;

    // 儲存從資料庫撈出的所有候選選項
    private List<Option> options = new ArrayList<>();

    /**
     * 建構子
     */
    public PreferenceDecision(String decisionType,FilterCondition filterCondition,String userId) {
        this.decisionType = decisionType;
        this.filterCondition = filterCondition;
        this.userId = userId;
    }

    /**
     * UML: loadOptions(type) : List<Option>
     * -------------------------
     * 1. 先清空舊的 options
     * 2. 依照 optionType 查詢資料庫
     * 3. 每一筆資料轉成 Option 物件
     * 4. 加入 options 清單中
     */
    public List<Option> loadOptions(String type) {
        options.clear(); // 清空舊資料，避免殘留上一次結果

        String sql = "SELECT * FROM options " +
            "WHERE optionType=? AND (userId=? OR userId='SYSTEM')";

        // 連接資料庫
        try (Connection conn = DBConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 設定 SQL 參數（對應 optionType）
            ps.setString(1, type);
            ps.setString(2, userId);

            // 執行查詢
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // 將每一筆資料轉成 Option 物件
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
            // 資料庫錯誤時，印出錯誤並回傳空清單
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * UML: makeDecision() : String
     * -------------------------
     * 1. 從資料庫載入選項
     * 2. 若沒有選項，直接回傳提示訊息
     * 3. 對每個選項計算偏好分數
     * 4. 找出最高分的選項
     * 5. 若有多個最高分，隨機選一個
     * 6. 組合成結果字串回傳
     */
    @Override
    public String makeDecision() {

        // 重新載入選項（確保使用最新資料）
        loadOptions(decisionType);

        // 若沒有任何選項，直接回傳提示
        if (options.isEmpty()) {
            return "目前沒有任何選項可供決策";
        }

        // 紀錄目前找到的最高分
        int bestScore = Integer.MIN_VALUE;

        // 儲存所有「分數最高」的選項
        List<Option> bestOptions = new ArrayList<>();

        // 逐一計算每個選項的分數
        for (Option option : options) {

            // 使用 FilterCondition 計算加權分數
            int score = filterCondition.calculateScore(option);

            if (score > bestScore) {
                // 若找到更高分，更新最高分並清空舊結果
                bestScore = score;
                bestOptions.clear();
                bestOptions.add(option);

            } else if (score == bestScore) {
                // 若分數相同，加入候選清單
                bestOptions.add(option);
            }
        }

        // 若最高分有多個選項，隨機挑一個
        Option resultOption =
                bestOptions.get(new Random().nextInt(bestOptions.size()));

        // 組合結果文字（包含總分）
        this.result =
                "【偏好加權決策｜總分：" + bestScore + "】\n\n"
                + resultOption.getDescription();

        // 紀錄決策時間
        this.decisionTime = LocalDateTime.now();

        return result;
    }

    /**
     * 取得目前載入的選項清單
     * （通常用於除錯或顯示）
     */
    public List<Option> getOptions() {
        return options;
    }
}
